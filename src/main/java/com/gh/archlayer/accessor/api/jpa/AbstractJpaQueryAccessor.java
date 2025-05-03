package com.gh.archlayer.accessor.api.jpa;

import com.gh.archlayer.accessor.api.impl.AbstractQueryAccessor;
import com.gh.archlayer.accessor.filter.JpaFilterResolverRegistry;
import com.gh.archlayer.accessor.model.PersistenceEntity;
import com.gh.archlayer.service.filter.Filter;
import com.gh.archlayer.service.model.QueryModel;
import com.gh.archlayer.service.paging.PageRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

public abstract class AbstractJpaQueryAccessor<M extends QueryModel, E extends PersistenceEntity> extends AbstractQueryAccessor<M, E> implements JpaQueryAccessor<M, E> {

	public abstract EntityManager getEntityManager();

	private CriteriaBuilder getCriteriaBuilder() {
		return getEntityManager().getCriteriaBuilder();
	}

	@Override
	public Collection<M> findMany(final CriteriaQuery<E> cq, Root<E> root, final PageRequest pageRequest, final List<? extends Filter<?>> filters) {
		return findStream(pageRequest, cq, root, filters).map(getMapper()::toModel).toList();
	}

	protected Stream<E> findStream(final PageRequest pageRequest, final CriteriaQuery<E> cq, Root<E> root, final List<? extends Filter<?>> filters) {
		applyFilters(cq, root, filters);
		applyOrder(cq, root, pageRequest);
		final TypedQuery<E> query = getEntityManager().createQuery(cq);
		if (pageRequest.firstResult() >= 0) {
			query.setFirstResult(pageRequest.firstResult());
		}
		if (pageRequest.maxResults() > 0) {
			query.setMaxResults(pageRequest.maxResults());
		}
		return query.getResultStream();
	}

	protected Collection<E> findEntities(final PageRequest pageRequest, final CriteriaQuery<E> cq, Root<E> root, final List<? extends Filter<?>> filters) {
		return findStream(pageRequest, cq, root, filters).toList();
	}

	protected Collection<E> findEntities(final CriteriaQuery<E> cq, Root<E> root, final List<? extends Filter<?>> filters) {
		return findEntities(PageRequest.DEFAULT, cq, root, filters);
	}

	protected Collection<E> findEntities(final CriteriaQuery<E> cq, Root<E> root) {
		return findEntities(PageRequest.DEFAULT, cq, root, List.of());
	}

	protected Collection<E> findEntities(final List<? extends Filter<?>> filters) {
		CriteriaQuery<E> cq = getCriteriaBuilder().createQuery(getEntityClass());
		return findEntities(PageRequest.DEFAULT, cq, cq.from(getEntityClass()), filters);
	}

	@Override
	public boolean exists(final List<? extends Filter<?>> filters) {
		final CriteriaQuery<Integer> cq = getCriteriaBuilder().createQuery(Integer.class);
		applyFilters(cq, cq.from(getEntityClass()), filters);
		return exists(cq);
	}

	@Override
	public boolean exists(final CriteriaQuery<Integer> cq, Root<E> root, final List<? extends Filter<?>> filters) {
		applyFilters(cq, root, filters);
		return exists(cq);
	}

	@Override
	public boolean exists(final CriteriaQuery<Integer> cq) {
		cq.select(getCriteriaBuilder().literal(1));
		return isNotEmpty(getEntityManager().createQuery(cq).setMaxResults(1).getResultList());
	}

	@Override
	public Collection<M> findMany(final CriteriaQuery<E> cq, Root<E> root, final PageRequest pageRequest) {
		return findMany(cq, root, pageRequest, List.of());
	}

	@Override
	public Collection<M> findMany(final CriteriaQuery<E> cq, Root<E> root) {
		return findMany(cq, root, PageRequest.DEFAULT);
	}

	@Override
	public Optional<M> findSingle(final CriteriaQuery<E> cq, Root<E> root, final List<? extends Filter<?>> filters) {
		return findSingleEntity(cq, root, filters).map(getMapper()::toModel);
	}

	@Override
	public Optional<M> findSingle(final CriteriaQuery<E> cq, Root<E> root) {
		return findSingle(cq, root, List.of());
	}

	protected Optional<E> findSingleEntity(final CriteriaQuery<E> cq, Root<E> root, final List<? extends Filter<?>> filters) {
		applyFilters(cq, root, filters);
		return Optional.ofNullable(getEntityManager().createQuery(cq).getSingleResultOrNull());
	}

	@Override
	public boolean existsById(final long id) {
		final CriteriaQuery<Integer> cq = getCriteriaBuilder().createQuery(Integer.class);
		final Root<E> root = cq.from(getEntityClass());
		cq.where(root.get("id").in(id));
		return exists(cq);
	}

	@Override
	public boolean existsByUid(final String uid) {
		final CriteriaQuery<Integer> cq = getCriteriaBuilder().createQuery(Integer.class);
		final Root<E> root = cq.from(getEntityClass());
		cq.where(root.get("uid").in(uid));
		return exists(cq);
	}


	@Override
	public Optional<M> findById(final long id) {
		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<E> cq = cb.createQuery(getEntityClass());
		final Root<E> root = cq.from(getEntityClass());
		cq.where(cb.equal(root.get("id"), id));
		return findSingle(cq, root);
	}

	protected Optional<E> findEntityById(final long id) {
		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<E> cq = cb.createQuery(getEntityClass());
		final Root<E> root = cq.from(getEntityClass());
		cq.where(cb.equal(root.get("id"), id));
		return Optional.ofNullable(getEntityManager().createQuery(cq).getSingleResultOrNull());
	}

	@Override
	public Optional<M> findByUid(final String uid) {
		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<E> cq = cb.createQuery(getEntityClass());
		final Root<E> root = cq.from(getEntityClass());
		cq.where(cb.equal(root.get("uid"), uid));
		return findSingle(cq, root);
	}

	@Override
	public Collection<M> findMany(final PageRequest pageRequest, final List<? extends Filter<?>> filters) {
		final CriteriaQuery<E> cq = getCriteriaBuilder().createQuery(getEntityClass());
		return findMany(cq, cq.from(getEntityClass()), pageRequest, filters);
	}

	@Override
	public Optional<M> findSingle(final List<? extends Filter<?>> filters) {
		CriteriaQuery<E> cq = getCriteriaBuilder().createQuery(getEntityClass());
		return findSingle(cq, cq.from(getEntityClass()), filters);
	}

	@Override
	public Collection<M> findByIds(final Collection<Long> ids, final PageRequest pageRequest, final List<? extends Filter<?>> filters) {
		return findEntitiesByIds(ids, pageRequest, filters).stream().map(getMapper()::toModel).toList();
	}

	protected Collection<E> findEntitiesByIds(final Collection<Long> ids, final PageRequest pageRequest, final List<? extends Filter<?>> filters) {
		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<E> cq = cb.createQuery(getEntityClass());
		final Root<E> root = cq.from(getEntityClass());
		cq.where(cb.in(root.get("id")).value(ids));
		return findEntities(pageRequest, cq, root, filters);
	}

	protected Collection<E> findEntitiesByUids(final Collection<String> uids, final PageRequest pageRequest, final List<? extends Filter<?>> filters) {
		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<E> cq = cb.createQuery(getEntityClass());
		final Root<E> root = cq.from(getEntityClass());
		cq.where(cb.in(root.get("uid")).value(uids));
		return findEntities(pageRequest, cq, root, filters);
	}

	@Override
	public Collection<M> findByUids(final Collection<String> uids, final PageRequest pageRequest, final List<? extends Filter<?>> filters) {
		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<E> cq = cb.createQuery(getEntityClass());
		final Root<E> root = cq.from(getEntityClass());
		cq.where(cb.in(root.get("uid")).value(uids));
		return findMany(cq, root, pageRequest, filters);
	}

	@Override
	public long count(final List<? extends Filter<?>> filters) {
		CriteriaQuery<Long> cq = getCriteriaBuilder().createQuery(Long.class);
		return count(cq, cq.from(getEntityClass()), filters);
	}

	@Override
	public long count(final CriteriaQuery<Long> cq, Root<E> root) {
		cq.select(getCriteriaBuilder().count(root));
		return getEntityManager().createQuery(cq).getSingleResult();
	}

	@Override
	public long count(final CriteriaQuery<Long> cq, Root<E> root, final List<? extends Filter<?>> filters) {
		applyFilters(cq, root, filters);
		return count(cq, root);
	}

	private void applyFilters(final CriteriaQuery<?> cq, Root<E> root, final List<? extends Filter<?>> filters) {
		final CriteriaBuilder cb = getCriteriaBuilder();
		final List<Predicate> predicates = filters.stream().map(filter -> JpaFilterResolverRegistry.resolve(filter, cb, root)).filter(Objects::nonNull).toList();
		if (isNotEmpty(predicates)) {
			cq.where(predicates.toArray(Predicate[]::new));
		}
	}

	private void applyOrder(final CriteriaQuery<E> cq, Root<E> root, final PageRequest pageRequest) {
		if (isNull(pageRequest)) {
			return;
		}
		final CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		final List<jakarta.persistence.criteria.Order> orders = pageRequest.orders().stream()
				.map(order -> switch (order.direction()) {
					case ASC -> cb.asc(root.get(order.field()));
					case DESC -> cb.desc(root.get(order.field()));
				}).toList();

		if (isNotEmpty(orders)) {
			cq.orderBy(orders);
		}
	}

}
