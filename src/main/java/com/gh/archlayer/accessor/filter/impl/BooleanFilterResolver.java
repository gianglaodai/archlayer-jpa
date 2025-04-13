package com.gh.archlayer.accessor.filter.impl;

import com.gh.archlayer.accessor.filter.JpaFilterResolver;
import com.gh.archlayer.service.filter.impl.BooleanFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class BooleanFilterResolver implements JpaFilterResolver<BooleanFilter> {
	@Override
	public Predicate toPredicate(final BooleanFilter filter, final CriteriaBuilder cb, final Root<?> root) {
		final Path<Boolean> path = (Path<Boolean>) resolvePath(root, filter.getField());
		return switch (filter.getOperator()) {
			case EQUALS -> cb.equal(path, filter.getValue());
			case NOT_EQUALS -> cb.notEqual(path, filter.getValue());
			default -> throw new UnsupportedOperationException("Unsupported operator: " + filter.getOperator());
		};
	}

	@Override
	public Class<BooleanFilter> getFilterType() {
		return BooleanFilter.class;
	}
}
