package com.gh.archlayer.accessor.filter.impl;

import com.gh.archlayer.accessor.filter.JpaFilterResolver;
import com.gh.archlayer.service.filter.impl.StringFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class StringFilterResolver implements JpaFilterResolver<StringFilter> {
	@Override
	public Predicate toPredicate(final StringFilter filter, final CriteriaBuilder cb, final Root<?> root) {
		final Path<String> path = (Path<String>) resolvePath(root, filter.getField());
		return switch (filter.getOperator()) {
			case EQUALS -> cb.equal(path, filter.getValue());
			case NOT_EQUALS -> cb.notEqual(path, filter.getValue());
			case LIKE -> cb.like(cb.lower(path), "%" + filter.getValue().toLowerCase() + "%");
			case NOT_LIKE -> cb.notLike(cb.lower(path), "%" + filter.getValue().toLowerCase() + "%");
			default -> throw new UnsupportedOperationException("Unsupported operator: " + filter.getOperator());
		};
	}

	@Override
	public Class<StringFilter> getFilterType() {
		return StringFilter.class;
	}
}
