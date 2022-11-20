package com.anf.core.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anf.core.services.SearchService;
import com.anf.core.util.ResolverUtil;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;

@Component(service = SearchService.class, immediate = true)
public class SearchServiceImpl implements SearchService {

	private static final Logger LOG = LoggerFactory.getLogger(SearchServiceImpl.class);
	
	@Reference
	QueryBuilder queryBuilder;
	
	@Reference
	ResourceResolverFactory resourceResolverFactory;
	
	@Activate
	public void activate() {
		LOG.info("This is going to activate the service");
	}
	
	public Map<String, String> createQueryUsingQueryBuilder(){
		Map<String, String> queryMap = new HashMap<String, String>();
		queryMap.put("type", "cq:Page");
		queryMap.put("path", "/content/anf-code-challenge/us/en");
		queryMap.put("1_property", "jcr:content/anfCodeChallenge");
		queryMap.put("1_property.value", "true");
		queryMap.put("p.limit", "10");
		return queryMap;
	}
	
	@Override
	public JSONObject getPages(String searchType) {
		JSONObject jsonResult = null;
		if (StringUtils.isNotEmpty(searchType)) {
			if (StringUtils.equalsIgnoreCase(searchType, "queryBuilder")) {
				jsonResult = getPagesUsingQueryBuilder();
			}else if (StringUtils.equalsIgnoreCase(searchType, "sql2")) {
				jsonResult = getPagesUsingSQL2();
			}
		}
		
		return jsonResult;
	}
	
	private JSONObject getPagesUsingQueryBuilder() {

		JSONObject jsonResult = new JSONObject();
		
		try {
			ResourceResolver resolver = ResolverUtil.getResourceResolver(resourceResolverFactory);
			final Session session = resolver.adaptTo(Session.class);
			Query query = queryBuilder.createQuery(PredicateGroup.create(createQueryUsingQueryBuilder()), session);
			SearchResult result = query.getResult();
			List<Hit> hits = result.getHits();
			JSONArray jsonArray = new JSONArray();
			for (Iterator iterator = hits.iterator(); iterator.hasNext();) {
				Hit hit = (Hit) iterator.next();
				Page page = hit.getResource().adaptTo(Page.class);
				JSONObject resultItem = new JSONObject();
				resultItem.put("title", page.getTitle());
				resultItem.put("path", page.getPath());
				jsonArray.put(resultItem);
			}
			jsonResult.put("results", jsonArray);
			
		} catch (LoginException e) {
			LOG.error(this.toString(), e);
		} catch (RepositoryException e) {
			LOG.error(this.toString(), e);
		} catch (JSONException e) {
			LOG.error(this.toString(), e);
		}
		
		return jsonResult;
	
		
	}
	
	private JSONObject getPagesUsingSQL2() {
		JSONObject searchResult = new JSONObject();
		 try {
			final String queryString = "SELECT parent.* FROM [cq:Page] AS parent \n"
			            + "INNER JOIN [nt:base] AS child ON ISCHILDNODE(child,parent) \n"
			            + "WHERE ISDESCENDANTNODE(parent, '/content/anf-code-challenge/us/en') AND child.[anfCodeChallenge] = 'true'";

			 ResourceResolver resolver = ResolverUtil.getResourceResolver(resourceResolverFactory);
			 final Session session = resolver.adaptTo(Session.class);
			 final javax.jcr.query.Query query = session.getWorkspace().getQueryManager().createQuery(queryString, javax.jcr.query.Query.JCR_SQL2);
			 final QueryResult result = query.execute();
			 NodeIterator pages = result.getNodes();
			 JSONArray resultArray = new JSONArray();
			 while (pages.hasNext()) {
				Node page = (Node) pages.nextNode();
				JSONObject resultObject = new JSONObject();
				resultObject.put("path", page.getPath());
				resultArray.put(resultObject);
				}
			 searchResult.put("Pages", resultArray);
		} catch (LoginException | RepositoryException | JSONException e) {
			LOG.error(this.toString(), e);
		}
	     
			return searchResult;
		}
	       

}
