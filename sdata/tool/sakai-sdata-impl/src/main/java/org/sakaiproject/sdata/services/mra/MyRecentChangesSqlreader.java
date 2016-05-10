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

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.db.api.SqlReader;

/**
 * TODO Javadoc
 * 
 * @author
 */
public class MyRecentChangesSqlreader implements SqlReader
{
	private static final Log log = LogFactory.getLog(MyRecentChangesSqlreader.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.db.api.SqlReader#readSqlResultRecord(java.sql.ResultSet)
	 */
	public MyRecentChangesSqlresult readSqlResultRecord(ResultSet result)
	{

		MyRecentChangesSqlresult res = new MyRecentChangesSqlresult();

		try
		{

			res.setVersion(result.getString("version"));
			res.setContext(result.getString("context"));
			res.setName(result.getString("name"));
			res.setTool(result.getString("tool"));

		}
		catch (SQLException e)
		{
			log.error("Error Executing sql reader ", e);

		}

		return res;
	}
}
