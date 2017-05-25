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

import static org.edgexfoundry.test.data.RegistrationData.TEST_ADDRESS;
import static org.edgexfoundry.test.data.RegistrationData.TEST_PASSWORD;
import static org.edgexfoundry.test.data.RegistrationData.TEST_PORT;
import static org.edgexfoundry.test.data.RegistrationData.TEST_PUBLISHER;
import static org.edgexfoundry.test.data.RegistrationData.TEST_TOPIC;
import static org.edgexfoundry.test.data.RegistrationData.TEST_USER;
import static org.junit.Assert.assertTrue;

import org.edgexfoundry.messaging.MQTTSender;
import org.edgexfoundry.test.category.RequiresNone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ RequiresNone.class })
public class MQTTSenderTest {

	private final String MSG_PAYLOAD = "this is a test";

	MQTTSender sender;

	@Before
	public void setup() {
		sender = new MQTTSender(TEST_ADDRESS, TEST_PORT, TEST_PUBLISHER, TEST_USER, TEST_PASSWORD, TEST_TOPIC, 0, 3600);
	}

	@After
	public void cleanup() {
		sender.closeClient();
	}

	@Test
	public void testSendMessage() {
		assertTrue(sender.sendMessage(new String(MSG_PAYLOAD).getBytes()));
	}

	// These tests are causing problems in Jenkins - TODO - someday research and fix
//	@Test
//	public void testSendMessageFail() throws MqttException {
//		sender.setTopic(null);
//		assertFalse(sender.sendMessage(new String(MSG_PAYLOAD).getBytes()));
//	}

//	@Test
//	public void testConnectFail() throws MqttException {
//		// create a client already using the persistence path
//		MqttClient client = new MqttClient(TEST_ADDRESS + ":" + TEST_PORT, TEST_PUBLISHER);
//		MqttConnectOptions connOpts = new MqttConnectOptions();
//		connOpts.setUserName(TEST_USER);
//		connOpts.setPassword(TEST_PASSWORD.toCharArray());
//		connOpts.setConnectionTimeout(1);
//		client.connect(connOpts);
//		assertFalse(sender.sendMessage(new String(MSG_PAYLOAD).getBytes()));
//		client.disconnect(3);
//		client.close();
//	}
}
