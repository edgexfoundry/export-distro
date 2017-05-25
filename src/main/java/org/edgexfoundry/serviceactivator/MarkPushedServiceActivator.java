/*******************************************************************************
 * Copyright 2017 Dell Inc.
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
 * @microservice:  export-distro
 * @author: Jim White, Dell
 * @version: 1.0.0
 *******************************************************************************/
package org.edgexfoundry.serviceactivator;

import org.edgexfoundry.controller.EventClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;

/**
 * Sends request to core data to mark the event and its readings as posted
 * 
 * @author jim_white
 *
 */
@MessageEndpoint
public class MarkPushedServiceActivator {

	@Autowired
	private EventClient eventClient;

	// TODO - someday deal with fact that some clients may not have received if
	// message failed during their processing. Also deal with fact that message
	// may be filtered by all and therefore never marked as sent.
	// private static final Logger logger =
	// Logger.getLogger(MarkPushedServiceActivator.class);
	// replace above logger with EdgeXLogger below
	private final static org.edgexfoundry.support.logging.client.EdgeXLogger logger = org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
			.getEdgeXLogger(MarkPushedServiceActivator.class);

	@ServiceActivator(inputChannel = "mark-outboud")
	public void mark(Message<?> msg) {
		try {
			String id = (String) msg.getPayload();
			if (eventClient.markedPushed(id))
				logger.info("Marked event & associated readings read for event id:  " + id);
			else
				logger.error("Problem marking event & associated readings pushed for event id:  " + id);
		} catch (Exception e) {
			logger.error("Problems contacting core data to mark event pushed for event:  " + msg.getPayload());
		}
	}
}
