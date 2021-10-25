package com.bancolombia.codigoton.pojos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bancolombia.codigoton.exceptions.DeskException;

/**
 * 
 * @author Alejandro
 *
 */
public class DeskPojo {

	private String name;
	private Map<String, String> filters;
	private List<String> clients;
	private Boolean canceled = Boolean.FALSE;

	public DeskPojo() {
	}

	public DeskPojo(String name) {
		this.name = name;
		this.filters = new HashMap<>();
		this.clients = new ArrayList<>();
	}

	/**
	 * Agregar clientes a la mesa.
	 * 
	 * @param clientCode codigo del cliente que ingresa a la mesa.
	 * @throws DeskException exepciones de validación de la mesa.
	 */
	public void addClient(String clientCode) throws DeskException {
		if (clients.size() <= 8) {
			if (clients.contains(clientCode)) {
				throw new DeskException("¡Customer " + clientCode + " is already on the table!");
			}
			clients.add(clientCode);
		} else {
			throw new DeskException("¡The table is complete!");
		}
	}

	public Boolean getCanceled() {
		return canceled;
	}

	public void setCanceled(Boolean canceled) {
		this.canceled = canceled;
	}
	
	public void addFilter(String key, String value) {
		filters.put(key, value);
	}

	public Map<String, String> getFilters() {
		return filters;
	}

	public void setFilters(Map<String, String> filters) {
		this.filters = filters;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getClients() {
		return clients;
	}

	public void setClients(List<String> clients) {
		this.clients = clients;
	}

}
