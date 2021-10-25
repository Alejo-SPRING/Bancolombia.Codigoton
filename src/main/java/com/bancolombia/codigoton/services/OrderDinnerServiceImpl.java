package com.bancolombia.codigoton.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bancolombia.codigoton.enums.CodeFilterEnum;
import com.bancolombia.codigoton.exceptions.DeskException;
import com.bancolombia.codigoton.exceptions.DinnerException;
import com.bancolombia.codigoton.model.entity.Client;
import com.bancolombia.codigoton.pojos.DeskPojo;

@Service
public class OrderDinnerServiceImpl implements IOrderDinnerService {

	@Autowired
	private IFileService fileService;
	@Autowired
	private IAccountService criteriaService;
	@Autowired
	private IDecryptService decryptService;
	private List<Object[]> clientsAll;
	private List<Object[]> clients;
	private Logger logger = LoggerFactory.getLogger(OrderDinnerServiceImpl.class);

	/**
	 * Inicia la construcción de las mesas, lectura de entrada, construccion y
	 * consulta con filtros de entrada, organizacion de datos
	 */
	@Override
	public void initOrderDinner() throws IOException, DinnerException, DeskException {
		DinnerService.resetDinner();
		List<String> filters = fileService.readFile("entrada.txt");
		buildDesks(filters);
		for (DeskPojo desk : DinnerService.getDinner().getDesks()) {
			clientsAll = criteriaService.getClientsByFilters(desk.getFilters());
			if (clientsAll.size() < 4) {
				desk.setCanceled(Boolean.TRUE);
			} else {
				clients = clientsAll.size() >= 8 ? clientsAll.stream().collect(Collectors.toList()).subList(0, 8)
						: clientsAll.stream().collect(Collectors.toList());
				clientsAll.removeAll(clients);
				orderIfEqualCompany(clients);
				orderBySex(clients);
				orderIfEqualBalance(clients);
				if (this.clients.size() < 4) {
					desk.setCanceled(Boolean.TRUE);
				} else {
					for (Object[] dataClient : this.clients) {
						Client client = (Client) dataClient[0];
						desk.addClient(client.getCode());
					}
				}
			}
		}
	}

	private void orderBySex(List<Object[]> clients) {
		Map<Boolean, List<Object[]>> clientsOrderSex = clients.stream().collect(Collectors.groupingBy(orderBySex()));
		if (clientsOrderSex.size() == 2 && (clientsOrderSex.get(true).size() != clientsOrderSex.get(false).size())) {
			calculateOrderBySex(clientsOrderSex.get(true), clientsOrderSex.get(false));
		}
	}

	/**
	 * Calcula cual sexo tiene mayor balance, elimina los de menor valance y los
	 * remplaza por el sexo opuesto
	 * 
	 * @param clientes
	 */
	private void calculateOrderBySex(List<Object[]> clientsMale, List<Object[]> clientsFemale) {
		if(clientsMale.size() > clientsFemale.size()) {
			this.clients.remove(clientsMale.get(clientsMale.size() - 1));	
		} else {
			this.clients.remove(clientsFemale.get(clientsFemale.size() -1 ));
		}
		replaceClients();
		orderIfEqualCompany(this.clients);
		orderBySex(this.clients);
	}

	private Function<Object[], Boolean> orderBySex() {
		return data -> {
			Client client = (Client) data[0];
			return client.getMale();
		};
	}

	/**
	 * Ordena los clientes si hay empresas iguales
	 * 
	 * @param clients
	 */
	private void orderIfEqualCompany(List<Object[]> clients) {
		List<Object[]> clientsFilter = new ArrayList<>();
		// Obtenemos los clientes con empresas iguales.
		for (Object[] clientData : clients) {
			clientsFilter.addAll(
					clients.stream().filter(isEqualCompany((Client) clientData[0])).collect(Collectors.toList()));
		}
		if (!clientsFilter.isEmpty()) {
			// agrupamos los clientes por empresa
			Map<String, List<Object[]>> clientGroupByCompany = clientsFilter.stream()
					.collect(Collectors.groupingBy(groupClientsByCompany()));
			Boolean isRemove = Boolean.FALSE;
			for (Entry<String, List<Object[]>> clientsGroup : clientGroupByCompany.entrySet()) {
				// ordena los clientes por el balance mayor al menor y elimina el de mayor
				// balance
				orderByBalance(clientsGroup.getValue());
				// eliminamos los clientes que tienen grupo igual y menor balance
				isRemove = clients.removeAll(clientsGroup.getValue());
			}
			if (isRemove) {
				logger.info("¡Clientes con company repetidas eliminados!");
				replaceClients();
				orderIfEqualCompany(this.clients);
			}
		}
	}

	/**
	 * Se calcula si faltan los 8 clientes de la mesa, si faltan se agregan los que
	 * faltan
	 */
	private void replaceClients() {
		logger.info("¡Remplazando clientes!");
		Integer clientsMissing = (this.clients.size() - 8) * -1;
		if (!this.clientsAll.isEmpty() && clientsMissing > 0) {
			if (this.clientsAll.size() >= clientsMissing) {
				this.clients.addAll(this.clientsAll.stream().collect(Collectors.toList()).subList(0, clientsMissing));
				this.clientsAll.removeAll(clients);
			} else {
				this.clients.addAll(this.clientsAll.stream().collect(Collectors.toList()));
				this.clientsAll.removeAll(clients);
			}
		}
	}

	private void orderByBalance(List<Object[]> clients) {
		// se organiza para ver los clientes con mayor balance al menor
		Collections.sort(clients, Collections.reverseOrder(orderByBalance()));
		// se elimina el cliente con mayor para eliminar los que tiene menor
		clients.remove(0);
	}

	private Comparator<Object[]> orderByBalance() {
		return (o1, o2) -> {
			BigDecimal balance1 = (BigDecimal) o1[1];
			BigDecimal balance2 = (BigDecimal) o2[1];
			return balance1.compareTo(balance2);
		};
	}

	private Function<Object[], String> groupClientsByCompany() {
		return data -> {
			Client client = (Client) data[0];
			return client.getCompany();
		};
	}

	private Predicate<Object[]> isEqualCompany(Client client) {
		return data -> {
			Client clientData = (Client) data[0];
			return clientData.getCompany().compareTo(client.getCompany()) == 0
					&& clientData.getId().compareTo(client.getId()) != 0;
		};
	}

	/**
	 * Valida si hay balances iguales, y si existen los ordena por codigo.
	 * 
	 * @param clients
	 */
	private void orderIfEqualBalance(List<Object[]> clients) {
		Boolean isBalanceEquals = Boolean.FALSE;
		for (Object[] clientData : clients) {
			Client client = (Client) clientData[0];
			if (client.getEncrypt()) {
				decryptCode(client);
			}
			Object[] data = clients.stream().filter(isEqualsBalance((Client) clientData[0], (BigDecimal) clientData[1]))
					.findFirst().orElse(null);
			if (data != null) {
				isBalanceEquals = Boolean.TRUE;
				break;
			}
		}
		if (isBalanceEquals) {
			orderByCode(clients);
		} else {
			Collections.sort(this.clients, Collections.reverseOrder(sortByClientBalance()));
		}
	}

	private Comparator<Object[]> sortByClientBalance() {
		return (clientData1, clientData2) -> {
			BigDecimal balance1 = (BigDecimal) clientData1[1];
			BigDecimal balance2 = (BigDecimal) clientData2[1];
			return balance1.compareTo(balance2);
		};
	}

	private void orderByCode(List<Object[]> clients) {
		Collections.sort(clients, sortByClientCode());
	}

	private Comparator<Object[]> sortByClientCode() {
		return (clientData1, clientData2) -> {
			Client client1 = (Client) clientData1[0];
			Client client2 = (Client) clientData2[0];
			return client1.getCode().compareTo(client2.getCode());
		};
	}

	private void decryptCode(Client client) {
		String code = decryptService.decryptCode(client.getCode());
		client.setCode(code);
	}

	private Predicate<Object[]> isEqualsBalance(Client client, BigDecimal balance) {
		return data -> {
			BigDecimal value = (BigDecimal) data[1];
			Client clientIter = (Client) data[0];
			return value.compareTo(balance) == 0 && client.getId().compareTo(clientIter.getId()) != 0;
		};
	}

	/**
	 * Se crean las mesas o grupos con los datos del texto plano
	 * 
	 * @param filters texto plano
	 * @throws DinnerException validaciones al agregar las mesas
	 */
	private void buildDesks(List<String> filters) throws DinnerException {
		DeskPojo desk = new DeskPojo();
		for (String filter : filters) {
			if (isDeskName(filter)) {
				if (desk.getName() != null) {
					DinnerService.getDinner().addDesk(desk);
				}
				desk = new DeskPojo(filter);
			} else if (isDeskFilter(filter)) {
				String[] filterBuild = buildFilter(filter);
				desk.addFilter(filterBuild[0], filterBuild[1]);
			}
		}
		// Guardamos la ultima mesa
		if (desk.getName() != null) {
			DinnerService.getDinner().addDesk(desk);
		}
	}

	private String[] buildFilter(String filter) {
		String filterFormat = filter.replace(" ", "");
		return filterFormat.split(":");
	}

	private Boolean isDeskName(String text) {
		return text.contains("General") || text.contains("Mesa");
	}

	/**
	 * Valdiar si un texto es un filtro
	 * 
	 * @param text texto a evaluar
	 * @return true si es un filtro de lo contrario false
	 */
	private Boolean isDeskFilter(String text) {
		for (CodeFilterEnum codeFilter : CodeFilterEnum.values()) {
			if (text.contains(codeFilter.name())) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

}
