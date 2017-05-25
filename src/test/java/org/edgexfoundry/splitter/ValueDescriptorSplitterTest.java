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
package org.edgexfoundry.splitter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.edgexfoundry.domain.core.Reading;
import org.edgexfoundry.domain.export.ExportMessage;
import org.edgexfoundry.splitter.ValueDescriptorSplitter;
import org.edgexfoundry.test.category.RequiresNone;
import org.edgexfoundry.test.data.ExportMessageData;
import org.edgexfoundry.test.data.ReadingData;
import org.edgexfoundry.test.data.RegistrationData;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

@Category({ RequiresNone.class })
public class ValueDescriptorSplitterTest {

	private ValueDescriptorSplitter splitter;
	private Message<ExportMessage> message;
	private ExportMessage export;

	@Before
	public void setup() {
		splitter = new ValueDescriptorSplitter();

		export = ExportMessageData.newTestInstance();
		Reading reading = ReadingData.newTestInstance();
		reading.setName(RegistrationData.TEST_DESCRIPTORS[0]);
		export.getEvent().addReading(reading);
		message = MessageBuilder.withPayload(export).build();
	}

	@Test
	public void testSplitMessageNoReduction() {
		assertEquals("Message changed when it should not have on reading reduction splitter", export,
				splitter.splitMessage(message));
	}

	@Test
	public void testSplitWithNoFilters() {
		export.getRegistration().setFilter(null);
		assertEquals("Message changed when it should not have through filter with no value descriptor filters", export,
				splitter.splitMessage(message));
	}

	@Test
	public void testSplitMessageRemoveReading() {
		export.getEvent().getReadings().get(0).setName("foobar");
		assertNull("Export Message should have been filtered but was not", splitter.splitMessage(message));
	}

	@Test(expected = Exception.class)
	public void testSplitMessageException() {
		Message<String> badmessage = MessageBuilder.withPayload("test").build();
		splitter.splitMessage(badmessage);
	}

}
