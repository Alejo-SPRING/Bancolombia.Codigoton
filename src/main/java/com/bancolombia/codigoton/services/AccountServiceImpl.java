package com.bancolombia.codigoton.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bancolombia.codigoton.model.services.IAccountServiceCriteriaDao;

@Service
public class AccountServiceImpl implements IAccountService {

	@Autowired
	private IAccountServiceCriteriaDao accountService;

	@Override
	public List<Object[]> getClientsByFilters(Map<String, String> filters) {
		return accountService.findClientByFilters(filters);
	}

}
