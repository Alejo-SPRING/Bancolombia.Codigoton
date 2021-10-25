package com.bancolombia.codigoton.services;

import java.io.IOException;

import com.bancolombia.codigoton.exceptions.DeskException;
import com.bancolombia.codigoton.exceptions.DinnerException;

public interface IOrderDinnerService {

	void initOrderDinner() throws IOException, DinnerException, DeskException;

}
