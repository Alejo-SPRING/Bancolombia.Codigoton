package com.bancolombia.codigoton.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Properties {

	@Value("${url.decrypt}")
	private String urlDecrypt;

	public String getUrlDecrypt() {
		return urlDecrypt;
	}

	public void setUrlDecrypt(String urlDecrypt) {
		this.urlDecrypt = urlDecrypt;
	}

}
