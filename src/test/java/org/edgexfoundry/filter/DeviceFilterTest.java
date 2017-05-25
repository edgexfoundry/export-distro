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

import java.util.ArrayList;
import java.util.List;

import org.edgexfoundry.domain.export.ExportMessage;
import org.edgexfoundry.filter.DeviceFilter;
import org.edgexfoundry.test.category.RequiresNone;
import org.edgexfoundry.test.data.ExportMessageData;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

@Category({ RequiresNone.class })
public class DeviceFilterTest {

	private Message<ExportMessage> message;
	private ExportMessage export;
	private DeviceFilter filter;

	@Before
	public void setup() {
		export = ExportMessageData.newTestInstance();
		message = MessageBuilder.withPayload(export).build();
		filter = new DeviceFilter();
	}

	@Test
	public void testAccept() {
		assertTrue("Device filter not accepting messages properly", filter.accept(message));
	}

	@Test
	public void testReject() {
		List<String> badDeviceIds = new ArrayList<>();
		badDeviceIds.add("foobar");
		export.getRegistration().getFilter().setDeviceIdentifiers(badDeviceIds);
		assertFalse("Filter accepting messages with bad device identifeirs", filter.accept(message));
	}

	@Test
	public void testNoFilterAccept() {
		export.getRegistration().getFilter().setDeviceIdentifiers(null);
		assertTrue("Filter not accepting messages with no device filter", filter.accept(message));
	}

	@Test
	public void testException() {
		export.setEvent(null);
		assertFalse("Null pointer exception not captured and/or returned as false on filter", filter.accept(message));
	}

}
