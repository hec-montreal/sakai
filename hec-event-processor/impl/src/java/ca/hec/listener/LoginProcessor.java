package ca.hec.listener;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import ca.hec.logic.EventProcessorLogic;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import org.sakaiproject.authz.api.Member;
import org.sakaiproject.event.api.Event;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.tool.api.SessionManager;

public class LoginProcessor implements EventProcessor {

	private static Logger logger = Logger.getLogger(LoginProcessor.class);

	private static final String EVENT_ID = "user.login";

	@Setter protected EventProcessorLogic eventProcessorLogic;

	@Setter protected SessionManager sessionManager;

	public String getEventIdentifer() {
		return EVENT_ID;
	}

	synchronized public void processEvent(Event event) {
		
		if(logger.isDebugEnabled()) {
			logger.debug("\n\n\n=============================================================\n" + event  
					+ "\n=============================================================\n\n\n");
		}

		DateFormat df =
				new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm");

		String userEid = sessionManager.getCurrentSession().getUserEid();
		if (userEid != null) {
			logger.info("user [" + userEid + "] login " + df.format(new Date()));
		} 
	}

	public void init() {
		this.eventProcessorLogic.registerEventProcessor(EVENT_ID, this);
	}
}
