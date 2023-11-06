/**
 * Copyright (c) 2005 The Apereo Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://opensource.org/licenses/ecl2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sakaiproject.webservices.hec;

import java.util.Base64;
import java.util.List;
import java.util.Base64.Encoder;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import lombok.extern.slf4j.Slf4j;

import org.sakaiproject.webservices.AbstractWebService;
import org.sakaiproject.user.api.UserNotDefinedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * AzureSelfSignupValidator.jws
 * <p/>
 * A webservice to return "continue" or "abort" to use during the Azure Self Signup workflow
 */

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
@Slf4j
public class SelfSignupValidator extends AbstractWebService {
    private String continueJson = "{ \"version\":\"1.0\", \"action\": \"Continue\", \"jobTitle\": \"ZoneCours\" }";
    private String rejectJson = "{ \"version\":\"1.0\", \"action\": \"ShowBlockPage\" }";

    private String authString = null;

    public void init() {
        String expectedUsername = serverConfigurationService.getString("selfsignup.azure.username");
        String expectedPassword = serverConfigurationService.getString("selfsignup.azure.password");

        String userDefinedContinueJson = serverConfigurationService.getString("selfsignup.azure.continuejson", null);
        String userDefinedRejectJson = serverConfigurationService.getString("selfsignup.azure.rejectjson", null);

        if (userDefinedContinueJson != null) {
            continueJson = userDefinedContinueJson;
        }
        if (userDefinedRejectJson != null) {
            rejectJson = userDefinedRejectJson;
        }

        Encoder enc = Base64.getEncoder();
        if (expectedUsername != null && expectedUsername != "" && expectedPassword != null && expectedPassword != "") {
            authString = "Basic " + enc.encodeToString((expectedUsername+":"+expectedPassword).getBytes());
        } 
        else {
            log.error("username and password must be set for Azure selfsignup webservice to work");
        }
    }

    /**
     * Verify email address has been enrolled in a site (thus allowing them to create an Azure account)
     * @param email	email address to validate
     * @param name		job name
     * @return
     * @
     */
    @WebMethod
    @Path("/azure-validator")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public String validate(String body, @HeaderParam("Authorization") String auth) {
        String emailAddress = null;

        log.debug(body);

        if (authString == null || auth == null || !auth.equals(authString)) {
            throw new WebApplicationException(Status.UNAUTHORIZED);
        }

        try {
            // I think jaxrs should be able to parse the json but can't get it to work
            ObjectMapper om = new ObjectMapper();
            JsonNode nameNode = om.readTree(body);
            emailAddress = nameNode.get("email").asText();

            if (emailAddress == null) {
                return rejectJson;
            }
    
            userDirectoryService.getUserByEmail(emailAddress);
        }
        catch (UserNotDefinedException e) {
            return rejectJson;
        }
        catch (Exception e) {
            e.printStackTrace();
            return rejectJson;
        }

        return continueJson;
    }
}
