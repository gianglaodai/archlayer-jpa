package com.gh.archlayer.accessor.filter;

import com.gh.archlayer.accessor.filter.impl.BooleanFilterResolver;
import com.gh.archlayer.accessor.filter.impl.DecimalFilterResolver;
import com.gh.archlayer.accessor.filter.impl.IntegerFilterResolver;
import com.gh.archlayer.accessor.filter.impl.LocalDateFilterResolver;
import com.gh.archlayer.accessor.filter.impl.LocalDateTimeFilterResolver;
import com.gh.archlayer.accessor.filter.impl.StringFilterResolver;
import com.gh.archlayer.service.filter.Filter;
import com.gh.archlayer.service.filter.impl.BooleanFilter;
import com.gh.archlayer.service.filter.impl.DecimalFilter;
import com.gh.archlayer.service.filter.impl.IntegerFilter;
import com.gh.archlayer.service.filter.impl.LocalDateFilter;
import com.gh.archlayer.service.filter.impl.LocalDateTimeFilter;
import com.gh.archlayer.service.filter.impl.StringFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.Map;

public class JpaFilterResolverRegistry {
	private static final Map<Class<?>, JpaFilterResolver<? extends Filter<?>>> resolvers = Map.of(
			BooleanFilter.class, new BooleanFilterResolver(),
			IntegerFilter.class, new IntegerFilterResolver(),
			DecimalFilter.class, new DecimalFilterResolver(),
			LocalDateFilter.class, new LocalDateFilterResolver(),
			LocalDateTimeFilter.class, new LocalDateTimeFilterResolver(),
			StringFilter.class, new StringFilterResolver());

	public static Predicate resolve(final Filter<?> filter, final CriteriaBuilder cb, final Root<?> root) {
		return resolveWithTypedResolver(filter, cb, root, resolvers.get(filter.getClass()));
	}

	@SuppressWarnings("unchecked")
	private static <T extends Filter<?>> Predicate resolveWithTypedResolver(final Filter<?> filter, final CriteriaBuilder cb, final Root<?> root, final JpaFilterResolver<T> resolver) {
		return resolver.toPredicate((T) filter, cb, root);
	}

}
