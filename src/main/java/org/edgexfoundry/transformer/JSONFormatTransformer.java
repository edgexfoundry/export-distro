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
package org.edgexfoundry.transformer;

import org.edgexfoundry.domain.core.Event;
import org.edgexfoundry.domain.export.ExportMessage;
import org.edgexfoundry.domain.export.ExportString;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;

import com.google.gson.Gson;

/**
 * Transform ExportMessage containing export client registration information and
 * Event object from core data into ExportString containing export client
 * registration information and event in JSON string format.
 * 
 * @author Jim
 *
 */
@MessageEndpoint
public class JSONFormatTransformer {

	// private static final Logger logger =
	// Logger.getLogger(JSONFormatTransformer.class);
	// replace above logger with EdgeXLogger below
	private final static org.edgexfoundry.support.logging.client.EdgeXLogger logger = org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
			.getEdgeXLogger(JSONFormatTransformer.class);
	private static Gson gson = new Gson();

	@Transformer(inputChannel = "json-inbound-eventmessages", outputChannel = "outbound-eventstring")
	public ExportString transformJSON(Message<?> msg) {
		try {
			ExportMessage exportMsg = (ExportMessage) msg.getPayload();
			String eventId = exportMsg.getEvent().getId();
			logger.debug("message arrived at JSON format transformer: " + eventId);
			ExportString export = new ExportString();
			export.setRegistration(exportMsg.getRegistration(), eventId);
			export.setEventString(eventToJSON(exportMsg.getEvent()));
			logger.debug("message leaving JSON format transformer " + eventId);
			return export;
		} catch (Exception e) {
			logger.error("Problem with JSON format transformation: " + e.getMessage());
			throw e;
		}
	}

	@Transformer(inputChannel = "iotcore-json-inbound-eventmessages", outputChannel = "outbound-eventstring")
	public ExportString transformIotCoreJSON(Message<?> msg) {
		try {
			ExportMessage exportMsg = (ExportMessage) msg.getPayload();
			String eventId = exportMsg.getEvent().getId();
			logger.debug("message arrived at IoT Core JSON format transformer: " + eventId);
			ExportString export = new ExportString();
			export.setRegistration(exportMsg.getRegistration(), eventId);
			export.setEventString(eventToJSON(exportMsg.getEvent()));
			logger.debug("message leaving IoT Core JSON format transformer " + eventId);
			return export;
		} catch (Exception e) {
			logger.error("Problem with IoT Core JSON format transformation: " + e.getMessage());
			throw e;
		}
	}

	@Transformer(inputChannel = "azure-json-inbound-eventmessages", outputChannel = "outbound-eventstring")
	public ExportString transformAzureJSON(Message<?> msg) {
		try {
			ExportMessage exportMsg = (ExportMessage) msg.getPayload();
			String eventId = exportMsg.getEvent().getId();
			logger.debug("message arrived at Azure JSON format transformer: " + eventId);
			ExportString export = new ExportString();
			export.setRegistration(exportMsg.getRegistration(), eventId);
			export.setEventString(eventToAzureJSON(exportMsg.getEvent()));
			export.setDeviceId(exportMsg.getEvent().getDevice());
			logger.debug("message leaving Azure JSON format transformer " + eventId);
			return export;
		} catch (Exception e) {
			logger.error("Problem with Azure JSON format transformation: " + e.getMessage());
			throw e;
		}
	}

	private String eventToJSON(Event event) {
		return gson.toJson(event);
	}

	private String eventToAzureJSON(Event event) {
		// TODO write custom serializer
		StringBuffer buffer = new StringBuffer();
		buffer.append("{'deviceId':'");
		buffer.append(event.getDevice());
		buffer.append("'");
		if (event.getReadings() != null)
			event.getReadings().stream().parallel().forEach(r -> {
				buffer.append(",'");
				buffer.append(r.getName());
				buffer.append("':'");
				buffer.append(r.getValue());
				buffer.append("'");
			});
		buffer.append("}");
		return buffer.toString();
	}

}
