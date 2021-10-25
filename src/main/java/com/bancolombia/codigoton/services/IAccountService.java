package com.bancolombia.codigoton.services;

import java.util.List;
import java.util.Map;

public interface IAccountService {

	List<Object[]> getClientsByFilters(Map<String, String> filters);
	
}
