/********************************************************************************** 
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/oracle/OracleSpecialAccessDAO.java $ 
 * $Id: OracleSpecialAccessDAO.java 71044 2010-10-29 17:37:24Z murthy@etudes.org $ 
 *********************************************************************************** 
 * 
 * Copyright (c) 2008, 2009, 2010 Etudes, Inc. 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 * 
 **********************************************************************************/
package org.etudes.jforum.dao.oracle;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.etudes.jforum.JForum;
import org.etudes.jforum.entities.SpecialAccess;
import org.etudes.jforum.util.preferences.SystemGlobals;


public class OracleSpecialAccessDAO extends org.etudes.jforum.dao.generic.GenericSpecialAccessDAO
{
	@Override
	public int addNew(SpecialAccess specialAccess) throws Exception
	{
		PreparedStatement p = this.getStatementForAutoKeys("SpecialAccessModel.addNew");

		p.setInt(1, specialAccess.getForumId());
		
		if (specialAccess.getStartDate() == null)
		{
		  p.setTimestamp(2, null);
		}
		else
		{
		  p.setTimestamp(2, new Timestamp(specialAccess.getStartDate().getTime()));
		}
		
		if (specialAccess.getEndDate() == null)
		{
		  p.setTimestamp(3, null);
		}
		else
		{
		  p.setTimestamp(3, new Timestamp(specialAccess.getEndDate().getTime()));		  
		}		
		p.setInt(4, specialAccess.isOverrideStartDate() ? 1 : 0);
		p.setInt(5, specialAccess.isOverrideEndDate() ? 1 : 0);
		p.setInt(6, specialAccess.isLockOnEndDate() ? 1 : 0);
		
		this.setAutoGeneratedKeysQuery(SystemGlobals.getSql("SpecialAccessModel.lastGeneratedSpecilaAccessId"));

		int specialAccessId = this.executeAutoKeysQuery(p);

		p.close();
		
		// save special access users
		OracleUtils.writeClobUTF16BinaryStream(SystemGlobals.getSql("SpecialAccessModel.addUsers"), specialAccessId, 
													getUserIdString(specialAccess.getUserIds()));

		return specialAccessId;
	
	}
	
	@Override
	public void update(SpecialAccess specialAccess) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("SpecialAccessModel.update"));
		
		p.setInt(1, specialAccess.getForumId());
		
		if (specialAccess.getStartDate() == null)
		{
			p.setTimestamp(2, null);
		}
		else
		{
			p.setTimestamp(2, new Timestamp(specialAccess.getStartDate().getTime()));
		}
		
		if (specialAccess.getEndDate() == null)
		{
			p.setTimestamp(3, null);
		}
		else
		{
			p.setTimestamp(3, new Timestamp(specialAccess.getEndDate().getTime()));		  
		}
		p.setInt(4, specialAccess.isLockOnEndDate() ? 1 : 0);
		p.setInt(5, specialAccess.isOverrideStartDate() ? 1 : 0);
		p.setInt(6, specialAccess.isOverrideEndDate() ? 1 : 0);
		p.setInt(7, specialAccess.getId());

		p.executeUpdate();
		
		p.close();
		
		// save special access users
		OracleUtils.writeClobUTF16BinaryStream(SystemGlobals.getSql("SpecialAccessModel.addUsers"), specialAccess.getId(), 
													getUserIdString(specialAccess.getUserIds()));
	}
	
	@Override
	protected String getSpecialAccessUsersFromResultSet(ResultSet rs) throws Exception
	{
		return OracleUtils.readClobUTF16BinaryStream(rs, "users");
	}

}
