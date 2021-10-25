package com.bancolombia.codigoton.model.services;

import java.util.List;
import java.util.Map;

public interface IAccountServiceCriteriaDao {

	List<Object[]> findClientByFilters(Map<String, String> filters);

}
