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

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.edgexfoundry.domain.core.Event;
import org.edgexfoundry.domain.export.ExportMessage;
import org.edgexfoundry.domain.export.ExportString;
import org.edgexfoundry.jaxb.JAXBEvent;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;

/**
 * Transform ExportMessage containing export client registration information and
 * Event object from core data into ExportString containing export client
 * registration information and event in XML string format.
 * 
 * @author Jim
 *
 */
@MessageEndpoint
public class XMLFormatTransformer {

	// private static final Logger logger =
	// Logger.getLogger(XMLFormatTransformer.class);
	// replace above logger with EdgeXLogger below
	private final static org.edgexfoundry.support.logging.client.EdgeXLogger logger = org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
			.getEdgeXLogger(XMLFormatTransformer.class);

	@Transformer(inputChannel = "xml-inbound-eventmessages", outputChannel = "outbound-eventstring")
	public ExportString transform(Message<?> msg) throws JAXBException {
		try {
			ExportMessage exportMsg = (ExportMessage) msg.getPayload();
			String eventId = exportMsg.getEvent().getId();
			logger.debug("message arrived at XML format transformer: " + eventId);
			ExportString export = new ExportString();
			export.setRegistration(exportMsg.getRegistration(), eventId);
			export.setEventString(eventToXML(exportMsg.getEvent()));
			logger.debug("message leaving XML format transformer " + eventId);
			return export;
		} catch (Exception e) {
			logger.error("Problem with XML format transformation: " + e.getMessage());
			throw e;
		}
	}

	private String eventToXML(Event event) throws JAXBException {
		JAXBEvent jaxbEvent = new JAXBEvent(event);
		StringWriter xmlString = new StringWriter();
		JAXBContext context = JAXBContext.newInstance(JAXBEvent.class);
		Marshaller marshallerObj = context.createMarshaller();
		marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshallerObj.marshal(jaxbEvent, xmlString);
		return xmlString.toString();
	}
}
