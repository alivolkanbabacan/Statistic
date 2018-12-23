package com.n26.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.Lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.n26.exception.OutDatedTransactionException;
import com.n26.model.Statistic;
import com.n26.model.Transaction;
import com.n26.service.TransactionService;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

/**
 * Implementation of {@link TransactionService}
 */
@Service
public class TransactionServiceImpl implements TransactionService {
	private final ExpiringMap<Transaction, Instant> transactionCache;
	private int cacheTimeInMilliSeconds;
	private Statistic statistic;

	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private final Lock readLock = readWriteLock.readLock();
	private final Lock writeLock = readWriteLock.writeLock();

	@Autowired
	public TransactionServiceImpl(@Value("${cache.time.in.milliseconds}") int cacheTimeInMilliSeconds) {
		this.cacheTimeInMilliSeconds = cacheTimeInMilliSeconds;
		this.statistic = new Statistic();
		this.transactionCache = ExpiringMap.builder().variableExpiration()
				.expirationListener((key, value) -> removedTransactionListener((Transaction) key)).build();
	}

	@Override
	public void saveTransaction(Transaction transaction) {
		if (!isTransactionValid(transaction)) {
			throw new OutDatedTransactionException(transaction);
		}

		writeLock.lock();
		try {
			transactionCache.put(transaction, transaction.getTimestamp(), ExpirationPolicy.CREATED,
					getExpireDateOfTransaction(transaction), TimeUnit.MILLISECONDS);

			statistic.addTransaction(transaction);
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public Statistic getStatistic() {
		readLock.lock();
		try {
			return statistic;
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public void deleteTransactions() {
		writeLock.lock();
		try {
			transactionCache.clear();

			statistic = new Statistic();
		} finally {
			writeLock.unlock();
		}
	}

	private boolean isTransactionValid(Transaction transaction) {
		long milliSeconds = ChronoUnit.MILLIS.between(transaction.getTimestamp(), Instant.now());
		if (milliSeconds < 0) {
			throw new IllegalArgumentException("Transaction's date is in the future: " + transaction);
		}

		return milliSeconds >= 0 && milliSeconds < cacheTimeInMilliSeconds;
	}

	private long getExpireDateOfTransaction(Transaction transaction) {
		long milliSeconds = ChronoUnit.MILLIS.between(transaction.getTimestamp(), Instant.now());

		return cacheTimeInMilliSeconds - milliSeconds;
	}

	/**
	 * Triggered when a transaction has expired and removed from transaction
	 * cache. Updates the Statistic.
	 */
	private void removedTransactionListener(Transaction transaction) {
		writeLock.lock();
		try {
			statistic.substractTransaction(transaction, transactionCache);
		} finally {
			writeLock.unlock();
		}
	}
}
