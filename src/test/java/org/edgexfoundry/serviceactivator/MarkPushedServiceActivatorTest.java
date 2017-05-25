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

import java.lang.reflect.Field;
import java.util.List;

import org.edgexfoundry.controller.EventClient;
import org.edgexfoundry.domain.core.Event;
import org.edgexfoundry.domain.core.Reading;
import org.edgexfoundry.serviceactivator.MarkPushedServiceActivator;
import org.edgexfoundry.test.category.RequiresNone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

@Category({ RequiresNone.class })
public class MarkPushedServiceActivatorTest {

	private static final String TEST_ID = "123456";

	private MarkPushedServiceActivator activator;

	private EventClient client;
	private Message<String> message;

	@Before
	public void setup() throws Exception {
		message = MessageBuilder.withPayload(TEST_ID).build();

		client = new EventClientMock();
		activator = new MarkPushedServiceActivator();
		setClient(client);
	}

	@Test
	public void testMark() {
		activator.mark(message);
	}

	// fails quietly - so no exception or results differ. Just log entry
	@Test
	public void testMarkException() {
		client = null;
		activator.mark(message);
	}

	// use Java reflection to set client (without Spring)
	private void setClient(EventClient client) throws Exception {
		Class<?> clazz = activator.getClass();
		Field temp = clazz.getDeclaredField("eventClient");
		temp.setAccessible(true);
		temp.set(activator, client);
	}

	public class EventClientMock implements EventClient {

		@Override
		public Event event(String id) {
			return null;
		}

		@Override
		public List<Event> events() {
			return null;
		}

		@Override
		public List<Event> eventsForDevice(String deviceId, int limit) {
			return null;
		}

		@Override
		public List<Event> events(long start, long end, int limit) {
			return null;
		}

		@Override
		public List<Reading> readingsForDeviceAndValueDescriptor(String deviceId, String valuedescriptor, int limit) {
			return null;
		}

		@Override
		public String add(Event event) {
			return null;
		}

		@Override
		public boolean markedPushed(String id) {
			return true;
		}

		@Override
		public boolean update(Event event) {
			return false;
		}

		@Override
		public boolean delete(String id) {
			return false;
		}

		@Override
		public int deleteByDevice(String deviceId) {
			return 0;
		}

		@Override
		public long scrubPushedEvents() {
			return 0;
		}

		@Override
		public long scrubOldEvents(long age) {
			return 0;
		}

	}

}
