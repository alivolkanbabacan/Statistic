package com.n26.model;

import java.math.BigDecimal;
import java.time.Instant;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.ToString;

/**
 * Model object for transactions provisioned in the system.
 */
@ToString
@Data
public class Transaction {
	@NotNull
	private BigDecimal amount;

	@NotNull
	private Instant timestamp;
}
