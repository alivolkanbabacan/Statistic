package com.n26.rest;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.n26.model.Transaction;
import com.n26.service.TransactionService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

/**
 * Controller class containing rest services regarding transaction operations
 * 
 * @see {@link Transaction}
 * @see {@link TransactionService}
 */
@Log
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TransactionController {

	private final TransactionService transactionService;

	/**
	 * Saves a given transaction.
	 * 
	 * @param {@link Transaction}
	 */
	@ApiOperation(value = "Transaction Handler", notes = "Saves Transactions")
	@ApiResponses(value = { @ApiResponse(code = 201, message = "In case of success"),
			@ApiResponse(code = 204, message = "If transaction is older than 60 seconds"),
			@ApiResponse(code = 400, message = "If the JSON is invalid"),
			@ApiResponse(code = 422, message = "If any of the fields are not parsable or the transaction date is in the future") })
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public void saveTransaction(@RequestBody @Valid Transaction transaction) {
		log.info("Saving transaction: " + transaction);
		transactionService.saveTransaction(transaction);
	}

	/**
	 * Deletes all transactions provisioned in the system.
	 */
	@ApiOperation(value = "Transaction Removal", notes = "Deletes All Transactions")
	@ApiResponses(value = { @ApiResponse(code = 204, message = "In case of success") })
	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteTransactions() {
		log.info("Deleting all transactions!");
		transactionService.deleteTransactions();
	}
}
