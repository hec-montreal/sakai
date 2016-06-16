/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/util/mail/Spammer.java $ 
 * $Id: Spammer.java 65298 2009-12-17 00:40:53Z murthy@etudes.org $ 
 *********************************************************************************** 
 * 
 * Copyright (c) 2008 Etudes, Inc. 
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
package org.etudes.jforum.util.mail;

import java.io.StringWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.entities.Attachment;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.sakaiproject.component.cover.ServerConfigurationService;

import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.Template;

/**
 * Dispatch emails to the world. TODO: should do some refactoring to send a personalized email to
 * each user.
 * 
 * @author Rafael Steil
 */
public class Spammer
{
	private static final Log logger = LogFactory.getLog(Spammer.class);

	private static int MESSAGE_HTML = 0;
	private static int MESSAGE_TEXT = 1;

	private Properties mailProps = new Properties();
	private static int messageFormat;
	private static Session session;
	// private static String username;
	// private static String password;
	private MimeMessage message;
	private String messageText;
	
	private boolean testMode = false;
	
	/** The SMTP server to connect to. */
	protected static final String SMTP_HOST = "mail.smtp.host";

	/** The SMTP server port to connect. */
	protected static final String SMTP_PORT = "mail.smtp.port";

	/** Email address to use for SMTP MAIL command. This sets the envelope return address. Defaults to message.getFrom() or InternetAddress.getLocalAddress(). */
	protected static final String SMTP_FROM = "mail.smtp.from";
	
	protected static final String EMAIL_TESTMODE = "testMode@org.sakaiproject.email.api.EmailService";

	protected Spammer() throws EmailException
	{
		/*String host = SystemGlobals.getValue(ConfigKeys.MAIL_SMTP_HOST);
		
		if (host != null) {
			int colon = host.indexOf(':');

			if (colon > 0) {
				mailProps.put("mail.smtp.host", host.substring(0, colon));
				mailProps.put("mail.smtp.port", String.valueOf(host.substring(colon + 1)));
			}
			else {
				mailProps.put("mail.smtp.host", SystemGlobals.getValue(ConfigKeys.MAIL_SMTP_HOST));
			}
			
			String localhost = SystemGlobals.getValue(ConfigKeys.MAIL_SMTP_LOCALHOST);
			
			if (localhost != null && localhost.trim().length() > 0) {
				mailProps.put("mail.smtp.localhost", localhost);
			}
		}*/
		// set the server host. sakai's email/email-impl/impl/src/java/org/sakaiproject/email/impl/BasicEmailService.java is setting this property
		String smtp = System.getProperty(SMTP_HOST);
		if (smtp == null || smtp.trim().length() == 0)
		{
			smtp = SakaiSystemGlobals.getValue(ConfigKeys.MAIL_SMTP_HOST);
		}
		mailProps.put(SMTP_HOST, smtp);
		if (logger.isDebugEnabled()) logger.debug("smtp is : "+ smtp);

		// set the port, if specified
		String smtpPort = System.getProperty(SMTP_PORT);
		if (smtpPort != null)
		{
			mailProps.put(SMTP_PORT, smtpPort);
		}
		if (logger.isDebugEnabled()) logger.debug("smtpPort is : "+ smtpPort);

		// set the mail envelope return address
		// String smtpFrom = System.getProperty(SMTP_FROM);
		String smtpFrom = "\"" + ServerConfigurationService.getString("ui.service", "Sakai") + "\"<no-reply@" + ServerConfigurationService.getServerName() + ">";
		mailProps.put(SMTP_FROM, smtpFrom);
		
		if (logger.isDebugEnabled()) logger.debug("smtpFrom is : "+ smtpFrom);
		
		mailProps.put("mail.mime.address.strict", "false");
		mailProps.put("mail.mime.charset", SystemGlobals.getValue(ConfigKeys.MAIL_CHARSET));
		// mailProps.put("mail.smtp.auth", SystemGlobals.getValue(ConfigKeys.MAIL_SMTP_AUTH));

		// username = SystemGlobals.getValue(ConfigKeys.MAIL_SMTP_USERNAME);
		// password = SystemGlobals.getValue(ConfigKeys.MAIL_SMTP_PASSWORD);

		messageFormat = SystemGlobals.getValue(ConfigKeys.MAIL_MESSSAGE_FORMAT).equals("html") 
			? MESSAGE_HTML
			: MESSAGE_TEXT;

		session = Session.getDefaultInstance(mailProps);
		
		this.testMode = ServerConfigurationService.getBoolean(EMAIL_TESTMODE, false);
	}

	public static Session getSession()
	{
		return session;
	}

	public final Message getMesssage()
	{
		return this.message;
	}

	public boolean dispatchMessages() throws Exception
	{
		Address[] addresses = message.getAllRecipients();
		
		if (this.testMode)
		{
			Address addressFrom = null;
			
			if (message.getFrom() != null && message.getFrom().length > 0)
			{
				addressFrom = message.getFrom()[0];
			}
			
			Address addressReplyTo[] = null;
			if (message.getReplyTo() != null)
			{
				addressReplyTo = message.getReplyTo();
			}
			testSendMail(addressFrom, addresses, message.getSubject(), message.getContent(), addressReplyTo);
			return true;
		}
		
		for (int i = 0; i < addresses.length; i++) {
			message.setRecipient(Message.RecipientType.TO, addresses[i]);
			Transport.send(message, new Address[] { addresses[i] });
		}
		
		/*if (SystemGlobals.getBoolValue(ConfigKeys.MAIL_SMTP_AUTH)) {
			if (username != null && !username.equals("") && password != null && !password.equals("")) {
				Transport transport = Spammer.getSession().getTransport("smtp");

				try {
					String host = SystemGlobals.getValue(ConfigKeys.MAIL_SMTP_HOST);
					if (host != null) {
						int colon = host.indexOf(':');
						if (colon > 0) {
							transport.connect(host.substring(0, colon), Integer.parseInt(host.substring(colon + 1)),
									username, password);
						}
						else {
							transport.connect(host, username, password);
						}
					}
				}
				catch (MessagingException e) {
					throw new EmailException("Could not connect to the mail server", e);
				}

				if (transport.isConnected()) {
					Address[] addresses = message.getAllRecipients();
					for (int i = 0; i < addresses.length; i++) {
						// Tricks and tricks
						message.setRecipient(Message.RecipientType.TO, addresses[i]);
						transport.sendMessage(message, new Address[] { addresses[i] });
					}
				}
				
				transport.close();
			}
		}
		else {
			Address[] addresses = message.getAllRecipients();
			for (int i = 0; i < addresses.length; i++) {
				message.setRecipient(Message.RecipientType.TO, addresses[i]);
				Transport.send(message, new Address[] { addresses[i] });
			}
		}*/

		return true;
	}

	protected final void prepareMessage(List addresses, SimpleHash params, String subject, String messageFile)
			throws EmailException
	{
		this.message = new MimeMessage(session);

		try {
			InternetAddress[] recipients = new InternetAddress[addresses.size()];

			String charset = SystemGlobals.getValue(ConfigKeys.MAIL_CHARSET);

			this.message.setSentDate(new Date());
			// this.message.setFrom(new InternetAddress(SystemGlobals.getValue(ConfigKeys.MAIL_SENDER)));
			String from = "\"" + ServerConfigurationService.getString("ui.service", "Sakai") + "\"<no-reply@" + ServerConfigurationService.getServerName() + ">";
			this.message.setFrom(new InternetAddress(from));
			this.message.setSubject(subject, charset);

			this.messageText = this.getMessageText(params, messageFile);

			if (messageFormat == MESSAGE_HTML) {
				this.message.setContent(this.messageText, "text/html; charset=" + charset);
			}
			else {
				this.message.setText(this.messageText, charset);
			}

			int i = 0;
			for (Iterator iter = addresses.iterator(); iter.hasNext(); i++) {
				recipients[i] = new InternetAddress((String) iter.next());
			}

			this.message.setRecipients(Message.RecipientType.TO, recipients);
		}
		catch (Exception e) {
			logger.warn(e);
			throw new EmailException(e);
		}
	}
	
	/**
	 * prepare attachment message
	 * @param addresses Addresses
	 * @param params Message params
	 * @param subject Message subject
	 * @param messageFile Message file
	 * @param attachments Attachments
	 * @throws EmailException
	 */
	protected final void prepareAttachmentMessage(List addresses, SimpleHash params, String subject, String messageFile, List attachments) throws EmailException
	{
		if (logger.isDebugEnabled()) logger.debug("prepareAttachmentMessage with attachments entering.....");
		
		this.message = new MimeMessage(session);

		try
		{
			InternetAddress[] recipients = new InternetAddress[addresses.size()];

			String charset = SystemGlobals.getValue(ConfigKeys.MAIL_CHARSET);

			this.message.setSentDate(new Date());
			// this.message.setFrom(new InternetAddress(SystemGlobals.getValue(ConfigKeys.MAIL_SENDER)));
			String from = "\"" + ServerConfigurationService.getString("ui.service", "Sakai") + "\"<no-reply@" + ServerConfigurationService.getServerName() + ">";
			this.message.setFrom(new InternetAddress(from));
			this.message.setSubject(subject, charset);

			this.messageText = this.getMessageText(params, messageFile);


			MimeBodyPart messageBodyPart = new MimeBodyPart();
		    
			String messagetype = "";
			
			// message
			if (messageFormat == MESSAGE_HTML)
			{
				messagetype = "text/html; charset=" + charset;
				messageBodyPart.setContent(this.messageText, messagetype);
			} 
			else 
			{
				
				messagetype = "text/plain; charset="+ charset;
				messageBodyPart.setContent(this.messageText, messagetype);
			}
			
			Multipart multipart = new MimeMultipart();

		    multipart.addBodyPart(messageBodyPart);

		    // attachments
		    Attachment attachment = null;
		    Iterator iterAttach = attachments.iterator();
			while (iterAttach.hasNext()) {
				attachment = (Attachment) iterAttach.next();
				// String filePath = SystemGlobals.getValue(ConfigKeys.ATTACHMENTS_STORE_DIR) + "/" + attachment.getInfo().getPhysicalFilename();
				String filePath = SakaiSystemGlobals.getValue(ConfigKeys.ATTACHMENTS_STORE_DIR) + "/" + attachment.getInfo().getPhysicalFilename();
					
			    messageBodyPart = new MimeBodyPart();
			    DataSource source = new FileDataSource(filePath);
			    try
				{
					messageBodyPart.setDataHandler(new DataHandler(source));
					messageBodyPart.setFileName(attachment.getInfo().getRealFilename());

					multipart.addBodyPart(messageBodyPart);
				} catch (MessagingException e)
				{
					if (logger.isWarnEnabled()) logger.warn("Error while attaching attachments in prepareAttachmentMessage(...) : "+ e);
				}
			}

		    message.setContent(multipart);

			int i = 0;
			for (Iterator iter = addresses.iterator(); iter.hasNext(); i++)
			{
				recipients[i] = new InternetAddress((String) iter.next());
			}

			this.message.setRecipients(Message.RecipientType.TO, recipients);
		} catch (Exception e)
		{
			logger.warn(e);
			throw new EmailException(e);
		}
		if (logger.isDebugEnabled()) logger.debug("prepareAttachmentMessage with attachments exiting.....");
	}
	
	/**
	 * Gets the message text to send in the email.
	 * 
	 * @param params The optional params. If no need of any, just pass null
	 * @param messageFile The optional message file to load the text. 
	 * @return The email message text
	 * @throws Exception
	 */
	protected String getMessageText(SimpleHash params, String messageFile) throws Exception
	{
		String templateEncoding = SystemGlobals.getValue(ConfigKeys.MAIL_TEMPLATE_ENCODING);
		
		StringWriter sWriter = new StringWriter();
		
		Template template = null;
		
		if (templateEncoding == null || "".equals(templateEncoding.trim())) {
			template = Configuration.getDefaultConfiguration().getTemplate(messageFile);
		}
		else {
			template = Configuration.getDefaultConfiguration().getTemplate(messageFile, templateEncoding);
		}
		
		template.process(params, sWriter);
		
		return sWriter.toString();
	}

	/**
	 * Gets the email body
	 * 
	 * @return String with the email body that will be sent to the user
	 */
	public String getMessageBody()
	{
		return this.messageText;
	}
	
	protected void testSendMail(Address address, Address[] addresses, String subject, Object content, Address[] addressReplyTo)
	{
		this.logger.info("sendMail: from: " + address + " to: " + arrayToStr(addresses) + " subject: " + subject + " replyTo: " + arrayToStr(addressReplyTo) + " content: " + content);
	}
	
	protected String arrayToStr(Object[] array)
	{
		StringBuffer buf = new StringBuffer();
		if (array != null)
		{
			buf.append("[");
			for (int i = 0; i < array.length; i++)
			{
				if (i != 0) buf.append(", ");
				buf.append(array[i].toString());
			}
			buf.append("]");
		}
		else
		{
			buf.append("");
		}

		return buf.toString();
	}
}
