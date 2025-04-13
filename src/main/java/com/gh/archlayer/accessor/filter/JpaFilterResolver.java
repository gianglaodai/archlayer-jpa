package com.gh.archlayer.accessor.filter;

import com.gh.archlayer.service.filter.Filter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public interface JpaFilterResolver<F extends Filter<?>> {
	Predicate toPredicate(F filter, CriteriaBuilder cb, Root<?> root);

	Class<F> getFilterType();

	default <T> Path<?> resolvePath(final Root<T> root, final String field) {
		final String[] parts = field.split("\\.");
		Path<?> path = root;
		for (final String part : parts) {
			path = path.get(part);
		}
		return path;
	}
}
