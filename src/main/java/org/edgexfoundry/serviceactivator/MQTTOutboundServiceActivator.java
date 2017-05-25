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
import org.edgexfoundry.messaging.MQTTSender;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;

/**
 * Send event data string to client's requested MQTT address.
 * 
 * @author jim_white
 *
 */
@MessageEndpoint
public class MQTTOutboundServiceActivator {

	// private static final Logger logger =
	// Logger.getLogger(MQTTOutboundServiceActivator.class);
	// replace above logger with EdgeXLogger below
	private final static org.edgexfoundry.support.logging.client.EdgeXLogger logger = org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
			.getEdgeXLogger(MQTTOutboundServiceActivator.class);

	@ServiceActivator(inputChannel = "outbound-mqtt", outputChannel = "mark-outboud")
	public String mqttOutbound(Message<?> msg) {
		try {
			ExportString exportString = (ExportString) msg.getPayload();
			logger.debug("message arrived at MQTT outbound sender: " + exportString.getEventId());
			Addressable addressable = exportString.getRegistration().getAddressable();
			if (addressable != null) {
				// TODO - someday cache and reuse clients
				MQTTSender sender = new MQTTSender(exportString.getRegistration().getAddressable());
				sender.sendMessage(exportString.getEventString().getBytes());
				sender.closeClient();
				logger.info("message sent to MQTT broker:  " + exportString.getRegistration().getAddressable() + " : "
						+ exportString.getEventId());
				return exportString.getEventId();
			} else
				logger.error(
						"No MQTT address information provided with registration.  Event message not sent for client.");
		} catch (Exception e) {
			logger.error("Problem with sending message via MQTT: " + e.getMessage());
		}
		return null;
	}

}
