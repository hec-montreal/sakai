/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/oracle/OracleUtils.java $ 
 * $Id: OracleUtils.java 70371 2010-09-22 20:22:42Z murthy@etudes.org $ 
 *********************************************************************************** 
 * 
 * Copyright (c) 2008, 2009, 2010 Etudes, Inc. 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 * 
 * Portions completed before July 1, 2004 Copyright (c) 2003, 2004 Rafael Steil, All rights reserved, licensed under the BSD license. 
 * http://www.opensource.org/licenses/bsd-license.php 
 * 
 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met: 
 * 
 * 1) Redistributions of source code must retain the above 
 * copyright notice, this list of conditions and the 
 * following disclaimer. 
 * 2) Redistributions in binary form must reproduce the 
 * above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or 
 * other materials provided with the distribution. 
 * 3) Neither the name of "Rafael Steil" nor 
 * the names of its contributors may be used to endorse 
 * or promote products derived from this software without 
 * specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT 
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE 
 ***********************************************************************************/
package org.etudes.jforum.dao.oracle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.JForum;

/**
 * @author Dmitriy Kiriy
 */
public class OracleUtils
{
	private static final Log logger = LogFactory.getLog(OracleUtils.class);

	public static String readBlobUTF16BinaryStream(ResultSet rs, String fieldName) throws IOException, SQLException 
	{
		Blob clob = rs.getBlob(fieldName);

		InputStream is = clob.getBinaryStream();
		StringBuffer sb = new StringBuffer();
		int readedBytes = 0;
		int bufferSize = 4096;

		do {
			byte[] bytes = new byte[bufferSize];
			readedBytes = is.read(bytes);
			if (readedBytes > 0) {
				String readed = new String(bytes, 0, readedBytes, "UTF-16");
				sb.append(readed);
			}
		} while (readedBytes == bufferSize);

		is.close();

		return sb.toString();
	}

	/**
	 * The query should look like:
	 * 
	 * SELECT blob_field from any_table WHERE id = ? FOR UPDATE
	 * 
	 * BUT KEEP IN MIND:
	 * 
	 * When you insert record in previous step, it should go with empty_blob() like:
	 * 
	 * INSERT INTO jforum_posts_text ( post_text ) VALUES (EMPTY_BLOB())
	 * 
	 * @param query
	 * @param idForQuery
	 * @param value
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void writeBlobUTF16BinaryStream(String query, int idForQuery, String value) throws IOException, SQLException 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(query);
		p.setInt(1, idForQuery);

		ResultSet rs = p.executeQuery();
		rs.next();
		Blob postText = rs.getBlob(1);
		
		//wipe out the Blob contents 
		postText.truncate(0); 
		 


		if (logger.isDebugEnabled())
			logger.debug("post test is a " + postText.getClass().getName());

		// OutputStream blobWriter = ((oracle.sql.BLOB)postText).setBinaryStream(0L);
		//OutputStream blobWriter = postText.setBinaryStream(0L); //patch Phil Rancourt
		OutputStream blobWriter = postText.setBinaryStream(1L);
		blobWriter.write(value.getBytes("UTF-16"));

		blobWriter.flush();
		blobWriter.close();
		rs.close();
		p.close();
	}
	
	/**
	 * Writes clob to database 
	 * @param query
	 * @param idForQuery
	 * @param value
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void writeClobUTF16BinaryStream(String query, int idForQuery, String value) throws IOException, SQLException {
		PreparedStatement p = JForum.getConnection().prepareStatement(query);
		p.setInt(1, idForQuery);

		ResultSet rs = p.executeQuery();
		rs.next();
		Clob clobval = rs.getClob(1);

		if (logger.isDebugEnabled())
			logger.debug("clobval is " + clobval.getClass().getName());

		//Writer clobWriter = clobval.setCharacterStream(0L);    //patch Phil Rancourt 
		Writer clobWriter = clobval.setCharacterStream(1L);
     
		char[] cbuffer = new char[value.length()];
		int srcBegin  = 0, dstBegin = 0;        
		value.getChars(srcBegin, value.length(), cbuffer, dstBegin);        
		clobWriter.write(cbuffer);
		
		clobWriter.close();
		
		rs.close();
		p.close();
	}
	
	/**
	 * read clob into a string
	 * @param rs
	 * @param fieldName
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static String readClobUTF16BinaryStream(ResultSet rs, String fieldName) throws IOException, SQLException 
	{
		Clob clob = rs.getClob(fieldName);

		Reader clobStream = clob.getCharacterStream();
		StringBuffer clobData = new StringBuffer();
		
		// Read from the Clob stream and write to the stringbuffer
		int nchars = 0;
		char[] buffer = new char[4096];
		while( (nchars = clobStream.read(buffer)) != -1 )        
			clobData.append(buffer, 0, nchars);      
		      
		clobStream.close();

		return clobData.toString();
	}
}
