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

package org.sakaiproject.sdata.services.mra;

import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO Javadoc
 * 
 * @author
 */
public class MyRecentChangesDbBean implements ServletContextListener
{

	private static final boolean DB_IS_ORACLE = "oracle".equalsIgnoreCase(
		org.sakaiproject.db.cover.SqlService.getVendor()); 

	private static final Log log = LogFactory.getLog(MyRecentChangesDbBean.class);

	public static MyRecentChangesObserver obs;

	static {
	    if (! DB_IS_ORACLE) {
		log.info("DB is not Oracle");
		obs = new MyRecentChangesObserver();
	    } else {
		log.info("DB is Oracle");
	    }
	}

	/**
	 * TODO Javadoc
	 */
	public void init()
	{

		log.info("sData Widget Data Service Initializing ...");

		try
		{
			boolean exist = false;
			boolean exist2 = false;
			List lsts = null;
			if (DB_IS_ORACLE) 
			{
				lsts = org.sakaiproject.db.cover.SqlService.dbRead("select table_name from tabs");
			}
			else 
			{
				lsts = org.sakaiproject.db.cover.SqlService.dbRead("SHOW TABLES");
			}

			for (int i = 0; i < lsts.size(); i++)
			{
				if (lsts.get(i).toString().equalsIgnoreCase("sdata_lastlogin"))
				{

					exist = true;
					// log.error("tables exist");
				}
				else if (lsts.get(i).toString().equalsIgnoreCase("sdata_indexqueue"))
				{
					exist2 = true;
					// log.error("tables exist");
				}

			}

			if (exist == false)
			{
				org.sakaiproject.db.cover.SqlService
						.dbWrite("create table sdata_lastlogin (userid varchar(255) not null, usereid varchar(255) not null, userdate timestamp not null, primary key(userid))");
				// log.info("MySakai Login Table Added");
				// log.info("MySakai Index Queue Table Added");

			}
			if (exist2 == false)
			{
				if (DB_IS_ORACLE) 
				{
					org.sakaiproject.db.cover.SqlService
						.dbWrite("create table sdata_indexqueue (id int not null, version timestamp not null, name varchar(255) not null, context varchar(255) not null, tool varchar(255) not null, primary key  (id))");
					org.sakaiproject.db.cover.SqlService
						.dbWrite("CREATE SEQUENCE SDATA_SEQ MINVALUE 1 NOMAXVALUE INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE");
					java.sql.Connection conn = org.sakaiproject.db.cover.SqlService.borrowConnection();
					java.sql.Statement stmt = conn.createStatement();
					stmt.execute("CREATE OR REPLACE TRIGGER TRG_SDATA" +
								" BEFORE INSERT ON SDATA_INDEXQUEUE" +
								" FOR EACH ROW" +
								" BEGIN" +
								" IF(:NEW.id IS NULL) THEN" +
								" SELECT SDATA_SEQ.nextval INTO :NEW.id FROM dual;" +
								" END IF;" +
								" END TRG_SDATA;");
					stmt.close();
					org.sakaiproject.db.cover.SqlService.returnConnection(conn);
				}
				else 
				{
					org.sakaiproject.db.cover.SqlService
						.dbWrite("create table sdata_indexqueue (id int not null AUTO_INCREMENT, version timestamp not null, name varchar(255) not null, context varchar(255) not null, tool varchar(255) not null, primary key  (id));");
				}			
			}

		}
		catch (Exception ex)
		{

			if (log.isDebugEnabled())
			{
				log.debug("alrdy created");
			}

		}

		// log.info("MySakai Observer added");

		// If we are using Oracle, the observer does not work, and
	    	// anyway, we don't use it so we are better just shutting it off.
		if (!DB_IS_ORACLE) {
			org.sakaiproject.event.cover.EventTrackingService.addObserver(obs);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent arg0)
	{
		if (!DB_IS_ORACLE) {
		    org.sakaiproject.event.cover.EventTrackingService.deleteObserver(obs);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent arg0)
	{
		init();

	}

}
