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
package org.edgexfoundry.router;

import org.edgexfoundry.domain.export.ExportFormat;
import org.edgexfoundry.domain.export.ExportMessage;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Router;

/**
 * Routes to the event message to appropriate formatting transformer based on
 * client registered export format type (JSON, XML, etc.)
 * 
 * @author jim_white
 *
 */
@MessageEndpoint
public class FormatRouter {

	public static final String OUT_XML = "xml-inbound-eventmessages";
	public static final String OUT_JSON = "json-inbound-eventmessages";
	public static final String OUT_SERIALIZED = "serial-inbound-eventmessages";
	public static final String OUT_AZURE = "azure-json-inbound-eventmessages";
	public static final String OUT_IOTCORE = "iotcore-json-inbound-eventmessages";

	// private static final Logger logger =
	// Logger.getLogger(FormatRouter.class);
	// replace above logger with EdgeXLogger below
	private final static org.edgexfoundry.support.logging.client.EdgeXLogger logger = org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
			.getEdgeXLogger(FormatRouter.class);

	@Router(inputChannel = "reading-filtered-eventmessages", resolutionRequired = "true")
	public String route(ExportMessage message) throws Exception {
		logger.debug("message arrived at format router:" + message.getEvent().getId());
		ExportFormat format = message.getRegistration().getFormat();
		if (format != null) {
			switch (format) {
			case JSON:
				return OUT_JSON;
			case XML:
				return OUT_XML;
			case SERIALIZED:
				return OUT_SERIALIZED;
			case IOTCORE_JSON:
				return OUT_IOTCORE;
			case AZURE_JSON:
				return OUT_AZURE;
			case CSV: // TODO - someday handle CSV format
				break;
			}
		}
		throw new Exception("Format requested not available");
	}
}
