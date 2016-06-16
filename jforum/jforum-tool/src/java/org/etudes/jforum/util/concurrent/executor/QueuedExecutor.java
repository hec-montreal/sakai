/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/util/concurrent/executor/QueuedExecutor.java $ 
 * $Id: QueuedExecutor.java 55476 2008-12-01 19:16:20Z murthy@etudes.org $ 
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
package org.etudes.jforum.util.concurrent.executor;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.util.concurrent.Executor;
import org.etudes.jforum.util.concurrent.Queue;
import org.etudes.jforum.util.concurrent.Result;
import org.etudes.jforum.util.concurrent.Task;
import org.etudes.jforum.util.concurrent.queue.UnboundedFifoQueue;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SystemGlobals;

/**
 * @author Rodrigo Kumpera
 */
public class QueuedExecutor implements Executor 
{
	private Thread currentThread;
	private final Queue queue;
	private final Object lock = new Object();
	private static final Log logger = LogFactory.getLog(QueuedExecutor.class);
	
	private static QueuedExecutor instance = new QueuedExecutor(new UnboundedFifoQueue());
	
	private QueuedExecutor(Queue queue) 
	{
		logger.info("Setting queue...");
		this.queue = queue;
	}
	
	public static QueuedExecutor getInstance()
	{
		return instance;
	}
	
	private class WorkerThread extends AbstractWorker 
	{
		protected Object take() throws InterruptedException 
		{
			return queue.get();
		}
		
		protected void cleanup() 
		{
			synchronized(lock) {
				currentThread = null;
				logger.info("Cleaning up the thread...");
			}
		}
	}

	public void execute(Task task) throws InterruptedException 
	{
		if (SystemGlobals.getBoolValue(ConfigKeys.BACKGROUND_TASKS)) {
			queue.put(task);
			synchronized(lock) {
				if(currentThread == null) {
					logger.info("Creating a new thread...");
					
					currentThread = new Thread(new WorkerThread(), "jforum");
					currentThread.setDaemon(true);
					currentThread.start();	
				}
			}
		}
		else {
			try {
				task.execute();
			}
			catch (Exception e) {
				logger.warn("Error while executing a task: " + e);
			}
		}
	}
	
	public Result executeWithResult(Task task) throws InterruptedException 
	{
		SimpleResult result = new SimpleResult(task);
		queue.put(result);
		
		synchronized(lock) {
			if(currentThread == null) {
				currentThread = new Thread(new WorkerThread(), "jforum");
				currentThread.setDaemon(true);
				currentThread.setName(this.getClass().getName() + "Thread");

				currentThread.start();	
			}
		}

		return result;
	}
}
