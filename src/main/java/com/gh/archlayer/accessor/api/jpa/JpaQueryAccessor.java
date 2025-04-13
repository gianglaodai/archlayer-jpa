package com.gh.archlayer.accessor.api.jpa;

import com.gh.archlayer.accessor.api.QueryAccessor;
import com.gh.archlayer.accessor.model.PersistenceEntity;
import com.gh.archlayer.service.filter.Filter;
import com.gh.archlayer.service.model.QueryModel;
import com.gh.archlayer.service.paging.PageRequest;
import jakarta.persistence.criteria.CriteriaQuery;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface JpaQueryAccessor<M extends QueryModel, E extends PersistenceEntity> extends QueryAccessor<M> {
	boolean exists(CriteriaQuery<Integer> cq, List<Filter<?>> filters);
	boolean exists(CriteriaQuery<Integer> cq);
	Collection<M> findMany(PageRequest pageRequest, CriteriaQuery<E> cq, List<Filter<?>> filters);
	Collection<M> findMany(PageRequest pageRequest, CriteriaQuery<E> cq);
	Collection<M> findMany(CriteriaQuery<E> cq);
	Optional<M> findSingle(CriteriaQuery<E> cq, List<Filter<?>> filters);
	Optional<M> findSingle(CriteriaQuery<E> cq);
	long count(CriteriaQuery<Long> cq);
	long count(CriteriaQuery<Long> cq, List<Filter<?>> filters);
}
