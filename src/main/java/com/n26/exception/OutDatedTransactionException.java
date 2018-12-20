package com.n26.exception;

import com.n26.model.Transaction;

/**
 * Custom Exception thrown when an old transaction is trying to be saved.
 * 
 */
public class OutDatedTransactionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public OutDatedTransactionException(Transaction transaction) {
		super("Outdated transaction: " + transaction);
	}
}
