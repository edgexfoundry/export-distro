/*******************************************************************************
 * Copyright 2017 Schlumberger
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
 * @author: Bernard Van Haecke, Schlumberger
 * @version: 1.0.0
 *******************************************************************************/
package org.edgexfoundry.serviceactivator;

import org.edgexfoundry.domain.export.ExportString;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.messaging.IotCoreMQTTSender;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.beans.factory.annotation.Value;

@MessageEndpoint
public class IotCoreMQTTOutboundServiceActivator {

	private final static org.edgexfoundry.support.logging.client.EdgeXLogger logger = org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
			.getEdgeXLogger(IotCoreMQTTOutboundServiceActivator.class);

	@Value("${outbound.iotcore.privatekeyfile}")
	private String privateKeyFile;

	@Value("${outbound.iotcore.algorithm}")
	private String algorithm;
	
	IotCoreMQTTSender sender;
	
	@ServiceActivator(inputChannel = "outbound-iotcore", outputChannel = "mark-outboud")
	public String mqttOutbound(Message<?> msg) {
		try {
			ExportString payload = (ExportString) msg.getPayload();
			logger.debug("message arrived at IoT Core MQTT outbound sender: " + payload.getEventId());
			Addressable addressable = payload.getRegistration().getAddressable();
			if (addressable != null) {
				// TODO - cache and reuse clients per clientId
				// String clientId = addressable.getPublisher();

				if (sender == null) {
					sender = new IotCoreMQTTSender(payload.getRegistration().getAddressable(), privateKeyFile, algorithm, 0, 600);
				}
				boolean ok = sender.sendMessage(payload.getEventString().getBytes());
				if (!ok) throw new Exception("error while sending message");
				logger.info("message sent to IoT Core MQTT broker:  " + payload.getRegistration().getAddressable() + " : " + payload.getEventId());
				return payload.getEventId();
			} else {
				logger.error("No MQTT address information provided with registration.  Event message not sent for client.");
			}
		} catch (Exception e) {
			logger.error("Problem when sending message via MQTT: " + e.getMessage());
		}
		return null;
	}
}
