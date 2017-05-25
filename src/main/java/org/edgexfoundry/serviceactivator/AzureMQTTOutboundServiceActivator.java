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
import org.edgexfoundry.messaging.AzureMQTTSender;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;

@MessageEndpoint
public class AzureMQTTOutboundServiceActivator {

	private final static org.edgexfoundry.support.logging.client.EdgeXLogger logger = org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
			.getEdgeXLogger(AzureMQTTOutboundServiceActivator.class);

	@ServiceActivator(inputChannel = "outbound-azure", outputChannel = "mark-outboud")
	public String mqttOutbound(Message<?> msg) {
		try {
			ExportString exportString = (ExportString) msg.getPayload();
			logger.debug("message arrived at Azure MQTT outbound sender: " + exportString.getEventId());
			Addressable addressable = exportString.getRegistration().getAddressable();
			if (addressable != null) {
				// TODO - someday cache and reuse clients
				AzureMQTTSender sender = new AzureMQTTSender(exportString.getRegistration().getAddressable(),
						exportString.getDeviceId());
				sender.sendMessage(exportString.getEventString().getBytes());
				logger.info("message sent to Azure MQTT broker:  " + exportString.getRegistration().getAddressable()
						+ " : " + exportString.getEventId());
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
