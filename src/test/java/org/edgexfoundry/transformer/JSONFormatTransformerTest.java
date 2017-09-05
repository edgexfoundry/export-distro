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
package org.edgexfoundry.transformer;

import static org.junit.Assert.assertEquals;

import org.edgexfoundry.domain.core.Reading;
import org.edgexfoundry.domain.export.ExportMessage;
import org.edgexfoundry.test.category.RequiresNone;
import org.edgexfoundry.test.data.ExportMessageData;
import org.edgexfoundry.transformer.JSONFormatTransformer;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

@Category({ RequiresNone.class })
public class JSONFormatTransformerTest {

	private final static String EXPECTED_JSON = "{\"pushed\":0,\"device\":\"livingroomthermosat\",\"created\":0,\"modified\":0,\"origin\":123456789}";
	private final static String EXPECTED_AZURE_JSON = "{'deviceId':'livingroomthermosat','key1':'value1'}";

	private JSONFormatTransformer transformer;
	private ExportMessage export;
	private Message<ExportMessage> message;

	@Before
	public void setup() {
		export = ExportMessageData.newTestInstance();
		message = MessageBuilder.withPayload(export).build();
		transformer = new JSONFormatTransformer();
	}

	@Test
	public void testTransformJSON() {
		assertEquals("JSON tranformed string not the same as the expected string", EXPECTED_JSON,
				transformer.transformJSON(message).getEventString());
	}

	@Test
	public void testTransformAzureJSON() {
		Reading r = new Reading();
		r.setName("key1");
		r.setValue("value1");
		message.getPayload().getEvent().addReading(r);
		assertEquals("Azure JSON tranformed string not the same as the expected string", EXPECTED_AZURE_JSON,
				transformer.transformAzureJSON(message).getEventString());
	}

	@Test
	public void testTransformIotCoreJSON() {
		assertEquals("JSON tranformed string not the same as the expected string", EXPECTED_JSON,
				transformer.transformIotCoreJSON(message).getEventString());
	}

	@Test(expected = Exception.class)
	public void testTransformException() {
		message.getPayload().setEvent(null);
		transformer.transformJSON(message);
	}

	@Test(expected = Exception.class)
	public void testAzureTransformException() {
		message.getPayload().setEvent(null);
		transformer.transformAzureJSON(message);
	}

}
