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
package org.edgexfoundry.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zeromq.ZMQ;

@Component
public class ZeroMQSender {

	// private static final Logger logger =
	// Logger.getLogger(ZeroMQSender.class);
	// replace above logger with EdgeXLogger below
	private final static org.edgexfoundry.support.logging.client.EdgeXLogger logger = org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
			.getEdgeXLogger(ZeroMQSender.class);
	private static final long PUB_UP_SLEEP = 1000;

	@Value("${outbound.zeromq.address}")
	private String outbound0MQAddress;

	private ZMQ.Socket publisher;
	private ZMQ.Context context;

	{
		context = ZMQ.context(1);
	}

	/**
	 * Send event string (in whatever form - JSON, XML, etc.) to client 0MQ
	 * topic
	 * 
	 * @param payload
	 *            - event String in whatever format, compressed, encrypted, etc.
	 *            per client need
	 */
	public void sendEventMessage(String payload) {
		try {
			if (publisher == null)
				getPublisher();
			if (publisher != null) {
				publisher.send(payload.getBytes(), 0);
				logger.debug("Sent export payload to 0MQ client:");
			} else
				logger.error("Issue sending payload to 0MQ client:");
		} catch (Exception e) {
			logger.error("Unable to send message via ZMQ");
		}
	}

	// allow only one thread at a time to get the publisher set up
	private synchronized void getPublisher() {
		try {
			if (publisher == null) {
				publisher = context.socket(ZMQ.PUB);
				publisher.bind(outbound0MQAddress);
				Thread.sleep(PUB_UP_SLEEP); // allow subscribers to connect
			}
		} catch (Exception e) {
			logger.error("Unable to get an outbound 0MQ publisher.  Error:  " + e);
			publisher = null;
		}
	}

	public String getOutbound0MQAddress() {
		return outbound0MQAddress;
	}

	public void setOutbound0MQAddress(String outbound0mqAddress) {
		outbound0MQAddress = outbound0mqAddress;
	}

}
