package com.gh.archlayer.accessor.filter.impl;

import com.gh.archlayer.accessor.filter.JpaFilterResolver;
import com.gh.archlayer.service.filter.impl.ComparableFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public abstract class ComparableFilterResolver<T extends Comparable<? super T>, F extends ComparableFilter<T>> implements JpaFilterResolver<F> {
	@Override
	public Predicate toPredicate(final F filter, final CriteriaBuilder cb, final Root<?> root) {
		final Path<T> path = (Path<T>) resolvePath(root, filter.getField());
		return switch (filter.getOperator()) {
			case EQUALS -> cb.equal(path, filter.getValue());
			case NOT_EQUALS -> cb.notEqual(path, filter.getValue());
			case GREATER_THAN -> cb.greaterThan(path, filter.getValue());
			case GREATER_THAN_OR_EQUAL -> cb.greaterThanOrEqualTo(path, filter.getValue());
			case LESS_THAN -> cb.lessThan(path, filter.getValue());
			case LESS_THAN_OR_EQUAL -> cb.lessThanOrEqualTo(path, filter.getValue());
			default -> throw new UnsupportedOperationException("Unsupported operator: " + filter.getOperator());
		};
	}
}
