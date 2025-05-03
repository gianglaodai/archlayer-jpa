package com.gh.archlayer.accessor.api.jpa;

import com.gh.archlayer.accessor.api.EntityMapper;
import com.gh.archlayer.accessor.model.PersistenceEntity;
import com.gh.archlayer.service.filter.Filter;
import com.gh.archlayer.service.model.DataModel;
import com.gh.archlayer.service.paging.PageRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.gh.archlayer.utils.FunctionUtils.peek;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

public abstract class AbstractJpaDataAccessor<M extends DataModel, E extends PersistenceEntity>
		extends AbstractJpaQueryAccessor<M, E>
		implements JpaDataAccessor<M, E> {
	private final int batchSize;

	public AbstractJpaDataAccessor(final int batchSize) {
		this.batchSize = batchSize;
	}

	public AbstractJpaDataAccessor() {
		this(100);
	}

	@Override
	public M save(final M model) {
		return save(model, PersistenceControl.NONE);
	}

	@Override
	public M save(final M model, final PersistenceControl persistenceControl) {
		final E entity = getEntityManager().merge(getMapper().toEntity(model));
		applyPersistenceControl(persistenceControl);
		return getMapper().toModel(entity);
	}

	@Override
	public Collection<M> save(final Collection<M> models) {
		return save(models, PersistenceControl.FLUSH);
	}

	@Override
	public Collection<M> save(final Collection<M> models, final PersistenceControl persistenceControl) {
		if (isEmpty(models)) {
			return List.of();
		}
		final EntityManager em = getEntityManager();
		final EntityMapper<M, E> mapper = getMapper();
		int index = 0;
		final List<M> result = new ArrayList<>();
		for (final M model : models) {
			final E entity = em.merge(mapper.toEntity(model));
			result.add(mapper.toModel(entity));
			if (++index % batchSize == 0) {
				em.flush();
				em.clear();
			}
		}
		applyPersistenceControl(persistenceControl);
		return result;
	}

	@Override
	public void deleteById(final long id) {
		deleteById(id, PersistenceControl.NONE);
	}

	@Override
	public void deleteById(final long id, final PersistenceControl persistenceControl) {
		findEntityById(id).map(peek(entity -> getEntityManager().remove(entity)))
				.ifPresent(_ -> applyPersistenceControl(persistenceControl));
	}

	@Override
	public void deleteByIds(final Collection<Long> ids) {
		deleteByIds(ids, PersistenceControl.FLUSH);
	}

	@Override
	public void deleteByIds(final Collection<Long> ids, final PersistenceControl persistenceControl) {
		deleteEntities(findEntitiesByIds(ids, PageRequest.DEFAULT, List.of()), persistenceControl);
	}

	@Override
	public void deleteByUids(final Collection<String> uids, final PersistenceControl persistenceControl) {
		deleteEntities(findEntitiesByUids(uids, PageRequest.DEFAULT, List.of()), persistenceControl);
	}

	@Override
	public void deleteByUids(final Collection<String> uids) {
		deleteByUids(uids, PersistenceControl.FLUSH);
	}

	@Override
	public void delete(final CriteriaQuery<E> cq, Root<E> root, final List<? extends Filter<?>> filters) {
		deleteEntities(findEntities(cq, root, filters), PersistenceControl.FLUSH);
	}

	@Override
	public void delete(final CriteriaQuery<E> cq, Root<E> root) {
		deleteEntities(findEntities(cq, root), PersistenceControl.FLUSH);
	}

	private void deleteEntities(final Collection<E> entities, final PersistenceControl persistenceControl) {
		if (isEmpty(entities)) {
			return;
		}
		final EntityManager em = getEntityManager();
		int index = 0;
		for (final E entity : entities) {
			if(nonNull(entity)){
				em.remove(entity);
			}
			if (++index % batchSize == 0) {
				em.flush();
				em.clear();
			}
		}
		applyPersistenceControl(persistenceControl);
	}

	private void applyPersistenceControl(final PersistenceControl persistenceControl) {
		switch (persistenceControl) {
			case FLUSH -> getEntityManager().flush();
			case FLUSH_AND_CLEAR -> {
				getEntityManager().flush();
				getEntityManager().clear();
			}
			case NONE -> {
			}
		}
	}
}
