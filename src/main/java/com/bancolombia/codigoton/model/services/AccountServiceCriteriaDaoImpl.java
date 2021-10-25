package com.bancolombia.codigoton.model.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bancolombia.codigoton.enums.CodeFilterEnum;
import com.bancolombia.codigoton.model.entity.Account;
import com.bancolombia.codigoton.model.entity.Client;

@Repository
public class AccountServiceCriteriaDaoImpl implements IAccountServiceCriteriaDao{

	@Autowired
	private EntityManager em;

	/**
	 * Se consultan los clientes con los filtros o criterios especificados dinamicamente
	 * @param Map<String, String> filters filtros de la mesa
	 * @return List<Object[]> lista con los clientes[0] y total del balance[1]
	 */
	@Override
	public List<Object[]> findClientByFilters(Map<String, String> filters) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		List<Predicate> predicates = new ArrayList<>();
		Root<Account> account = cq.from(Account.class);
		Join<Account, Client> join = account.join("client", JoinType.INNER);
		cq.multiselect(join, cb.sum(account.get("balance")));
		Boolean isRIAndRFAdd = Boolean.FALSE;
		for (Entry<String, String> values : filters.entrySet()) {
			if (values.getKey().equals(CodeFilterEnum.TC.name())) {
				predicates.add(cb.equal(account.get("client").get("type"), Integer.valueOf(values.getValue())));
			} else if (values.getKey().equals(CodeFilterEnum.UG.name())) {
				predicates.add(cb.equal(account.get("client").get("location"), Integer.valueOf(values.getValue())));
			} else if (values.getKey().equals(CodeFilterEnum.RI.name())) {
				if (isRIAndRF(filters)) {
					isRIAndRFAdd = Boolean.TRUE;
					cq.having(
							cb.greaterThan(cb.sum(account.get("balance")),
									new BigDecimal(filters.get(CodeFilterEnum.RI.name())).setScale(2)),
							cb.lessThan(cb.sum(account.get("balance")),
									new BigDecimal(filters.get(CodeFilterEnum.RF.name())).setScale(2)));
				} else {
					cq.having(cb.greaterThan(cb.sum(account.get("balance")),
							new BigDecimal(values.getValue()).setScale(2)));
				}
			} else if (values.getKey().equals(CodeFilterEnum.RF.name())) {
				if (!isRIAndRFAdd) {
					cq.having(
							cb.lessThan(cb.sum(account.get("balance")), new BigDecimal(values.getValue()).setScale(2)));
				}
			}
		}
		cq.where(predicates.toArray(new Predicate[] {}));
		cq.groupBy(account.get("client").get("id"));
		cq.orderBy(cb.desc(cb.sum(account.get("balance"))));
		return em.createQuery(cq).getResultList();
	}

	private Boolean isRIAndRF(Map<String, String> filters) {
		return filters.containsKey(CodeFilterEnum.RI.name()) && filters.containsKey(CodeFilterEnum.RF.name());
	}

}
