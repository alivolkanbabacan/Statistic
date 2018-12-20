package com.n26.service;

import com.n26.model.Statistic;
import com.n26.model.Transaction;

/**
 * Service interface for transaction and statistic related operations
 * 
 * @see {@link Transaction}
 * @see {@link Statistic}
 */
public interface TransactionService {
	/**
	 * Saves a transaction.
	 * 
	 * @param {@link Transaction}
	 */
	void saveTransaction(Transaction transaction);

	/**
	 * Deletes all transactions in the system.
	 */
	void deleteTransactions();

	/**
	 * Returns statistic of the transactions for the last minute.
	 * 
	 * @return {@link Statistic}
	 */
	Statistic getStatistic();
}
