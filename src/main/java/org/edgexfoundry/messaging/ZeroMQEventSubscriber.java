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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;

import org.edgexfoundry.domain.core.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zeromq.ZMQ;

/**
 * Core Data message ingestion bean - gets messages out of ZeroMQ. Export
 * service message origination point
 * 
 * @author jim_white
 *
 */
@Component
public class ZeroMQEventSubscriber implements EventSubscriber {

	// private static final Logger logger =
	// Logger.getLogger(ZeroMQEventSubscriber.class);
	// replace above logger with EdgeXLogger below
	private final static org.edgexfoundry.support.logging.client.EdgeXLogger logger = org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
			.getEdgeXLogger(ZeroMQEventSubscriber.class);

	@Value("${zeromq.port}")
	private String zeromqAddressPort;
	@Value("${zeromq.host}")
	private String zeromqAddress;

	@Autowired
	private DistroGateway gateway;

	private ZMQ.Socket subscriber;
	private ZMQ.Context context;

	{
		context = ZMQ.context(1);
		// super.setOutputChannelName("export-inboud-events");
	}

	@Override
	public void receive() {
		getSubscriber();
		Event event = null;
		byte[] raw;
		// long rawTime;
		logger.info("Watching for new Event messages...");
		try {
			while (!Thread.currentThread().isInterrupted()) {
				raw = subscriber.recv();
				// rawTime = System.currentTimeMillis();
				event = toEvent(raw);
				// logger.error("--->" + event.getId() + " distro-trx@ " +
				// System.currentTimeMillis());
				// logger.error("--->" + event.getId() + " distro-rec@ " +
				// rawTime);
				logger.info("Event entering export with id:  " + event.getId());
				// Message<Event> message =
				// MessageBuilder.withPayload(event).build();
				gateway.receiveEvent(event);
				// sendMessage(message);
				logger.info("Event sent to export with id:  " + event.getId());
				// logger.error("--->" + event.getId() + " distro-sent@ " +
				// System.currentTimeMillis());
			}
		} catch (Exception e) {
			logger.error("Unable to receive messages via ZMQ: " + e.getMessage());
		}
		logger.error("Shutting off Event message watch due to error!");
		if (subscriber != null)
			subscriber.close();
		subscriber = null;
		// try to restart
		logger.debug("Attempting restart of Event message watch.");
		receive();
		// context.term();
	}

	public String getZeromqAddress() {
		return zeromqAddress;
	}

	public void setZeromqAddress(String zeromqAddress) {
		this.zeromqAddress = zeromqAddress;
	}

	public String getZeromqAddressPort() {
		return zeromqAddressPort;
	}

	public void setZeromqAddressPort(String zeromqAddressPort) {
		this.zeromqAddressPort = zeromqAddressPort;
	}

	// @Override
	// public void setOutputChannelName(String outputChannelName) {
	// super.setOutputChannelName(outputChannelName);
	// }

	private ZMQ.Socket getSubscriber() {
		if (subscriber == null) {
			try {
				subscriber = context.socket(ZMQ.SUB);
				subscriber.connect(zeromqAddress + ":" + zeromqAddressPort);
				subscriber.subscribe("".getBytes());
			} catch (Exception e) {
				logger.error("Unable to get a ZMQ subscriber.  Error:  " + e);
				subscriber = null;
			}
		}
		return subscriber;
	}

	private static Event toEvent(byte[] eventBytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bis = new ByteArrayInputStream(eventBytes);
		ObjectInput in = new ObjectInputStream(bis);
		Event event = (Event) in.readObject();
		return event;
	}

}
