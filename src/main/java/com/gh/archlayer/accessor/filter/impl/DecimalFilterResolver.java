package com.gh.archlayer.accessor.filter.impl;

import com.gh.archlayer.service.filter.impl.DecimalFilter;

import java.math.BigDecimal;

public class DecimalFilterResolver extends NumberFilterResolver<BigDecimal, DecimalFilter> {
	@Override
	public Class<DecimalFilter> getFilterType() {
		return DecimalFilter.class;
	}
}
