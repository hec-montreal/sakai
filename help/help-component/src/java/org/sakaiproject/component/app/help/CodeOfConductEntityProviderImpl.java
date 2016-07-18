package org.sakaiproject.component.app.help;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.app.help.CodeOfConductEntityProvider;
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
import org.sakaiproject.util.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

public class CodeOfConductEntityProviderImpl implements CodeOfConductEntityProvider, AutoRegisterEntityProvider, RESTful
{
	protected final Log log = LogFactory.getLog(getClass());
	private ResourceLoader msgs = new ResourceLoader("CodeOfConductMessages");
	private static PropertiesConfiguration otherLangProp;

	private UserDirectoryService userDirectoryService;

	public void setUserDirectoryService(UserDirectoryService us)
	{
		this.userDirectoryService = us;
	}

	private PreferencesService preferencesService;

	public void setPreferencesService(PreferencesService ps)
	{
		this.preferencesService = ps;
	}

	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource)
	{
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public String getEntityPrefix()
	{
		return ENTITY_PREFIX;
	}

	@Override
	@Transactional
	public String createEntity(EntityReference ref, Object entity, Map<String, Object> params)
	{
		User currentUser = userDirectoryService.getCurrentUser();
		String userId = currentUser.getEid();

		log.info("Accepting code of conduct for " + userId);

		if (userId == null || userId.isEmpty())
			throw new SecurityException("You must be logged in to perform this request.");

		if (hasUserAccepted(userId))
		{
			throw new IllegalArgumentException("User has already accepted the code of conduct.");
		}
		else
		{
			jdbcTemplate.update("insert into CODE_OF_CONDUCT values (?,?)", new Object[] { userId, new java.util.Date(System.currentTimeMillis()) });
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
		valuesMap.put("type", type);
		Locale currentUserLocale = preferencesService.getLocale(currentUserId);
		URL url;
		if ((currentUserLocale == null) || currentUserLocale.equals(Locale.CANADA_FRENCH))
			url = getClass().getClassLoader().getResource("CodeOfConductMessages.properties");
		else
			url = getClass().getClassLoader().getResource("CodeOfConductMessages_fr_CA.properties");
		try
		{

			otherLangProp = new PropertiesConfiguration();
			otherLangProp.setListDelimiter('?');
			otherLangProp.load(url);
			valuesMap.put("otherVersionTitle", otherLangProp.getProperty("codeOfConduct.title"));
			valuesMap.put("otherVersionContent", otherLangProp.getProperty("codeOfConduct.body"));

		}
		catch (ConfigurationException e)
		{
			log.error("Can not load the other version of the code of conduct");
		}
		valuesMap.put("title", msgs.get("codeOfConduct.title"));
		valuesMap.put("content", msgs.get("codeOfConduct.body"));

		return valuesMap;
	}

	@Transactional
	private boolean hasUserAccepted(String id)
	{
		int i = jdbcTemplate.queryForInt("select count(*) from CODE_OF_CONDUCT where MATRICULE = ?", new Object[] { id });

		if (i > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
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
}
