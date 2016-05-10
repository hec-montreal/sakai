/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/trunk/sdata/sdata-tool/impl/src/java/org/sakaiproject/sdata/tool/JCRDumper.java $
 * $Id: JCRDumper.java 45207 2008-02-01 19:01:06Z ian@caret.cam.ac.uk $
 ***********************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.sdata.services.ci;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.cover.SecurityService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.citation.api.Citation;
import org.sakaiproject.citation.api.CitationCollection;
import org.sakaiproject.citation.api.CitationService;
import org.sakaiproject.citation.api.Schema;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentEntity;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.content.api.ResourceType;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.event.cover.NotificationService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.sdata.tool.CHSNodeMap;
import org.sakaiproject.sdata.tool.api.ServiceDefinition;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;

/**
 * For citation CRUD operation
 */
public class CiBean implements ServiceDefinition {
	private static final int CREATE = 0;
	private static final int DELETE = 1;
	private static final int UPDATE = 2;

	private static final Log log = LogFactory.getLog(CiBean.class);

	private Session currentSession;

	private Map<String, Object> map = new HashMap<String, Object>();
	private Map<String, Object> itemMap = new HashMap<String, Object>();
	private ContentHostingService contentHostingService;
	private CitationService citationService;
	private String entityPath;
	private HttpServletRequest request;
	private HttpServletResponse response;

	/**
	 * Citation bean
	 * 
	 * @param sessionManager
	 * @param siteService
	 * @param contentHostingService
	 * @param entityPath
	 * @param request
	 */
	public CiBean(SiteService siteService, SessionManager sessionManager,
			CitationService citationService,
			ContentHostingService contentHostingService, String entityPath,
			HttpServletRequest request, HttpServletResponse response) {
		this.citationService = citationService;
		this.contentHostingService = contentHostingService;
		this.entityPath = entityPath;
		this.request = request;
		this.response = response;

		String method = request.getMethod().toLowerCase();

		if ("get".equals(method)) {
			doGet();
		} else if ("post".equals(method)) {
			String methodParam = request.getParameter("_method");
			if (methodParam == null || "post".equals(methodParam)) {
				crud(CREATE); // creation
			} else {
				if ("put".equals(methodParam)) {
					crud(UPDATE); // update
				} else if ("delete".equals(methodParam)) {
					crud(DELETE); // delete
				} else {
					sendError(" Bad Request with methodParam=" + methodParam,
							HttpServletResponse.SC_BAD_REQUEST);
				}
			}
		}

	}

	private CitationReq getRequestProperties() {
		// TODO: in real REST should not be a request parameter
		String citationId = request.getParameter("cid");
		String[] keys = request.getParameterValues("cipkeys");
		String[] values = request.getParameterValues("cipvalues");
		String citationListName = request.getParameter("listname");

		Map<String, List<String>> properties = new HashMap<String, List<String>>();
		if (keys != null) {
			for (int i = 0; i < keys.length; i++) {
				String key = keys[i];
				String value = values[i];
				// multivaluated val
				List<String> list = properties.get(key);
				if (list == null) {
					list = new ArrayList<String>();
				}
				list.add(value);
				properties.put(key, list);
			}
		}
		CitationReq res = new CitationReq();
		res.setId(citationId);
		res.setProperties(properties);
		res.setCitationListName(citationListName);
		return res;
	}

	private void createOrUpdateCitationProperty(String property,
			Citation citation, String value) {
		if (value == null) {
			value = "";
		}
		// if (property.equalsIgnoreCase("preferredUrl"))
		// property = "sakai:url_string";
		//	
		List<String> values = new Vector<String>();
		values.add(value);

		if (!"".equals(citation.getCitationProperty(property))) {
			citation.updateCitationProperty(property, values);
		} else {
			citation.setCitationProperty(property, value);
		}

		// //If it a citation of type unknown, we mark the url as the preferred
		// url
		// String type = (String)
		// citation.getCitationProperty("sakai:mediatype");
		// if (property.equalsIgnoreCase("sakai:url_string")){
		// if(!"undefined".equalsIgnoreCase(value)){
		// //TODO: cette methode permet de sauvegarder plusieurs urls pour une
		// meme
		// //citation
		// String urlId = citation.addCustomUrl(citation.getDisplayName(),
		// value);
		// citation.setPreferredUrl(urlId);
		// }
		//			
		// }

	}

	private List<String> getCiEditableProperties() {
		return CiEditableProperties.getList();
	}

	private void createCitationInList(ContentResource resource,
			CitationCollection citationList, CitationReq citeReq) {
		if (citationList != null) {
			String citationId = citeReq.getId();
			if (citationId == null) {
				// citationid = null add a citation to citationList
				try {
					Citation newCitation = citationService.addCitation(citeReq
							.getType());
					List<String> ciEditableProperties = getCiEditableProperties();
					for (String editableProp : ciEditableProperties) {
						createOrUpdateCitationProperty(editableProp,
								newCitation, citeReq.getProperty(editableProp));
					}

					String citationName = citeReq.getCitationName();
					newCitation.setDisplayName(citationName);

					String citationPreferredUrl = citeReq
							.getCitationPreferredUrl();
					if (citationPreferredUrl != null
							&& !"undefined"
									.equalsIgnoreCase(citationPreferredUrl
											.trim())
							&& !""
									.equalsIgnoreCase(citationPreferredUrl
											.trim())) {
						String urlId;
						// In case this citation already has a preferred url, we
						// update it
						if (newCitation.hasPreferredUrl()) {
							urlId = newCitation.getPreferredUrlId();
							newCitation.updateCustomUrl(urlId, newCitation
									.getDisplayName(), citationPreferredUrl,
									Citation.OMIT_PREFIX_TEXT);
						} else {
							urlId = newCitation.addCustomUrl(newCitation
									.getDisplayName(), citationPreferredUrl);
							newCitation.setPreferredUrl(urlId);
						}
					}

					newCitation.setAdded(true);
					citationList.add(newCitation);
					citationList.saveCitation(newCitation);
					citationService.save(citationList);
					// then write
					Map<String, Object> listMap = new HashMap<String, Object>();
					writeCitationList(resource, citationList.getId(), listMap);
					map.put("items", listMap);
				} catch (Exception e) {
					sendError("Cannot create resource for citationlist:"
							+ citationList.getId(), e,
							HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}

			}
		} else {
			sendError("Invalid parameter, CitationList must not be null",
					HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	private void updateCitationInList(ContentResource resource,
			CitationCollection citationList, CitationReq citeReq) {
		if (citationList != null) {
			String citationId = citeReq.getId();
			if (citationId != null) {
				String type = citeReq.getType();
				if (type != null && !"".equals(type)) {
					if (citationList.getCitations().size() != 0) {
						// searching for citation to update:
						try {
							Citation citationToUpdate = citationList
									.getCitation(citationId);

							// update type of citation
							Schema schema = citationService.getSchema(type);
							if (schema == null) {
								schema = citationService
										.getSchema(CitationService.UNKNOWN_TYPE);
							}
							citationToUpdate.setSchema(schema);

							List<String> ciEditableProperties = getCiEditableProperties();
							for (String editableProp : ciEditableProperties) {
								createOrUpdateCitationProperty(editableProp,
										citationToUpdate, citeReq
												.getProperty(editableProp));
							}

							String citationPreferredUrl = citeReq
									.getCitationPreferredUrl();
							String citationPreferredUrlLabel = 
							    citeReq.getCitationPreferredUrlLabel();
							String urlId;
							
							// In case this citation already has a preferred
							// url, we
							// update it
							if (citationPreferredUrl == null || "".equalsIgnoreCase(citationPreferredUrl)) {
								citationToUpdate.setPreferredUrl(null);
							} else {
								if (citationToUpdate.hasPreferredUrl()) {
									urlId = citationToUpdate
											.getPreferredUrlId();
									citationToUpdate.updateCustomUrl(urlId,
											citationPreferredUrlLabel,
											citationPreferredUrl,
											Citation.OMIT_PREFIX_TEXT);
								} else {
									urlId = citationToUpdate.addCustomUrl(
											citationPreferredUrlLabel,
											citationPreferredUrl);
									citationToUpdate.setPreferredUrl(urlId);
								}

							}
							citationToUpdate.setAdded(true);
							citationList.saveCitation(citationToUpdate);

							// writeCitationList
							Map<String, Object> listMap = new HashMap<String, Object>();
							writeCitationList(resource, citationList.getId(),
									listMap);
							map.put("items", listMap);

						} catch (IdUnusedException e) {
							sendError("Can't find citation id =" + citationId
									+ " from citationLIstid="
									+ citationList.getId(), e,
									HttpServletResponse.SC_BAD_REQUEST);
						}
					} else {
						sendError("CitationList is empty citationListId="
								+ citationList.getId(),
								HttpServletResponse.SC_EXPECTATION_FAILED);
					}
				} else {
					sendError(
							"Invalid parameter, type for citation must not be not null, and not empty citationListId="
									+ citationList.getId(),
							HttpServletResponse.SC_BAD_REQUEST);
				}
			}
		} else {
			sendError("Invalid parameter, CitationList must not be null",
					HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	private void sendError(String message, int httpError) {
		sendError(message, null, httpError);
	}

	private void sendError(String message, Exception e, int httpError) {
		if (e != null) {
			log.error(message, e);
		} else {
			log.error(message);
		}
		try {
			response.sendError(httpError);
		} catch (IOException e1) {
			log.error("Cannot send error on servlet response ", e1);
		}
	}

	private void sendWarn(String message, Exception e, int httpError) {
		if (e != null) {
			log.warn(message, e);
		} else {
			log.warn(message);
		}
		try {
			response.sendError(httpError);
		} catch (IOException e1) {
			log.error("Cannot send error on servlet response ", e1);
		}
	}

	private void crud(int action) {
	
		String id = entityPath;
		CitationReq citeReq = getRequestProperties();
		// try to access to the entity
		// is it a collection ?
		boolean isCollection = false;
		boolean isResource = false;

		SecurityService.pushAdvisor(new SecurityAdvisor() {
			public SecurityAdvice isAllowed(String userId, String function,
					String reference) {
				return SecurityAdvice.ALLOWED;
			}
		});

		try {
			isCollection = isValidCollection(id);

			if (isCollection) {
				switch (action) {
				case CREATE:
					createCitationList(id, citeReq);
					break;
				case UPDATE:
					updateCitationList(id, citeReq);
				default:
					sendError("Bad action request" + action,
							HttpServletResponse.SC_BAD_REQUEST);
					break;
				}
			} else {
				if (id.endsWith("/"))
					id = id.substring(0, id.lastIndexOf("/"));
				isResource = isValidRessource(id);
				// update resource
				if (isResource) {
					ContentResource resource;
					try {
						resource = contentHostingService.getResource(id);
						boolean isCitationCollection = isCitationCollection(resource);
						if (isCitationCollection) {
							// update citationList
							String citeCollectionId;
							try {
								citeCollectionId = new String(resource
										.getContent());
								CitationCollection citationList = citationService
										.getCollection(citeCollectionId);
								switch (action) {
								case UPDATE: // update citation in citation list
									updateCitationInList(resource,
											citationList, citeReq);
									break;
								case DELETE:
									deleteCitationList(resource, citationList);
									break;
								case CREATE: // create citation in citation list
									createCitationInList(resource,
											citationList, citeReq);
									break;
								default:
									sendError("Bad action request action="
											+ action,
											HttpServletResponse.SC_BAD_REQUEST);
									break;
								}

							} catch (ServerOverloadException e) {
								sendError(
										"Abnormal Overload exception while trying to getContent of citationList resourceId="
												+ id,
										e,
										HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
							}
						}
					} catch (IdUnusedException e) {
						// not a resource ? should be one ?
						sendError(
								"the resource must be identified by id " + id,
								e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					} catch (TypeException e) {
						// not a resource ? should be one ?
						sendError("type error on identified resource id " + id,
								e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					}
				}

			}
		} catch (PermissionException pe) {
			sendWarn("CiBean not allowed to edit or create id=" + id, pe,
					HttpServletResponse.SC_FORBIDDEN);
		}

		SecurityService.clearAdvisors();

		}

	private void createCitationList(String collectionId, CitationReq citeReq) {
		if (citeReq != null) {
			try {
				if (isValidCollection(collectionId)) {
					CitationCollection citationList = citationService
							.addCollection();

					// if citationListName is not set, use citationList
					// id...
					String citationListName = citeReq.getCitationListName();
					if (citationListName == null || "".equals(citationListName)) {
						citationListName = citationList.getId();
					}
					try {
						ContentResourceEdit cre = contentHostingService
								.addResource(
										buildCollectionPath(collectionId),
										citationListName,
										null,
										ContentHostingService.MAXIMUM_ATTEMPTS_FOR_UNIQUENESS);

						cre.setResourceType(CitationService.CITATION_LIST_ID);
						cre.setContentType(ResourceType.MIME_TYPE_HTML);

						ResourcePropertiesEdit props = cre.getPropertiesEdit();
						// set the alternative_reference to point to
						// reference_root
						// for
						// CitationService
						props
								.addProperty(
										ContentHostingService.PROP_ALTERNATE_REFERENCE,
										org.sakaiproject.citation.api.CitationService.REFERENCE_ROOT);
						props.addProperty(ResourceProperties.PROP_CONTENT_TYPE,
								ResourceType.MIME_TYPE_HTML);
						props.addProperty(ResourceProperties.PROP_DISPLAY_NAME,
								citationListName);

						cre.setContent(citationList.getId().getBytes());
						contentHostingService.commitResource(cre,
												NotificationService.NOTI_NONE);
						ContentResource resource = contentHostingService
								.getResource(cre.getId());
						// if commit is a success, then save citation
						citationService.save(citationList);
						// then write
						Map<String, Object> listMap = new HashMap<String, Object>();
						writeCitationList(resource, collectionId, listMap);
						map.put("items", listMap);

					} catch (Exception e) {
						sendError("Cannot create resource for citationlist:"
								+ collectionId, e,
								HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					}
				}
			} catch (PermissionException e) {
				sendWarn("Not allowed to create citationlist in collection "
						+ collectionId, e, HttpServletResponse.SC_FORBIDDEN);
			}
		} else {
			sendError("Bad request", HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	private void updateCitationList(String collectionId, CitationReq citeReq) {
		if (citeReq != null) {
			try {
				if (isValidCollection(collectionId)) {

					ContentResource resource = contentHostingService
							.getResource(citeReq.getId());
					boolean isCitationCollection = isCitationCollection(resource);
					if (isCitationCollection) {
						// if citationListName is not set, use citationList
						// id...
						String citeCollectionId = new String(resource
								.getContent());
						CitationCollection citationList = citationService
								.getCollection(citeCollectionId);
						String citationListName = citeReq.getCitationListName();
						if (citationListName != null
								&& !citationListName.trim().equals("")) {
							try {
								ContentResourceEdit cre = contentHostingService
										.editResource(citeReq.getId());
								ResourcePropertiesEdit props = cre
										.getPropertiesEdit();
								props.addProperty(
										ResourceProperties.PROP_DISPLAY_NAME,
										citationListName);
								contentHostingService.commitResource(cre,
												NotificationService.NOTI_NONE);
								citationService.save(citationList);
								// then write
								Map<String, Object> listMap = new HashMap<String, Object>();
								writeCitationList(resource, collectionId,
										listMap);
								map.put("items", listMap);
							} catch (Exception e) {
								sendError(
										"Cannot update citationlist:"
												+ collectionId,
										e,
										HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
							}
						}
					}
				}
			} catch (Exception e) {
				sendWarn("Cannot update citationlist", e,
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		} else {
			sendError("Bad request", HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	private void deleteCitationList(ContentResource resource,
			CitationCollection citationList) {
		try {
			contentHostingService.checkResource(resource.getId());
			// FIXME: bad id
			ContentResourceEdit cre = contentHostingService
					.editResource(resource.getId());
			contentHostingService.removeResource(cre);
			citationService.removeCollection(citationList);
		} catch (PermissionException e) {
			sendError("Cannot remove citationList=" + citationList.getId(), e,
					HttpServletResponse.SC_EXPECTATION_FAILED);
		} catch (IdUnusedException e) {
			sendError("Cannot remove citationList=" + citationList.getId(), e,
					HttpServletResponse.SC_EXPECTATION_FAILED);
		} catch (TypeException e) {
			sendError("Cannot remove citationList=" + citationList.getId(), e,
					HttpServletResponse.SC_EXPECTATION_FAILED);
		} catch (InUseException e) {
			sendError("Cannot remove citationList=" + citationList.getId(), e,
					HttpServletResponse.SC_CONFLICT);
		}

	}

	private void doGet() {
		String id = entityPath;
		// try to access to the entity
		// is it a collection ?
		boolean isCollection = false;
		boolean isResource = false;
		try {
			isCollection = isValidCollection(id);

			if (isCollection) {
				// list the citations within the collection
				listCitationLists(id);
			} else {
				isResource = isValidRessource(id);
				// TODO: list the citation content OR download it ?
			}
		} catch (PermissionException pe) {
			log.info("CiBean not allowed to access to id=" + id, pe);
			try {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
			} catch (IOException ioe) {
				log.error("Cannot send error on servlet response ", ioe);
			}
		}
	}

	private String buildCollectionPath(String collectionId) {
		if (!collectionId.endsWith("/")) {
			collectionId += "/";
		}
		return collectionId;
	}

	private boolean isCitationCollection(ContentResource resource) {
		boolean res = false;
		if (resource.getResourceType().equals(CitationService.CITATION_LIST_ID)) {
			res = true;
		}
		return res;
	}

	private void writeCitationList(ContentResource resource,
			String collectionId, Map<String, Object> listMap) {
		try {
			// Warning the citationCollectionid is stored in the content
			// of the resource !!.
			String citeCollectionId = new String(resource.getContent());
			CitationCollection citationList = citationService
					.getCollection(citeCollectionId);

			Map<String, Object> citationListMap = new HashMap<String, Object>();
			Map<String, Object> citationPropertiesMap = null;

			// loop over citationlist
			List<Citation> list = citationList.getCitations();
			for (Citation citation : list) {
				Map<String, Object> citationMap = new HashMap<String, Object>();
				citationPropertiesMap = citation.getCitationProperties();
				
				//add the mediatype to the properties map
				citationPropertiesMap.put("sakai:mediatype", citation.getSchema().getIdentifier());

				// we retrieve the url of the citation, it points to a library
				// by default
				// it can also return an url chosen by the user

				if (citation.hasPreferredUrl()) {
					citationPropertiesMap.put("preferredUrl", citation
							.getCustomUrl(citation.getPreferredUrlId()));
				} else {
					citationPropertiesMap.put("url", citation.getOpenurl());
				}

				citationMap.put("properties", citationPropertiesMap);

				citationListMap.put(citation.getId(), citationMap);
			}

			Map<String, Object> colContentMap = new HashMap<String, Object>();

			colContentMap.put("sakai:displayname", resource.getProperties()
					.getProperty(
							resource.getProperties().getNamePropDisplayName()));
			colContentMap
					.put("DAV:getlastmodified", resource.getProperties()
							.getProperty(
									resource.getProperties()
											.getNamePropModifiedDate()));
			colContentMap.put("id", citationList.getId());
			colContentMap.put("description", citationList.getDescription());
			colContentMap.put("properties", citationList.getProperties());
			colContentMap.put("path", resource.getId());
			colContentMap.put("citations", citationListMap);
			listMap.put(resource.getProperties().getProperty(
					resource.getProperties().getNamePropDisplayName()),
					colContentMap);

		} catch (IdUnusedException e) {
			log.error("citationList not identified for resourceId="
					+ collectionId, e);
		} catch (ServerOverloadException e) {
			log.error(
					"Resource content anormaly big for a citationList resourceId="
							+ collectionId, e);
		}
	}

	private void writeCollection(ContentEntity entity, String id,
			Map<String, Object> listMap) {
		try {
			if (isValidCollection(id)) {
				ContentCollection cc = (ContentCollection) entity;
				Map<String, Object> outputMap = new HashMap<String, Object>();
				// mixinNodeType useful ?
				// outputMap.put("mixinNodeType",
				// CHSNodeMap.getMixinTypes(entity));
				outputMap.put("properties", CHSNodeMap.getProperties(entity));
				outputMap.put("name", CHSNodeMap.getName(entity));
				outputMap.put("path", entity.getId());
				outputMap.put("primaryNodeType", "nt:folder");
				listMap.put(CHSNodeMap.getName(entity), outputMap);
			}
		} catch (PermissionException e) {
			// not permitted, nothing, just leave
		}
	}

	private void listCitationLists(String collectionId) {
		collectionId = buildCollectionPath(collectionId);
		ContentCollection ce = null;

		try {
			ce = contentHostingService.getCollection(collectionId);
		} catch (Exception e) {
			// do nothing and return
			return;
		}

		Map<String, Object> listMap = new HashMap<String, Object>();

		// list resources and collections
		List<ContentEntity> entities = ce.getMemberResources();

		for (ContentEntity entity : entities) {
			String id = entity.getId();
			// is a collection ?
			if (contentHostingService.isCollection(id)) {
				writeCollection(entity, id, listMap);
			} else {// is a resource
				ContentResource resource = (ContentResource) entity;
				if (isCitationCollection(resource)) {
					writeCitationList(resource, collectionId, listMap);
				}
			}
		}
		map.put("items", listMap);
	}

	private boolean isCollection(String id) {
		return contentHostingService.isCollection(id);
	}

	private boolean isValidCollection(String id) throws PermissionException {
		id = buildCollectionPath(id);
		boolean res = true;
		try {
			contentHostingService.checkCollection(id);
			res = isCollection(id);
		} catch (IdUnusedException e) {
			res = false;
		} catch (TypeException e) {
			res = false;
		}
		return res;
	}

	private boolean isValidRessource(String id) throws PermissionException {
		boolean res = true;
		try {
			contentHostingService.checkResource(id);
		} catch (IdUnusedException e) {
			res = false;
		} catch (TypeException e) {
			res = false;
		}
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.ServiceDefinition#getResponseMap()
	 */
	public Map<String, Object> getResponseMap() {

		return map;
	}

}
