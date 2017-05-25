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
package org.edgexfoundry.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.List;

import org.edgexfoundry.controller.ValueDescriptorClient;
import org.edgexfoundry.domain.common.IoTType;
import org.edgexfoundry.domain.common.ValueDescriptor;
import org.edgexfoundry.domain.core.Event;
import org.edgexfoundry.domain.core.Reading;
import org.edgexfoundry.filter.ValidEventFilter;
import org.edgexfoundry.test.category.RequiresNone;
import org.edgexfoundry.test.data.EventData;
import org.edgexfoundry.test.data.ReadingData;
import org.edgexfoundry.test.data.RegistrationData;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

@Category({ RequiresNone.class })
public class ValidEventFilterTest {

	private ValueDescriptorClient client;
	private ValidEventFilter filter;
	private Event event;
	private Message<Event> message;

	@Before
	public void setup() throws Exception {
		filter = new ValidEventFilter();
		filter.setConductCheck(true);
		client = new ValueDescriptorClientMock(IoTType.I);
		setValueDescriptorClient(client);

		event = EventData.newTestInstance();
		Reading reading = ReadingData.newTestInstance();
		event.addReading(reading);
		message = MessageBuilder.withPayload(event).build();
	}

	@Test
	public void testConductCheckOff() {
		filter.setConductCheck(false);
		assertTrue("With check off, message not being accepted", filter.accept(message));
	}

	@Test
	public void testAcceptValidEventWithIntReading() {
		assertTrue("Event should be valid but is filtered", filter.accept(message));
	}

	@Test
	public void testRejectValidEventWithBadIntReading() {
		event.getReadings().get(0).setValue("nan");
		assertFalse("Event should be rejected but wasn't", filter.accept(message));
	}

	@Test
	public void testAcceptValidEventWithStringReading() throws Exception {
		client = new ValueDescriptorClientMock(IoTType.S);
		setValueDescriptorClient(client);
		assertTrue("Event should be valid but is filtered", filter.accept(message));
	}

	@Test
	public void testRejectValidEventWithBadStringReading() {
		event.getReadings().get(0).setValue("");
		client = new ValueDescriptorClientMock(IoTType.S);
		assertFalse("Event should be rejected but wasn't", filter.accept(message));
	}

	@Test
	public void testAcceptValidEventWithJSONReading() throws Exception {
		client = new ValueDescriptorClientMock(IoTType.J);
		event.getReadings().get(0).setValue("{\"name\":\"foo\"}");
		setValueDescriptorClient(client);
		assertTrue("Event should be valid but is filtered", filter.accept(message));
	}

	@Test
	public void testRejectValidEventWithBadJSONReading() {
		event.getReadings().get(0).setValue("badJSON");
		client = new ValueDescriptorClientMock(IoTType.J);
		assertFalse("Event should be rejected but wasn't", filter.accept(message));
	}

	@Test
	public void testAcceptValidEventWithBooleanReading() throws Exception {
		client = new ValueDescriptorClientMock(IoTType.B);
		event.getReadings().get(0).setValue("true");
		setValueDescriptorClient(client);
		assertTrue("Event should be valid but is filtered", filter.accept(message));
	}

	@Test
	public void testRejectValidEventWithBadBooleanReading() {
		event.getReadings().get(0).setValue("nottrue");
		client = new ValueDescriptorClientMock(IoTType.B);
		assertFalse("Event should be rejected but wasn't", filter.accept(message));
	}

	@Test
	public void testAcceptValidEventWithFloatReading() throws Exception {
		client = new ValueDescriptorClientMock(IoTType.F);
		setValueDescriptorClient(client);
		assertTrue("Event should be valid but is filtered", filter.accept(message));
	}

	@Test
	public void testRejectValidEventWithBadFloatNReading() {
		event.getReadings().get(0).setValue("nan");
		client = new ValueDescriptorClientMock(IoTType.F);
		assertFalse("Event should be rejected but wasn't", filter.accept(message));
	}

	@Test
	public void testAcceptValidEventnoReadings() {
		event.setReadings(null);
		assertFalse("Event with no readings should not be valid but was", filter.accept(message));
	}

	@Test
	public void testRejectedBadReadingData() {
		event.getReadings().get(0).setValue("-1");
		assertFalse("Event with reading outside value descriptor still passed as valid", filter.accept(message));
	}

	@Test
	public void testException() {
		Message<String> badmessage = MessageBuilder.withPayload("test").build();
		assertFalse("bad message not rejected by valid event filter", filter.accept(badmessage));
	}

	// use Java reflection to set client (without Spring)
	private void setValueDescriptorClient(ValueDescriptorClient client) throws Exception {
		Class<?> filterClass = filter.getClass();
		Field temp = filterClass.getDeclaredField("vdClient");
		temp.setAccessible(true);
		temp.set(filter, client);
	}

	public class ValueDescriptorClientMock implements ValueDescriptorClient {

		private IoTType type = IoTType.I;

		public ValueDescriptorClientMock(IoTType type) {
			this.type = type;
		}

		@Override
		public ValueDescriptor valueDescriptor(String id) {
			return null;
		}

		@Override
		public List<ValueDescriptor> valueDescriptors() {
			return null;
		}

		@Override
		public ValueDescriptor valueDescriptorByName(String name) {
			ValueDescriptor vd = new ValueDescriptor();
			vd.setName(RegistrationData.TEST_DEVICE_NAMES.get(0));
			vd.setMax(1000);
			vd.setMin(0);
			vd.setType(type);
			return vd;
		}

		@Override
		public List<ValueDescriptor> valueDescriptorByUOMLabel(String uomLabel) {
			return null;
		}

		@Override
		public List<ValueDescriptor> valueDescriptorByLabel(String label) {
			return null;
		}

		@Override
		public String add(ValueDescriptor valueDescriptor) {
			return null;
		}

		@Override
		public boolean update(ValueDescriptor valueDescriptor) {
			return false;
		}

		@Override
		public boolean delete(String id) {
			return false;
		}

		@Override
		public boolean deleteByName(String name) {
			return false;
		}

		@Override
		public List<ValueDescriptor> valueDescriptorsForDeviceByName(String name) {
			return null;
		}

		@Override
		public List<ValueDescriptor> valueDescriptorsForDeviceById(String id) {
			return null;
		}

	}

}
