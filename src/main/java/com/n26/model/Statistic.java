package com.n26.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Comparator;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.n26.util.CustomBigDecimalSerializer;

import lombok.Getter;
import lombok.ToString;
import net.jodah.expiringmap.ExpiringMap;

/**
 * Model object for statistics of transactions provisioned in the system.
 */
@ToString
@Getter
public class Statistic {
	@JsonSerialize(using = CustomBigDecimalSerializer.class)
	private BigDecimal sum;

	@JsonSerialize(using = CustomBigDecimalSerializer.class)
	private BigDecimal avg;

	@JsonSerialize(using = CustomBigDecimalSerializer.class)
	private BigDecimal max;

	@JsonSerialize(using = CustomBigDecimalSerializer.class)
	private BigDecimal min;
	private long count;

	public Statistic() {
		sum = new BigDecimal(0);
		avg = new BigDecimal(0);
		max = new BigDecimal(0);
		min = new BigDecimal(0);
	}

	/**
	 * Updates Statistic object with the values of the given transaction
	 * 
	 * @param {@link Transaction}
	 */
	public void addTransaction(Transaction transaction) {
		if (count == 0) {
			max = transaction.getAmount();
			min = transaction.getAmount();
		}
		count++;
		sum = this.sum.add(transaction.getAmount());
		max = this.max.max(transaction.getAmount());
		min = this.min.min(transaction.getAmount());
		avg = this.sum.divide(new BigDecimal(this.count), 2, RoundingMode.HALF_UP);
	}

	/**
	 * Updates Statistic object by extracting the values of the removed
	 * transaction. Remaining transaction cache is needed in case of calculating
	 * new max and min values
	 * 
	 * @param {@link Transaction}
	 * @param transaction cache after removal of the transaction
	 */
	public void substractTransaction(Transaction transaction, ExpiringMap<Transaction, Instant> cache) {
		count--;
		if (count == 0) {
			sum = new BigDecimal(0);
			avg = new BigDecimal(0);
			max = new BigDecimal(0);
			min = new BigDecimal(0);

			return;
		}

		sum = sum.subtract(transaction.getAmount());
		avg = sum.divide(new BigDecimal(this.count), 2, RoundingMode.HALF_UP);

		if (transaction.getAmount().equals(max) && transaction.getAmount().equals(min)) {
			// No need to update max or min. As there is at least another
			// transaction with the same amount in the cache
			return;
		} else if (transaction.getAmount().equals(max)) {

			// Get the next maximum amount from transaction cache
			this.max = cache.keySet().parallelStream().max(Comparator.comparing(Transaction::getAmount)).get()
					.getAmount();
			
		} else if (transaction.getAmount().equals(min)) {

			// Get the next minimum amount from transaction cache
			this.min = cache.keySet().parallelStream().min(Comparator.comparing(Transaction::getAmount)).get()
					.getAmount();
		}
	}
}
