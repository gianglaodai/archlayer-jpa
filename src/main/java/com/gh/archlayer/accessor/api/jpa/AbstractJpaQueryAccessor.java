package com.gh.archlayer.accessor.api.jpa;

import com.gh.archlayer.accessor.api.impl.AbstractQueryAccessor;
import com.gh.archlayer.accessor.filter.JpaFilterResolverRegistry;
import com.gh.archlayer.accessor.model.PersistenceEntity;
import com.gh.archlayer.service.filter.Filter;
import com.gh.archlayer.service.model.QueryModel;
import com.gh.archlayer.service.paging.Order;
import com.gh.archlayer.service.paging.PageRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

public abstract class AbstractJpaQueryAccessor<M extends QueryModel, E extends PersistenceEntity>
		extends AbstractQueryAccessor<M, E>
		implements JpaQueryAccessor<M, E> {

	public abstract EntityManager getEntityManager();

	private CriteriaBuilder getCriteriaBuilder() {
		return getEntityManager().getCriteriaBuilder();
	}

	@Override
	public Collection<M> findMany(final PageRequest pageRequest, final CriteriaQuery<E> cq, final List<Filter<?>> filters) {
		return findStream(pageRequest, cq, filters).map(getMapper()::toModel).toList();
	}

	protected Stream<E> findStream(final PageRequest pageRequest, final CriteriaQuery<E> cq, final List<Filter<?>> filters) {
		applyFilters(cq, filters);
		applyOrder(cq, pageRequest);
		final TypedQuery<E> query = getEntityManager().createQuery(cq);
		if (pageRequest.firstResult() >= 0) {
			query.setFirstResult(pageRequest.firstResult());
		}
		if (pageRequest.maxResults() > 0) {
			query.setMaxResults(pageRequest.maxResults());
		}
		return query.getResultStream();
	}

	protected Collection<E> findEntities(final PageRequest pageRequest, final CriteriaQuery<E> cq, final List<Filter<?>> filters) {
		return findStream(pageRequest, cq, filters).toList();
	}

	protected Collection<E> findEntities(final CriteriaQuery<E> cq, final List<Filter<?>> filters) {
		return findEntities(PageRequest.DEFAULT, cq, filters);
	}

	protected Collection<E> findEntities(final CriteriaQuery<E> cq) {
		return findEntities(PageRequest.DEFAULT, cq, List.of());
	}

	protected Collection<E> findEntities(final List<Filter<?>> filters) {
		return findEntities(PageRequest.DEFAULT, getCriteriaBuilder().createQuery(getEntityClass()), filters);
	}

	@Override
	public boolean exists(final CriteriaQuery<Integer> cq, final List<Filter<?>> filters) {
		applyFilters(cq, filters);
		return exists(cq);
	}

	@Override
	public boolean exists(final CriteriaQuery<Integer> cq) {
		cq.select(getCriteriaBuilder().literal(1));
		return isNotEmpty(getEntityManager()
				.createQuery(cq)
				.setMaxResults(1)
				.getResultList());
	}

	@Override
	public Collection<M> findMany(final PageRequest pageRequest, final CriteriaQuery<E> cq) {
		return findMany(pageRequest, cq, List.of());
	}

	@Override
	public Collection<M> findMany(final CriteriaQuery<E> cq) {
		return findMany(PageRequest.DEFAULT, cq);
	}

	@Override
	public Optional<M> findSingle(final CriteriaQuery<E> cq, final List<Filter<?>> filters) {
		return findSingleEntity(cq, filters).map(getMapper()::toModel);
	}

	@Override
	public Optional<M> findSingle(final CriteriaQuery<E> cq) {
		return findSingle(cq, List.of());
	}

	protected Optional<E> findSingleEntity(final CriteriaQuery<E> cq, final List<Filter<?>> filters) {
		applyFilters(cq, filters);
		return Optional.ofNullable(getEntityManager()
				.createQuery(cq)
				.getSingleResultOrNull());
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
	public boolean exists(final List<Filter<?>> filters) {
		final CriteriaQuery<Integer> cq = getCriteriaBuilder().createQuery(Integer.class);
		applyFilters(cq, filters);
		return exists(cq);
	}

	@Override
	public Optional<M> findById(final long id) {
		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<E> cq = cb.createQuery(getEntityClass());
		final Root<E> root = cq.from(getEntityClass());
		cq.where(cb.equal(root.get("id"), id));
		return findSingle(cq);
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
		return findSingle(cq);
	}

	@Override
	public Collection<M> findMany(final PageRequest pageRequest, final List<Filter<?>> filters) {
		final CriteriaQuery<E> cq = getCriteriaBuilder().createQuery(getEntityClass());
		return findMany(pageRequest, cq, filters);
	}

	@Override
	public Optional<M> findSingle(final List<Filter<?>> filters) {
		return findSingle(getCriteriaBuilder().createQuery(getEntityClass()), filters);
	}

	@Override
	public Collection<M> findByIds(final Collection<Long> ids, final PageRequest pageRequest, final List<Filter<?>> filters) {
		return findEntitiesByIds(ids, pageRequest, filters).stream().map(getMapper()::toModel).toList();
	}

	protected Collection<E> findEntitiesByIds(final Collection<Long> ids, final PageRequest pageRequest, final List<Filter<?>> filters) {
		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<E> cq = cb.createQuery(getEntityClass());
		final Root<E> root = cq.from(getEntityClass());
		cq.where(cb.in(root.get("id")).value(ids));
		return findEntities(pageRequest, cq, filters);
	}

	protected Collection<E> findEntitiesByUids(final Collection<String> uids, final PageRequest pageRequest, final List<Filter<?>> filters) {
		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<E> cq = cb.createQuery(getEntityClass());
		final Root<E> root = cq.from(getEntityClass());
		cq.where(cb.in(root.get("uid")).value(uids));
		return findEntities(pageRequest, cq, filters);
	}

	@Override
	public Collection<M> findByUids(final Collection<String> uids, final PageRequest pageRequest, final List<Filter<?>> filters) {
		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<E> cq = cb.createQuery(getEntityClass());
		final Root<E> root = cq.from(getEntityClass());
		cq.where(cb.in(root.get("uid")).value(uids));
		return findMany(pageRequest, cq, filters);
	}

	@Override
	public long count(final List<Filter<?>> filters) {
		return count(getCriteriaBuilder().createQuery(Long.class), filters);
	}

	@Override
	public long count(final CriteriaQuery<Long> cq) {
		final Root<E> root = cq.from(getEntityClass());
		cq.select(getCriteriaBuilder().count(root));
		return getEntityManager().createQuery(cq).getSingleResult();
	}

	@Override
	public long count(final CriteriaQuery<Long> cq, final List<Filter<?>> filters) {
		applyFilters(cq, filters);
		return count(cq);
	}

	private void applyFilters(final CriteriaQuery<?> cq, final List<Filter<?>> filters) {
		final CriteriaBuilder cb = getCriteriaBuilder();
		final Root<E> root = cq.from(getEntityClass());
		final List<Predicate> predicates = filters.stream()
				.map(filter -> JpaFilterResolverRegistry.resolve(filter, cb, root))
				.filter(Objects::nonNull).toList();
		if (isNotEmpty(predicates)) {
			cq.where(predicates.toArray(Predicate[]::new));
		}
	}

	private void applyOrder(final CriteriaQuery<E> cq, final PageRequest pageRequest) {
		if (isNull(pageRequest)) {
			return;
		}
		final CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		final Root<E> root = cq.from(getEntityClass());
		final List<jakarta.persistence.criteria.Order> orders = pageRequest.orders()
				.stream()
				.map(order -> {
					final Path<Object> path = root.get(order.field());
					return order.direction() == Order.Direction.ASC ? cb.asc(path) : cb.desc(path);
				})
				.toList();

		if (isNotEmpty(orders)) {
			cq.orderBy(orders);
		}
	}

}
