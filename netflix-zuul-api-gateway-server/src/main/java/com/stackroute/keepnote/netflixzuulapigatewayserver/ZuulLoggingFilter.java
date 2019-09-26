package com.stackroute.keepnote.netflixzuulapigatewayserver;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;

/*
 * Implement zuul logging filter by extending zuul filter
 */
@Component
public class ZuulLoggingFilter extends ZuulFilter{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public String filterType() {
		return "route";
	}

	@Override
	public int filterOrder() {
		return 0;
	}

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() {
		System.out.println("Using Route Filter");

		return null;
	}

}
