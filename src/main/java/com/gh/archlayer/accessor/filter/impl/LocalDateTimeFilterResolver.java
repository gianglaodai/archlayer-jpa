package com.gh.archlayer.accessor.filter.impl;

import com.gh.archlayer.service.filter.impl.LocalDateTimeFilter;

import java.time.LocalDateTime;

public class LocalDateTimeFilterResolver extends ComparableFilterResolver<LocalDateTime, LocalDateTimeFilter> {
	@Override
	public Class<LocalDateTimeFilter> getFilterType() {
		return LocalDateTimeFilter.class;
	}
}
