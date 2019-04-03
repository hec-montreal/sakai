package org.sakaiproject.sdata.services.mra;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.announcement.api.AnnouncementMessage;
import org.sakaiproject.announcement.api.AnnouncementService;
import org.sakaiproject.component.api.ComponentManager;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.event.api.Event;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.message.api.MessageChannel;
import org.sakaiproject.sdata.tool.api.ServiceDefinition;
import org.sakaiproject.search.api.InvalidSearchQueryException;
import org.sakaiproject.search.api.SearchList;
import org.sakaiproject.search.api.SearchResult;
import org.sakaiproject.search.api.SearchService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.SiteService.SelectionType;
import org.sakaiproject.site.api.SiteService.SortType;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;

/**
 * TODO Javadoc
 * 
 * @author
 */
public class MyRecentChangesBean implements ServiceDefinition
{

	private EventTrackingService eventTrackingService;

	private Event event;

	private List<Site> mySites;

	private ArrayList<String> arlSiteId = new ArrayList<String>();

	private SearchService searchService;

	private ContentHostingService contentHostingService;

	private AnnouncementService announcementService;

	private EntityManager entityManager;

	private SiteService siteService;

	private ComponentManager componentManager;

	private SqlService sqlService;

	Map<String, Object> resultMap = new HashMap<String, Object>();

	private SessionManager sessionManager;

	private static final Log log = LogFactory.getLog(MyRecentChangesBean.class);

	/**
	 * TODO Javadoc
	 * 
	 * @param sessionManager
	 * @param siteService
	 * @param componentManager
	 * @param sqlService
	 * @param searchService
	 * @param contentHostingService
	 * @param announcementService
	 * @param entityManager
	 * @param paging
	 */
	public MyRecentChangesBean(SessionManager sessionManager, SiteService siteService,
			ComponentManager componentManager, SqlService sqlService,
			SearchService searchService, ContentHostingService contentHostingService,
			AnnouncementService announcementService, EntityManager entityManager,
			int paging)
	{
		this.announcementService = announcementService;
		this.entityManager = entityManager;
		this.contentHostingService = contentHostingService;
		this.searchService = searchService;
		this.sqlService = sqlService;
		this.componentManager = componentManager;
		this.siteService = siteService;
		this.sessionManager = sessionManager;

		try
		{
			search(paging);
		}
		catch (PermissionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IdUnusedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (TypeException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidSearchQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	} // END CONSTRUCTOR

	/**
	 * TODO Javadoc
	 * 
	 * @param paging
	 * @throws PermissionException
	 * @throws IdUnusedException
	 * @throws TypeException
	 * @throws ParseException
	 * @throws InvalidSearchQueryException 
	 */
	private void search(int paging) throws PermissionException, IdUnusedException,
			TypeException, ParseException, InvalidSearchQueryException
	{

		Date lastLogin = null;
		List<String> lstLastLogin;
		SearchResult searchResult = null;
		String currentpage;

		List<Map> myRecentResults = new ArrayList<Map>();

		List<MyRecentChangesResult> results = new ArrayList<MyRecentChangesResult>();

		try
		{

			Session currentSession = sessionManager.getCurrentSession();
			String currentUser = currentSession.getUserId();

			// log.error(currentSession);
			// log.error(currentUser);

			// setEventTrackingService(org.sakaiproject.event.cover.EventTrackingService.getInstance());
			// setEvent(getEventTrackingService().newEvent("octopus.recent.activity",
			// null, true));

			// post the event
			// getEventTrackingService().post(getEvent());

			// this.setLastLogin(null);
			lstLastLogin = sqlService
					.dbRead("select userdate from sdata_lastlogin where userid='"
							+ currentSession.getUserId() + "'");
			if (lstLastLogin.size() != 0)
			{

				lastLogin = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(lstLastLogin.get(0).substring(0, 19));

			}

			mySites = siteService.getSites(SelectionType.ACCESS, null, null, null,
					SortType.TITLE_ASC, null);

			try
			{
				mySites.add(0, (siteService.getSite(siteService
						.getUserSiteId(currentSession.getUserId()))));

			}
			catch (IdUnusedException e)
			{
				e.printStackTrace();
			}
			for (int i = 0; i < mySites.size(); i++)
			{
				log.error("the site ids: " + mySites.get(i).getId());

				arlSiteId.add(mySites.get(i).getId());
			}

			arlSiteId.add(siteService.getUserSiteId(currentSession.getUserId())
					.substring(1));

		}
		catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}

		// ////////
		// //////// DELETE INDEXED FILES FROM INDEX QUEUE
		// ////////
		SearchList searchList = null;
		if (searchService != null)
		{
			searchList = searchService.search("tool:content tool:announcement",
					arlSiteId, 0, 50, null, "dateRelevanceSort");
		
			int ii = -1, iii = 0;
			do
			{
				ii += 1;
				if (ii < searchList.size())
				{
					if (searchList.get(ii) == null)
					{
						break;
					}
					else
					{
						searchResult = ((SearchResult) searchList.get(ii));
						if (searchResult.getId() != null
								&& !searchResult.getId().equals(""))
						{

							for (int r = 0; r < searchResult.getFieldNames().length; r++)
							{
								String s = "";
								if (searchResult.getFieldNames()[r].equals("indexdate"))
								{
									for (int y = 0; y < searchResult
											.getValues(searchResult.getFieldNames()[r]).length; y++)
									{
										s += searchResult.getValues(searchResult
												.getFieldNames()[r])[y];
									}
									long l = Long.parseLong(s);
									s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
											.format(new java.util.Date(l));
									sqlService
											.dbWrite("delete from sdata_indexqueue where version<'"
													+ s + "'");
								}
							}

							break;
						}
					}
				}
				else
				{

					break;

				}

			}

			while ((searchResult.getId() == null || searchResult.getId().equals(""))
					&& ii < 50);
		}

		//////////
		////////// GET INDEXED RESULTS FROM INDEXQUEUE
		//////////

		String finalResult = "";

		int totalrecordsshown = 0;
		String sites = "";
		for (int i = 0; i < arlSiteId.size(); i++)
		{
			if (i == 0)
			{
				sites += "context='" + arlSiteId.get(i) + "' ";
			}
			else
			{
				sites += "OR context='" + arlSiteId.get(i) + "' ";
			}
		}

		List<MyRecentChangesSqlresult> lst = sqlService
				.dbRead("select * from sdata_indexqueue where " + sites
						+ "order by version desc", null, new MyRecentChangesSqlreader());

		ArrayList<String> arlUsed = new ArrayList<String>();

		// BEGIN NEW STUFF

		for (int i = 0; i < lst.size(); i++)
		{

			MyRecentChangesSqlresult mres = lst.get(i);

			if (mres.getTool().equals("content"))
			{
				try {
					
					String eid = mres.getName().substring(8);

					if (!contentHostingService.isCollection(eid))
					{
	
						if (contentHostingService.allowGetResource(eid)
								&& !arlUsed.contains(mres.getName()))
						{
	
							if (totalrecordsshown >= (paging - 1) * 5
									&& totalrecordsshown < (paging) * 5)
							{
	
								ContentResource cres = contentHostingService.getResource(eid);
	
								Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
										.parse(mres.getVersion().substring(0, 19));
	
								MyRecentChangesResult mrcs = new MyRecentChangesResult();
	
								for (Site s : mySites)
								{
									// TODO
									if (mres.getContext().equals(s.getId()))
									{
	
										mrcs.setSitename(s.getTitle());
	
									}
								}
	
								mrcs.setTool(mres.getTool());
								mrcs.setVersion(mres.getVersion());
								mrcs.setContext(mres.getContext());
								mrcs.setName(cres.getUrl().substring(
										cres.getUrl().lastIndexOf("/") + 1));
								mrcs.setReference(cres.getReference());
								results.add(mrcs);
	
							}
	
							totalrecordsshown += 1;
							arlUsed.add(mres.getName());
							if (totalrecordsshown == 50)
							{
								break;
							}
	
						}
					}
				} catch (Exception ex){};
			}
			else if (mres.getTool().equals("announcement") && announcementService != null)
			{

				try
				{

					if (!arlUsed.contains(mres.getName()))
					{

						MessageChannel messageChannel = announcementService
								.getChannel("/announcement/channel/" + mres.getContext()
										+ "/main");
						AnnouncementMessage announcementMessage = (AnnouncementMessage) messageChannel
								.getMessage(mres.getName().substring(
										mres.getName().lastIndexOf('/') + 1));

						if (!announcementMessage.getAnnouncementHeader().getDraft())
						{

							if (totalrecordsshown >= (paging - 1) * 5
									&& totalrecordsshown < (paging) * 5)
							{

								Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
										.parse(mres.getVersion().substring(0, 19));

								MyRecentChangesResult mrcs = new MyRecentChangesResult();

								for (Site s : mySites)
								{
									// TODO
									if (mres.getContext().equals(s.getId()))
									{

										mrcs.setSitename(s.getTitle());

									}
								}

								mrcs.setTool(mres.getTool());
								mrcs.setVersion(mres.getVersion());
								mrcs.setContext(mres.getContext());
								mrcs.setName(announcementMessage.getAnnouncementHeader()
										.getSubject());
								mrcs.setReference(announcementMessage.getReference());
								mrcs.setCleanVersion(mres.getVersion());
								results.add(mrcs);

							}

							totalrecordsshown += 1;
							arlUsed.add(mres.getName());
							if (totalrecordsshown == 50)
							{
								break;
							}

						}

					}

				}
				catch (Exception ex){};

			}

		}

		// ////////
		// //////// FILL WITH INDEXED RESULTS
		// ////////

		if (totalrecordsshown < 50 && searchList != null)
		{

			int ii = -1;
			int iii = 0;
			do
			{
				ii += 1;

				if (ii < searchList.size())
				{
					if (searchList.get(ii) == null)
					{
						break;
					}
					else
					{
						SearchResult srl = (SearchResult) searchList.get(ii);

						if (srl.getId() != null && !srl.getId().equals("")
								&& !arlUsed.contains(srl.getReference()))
						{
							// log.error("+++++ " + srl.getTool());
							if (srl.getTool().equals("content"))
							{

								if (totalrecordsshown >= (paging - 1) * 5
										&& totalrecordsshown < (paging) * 5)
								{

									long l = 0;
									String s = "";
									String s2 = "";
									for (int r = 0; r < srl.getFieldNames().length; r++)
									{
										if (srl.getFieldNames()[r].equals("siteid"))
										{
											for (int y = 0; y < srl.getValues(srl
													.getFieldNames()[r]).length; y++)
											{
												s += srl
														.getValues(srl.getFieldNames()[r])[y];
											}
										}
										else if (srl.getFieldNames()[r]
												.equals("indexdate"))
										{
											for (int y = 0; y < srl.getValues(srl
													.getFieldNames()[r]).length; y++)
											{
												s2 += srl
														.getValues(srl.getFieldNames()[r])[y];
											}
											l = Long.parseLong(s2);
											s2 = new SimpleDateFormat(
													"yyyy-MM-dd HH:mm:ss")
													.format(new java.util.Date(l));
										}
									}

									Date d = new java.util.Date(l);

									MyRecentChangesResult mrcs = new MyRecentChangesResult();

									mrcs.setName(srl.getTitle());

									for (Site ss : mySites)
									{
										// log.error("BIER :: " + s);
										if (s.equals(ss.getId()))
										{

											mrcs.setSitename(ss.getTitle());
											mrcs.setContext(ss.getId());
										}
									}

									mrcs.setTool(srl.getTool());
									mrcs.setVersion(s2);
									mrcs.setReference(srl.getReference());
									results.add(mrcs);

								}

								totalrecordsshown += 1;
								arlUsed.add(srl.getReference());

							}
							else if (srl.getTool().equals("announcement"))
							{

								if (totalrecordsshown >= (paging - 1) * 5
										&& totalrecordsshown < (paging) * 5)
								{

									long l = 0;
									String s = "";
									String s2 = "";
									for (int r = 0; r < srl.getFieldNames().length; r++)
									{
										if (srl.getFieldNames()[r].equals("siteid"))
										{
											for (int y = 0; y < srl.getValues(srl
													.getFieldNames()[r]).length; y++)
											{
												s += srl
														.getValues(srl.getFieldNames()[r])[y];
											}
										}
										else if (srl.getFieldNames()[r]
												.equals("indexdate"))
										{
											for (int y = 0; y < srl.getValues(srl
													.getFieldNames()[r]).length; y++)
											{
												s2 += srl
														.getValues(srl.getFieldNames()[r])[y];
											}
											l = Long.parseLong(s2);
											s2 = new SimpleDateFormat(
													"yyyy-MM-dd HH:mm:ss")
													.format(new java.util.Date(l));
										}
									}

									Date d = new java.util.Date(l);

									String[] arr = srl.getTitle().substring(9).split(
											" From ");
									String arrResult = "";
									for (int i = 0; i < arr.length - 1; i++)
									{
										arrResult += arr[i];
									}

									MyRecentChangesResult mrcs = new MyRecentChangesResult();
									mrcs.setContext(s);
									mrcs.setName(arrResult);

									for (Site ss : mySites)
									{
										if (mrcs.getContext().equals(ss.getId()))
										{

											mrcs.setSitename(ss.getTitle());

										}
									}

									mrcs.setTool(srl.getTool());
									mrcs.setVersion(s2);
									mrcs.setReference(srl.getReference());
									results.add(mrcs);

								}

								totalrecordsshown += 1;
								arlUsed.add(searchResult.getReference());

							}

						}
					}
				}
				else
				{

					break;

				}
			}
			while (totalrecordsshown < 50 && ii < 50);

		}

		// END NEW STUFF
		Date cleanDate = new Date();
		for (MyRecentChangesResult mrcsr : results)
		{

			Map<String, String> mrcsr_map = new HashMap<String, String>();

			// log.error("the site name: " + mrcsr.getSitename());
			if (mrcsr.getSitename().equals("My Workspace"))
			{
				mrcsr_map.put("siteName", "Personal Tools");
				log.error("a my workspace file");

			}
			else
			{
				mrcsr_map.put("siteName", mrcsr.getSitename());
			}
			// mrcsr_map.put("siteName", mrcsr.getSitename());
			mrcsr_map.put("context", mrcsr.getContext());
			mrcsr_map.put("name", mrcsr.getName());
			mrcsr_map.put("tool", mrcsr.getTool());
			mrcsr_map.put("version", mrcsr.getVersion());
			mrcsr_map.put("reference", mrcsr.getReference());

			// today in format brengen om te compairen
			String tiday = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			String cut = "leeg";
			mrcsr.setCleanVersion(mrcsr.getVersion().substring(0,
					mrcsr.getVersion().lastIndexOf('-') + 3));

			if (mrcsr.getVersion().substring(0, mrcsr.getVersion().lastIndexOf('-') + 3)
					.equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date())))
			{

				mrcsr.setCleanVersion("Today");

			}
			else
			{
				String test = mrcsr.getVersion();
				Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(test);
				mrcsr.setCleanVersion(new SimpleDateFormat("dd MMM yy HH:mm").format(d));

			}

			mrcsr_map.put("cleanVersion", mrcsr.getCleanVersion());
			myRecentResults.add(mrcsr_map);

		}

		if (lastLogin == null)
		{
			// TODO
			resultMap.put("lastLogin", "NO USER LOGGED IN");

		}
		else
		{

			resultMap.put("lastLogin", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(lastLogin));
		}

		resultMap.put("total", totalrecordsshown);

		resultMap.put("items", myRecentResults);

		// JSONArray jsonArray = JSONArray.fromObject(lst);

		// return jsonArray.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.ServiceDefinition#getResponseMap()
	 */
	public Map<String, Object> getResponseMap()
	{
		return resultMap;
	}

}
