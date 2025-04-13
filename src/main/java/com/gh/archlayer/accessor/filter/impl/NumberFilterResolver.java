package com.gh.archlayer.accessor.filter.impl;

import com.gh.archlayer.accessor.filter.JpaFilterResolver;
import com.gh.archlayer.service.filter.impl.NumberFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public abstract class NumberFilterResolver<N extends Number & Comparable<? super N>, F extends NumberFilter<N>> implements JpaFilterResolver<F> {
	@Override
	public Predicate toPredicate(final F filter, final CriteriaBuilder cb, final Root<?> root) {
		final Path<N> path = (Path<N>) resolvePath(root, filter.getField());
		return switch (filter.getOperator()) {
			case EQUALS -> cb.equal(path, filter.getValue());
			case NOT_EQUALS -> cb.notEqual(path, filter.getValue());
			case GREATER_THAN -> cb.gt(path, filter.getValue());
			case GREATER_THAN_OR_EQUAL -> cb.ge(path, filter.getValue());
			case LESS_THAN -> cb.lt(path, filter.getValue());
			case LESS_THAN_OR_EQUAL -> cb.le(path, filter.getValue());
			default -> throw new UnsupportedOperationException("Unsupported operator: " + filter.getOperator());
		};
	}
}
