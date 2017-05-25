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

import org.edgexfoundry.domain.export.ExportString;
import org.edgexfoundry.domain.meta.Addressable;
import org.springframework.http.HttpMethod;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.http.outbound.HttpRequestExecutingMessageHandler;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

/**
 * Send export data to REST channel of client's desire.
 * 
 * @author jim_white
 *
 */
@MessageEndpoint
public class RESTOutboundServiceActivator {

	// private static final Logger logger =
	// Logger.getLogger(RESTOutboundServiceActivator.class);
	// replace above logger with EdgeXLogger below
	private final static org.edgexfoundry.support.logging.client.EdgeXLogger logger = org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
			.getEdgeXLogger(RESTOutboundServiceActivator.class);

	@ServiceActivator(inputChannel = "outbound-rest", outputChannel = "mark-outboud")
	public String restOutbound(Message<?> msg) {
		try {
			ExportString exportString = (ExportString) msg.getPayload();
			logger.debug("message arrived at REST outbound sender: " + exportString.getEventId());
			Addressable addressable = exportString.getRegistration().getAddressable();
			String uri = addressable.getAddress() + ":" + addressable.getPort() + addressable.getPath();
			HttpRequestExecutingMessageHandler handler = new HttpRequestExecutingMessageHandler(uri);
			handler.setHttpMethod(HttpMethod.POST);
			handler.setExpectReply(false);
			Message<String> message = MessageBuilder.withPayload(exportString.getEventString()).build();
			handler.handleMessage(message);
			logger.info("message sent to REST address:  " + uri + " : " + exportString.getEventId());
			return exportString.getEventId();
		} catch (Exception e) {
			logger.error("Problem with sending message via REST: " + e.getMessage());
			return null;
		}
	}

}
