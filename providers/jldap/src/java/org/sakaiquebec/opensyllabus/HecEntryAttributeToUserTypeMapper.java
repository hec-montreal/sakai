/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2011 The Sakai Foundation, The Sakai Quebec Team.
 *
 * Licensed under the Educational Community License, Version 1.0
 * (the "License"); you may not use this file except in compliance with the
 * License.
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
 ******************************************************************************/
package org.sakaiquebec.opensyllabus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPEntry;

import edu.amc.sakai.user.EntryAttributeToUserTypeMapper;
import edu.amc.sakai.user.LdapAttributeMapper;

import org.sakaiproject.memory.api.Cache;
import org.sakaiproject.memory.api.MemoryService;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class HecEntryAttributeToUserTypeMapper extends EntryAttributeToUserTypeMapper {

	private static Log M_log = LogFactory.getLog(HecEntryAttributeToUserTypeMapper.class);

	private Cache cache;
	private final String CACHE_NAME = "org.sakaiquebec.opensyllabus.cache.instructors";

	public String mapLdapEntryToSakaiUserType(LDAPEntry ldapEntry,
			LdapAttributeMapper mapper) {

		// see if user is in PeopleSoft as instructor
		if (isInstructor(ldapEntry.getAttribute("CN").getStringValue())) {
			return "instructor";
		}

		LDAPAttribute userTypeAttr =
				getUserTypeAttribute(ldapEntry, mapper);

		if ( userTypeAttr == null ) {
			return getDefaultSakaiUserType();
		}

		String[] userTypeAttrValues =
				getUserTypeAttribute(ldapEntry, mapper).getStringValueArray();

		String userType = mapUserTypeAttributeValues(userTypeAttrValues);
		return userType;

	}

	protected String mapUserTypeAttributeValue(String attrValue) {
		if (getAttributeValueToSakaiUserTypeMap() == null) {
			return null;
		}
		String[] t = attrValue.split("\\|");
		if (t != null)
			attrValue = t[0];

		return getAttributeValueToSakaiUserTypeMap().get(attrValue);
	}

	private boolean isInstructor(String id) {
		List<String> instructors = null;

		if(cache != null && cache.containsKey("instructorList")){
			instructors = (List<String>)cache.get("instructorList");
		} 
		
		if (instructors == null) {

			instructors = new ArrayList<String>();

			Connection conn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;

			try {
				conn = dataSource.getConnection();

				ps = conn.prepareStatement("select EMPLID from PSFTCONT.ZONECOURS2_PS_N_NATURE_EMPLOI where DESCR = 'Enseignant'");

				ps.execute();
				rs = ps.getResultSet();

				// will return true if there is data
				while (rs.next()) {
					instructors.add(rs.getString("EMPLID"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			finally {
				try { rs.close(); } catch (Exception e) { /* ignored */ }
				try { ps.close(); } catch (Exception e) { /* ignored */ }
				try { conn.close(); } catch (Exception e) { /* ignored */ }
			}

			cache.put("instructorList", instructors);
		}

		return instructors.contains(id);
	}

	public void init() {
		cache = memoryService.newCache(CACHE_NAME, "");
	}

	private DataSource dataSource;

	public void setDataSource(DataSource ds) {
		this.dataSource = ds;
	}

	private MemoryService memoryService;

	public void setMemoryService(MemoryService ms) {
		this.memoryService = ms;
	}


}
