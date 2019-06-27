package ca.hec.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import ca.hec.listener.EventProcessor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.event.api.Event;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;

public class EventProcessorLogicImpl implements Observer, EventProcessorLogic {
	private static Logger logger = Logger.getLogger(EventProcessorLogicImpl.class);
	
	protected EventProcessingThread eventProcessingThread = new EventProcessingThread();
	protected Queue<EventCopy> eventQueue = new ConcurrentLinkedQueue<EventCopy>();
	protected Object eventQueueLock = new Object();
	
	protected static long EventProcessorThreadId = 0L;

	protected String serverId = null;
	protected boolean loopTimerEnabled = false; //for debugging purposes

	protected Map<String,List<EventProcessor>> eventProcessors = new HashMap<String,List<EventProcessor>>();
	
	private static final String ADMIN = "admin";

	/************************************************************************
	 * Spring-injected classes
	 ************************************************************************/
	
	@Setter protected SakaiProxyImpl sakaiProxy;
	@Setter protected SessionManager sessionManager;


	/************************************************************************
	 * init() and destroy()
	 ************************************************************************/

	public void init() {
		logger.info("init()");
		
		if(serverId == null) {
			serverId = sakaiProxy.getServerId();
		}
		
		if (!sakaiProxy.isEventProcessingThreadDisabled())
		{
			if(this.eventProcessingThread == null) {
				this.eventProcessingThread = new EventProcessingThread();
			}
			this.eventProcessingThread.start();
			
			this.sakaiProxy.addLocalEventListener(this);
			
		}
	}
	
	public void destroy() {
		logger.info("destroy()");
		
		synchronized(eventQueueLock) {
			if(this.eventQueue != null) {
				// empty the event queue 
				this.eventQueue.clear();
				
				// shut down daemon once it's done processing events
				if(this.eventProcessingThread != null) {
					this.eventProcessingThread.close();
					this.eventProcessingThread = null;
				}
				
				// destroy the event queue 
				this.eventQueue = null;
			}
		}
	}

	/************************************************************************
	 * Observer method
	 ************************************************************************/
	public void update(Observable arg0, Object obj) {
		if(obj instanceof Event) {
			Event event = (Event) obj;
			if(getEventProcessors(event.getEvent()) != null) {
				if(logger.isDebugEnabled()) {
					logger.debug("adding event to queue: " + event.getEvent());
				}
				synchronized(this.eventQueueLock) {
					if(this.eventQueue != null) {
						this.eventQueue.add(new EventCopy(event));	
					}
				}
				if(this.eventProcessingThread == null || ! this.eventProcessingThread.isAlive()) {
					if( eventQueue != null) {
						// the update() method gets called if and only if EventProcessorLogic is registered as an observer.
						// EventProcessorLogic is registered as an observer if and only if event processing is enabled.
						// So if the eventProcessingThread is null or disabled in some way, we should restart it, 
						// unless the eventQueue is null, which should happen if and only if we are shutting down.
						this.eventProcessingThread = null;
						this.eventProcessingThread = new EventProcessingThread();
						this.eventProcessingThread.start();
					}
				}
			}
		}
	}

	/************************************************************************
	 * Event processing : processors
	 ************************************************************************/
	/* (non-Javadoc)
	 * @see ca.hec.logic.EventProcessorLogic#registerEventProcessor(ca.hec.listener.EventProcessor)
	 */
	@Override
	public void registerEventProcessor(EventProcessor eventProcessor) {
		registerEventProcessor(null, eventProcessor);
		
	}
		
	/* (non-Javadoc)
	 * @see ca.hec.logic.EventProcessorLogic#registerEventProcessor(java.lang.String, ca.hec.listener.EventProcessor)
	 */
	@Override
	public void registerEventProcessor(String eventId, EventProcessor eventProcessor) {
		logger.debug("Registering : "+eventId+" -> "+eventProcessor.getClass().getSimpleName()+" => "+sakaiProxy.isEventProcessingListenerEnabled(eventProcessor));
		if(sakaiProxy.isEventProcessingListenerEnabled(eventProcessor)) {
			if(StringUtils.isBlank(eventId)){
				eventId = eventProcessor.getEventIdentifer();
			}
			if(eventProcessor != null && eventId != null) {
				List processorsList = this.eventProcessors.get(eventId);
				if(processorsList == null) {
					processorsList = new ArrayList<EventProcessor>();
				}
				processorsList.add(eventProcessor);
				this.eventProcessors.put(eventId, processorsList);
			}
		}
		
	}
	
	/* (non-Javadoc)
	 * @see ca.hec.logic.EventProcessorLogic#getEventProcessor(java.lang.String)
	 */
	@Override
	public List<EventProcessor> getEventProcessors(String eventIdentifier) {
		return this.eventProcessors.get(eventIdentifier);
	}

	
	/************************************************************************
	 * Event processing daemon (or thread?)
	 ************************************************************************/

	public class EventProcessingThread extends Thread
	{
		protected static final String EVENT_PROCESSING_THREAD_SHUT_DOWN_MESSAGE = 
			"\n===================================================\n  Event Processing Thread shutting down  \n===================================================";

		protected boolean timeToQuit = false;
		
		protected long loopTimer = 0L;
		protected String loopActivity = "";
		
		private long sleepTime = 2L;

		public EventProcessingThread() {
			super("Event Processing Thread");
			logger.info("Created Event Processing Thread");
			
			this.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
				public void uncaughtException(Thread arg0, Throwable arg1) {
					logger.error(EVENT_PROCESSING_THREAD_SHUT_DOWN_MESSAGE, arg1);
				}
			});
		}

		public void close() {
			timeToQuit = true;
		}

		public void run() {
			// wait till ComponentManager is ready
			ComponentManager.waitTillConfigured();

			try {
				EventProcessorThreadId = Thread.currentThread().getId();
				logger.info("Started Event Processing Thread: " + EventProcessorThreadId);
								
				sakaiProxy.startAdminSession();
				while(! timeToQuit) {
					if(loopTimerEnabled) {
						loopTimer = System.currentTimeMillis();
						loopActivity = "nothing";
					}
//					if(logger.isDebugEnabled()) {
//						logger.debug("Event Processing Thread checking event queue: " + eventQueue.size());
//					}
					//pop event from queue
					EventCopy event = null;
					synchronized(eventQueueLock) {
						if(eventQueue != null && ! eventQueue.isEmpty()) {
							event = eventQueue.poll();
						}
					}
					
					if(event != null) {
						if(loopTimerEnabled) {
							loopActivity = "processingEvents";
						}
						if(logger.isDebugEnabled()) {
							logger.debug("Event Processing Thread is processing event: " + event.getEvent());
						}
						//get list of event processors based on event ID
						List<EventProcessor> eventProcessorsList = getEventProcessors(event.getEvent());
						//create new thread to process the event for each listener
						for(EventProcessor ep : eventProcessorsList) {
							ListenerProcessingThread listenerProcessingThread = new ListenerProcessingThread(ep, event);
							listenerProcessingThread.start();
						}
						
						if(loopTimerEnabled) {
							long elapsedTime = System.currentTimeMillis() - loopTimer;
							StringBuilder buf = new StringBuilder("EventProcessingThread.activityTimer\t");
							buf.append(loopTimer);
							buf.append("\t");
							buf.append(elapsedTime);
							buf.append("\t");
							buf.append(loopActivity);
							logger.info(buf.toString());
						}
					}
					
					if(eventQueue == null || eventQueue.isEmpty()) {
						try {
							Thread.sleep(sleepTime * 1000L);
						} catch (InterruptedException e) {
							logger.warn("InterruptedException in Event Processing Thread: " + e);
						}
					}
				}
				
				logger.warn(EVENT_PROCESSING_THREAD_SHUT_DOWN_MESSAGE);
				
			} catch(Throwable t) {
				logger.error("Unhandled throwable is stopping Event Processing Thread", t);
				throw new RuntimeException(t);
			}   
		}
	}

	public class ListenerProcessingThread extends Thread {
		private EventProcessor eventProcessor;
		private EventCopy event;
		
		public ListenerProcessingThread(EventProcessor eventProcessor, EventCopy event){
			this.eventProcessor = eventProcessor;
			this.event = event;
		}
		
		public void run() {
			SecurityAdvisor advisor = new LogicSecurityAdvisor();
			Session sakaiSession = sessionManager.getCurrentSession();
			try {
				sakaiSession.setUserId(ADMIN);
				sakaiSession.setUserEid(ADMIN);

				sakaiProxy.pushSecurityAdvisor(advisor);
				eventProcessor.processEvent(event);
			} catch (Exception e) {
				logger.warn("Error processing event: " + event, e);
			} finally {
				sakaiProxy.popSecurityAdvisor(advisor);
				sakaiProxy.clearThreadLocalCache();
				sakaiSession.setUserId(null);
				sakaiSession.setUserEid(null);
			}
		}
	}

	public class LogicSecurityAdvisor implements SecurityAdvisor 
	{
		/**
		 */
		public LogicSecurityAdvisor() {
			super();
		}

		/*
		 * (non-Javadoc)
		 * @see org.sakaiproject.authz.api.SecurityAdvisor#isAllowed(java.lang.String, java.lang.String, java.lang.String)
		 */
		public SecurityAdvice isAllowed(String userId, String function, String reference) {

			long threadId = Thread.currentThread().getId();

			if(threadId == EventProcessorLogicImpl.EventProcessorThreadId) {
				return SecurityAdvice.ALLOWED;
			}
			return SecurityAdvice.PASS;
		}
	}
}
