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

import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;

/**
 * Global Spring Integration exception capture. Logs any unanticipated message
 * exceptions captured in errorChannel.
 * 
 * @author jim_white
 *
 */
@MessageEndpoint
public class GlobalErrorServiceActivator {

	//private static final Logger logger = Logger.getLogger(GlobalErrorServiceActivator.class);
	//replace above logger with EdgeXLogger below
	private final static org.edgexfoundry.support.logging.client.EdgeXLogger logger = org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
			.getEdgeXLogger(GlobalErrorServiceActivator.class);

	@ServiceActivator(inputChannel = "errorChannel")
	public void displayMessage(Message<?> msg) {
		logger.error("Unanticipated issue:  " + msg);
	}

}
