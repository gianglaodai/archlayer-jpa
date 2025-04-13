package com.gh.archlayer.accessor.filter.impl;

import com.gh.archlayer.service.filter.impl.LocalDateFilter;

import java.time.LocalDate;

public class LocalDateFilterResolver extends ComparableFilterResolver<LocalDate, LocalDateFilter> {
	@Override
	public Class<LocalDateFilter> getFilterType() {
		return  LocalDateFilter.class;
	}
}
