package com.n26.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.n26.util.CustomBigDecimalSerializer;

import lombok.Getter;
import lombok.ToString;

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
	 * Updates Statistic object with the values of transaction given.
	 * 
	 * @param {@link Transaction}
	 */
	public void addTransaction(Transaction transaction) {
		if (this.count == 0) {
			this.max = transaction.getAmount();
			this.min = transaction.getAmount();
		}
		this.count++;
		this.sum = this.sum.add(transaction.getAmount());
		this.max = this.max.max(transaction.getAmount());
		this.min = this.min.min(transaction.getAmount());
		this.avg = this.sum.divide(new BigDecimal(this.count), 2, RoundingMode.HALF_UP);
	}
}
