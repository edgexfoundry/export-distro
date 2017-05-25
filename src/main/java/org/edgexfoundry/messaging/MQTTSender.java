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

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.edgexfoundry.domain.meta.Addressable;

public class MQTTSender implements MqttCallback {

	// private final static Logger logger = Logger.getLogger(MQTTSender.class);
	// replace above logger with EdgeXLogger below
	private final static org.edgexfoundry.support.logging.client.EdgeXLogger logger = org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
			.getEdgeXLogger(MQTTSender.class);

	private MqttClient client = null;

	private String brokerUrl;
	private int brokerPort;
	private String clientId;
	private String user;
	private String password;
	private String topic;
	private int qos;
	private int keepAlive;

	public MQTTSender(String brokerUrl, int brokerPort, String clientId, String user, String password, String topic,
			int qos, int keepAlive) {
		this.brokerUrl = brokerUrl;
		this.brokerPort = brokerPort;
		this.clientId = clientId;
		this.user = user;
		this.password = password;
		this.topic = topic;
		this.qos = qos;
		this.keepAlive = keepAlive;
		this.connectClient();
	}

	public MQTTSender(Addressable addressable) {
		this(addressable.getAddress(), addressable.getPort(), addressable.getPublisher(), addressable.getUser(),
				addressable.getPassword(), addressable.getTopic(), 0, 3600);
	}

	public boolean sendMessage(byte[] messagePayload) {
		if (client != null) {
			try {
				MqttMessage message = new MqttMessage(messagePayload);
				message.setQos(qos);
				message.setRetained(false);
				client.publish(topic, message);
				return true;
			} catch (Exception e) {
				logger.error("Failed to send outbound message to topic:  " + topic + " - unexpected issue: "
						+ new String(messagePayload));
				e.printStackTrace();
			}
		}
		return false;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getTopic() {
		return topic;
	}

	private void connectClient() {
		try {
			client = new MqttClient(brokerUrl + ":" + brokerPort, clientId);
			client.setCallback(this);
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setUserName(user);
			connOpts.setPassword(password.toCharArray());
			connOpts.setCleanSession(true);
			connOpts.setKeepAliveInterval(keepAlive);
			logger.debug("Connecting to broker:  " + brokerUrl);
			client.connect(connOpts);
			logger.debug("Connected");
		} catch (MqttException e) {
			logger.error("Failed to connect to MQTT client ( " + brokerUrl + ":" + brokerPort + "/" + clientId
					+ ") for outbound messages");
			e.printStackTrace();
		}
	}

	public void closeClient() {
		try {
			if (client != null) {
				client.disconnect();
				client.close();
			}
		} catch (MqttException e) {
			logger.error("Problems disconnecting and closing the client.");
			e.printStackTrace();
		}
	}

	@Override
	public void connectionLost(Throwable cause) {
		logger.error("Outgoing sendor publisher connection lost for topic " + topic + " - issue:"
				+ cause.getLocalizedMessage());
		try {
			client.close();
		} catch (MqttException e) {
			logger.error("Unable to close the client.");
			e.printStackTrace();
		}
		connectClient();
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		logger.error("Message received on Outgoing Sender for topic: " + topic
				+ ", which should not happen.  Payload:  " + message.getPayload().toString());
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		logger.debug("Message delivered successfully by Outgoing Sender to topic:  " + topic + ".  Token:  "
				+ token.toString());
	}

}
