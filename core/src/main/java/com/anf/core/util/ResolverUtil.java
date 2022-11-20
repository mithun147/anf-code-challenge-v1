package com.anf.core.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

public class ResolverUtil {
	
	private ResolverUtil() {
		
	}
	
	public static final String ANF_SERVICE_USER = "anfserviceuser";
	
	public static ResourceResolver getResourceResolver(ResourceResolverFactory resourceResolverFactory ) throws LoginException {
		final Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(ResourceResolverFactory.SUBSERVICE, ANF_SERVICE_USER);
		ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(paramMap);
		return resourceResolver;
	}

}
