package com.n26.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

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

	@Autowired
	public TransactionServiceImpl(@Value("${cache.time.in.milliseconds}") int cacheTimeInMilliSeconds) {
		this.cacheTimeInMilliSeconds = cacheTimeInMilliSeconds;
		this.transactionCache = ExpiringMap.builder().variableExpiration().build();
	}

	@Override
	public void saveTransaction(Transaction transaction) {
		if (!isTransactionValid(transaction)) {
			throw new OutDatedTransactionException(transaction);
		}

		transactionCache.put(transaction, transaction.getTimestamp(), ExpirationPolicy.CREATED,
				getExpireDateOfTransaction(transaction), TimeUnit.MILLISECONDS);

	}

	@Override
	public Statistic getStatistic() {
		Statistic statistic = new Statistic();

		transactionCache.keySet().stream().forEach(e -> statistic.addTransaction(e));

		return statistic;
	}

	@Override
	public void deleteTransactions() {
		transactionCache.clear();
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
}
