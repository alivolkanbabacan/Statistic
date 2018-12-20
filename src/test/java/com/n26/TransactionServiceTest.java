package com.n26;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.n26.exception.OutDatedTransactionException;
import com.n26.model.Statistic;
import com.n26.model.Transaction;
import com.n26.service.TransactionService;

/**
 * Test cases related to transaction services
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TransactionServiceTest {

	@Autowired
	private TransactionService transactionService;

	/**
	 * Save two appropriate transactions
	 */
	@Before
	public void populateTransactionList() {
		Transaction transaction1 = new Transaction();
		transaction1.setAmount(new BigDecimal("50.0"));
		transaction1.setTimestamp(Instant.now());

		Transaction transaction2 = new Transaction();
		transaction2.setAmount(new BigDecimal("60.0"));
		transaction2.setTimestamp(Instant.now().minusSeconds(10));

		transactionService.saveTransaction(transaction1);
		transactionService.saveTransaction(transaction2);
	}

	/**
	 * Try to save an old transaction
	 */
	@Test(expected = OutDatedTransactionException.class)
	public void testSaveOutDatedTransaction() {
		Transaction transaction = new Transaction();
		transaction.setAmount(new BigDecimal("70.0"));
		transaction.setTimestamp(Instant.now().minusSeconds(70));

		transactionService.saveTransaction(transaction);
	}

	/**
	 * Try to save a transaction with a date that is in the future
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSaveIllegalDatedTransaction() {
		Transaction transaction = new Transaction();
		transaction.setAmount(new BigDecimal("80.0"));
		transaction.setTimestamp(Instant.now().plusSeconds(70));

		transactionService.saveTransaction(transaction);
	}

	/**
	 * Try to get the statistic of the already existing transfers
	 */
	@Test
	public void testGetStatistic() {
		Statistic statistic = transactionService.getStatistic();
		assertEquals(new BigDecimal("110.0"), statistic.getSum());
		assertEquals(new BigDecimal("60.0"), statistic.getMax());
		assertEquals(new BigDecimal("50.0"), statistic.getMin());
		assertEquals(2, statistic.getCount());
		assertEquals(new BigDecimal("55.00"), statistic.getAvg());
	}

	/**
	 * Delete all transactions and get a statistic to verify
	 */
	@Test
	public void testDeleteTransactions() {
		transactionService.deleteTransactions();

		Statistic statistic = transactionService.getStatistic();
		assertEquals(new BigDecimal("0"), statistic.getSum());
		assertEquals(new BigDecimal("0"), statistic.getMax());
		assertEquals(new BigDecimal("0"), statistic.getMin());
		assertEquals(0, statistic.getCount());
		assertEquals(new BigDecimal("0"), statistic.getAvg());
	}
}
