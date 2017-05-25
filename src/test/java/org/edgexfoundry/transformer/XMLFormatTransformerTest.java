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

import javax.xml.bind.JAXBException;

import org.edgexfoundry.domain.export.ExportMessage;
import org.edgexfoundry.test.category.RequiresNone;
import org.edgexfoundry.test.data.ExportMessageData;
import org.edgexfoundry.transformer.XMLFormatTransformer;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

@Category({ RequiresNone.class })
public class XMLFormatTransformerTest {
	private final static String EXPECTED_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
			+ "<Event>\n" + "    <event>\n" + "        <created>0</created>\n" + "        <modified>0</modified>\n"
			+ "        <origin>123456789</origin>\n" + "        <device>livingroomthermosat</device>\n"
			+ "        <pushed>0</pushed>\n" + "    </event>\n" + "</Event>\n";

	private XMLFormatTransformer transformer;
	private ExportMessage export;
	private Message<ExportMessage> message;

	@Before
	public void setup() {
		export = ExportMessageData.newTestInstance();
		message = MessageBuilder.withPayload(export).build();
		transformer = new XMLFormatTransformer();
	}

	@Test
	public void testTransform() throws JAXBException {
		assertEquals("XML tranformed string not the same as the expected string", EXPECTED_XML,
				transformer.transform(message).getEventString());
	}

	@Test(expected = Exception.class)
	public void testTransformException() throws JAXBException {
		message.getPayload().setEvent(null);
		transformer.transform(message);
	}

}
