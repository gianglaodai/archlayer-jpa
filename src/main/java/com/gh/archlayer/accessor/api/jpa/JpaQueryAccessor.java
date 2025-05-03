package com.gh.archlayer.accessor.api.jpa;

import com.gh.archlayer.accessor.api.QueryAccessor;
import com.gh.archlayer.accessor.model.PersistenceEntity;
import com.gh.archlayer.service.filter.Filter;
import com.gh.archlayer.service.model.QueryModel;
import com.gh.archlayer.service.paging.PageRequest;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface JpaQueryAccessor<M extends QueryModel, E extends PersistenceEntity> extends QueryAccessor<M> {

	boolean exists(CriteriaQuery<Integer> cq, Root<E> root, List<? extends Filter<?>> filters);

	boolean exists(CriteriaQuery<Integer> cq);

	Collection<M> findMany(CriteriaQuery<E> cq, Root<E> root, PageRequest pageRequest, List<? extends Filter<?>> filters);

	Collection<M> findMany(CriteriaQuery<E> cq, Root<E> root, PageRequest pageRequest);

	Collection<M> findMany(CriteriaQuery<E> cq, Root<E> root);

	Optional<M> findSingle(CriteriaQuery<E> cq, Root<E> root, List<? extends Filter<?>> filters);

	Optional<M> findSingle(CriteriaQuery<E> cq, Root<E> root);

	long count(CriteriaQuery<Long> cq, Root<E> root);

	long count(CriteriaQuery<Long> cq, Root<E> root, List<? extends Filter<?>> filters);
}
