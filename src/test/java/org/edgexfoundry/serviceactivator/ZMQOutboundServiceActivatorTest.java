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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.edgexfoundry.domain.export.ExportString;
import org.edgexfoundry.messaging.ZeroMQSender;
import org.edgexfoundry.serviceactivator.ZMQOutboundServiceActivator;
import org.edgexfoundry.test.category.RequiresNone;
import org.edgexfoundry.test.data.ExportStringData;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

@Category({ RequiresNone.class })
public class ZMQOutboundServiceActivatorTest {

	private ZMQOutboundServiceActivator activator;
	private ExportString string;
	private Message<ExportString> message;
	private static final String TEST_STRING = "foobar";
	private static final String TEST_ID = "1234";
	private static final String TEST_0MQ_ADDR = "tcp://*:5566";

	@Before
	public void setup() {
		activator = new ZMQOutboundServiceActivator();
		ZeroMQSender sendor = new ZeroMQSender();
		sendor.setOutbound0MQAddress(TEST_0MQ_ADDR);
		activator.setSendor(sendor);
		string = ExportStringData.newTestInstance();
		string.setEventString(TEST_STRING);
		string.setEventId(TEST_ID);
		message = MessageBuilder.withPayload(string).build();
	}

	@Test
	public void testZMQOutbound() {
		assertEquals("Returned event id does not match expected", TEST_ID, activator.zmqOutbound(message));
	}

	@Test
	public void testZMQOutboundException() {
		assertNull("Not returning null when an exception occurs",
				activator.zmqOutbound(MessageBuilder.withPayload("foo").build()));
	}

}
