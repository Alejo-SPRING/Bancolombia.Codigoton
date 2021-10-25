package com.bancolombia.codigoton.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bancolombia.codigoton.util.Properties;

@Service
public class DecryptServiceImpl implements IDecryptService {

	@Autowired
	private Properties properties;

	/**
	 * Desencripta condigos, con la api proporcionada.
	 * @param String code codigo encriptado
	 * @return String se retorna el codigo desencriptado
	 */
	@Override
	public String decryptCode(String code) {
		RestTemplate rest = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<Object> request = new HttpEntity<>(headers);
		ResponseEntity<Object> response = rest.exchange(properties.getUrlDecrypt() + code, HttpMethod.GET, request, Object.class);
		return response.getBody().toString();
	}
	
}
