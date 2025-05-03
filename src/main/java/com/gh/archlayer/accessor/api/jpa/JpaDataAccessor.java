package com.gh.archlayer.accessor.api.jpa;

import com.gh.archlayer.accessor.api.DataAccessor;
import com.gh.archlayer.accessor.model.PersistenceEntity;
import com.gh.archlayer.service.filter.Filter;
import com.gh.archlayer.service.model.DataModel;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.Collection;
import java.util.List;

public interface JpaDataAccessor<M extends DataModel, E extends PersistenceEntity> extends DataAccessor<M>, JpaQueryAccessor<M, E> {
	enum PersistenceControl {
		NONE,
		FLUSH,
		FLUSH_AND_CLEAR
	}

	M save(M model, PersistenceControl persistenceControl);

	Collection<M> save(Collection<M> models, PersistenceControl persistenceControl);

	void deleteById(long id, PersistenceControl persistenceControl);

	void deleteByIds(Collection<Long> ids, PersistenceControl persistenceControl);

	void deleteByUids(Collection<String> uids, PersistenceControl persistenceControl);

	void delete(CriteriaQuery<E> cq, Root<E> root, List<? extends Filter<?>> filters);

	void delete(CriteriaQuery<E> cq, Root<E> root);

}
