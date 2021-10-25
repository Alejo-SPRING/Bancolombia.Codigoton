package com.bancolombia.codigoton;

import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bancolombia.codigoton.pojos.DeskPojo;
import com.bancolombia.codigoton.services.DinnerService;
import com.bancolombia.codigoton.services.IOrderDinnerService;

@SpringBootTest
class ApplicationTests {
	
	@Autowired
	private IOrderDinnerService orderDinner;
	private Logger logger = LoggerFactory.getLogger(ApplicationTests.class);
	
	@Test
	void contextLoads() {
		try {
			orderDinner.initOrderDinner();
			logger.info(buildResponseExit());
		} catch (Exception e) {
			e.printStackTrace();
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
				String codes = desk.getClients().stream().map(String::valueOf)
						.collect(Collectors.joining(","));
				salida.append(codes);
			}
			salida.append("\n\n");
		}
		return salida.toString();
	}
	
}
