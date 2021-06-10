package org.sakaiproject.component.app.help;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.app.help.CodeOfConductEntityProvider;
import org.sakaiproject.component.app.help.model.CodeOfConductEntryBean;
import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.entitybroker.entityprovider.capabilities.AutoRegisterEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.capabilities.RESTful;
import org.sakaiproject.entitybroker.entityprovider.extension.Formats;
import org.sakaiproject.entitybroker.entityprovider.search.Search;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.user.api.PreferencesService;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

public class CodeOfConductEntityProviderImpl extends HibernateDaoSupport implements CodeOfConductEntityProvider, AutoRegisterEntityProvider, RESTful
{
	protected final Log log = LogFactory.getLog(getClass());

	private static String titleFr;
	private static String bodyFr;
	private static String acceptButtonFr;
	private static String titleEn;
	private static String bodyEn;
	private static String acceptButtonEn;

	private UserDirectoryService userDirectoryService;
	private PreferencesService preferencesService;
		
	public void init() {
		URL url;
		PropertiesConfiguration properties;
		try
		{
			url = getClass().getClassLoader().getResource("CodeOfConductMessages.properties");
			properties = new PropertiesConfiguration();
			//disable list delimiter
			properties.setListDelimiter((char) 0);
			properties.load(url);
			titleEn = (String) properties.getProperty("codeOfConduct.title");
			acceptButtonEn = (String) properties.getProperty("acceptButton");			
		}
		catch (ConfigurationException e)
		{
			log.error("Can not load the english version of the code of conduct");
		}

		try
		{
			url = getClass().getClassLoader().getResource("CodeOfConductMessages_fr_CA.properties");
			properties = new PropertiesConfiguration();
			//disable list delimiter
			properties.setListDelimiter((char) 0);
			properties.load(url);
			titleFr = (String) properties.getProperty("codeOfConduct.title");
			acceptButtonFr = (String) properties.getProperty("acceptButton");
		}
		catch (ConfigurationException e)
		{
			log.error("Can not load the french version of the code of conduct");
		}

    }

	@Override
	public String getEntityPrefix()
	{
		return ENTITY_PREFIX;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@Transactional
	public String createEntity(EntityReference ref, Object entity, Map<String, Object> params)
	{
		User currentUser = userDirectoryService.getCurrentUser();
		String userId = currentUser.getEid();

		if (userId == null || userId.isEmpty())
			throw new SecurityException("You must be logged in to perform this request.");

		if (!hasUserAccepted(userId))
		{
			CodeOfConductEntryBean entry = new CodeOfConductEntryBean();
			
			entry.setMatricule(userId);
			entry.setDate(new Date());

			getHibernateTemplate().save(entry);
		}

		return userId;
	}

	@Override
	public Object getSampleEntity()
	{
		return null;
	}

	@Override
	public void updateEntity(EntityReference ref, Object entity, Map<String, Object> params)
	{

	}

	@Override
	public Object getEntity(EntityReference ref)
	{
		HashMap<String, Object> valuesMap = new HashMap<String, Object>();

		Session currentUserSession = SessionManager.getCurrentSession();
		if (currentUserSession.getAttribute(SEEN_AT_LOGIN) != null)
		{
			valuesMap.put(SEEN_AT_LOGIN, currentUserSession.getAttribute(SEEN_AT_LOGIN));
		}
		else
		{
			valuesMap.put(SEEN_AT_LOGIN, currentUserSession.getId());
			currentUserSession.setAttribute(SEEN_AT_LOGIN, currentUserSession.getId());
		}

		if (hasUserAccepted(userDirectoryService.getCurrentUser().getEid()))
		{
			valuesMap.put("hasUserAccepted", true);
		}
		else
		{
			valuesMap.put("hasUserAccepted", false);
		}

		String currentUserId = userDirectoryService.getCurrentUser().getId();

		String type = userDirectoryService.getCurrentUser().getType();
		valuesMap.put("userType", type);

		valuesMap.put("titleEn", titleEn);
		valuesMap.put("titleFr", titleFr);
		valuesMap.put("acceptButtonEn", acceptButtonEn);
		valuesMap.put("acceptButtonFr", acceptButtonFr);

		return valuesMap;
	}

	private boolean hasUserAccepted(String id)
	{
		CodeOfConductEntryBean entry = getHibernateTemplate().get(CodeOfConductEntryBean.class, id);
		
		return entry != null;
	}

	@Override
	public void deleteEntity(EntityReference ref, Map<String, Object> params)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public List<?> getEntities(EntityReference ref, Search search)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getHandledOutputFormats()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getHandledInputFormats()
	{
		return new String[] { Formats.HTML, Formats.XML, Formats.JSON };
	}
	
	public void setUserDirectoryService(UserDirectoryService us)
	{
		this.userDirectoryService = us;
	}

	public void setPreferencesService(PreferencesService ps)
	{
		this.preferencesService = ps;
	}
}
