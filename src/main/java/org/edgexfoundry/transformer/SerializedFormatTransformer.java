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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Base64;

import org.edgexfoundry.domain.core.Event;
import org.edgexfoundry.domain.export.ExportMessage;
import org.edgexfoundry.domain.export.ExportString;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;

/**
 * Transform ExportMessage containing export client registration information and
 * Event object from core data into ExportString containing export client
 * registration information and event in serialized string format.
 * 
 * @author Jim
 *
 */
@MessageEndpoint
public class SerializedFormatTransformer {

	// private static final Logger logger =
	// Logger.getLogger(SerializedFormatTransformer.class);
	// replace above logger with EdgeXLogger below
	private final static org.edgexfoundry.support.logging.client.EdgeXLogger logger = org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
			.getEdgeXLogger(SerializedFormatTransformer.class);

	@Transformer(inputChannel = "serial-inbound-eventmessages", outputChannel = "outbound-eventstring")
	public ExportString transform(Message<?> msg) throws Exception {
		try {
			ExportMessage exportMsg = (ExportMessage) msg.getPayload();
			String eventId = exportMsg.getEvent().getId();
			logger.debug("message arrived at serialized format transformer: " + eventId);
			ExportString export = new ExportString();
			export.setRegistration(exportMsg.getRegistration(), eventId);
			export.setEventString(toString(exportMsg.getEvent()));
			logger.debug("message leaving Serialized format transformer " + eventId);
			return export;
		} catch (Exception e) {
			logger.error("Problem with Serialized format transformation: " + e.getMessage());
			throw e;
		}
	}

	/**
	 * Serialize the event to a string
	 * 
	 * @param Event
	 * @return serialized byte array
	 * @throws IOException
	 */
	private String toString(Event event) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try (ObjectOutput out = new ObjectOutputStream(bos)) {
			out.writeObject(event);
			return Base64.getEncoder().encodeToString(bos.toByteArray());
		}
	}
}
