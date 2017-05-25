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

import org.edgexfoundry.domain.core.Event;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;

/**
 * Handle rejected inbound events.
 * 
 * @author jim_white
 *
 */
@MessageEndpoint
public class RejectedEventsServiceActivator {

	// TODO - someday send and alert/notification when data is being rejected.

	// private static final Logger logger =
	// Logger.getLogger(RejectedEventsServiceActivator.class);
	// replace above logger with EdgeXLogger below
	private final static org.edgexfoundry.support.logging.client.EdgeXLogger logger = org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
			.getEdgeXLogger(RejectedEventsServiceActivator.class);

	@SuppressWarnings("unchecked")
	@ServiceActivator(inputChannel = "invalid-inbound-events")
	public void displayMessage(Message<?> msg) {
		Message<Event> eventMsg = (Message<Event>) msg;
		Event event = eventMsg.getPayload();
		logger.error("Rejected Event:  " + event);
	}

}
