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

import org.edgexfoundry.domain.export.ExportDestination;
import org.edgexfoundry.domain.export.ExportString;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Router;

/**
 * Routes to appropriate channel based on client's requested distribution
 * mechanism (example MQTT, REST, etc.)
 * 
 * @author jim_white
 *
 */
@MessageEndpoint
public class DistributionRouter {

	// private static final Logger logger =
	// Logger.getLogger(DistributionRouter.class);
	// replace above logger with EdgeXLogger below
	private final static org.edgexfoundry.support.logging.client.EdgeXLogger logger = org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
			.getEdgeXLogger(DistributionRouter.class);

	public static final String OUT_REST = "outbound-rest";
	public static final String OUT_MQTT = "outbound-mqtt";
	public static final String OUT_ZMQ = "outbound-zmq";
	public static final String OUT_AZURE = "outbound-azure";
	public static final String OUT_IOTCORE = "outbound-iotcore";

	@Router(inputChannel = "outbound-encrypted-eventstring", resolutionRequired = "true")
	public String route(ExportString message) throws Exception {
		logger.debug("message arrived at distribution router: " + message.getEventId());
		ExportDestination destination = message.getRegistration().getDestination();
		switch (destination) {
		case REST_ENDPOINT:
			return OUT_REST;
		case MQTT_TOPIC:
			return OUT_MQTT;
		case ZMQ_TOPIC:
			return OUT_ZMQ;
		case IOTCORE_MQTT:
			return OUT_IOTCORE;
		case AZURE_MQTT:
			return OUT_AZURE;
		default:
			throw new Exception("Destination not available in distribution router");
		}
	}

}
