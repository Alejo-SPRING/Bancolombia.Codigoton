package com.bancolombia.codigoton.services;

import java.util.ArrayList;
import java.util.List;

import com.bancolombia.codigoton.exceptions.DinnerException;
import com.bancolombia.codigoton.pojos.DeskPojo;

public class DinnerService {

	private List<DeskPojo> desks;
	private static DinnerService organizerService;

	private DinnerService() {
		desks = new ArrayList<>();
	}

	public static void resetDinner() {
		organizerService = new DinnerService();
	}
	
	public static DinnerService getDinner() {
		if (organizerService == null) {
			organizerService = new DinnerService();
		}
		return organizerService;
	}

	/**
	 * Agrega una nueva mesa
	 * 
	 * @param desk nueva mesa
	 * @throws DinnerException validación si la mesa ya existe
	 */
	public void addDesk(DeskPojo desk) throws DinnerException {
		DeskPojo deskData = desks.stream().filter(d -> d.getName().equalsIgnoreCase(desk.getName())).findFirst()
				.orElse(null);
		if (deskData != null) {
			throw new DinnerException("¡The Desk is exist!");
		}
		desks.add(desk);
	}

	public List<DeskPojo> getDesks() {
		return desks;
	}

}
