/*
 * Copyright 2015 Adobe Systems Incorporated
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.anf.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import com.anf.core.beans.UserDetails;
import com.anf.core.services.ContentService;

/**
 * @author Mithun Halder
 *
 */
@Component(service = Servlet.class,
        property = {Constants.SERVICE_DESCRIPTION + "=Servlet to store user data",
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,

                "sling.servlet.paths=" + "/bin/saveUserDetails"}

)
public class UserServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Reference
    private ContentService contentService;

    Log log = LogFactory.getLog(UserServlet.class);



    @Override
    protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
            throws ServletException, IOException {
        try {
            final UserDetails userDetails = this.getUserDetails(req);
            final int age = userDetails.getAge();
            final boolean validAge = this.contentService.validateAge(age);
            if (validAge) {
                this.contentService.commitUserDetails(userDetails);
                this.log.info("getUserDetails(req) : " + this.getUserDetails(req));
            } else {
                final PrintWriter out = resp.getWriter();
                resp.setStatus(500);
                out.print("You are not elligible");
                out.flush();
            }
        } catch (RepositoryException | LoginException e) {
            this.log.error(e);
        }
    }

    private UserDetails getUserDetails(final SlingHttpServletRequest request) {
        final UserDetails userDetails = new UserDetails();
        final String ageStr = request.getParameter("age");
        if (StringUtils.isNotEmpty(ageStr) || StringUtils.isNumeric(ageStr)) {
            userDetails.setAge(Integer.valueOf(ageStr));
        } else {
            userDetails.setAge(0);
        }

        userDetails.setFirstName(request.getParameter("firstName"));
        userDetails.setLastName(request.getParameter("lastName"));
        userDetails.setCountry(request.getParameter("country"));
        return userDetails;
    }
}
