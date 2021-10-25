package com.bancolombia.codigoton.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bancolombia.codigoton.exceptions.DeskException;
import com.bancolombia.codigoton.exceptions.DinnerException;
import com.bancolombia.codigoton.pojos.DeskPojo;
import com.bancolombia.codigoton.services.DinnerService;
import com.bancolombia.codigoton.services.IOrderDinnerService;

@RestController
@RequestMapping("/codeton/organization/dinner")
public class RestDinner {

	@Autowired
	private IOrderDinnerService orderDinnerService;
	private Logger logger = LoggerFactory.getLogger(RestDinner.class);
	private Map<String, Object> body;

	@GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
	private ResponseEntity<Object> organiationDinner() {
		body = new HashMap<>();
		try {
			orderDinnerService.initOrderDinner();
			return new ResponseEntity<>(buildResponseExit(), HttpStatus.OK);
		} catch (IOException | DinnerException | DeskException e) {
			StringBuilder builder = new StringBuilder(e.getMessage());
			if (builder.toString().isEmpty() || builder.toString().isBlank()) {
				for (StackTraceElement trace : e.getStackTrace()) {
					builder.append(trace.toString()).append("\n");
				}
			}
			logger.error(builder.toString());
			body.put("message", builder.toString());
			return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Construye la salida solicitada, con los datos resultantes de los filtros
	 * 
	 * @return salida construida
	 */
	private String buildResponseExit() {
		StringBuilder salida = new StringBuilder();
		for (DeskPojo desk : DinnerService.getDinner().getDesks()) {
			salida.append(desk.getName()).append("\n");
			if (desk.getCanceled()) {
				salida.append("CANCELADA");
			} else {
				String codes = desk.getClients().stream().map(String::valueOf).collect(Collectors.joining(","));
				salida.append(codes);
			}
			salida.append("\n\n");
		}
		return salida.toString();
	}

}
