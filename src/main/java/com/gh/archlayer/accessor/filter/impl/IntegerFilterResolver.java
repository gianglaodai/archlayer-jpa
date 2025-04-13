package com.gh.archlayer.accessor.filter.impl;

import com.gh.archlayer.service.filter.impl.IntegerFilter;

import java.math.BigInteger;

public class IntegerFilterResolver extends NumberFilterResolver<BigInteger, IntegerFilter> {
	@Override
	public Class<IntegerFilter> getFilterType() {
		return IntegerFilter.class;
	}
}
