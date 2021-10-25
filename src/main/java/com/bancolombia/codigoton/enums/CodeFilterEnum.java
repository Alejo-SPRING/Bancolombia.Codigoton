package com.bancolombia.codigoton.enums;

public enum CodeFilterEnum {

	TC("Tipo de Cliente"),
	UG("Código de ubicación geográfica"),
	RI("Rango Inicial Balance"),
	RF("Rango Final Balance");
	
	private String value;

	private CodeFilterEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
