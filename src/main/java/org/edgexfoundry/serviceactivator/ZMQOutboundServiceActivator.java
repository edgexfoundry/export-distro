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
import org.edgexfoundry.messaging.ZeroMQSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;

/**
 * Send event data string to ZMQ address.
 * 
 * @author jim_white
 *
 */
@MessageEndpoint
public class ZMQOutboundServiceActivator {

	// private static final Logger logger =
	// Logger.getLogger(ZMQOutboundServiceActivator.class);
	// replace above logger with EdgeXLogger below
	private final static org.edgexfoundry.support.logging.client.EdgeXLogger logger = org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
			.getEdgeXLogger(ZMQOutboundServiceActivator.class);

	@Autowired
	private ZeroMQSender sendor;

	@ServiceActivator(inputChannel = "outbound-zmq", outputChannel = "mark-outboud")
	public String zmqOutbound(Message<?> msg) {
		try {
			ExportString exportString = (ExportString) msg.getPayload();
			logger.debug("message arrived at 0MQ outbound sender: " + exportString.getEventId());
			// today, for ZMQ, subscribers will listen to pre-subscribed EdgeX
			// ZMQ outbound port.
			// TODO - someday, reverse the direction and allow clients to set up
			// individual ZMQ port to publish out of
			logger.debug(".....sending: " + exportString.getEventString());
			sendor.sendEventMessage(exportString.getEventString());
			// logger.error("--->" + exportString.getEventId() + " distro-send@
			// " + System.currentTimeMillis());
			logger.info("message sent via 0MQ " + exportString.getEventId());
			return exportString.getEventId();
		} catch (Exception e) {
			logger.error("Problem with sending message via 0MQ: " + e.getMessage());
		}
		return null;
	}

	public ZeroMQSender getSendor() {
		return sendor;
	}

	public void setSendor(ZeroMQSender sendor) {
		this.sendor = sendor;
	}

}
