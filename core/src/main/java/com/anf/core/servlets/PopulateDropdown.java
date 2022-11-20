package com.anf.core.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component(service = Servlet.class, immediate = true,
        property = {Constants.SERVICE_DESCRIPTION + "=Populating Dropdown using JSON Data",
                "sling.servlet.resourceTypes=anf-code-challenge/json",
                "sling.servlet.methods=" + HttpConstants.METHOD_GET})
public class PopulateDropdown extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doGet(final SlingHttpServletRequest request,
            final SlingHttpServletResponse response) throws ServletException, IOException {

        try {
            final ResourceResolver resolver = request.getResourceResolver();
            final Resource resource = request.getResource();
            if (resource != null) {
                final Resource dataSource = resource.getChild("datasource");
                final ValueMap dataSourceValueMap = dataSource.getValueMap();
                final String jsonPath = dataSourceValueMap.get("path", String.class);
                String jsonString = null;
                if ((jsonPath != null) && (jsonPath.trim().length() > 0)) {
                    final Resource jsonResource = resolver.getResource(jsonPath + "/jcr:content");
                    jsonString = this.getJsonFromFile(jsonResource);
                }

                final List<Resource> optionResourceList = new ArrayList<>();
                final ObjectMapper mapper = new ObjectMapper();
                final Map<String, String> map = mapper.readValue(jsonString, Map.class);
                final Set<String> keySet = map.keySet();
                final Iterator<String> oi = keySet.iterator();

                while (oi.hasNext()) {
                    final String key = oi.next();
                    final ValueMap vm = new ValueMapDecorator(new HashMap<>());
                    vm.put("value", key);
                    vm.put("text", map.get(key));
                    optionResourceList.add(new ValueMapResource(resolver, new ResourceMetadata(),
                            "nt:unstructured", vm));
                }

                final DataSource ds = new SimpleDataSource(optionResourceList.iterator());
                request.setAttribute(DataSource.class.getName(), ds);
            }
        } catch (final ValueFormatException e) {
            this.logger.error(this.getServletName(), e);
        } catch (final RepositoryException e) {
            this.logger.error(this.getServletName(), e);
        } catch (final IOException e) {
            this.logger.error(this.getServletName(), e);
        }

    }


    private String getJsonFromFile(final Resource jsonResource)
            throws RepositoryException, IOException {
        String json = null;
        if (!ResourceUtil.isNonExistingResource(jsonResource)) {
            final Node cfNode = jsonResource.adaptTo(Node.class);
            final InputStream in = cfNode.getProperty("jcr:data").getBinary().getStream();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            final StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            json = sb.toString();
            reader.close();

        }
        return json;
    }



}
