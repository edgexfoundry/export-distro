/*******************************************************************************
 * Copyright 2017 Schlumberger
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
 * @author: Bernard Van Haecke, Schlumberger
 * @version: 1.0.0
 *******************************************************************************/
package org.edgexfoundry.serviceactivator;

import static org.junit.Assert.assertNull;

import org.apache.commons.lang.reflect.FieldUtils;
import org.edgexfoundry.domain.export.ExportRegistration;
import org.edgexfoundry.domain.export.ExportString;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.Protocol;
import org.edgexfoundry.serviceactivator.IotCoreMQTTOutboundServiceActivator;
import org.edgexfoundry.test.category.RequiresNone;
import org.edgexfoundry.test.data.ExportStringData;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

@Category({ RequiresNone.class })
public class IotCoreMQTTOutboundServiceActivatorTest {

	private IotCoreMQTTOutboundServiceActivator activator;
	private ExportString string;
	private Message<ExportString> message;
	private static final String TEST_STRING = "foobar";
	private static final String TEST_ID = "1234";
	// standard clientid format for IoT Core
	private static final String TEST_PUBLISHER = "projects/project1/locations/us-central1/registries/registry1/devices/device1";
	// standard topic format for IoT Core
	private static final String TEST_TOPIC = "/devices/device1/events";
	private static final String TEST_USER = "unused";
	private static final String TEST_PASSWORD = "";
	private static final String TEST_ADDRESS = "unused.com";
	private static final int TEST_PORT = 8883;

	@Before
	public void setup() {
		activator = new IotCoreMQTTOutboundServiceActivator();
		string = ExportStringData.newTestInstance();
		string.setEventString(TEST_STRING);
		string.setEventId(TEST_ID);
		
		Addressable addressable = new Addressable(TEST_STRING, Protocol.OTHER, TEST_ADDRESS, TEST_PORT, TEST_PUBLISHER,
			      TEST_USER, TEST_PASSWORD, TEST_TOPIC);
		ExportRegistration er = new ExportRegistration();
		er.setAddressable(addressable);
		string.setRegistration(er, TEST_ID);
		
		message = MessageBuilder.withPayload(string).build();
	    try {
			FieldUtils.writeField(activator, "privateKeyFile", "rsa_private_pkcs8", true);
		    FieldUtils.writeField(activator, "algorithm", "RS256", true);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Ignore
	@Test
	public void testMqttOutbound() {
		assertNull("Not returning null for bogus connection parameters", activator.mqttOutbound(message));
	}

	@Test
	public void testMqttOutboundNoAddressable() {
		string.getRegistration().setAddressable(null);
		assertNull("Not returning null when no MQTT addressable is available", activator.mqttOutbound(message));
	}

	@Test
	public void testMqttOutboundException() {
		string.setRegistration(null, null);
		assertNull("Not returning null when an exception occurs", activator.mqttOutbound(message));
	}

}
