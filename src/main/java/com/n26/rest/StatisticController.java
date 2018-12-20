package com.n26.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.n26.model.Statistic;
import com.n26.service.TransactionService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

/**
 * Controller class containing rest services regarding statistic operations
 * 
 * @see {@link Statistic}
 */
@Log
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StatisticController {

	private final TransactionService transactionService;

	/**
	 * Returns statistic of the transactions for the last minute.
	 * 
	 * @return {@link Statistic}
	 */
	@ApiOperation(value = "Last 60 second statistic", notes = "Returns the statistic of transactions for the last minute", response = Statistic.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "In case of success", response = Statistic.class) })
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public Statistic getStatistic() {
		Statistic statistic = transactionService.getStatistic();
		log.info("Statistic requested: " + statistic);
		return statistic;
	}
}
