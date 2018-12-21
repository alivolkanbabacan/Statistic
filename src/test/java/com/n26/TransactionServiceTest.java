package com.n26;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class TransactionServiceTest {

	@Autowired
	private TransactionService transactionService;

	/**
	 * Cleans the transaction cache after each test
	 */
	@After
	public void cleanTransactions() {
		transactionService.deleteTransactions();
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
	public void testSaveFutureDatedTransaction() {
		Transaction transaction = new Transaction();
		transaction.setAmount(new BigDecimal("80.0"));
		transaction.setTimestamp(Instant.now().plusSeconds(70));

		transactionService.saveTransaction(transaction);
	}

	/**
	 * Getting Statistic after the only transaction has been removed from cache,
	 * thus when cache is empty
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testEmptyCache() throws InterruptedException {
		Transaction transaction = new Transaction();
		transaction.setAmount(new BigDecimal("50.0"));
		transaction.setTimestamp(Instant.now().minusMillis(59990));
		transactionService.saveTransaction(transaction);

		Thread.sleep(500);

		Statistic statistic = transactionService.getStatistic();
		assertEquals(new BigDecimal("0"), statistic.getSum());
		assertEquals(new BigDecimal("0"), statistic.getMax());
		assertEquals(new BigDecimal("0"), statistic.getMin());
		assertEquals(0, statistic.getCount());
		assertEquals(new BigDecimal("0"), statistic.getAvg());
	}

	/**
	 * Getting Statistic after the transaction with the maximum amount has been
	 * removed
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testMaximumAmountRemoved() throws InterruptedException {
		Transaction transaction1 = new Transaction();
		transaction1.setAmount(new BigDecimal("50.0"));
		transaction1.setTimestamp(Instant.now());

		Transaction transaction2 = new Transaction();
		transaction2.setAmount(new BigDecimal("60.0"));
		transaction2.setTimestamp(Instant.now());

		Transaction transaction3 = new Transaction();
		transaction3.setAmount(new BigDecimal("70.0"));
		transaction3.setTimestamp(Instant.now().minusMillis(59990));

		transactionService.saveTransaction(transaction1);
		transactionService.saveTransaction(transaction2);
		transactionService.saveTransaction(transaction3);

		Thread.sleep(500);

		Statistic statistic = transactionService.getStatistic();
		assertEquals(new BigDecimal("110.0"), statistic.getSum());
		assertEquals(new BigDecimal("60.0"), statistic.getMax());
		assertEquals(new BigDecimal("50.0"), statistic.getMin());
		assertEquals(2, statistic.getCount());
		assertEquals(new BigDecimal("55.00"), statistic.getAvg());
	}

	/**
	 * Getting Statistic after the transaction with the minimum amount has been
	 * removed
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testMinimumAmountRemoved() throws InterruptedException {
		Transaction transaction1 = new Transaction();
		transaction1.setAmount(new BigDecimal("70.0"));
		transaction1.setTimestamp(Instant.now());

		Transaction transaction2 = new Transaction();
		transaction2.setAmount(new BigDecimal("60.0"));
		transaction2.setTimestamp(Instant.now());

		Transaction transaction3 = new Transaction();
		transaction3.setAmount(new BigDecimal("50.0"));
		transaction3.setTimestamp(Instant.now().minusMillis(59990));

		transactionService.saveTransaction(transaction1);
		transactionService.saveTransaction(transaction2);
		transactionService.saveTransaction(transaction3);

		Thread.sleep(500);

		Statistic statistic = transactionService.getStatistic();
		assertEquals(new BigDecimal("130.0"), statistic.getSum());
		assertEquals(new BigDecimal("70.0"), statistic.getMax());
		assertEquals(new BigDecimal("60.0"), statistic.getMin());
		assertEquals(2, statistic.getCount());
		assertEquals(new BigDecimal("65.00"), statistic.getAvg());
	}

	/**
	 * Getting the Statistic when amounts of all transactions in the cache are
	 * equal and one of them has been removed. This way if all amounts are
	 * equal, we don't want to traverse the whole cache to recalculate max and
	 * min values of Statistic
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testRemovalFromEqualCache() throws InterruptedException {
		Transaction transaction1 = new Transaction();
		transaction1.setAmount(new BigDecimal("50.0"));
		transaction1.setTimestamp(Instant.now());

		Transaction transaction2 = new Transaction();
		transaction2.setAmount(new BigDecimal("50.0"));
		transaction2.setTimestamp(Instant.now().minusMillis(59990));

		transactionService.saveTransaction(transaction1);
		transactionService.saveTransaction(transaction2);

		Thread.sleep(500);

		Statistic statistic = transactionService.getStatistic();
		assertEquals(new BigDecimal("50.0"), statistic.getSum());
		assertEquals(new BigDecimal("50.0"), statistic.getMax());
		assertEquals(new BigDecimal("50.0"), statistic.getMin());
		assertEquals(1, statistic.getCount());
		assertEquals(new BigDecimal("50.00"), statistic.getAvg());
	}
}
