package com.anf.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.anf.core.services.SearchService;

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Servlet to get first 10 pages",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET,

		"sling.servlet.paths=" + "/bin/getFirst10Pages" }

)
public class SearchServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;

	@Reference
	SearchService searchService;
	Log log = LogFactory.getLog(SearchServlet.class);

	JSONObject jsonObject;

	@Override
	protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
			throws ServletException, IOException {
		String searchType = req.getParameter("searchType");
		if (StringUtils.isNotEmpty(searchType)) {
			jsonObject = searchService.getPages(searchType);
			resp.setContentType("application/json");
			PrintWriter out = resp.getWriter();
			out.write(jsonObject.toString());
			out.flush();
		}
	}

}
