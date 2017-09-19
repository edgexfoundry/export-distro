/*******************************************************************************
 * Copyright 2017 Schlumberger
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 * @microservice: export-distro
 * @author: Bernard Van Haecke, Schlumberger
 * @version: 1.0.0
 *******************************************************************************/
package org.edgexfoundry.messaging;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.edgexfoundry.domain.meta.Addressable;
import org.joda.time.DateTime;

public class IotCoreMQTTSender implements MqttCallback {

  private final static org.edgexfoundry.support.logging.client.EdgeXLogger logger =
      org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
          .getEdgeXLogger(IotCoreMQTTSender.class);

  private MqttClient client = null;

  private String clientId;
  private String username;
  private String password;
  private String topic;
  private int qos;
  private int keepAlive;

  private String projectId;
  private String deviceId;
  private String privateKeyFile;
  private String algorithm;

  private static final String mqttBridgeHostname = "mqtt.googleapis.com";
  private static final short mqttBridgePort = 8883;
  private static final short jwtExpiration = 60; // + 10 minutes skew


  public IotCoreMQTTSender(Addressable addressable, String privateKeyFile, String algorithm,
      int qos, int keepAlive) {
    this.clientId = addressable.getPublisher();
    this.projectId = clientId.substring(9, clientId.indexOf("/locations/"));
    this.deviceId = clientId.substring(clientId.indexOf("/devices/") + 9);
    this.privateKeyFile = privateKeyFile;
    this.algorithm = algorithm;
    this.topic = addressable.getTopic();
    if ((this.topic == null) || this.topic.equals("")) {
      this.topic = "/devices/" + deviceId + "/events";
    }
    this.username = "unused";
    this.qos = qos;
    this.keepAlive = keepAlive;

    this.connectClient();
  }

  public synchronized boolean sendMessage(byte[] messagePayload) {
    logger.info("sending message");
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
    } else {
      logger.info("client is null");
    }
    return false;
  }

  public void setTopic(String topic) {
    this.topic = topic;
  }

  public String getTopic() {
    return topic;
  }

  private PrivateKey loadKeyFile(String filename, String algorithm) throws Exception {
    filename =
        this.getClass().getClassLoader().getResource("").toString().replace("file:", "") + filename;
    logger.info("loading keyfile: " + filename);
    byte[] keyBytes = Files.readAllBytes(Paths.get(filename));
    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
    KeyFactory kf = KeyFactory.getInstance(algorithm);
    return kf.generatePrivate(spec);
  }

  private String createJwt(String projectId, String privateKeyFile, String algorithm)
      throws Exception {
    DateTime now = new DateTime();
    JwtBuilder jwtBuilder = Jwts.builder().setIssuedAt(now.toDate())
        .setExpiration(now.plusMinutes(jwtExpiration).toDate()).setAudience(projectId);

    if (algorithm.equals("RS256")) {
      PrivateKey privateKey = loadKeyFile(privateKeyFile, "RSA");
      return jwtBuilder.signWith(SignatureAlgorithm.RS256, privateKey).compact();
    } else if (algorithm.equals("ES256")) {
      PrivateKey privateKey = loadKeyFile(privateKeyFile, "EC");
      return jwtBuilder.signWith(SignatureAlgorithm.ES256, privateKey).compact();
    } else {
      throw new IllegalArgumentException(
          "Invalid algorithm " + algorithm + ". Should be one of 'RS256' or 'ES256'.");
    }
  }

  private void connectClient() {
    try {
      String mqttServerAddress = String.format("ssl://%s:%s", mqttBridgeHostname, mqttBridgePort);

      MqttConnectOptions connectOptions = new MqttConnectOptions();
      connectOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
      connectOptions.setUserName(username);
      password = createJwt(projectId, privateKeyFile, algorithm);
      connectOptions.setPassword(password.toCharArray());

      connectOptions.setCleanSession(true);
      connectOptions.setKeepAliveInterval(keepAlive);

      client = new MqttClient(mqttServerAddress, clientId, new MemoryPersistence());
      client.setCallback(this);

      logger.debug("Connecting to broker:  " + mqttServerAddress);
      client.connect(connectOptions);
      logger.debug("Connected");

    } catch (Exception e) {
      logger.error("Failed to connect to MQTT client ( " + mqttBridgeHostname + ":" + mqttBridgePort
          + "/" + clientId + ") for outbound messages");
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
    logger.error("Outgoing sender publisher connection lost for topic " + topic + " - issue:"
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
    logger.debug("Message received on Outgoing Sender for topic: " + topic + ".  Payload:  "
        + message.getPayload().toString());
  }

  @Override
  public void deliveryComplete(IMqttDeliveryToken token) {
    logger.debug("Message delivered successfully by Outgoing Sender to topic:  " + topic
        + ".  Token:  " + token.toString());
  }
}
