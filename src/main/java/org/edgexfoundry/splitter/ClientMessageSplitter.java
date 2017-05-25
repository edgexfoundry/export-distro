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
package org.edgexfoundry.splitter;

import java.util.List;
import java.util.stream.Collectors;

import org.edgexfoundry.dao.ExportRegistrationRepository;
import org.edgexfoundry.domain.core.Event;
import org.edgexfoundry.domain.export.ExportMessage;
import org.edgexfoundry.domain.export.ExportRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Splitter;
import org.springframework.integration.splitter.AbstractMessageSplitter;
import org.springframework.messaging.Message;

/**
 * Takes the Event message and creates new ExportMessages (replicas) containing
 * the event and client registration information (one new ExportMessage per
 * client registration) and pushes these back into the EAI (pipe-filter)
 * channel.
 * 
 */
@MessageEndpoint
public class ClientMessageSplitter extends AbstractMessageSplitter {

	// private static final Logger logger =
	// Logger.getLogger(ClientMessageSplitter.class);
	// replace above logger with EdgeXLogger below
	private final static org.edgexfoundry.support.logging.client.EdgeXLogger logger = org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
			.getEdgeXLogger(ClientMessageSplitter.class);

	@Autowired
	ExportRegistrationRepository repos;

	@SuppressWarnings("unchecked")
	@Override
	@Splitter(inputChannel = "valid-inbound-events", outputChannel = "inbound-eventmessages")
	public Object splitMessage(Message<?> msg) {
		try {
			Message<Event> eventMsg = (Message<Event>) msg;
			Event event = eventMsg.getPayload();
			logger.debug("message arrived at client message splitter: " + event.getId());
			List<ExportRegistration> clients = getClients();
			Object o = clients.stream().map(c -> createExportMessage(c, event)).collect(Collectors.toList());
			logger.debug("message leaving client message splitter: " + event.getId());
			return o;
		} catch (Exception e) {
			logger.error("problem splitting messages per client request:  " + e.getMessage());
			throw e;
		}
	}

	private List<ExportRegistration> getClients() {
		return repos.findByEnable(true);
	}

	private ExportMessage createExportMessage(ExportRegistration registration, Event event) {
		return new ExportMessage(registration, event);
	}

}
