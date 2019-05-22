package org.sakaiproject.api.app.help;

import org.sakaiproject.entitybroker.entityprovider.EntityProvider;

public interface CodeOfConductEntityProvider extends EntityProvider
{
	public final static String ENTITY_PREFIX = "code_of_conduct";
	public final static String TARGETED_USERS = "student";
	public final static String SEEN_AT_LOGIN = "seenAtLogin";
}
