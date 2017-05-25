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
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.edgexfoundry.dao.ExportRegistrationRepository;
import org.edgexfoundry.domain.core.Event;
import org.edgexfoundry.domain.core.Reading;
import org.edgexfoundry.domain.export.ExportMessage;
import org.edgexfoundry.domain.export.ExportRegistration;
import org.edgexfoundry.splitter.ClientMessageSplitter;
import org.edgexfoundry.test.category.RequiresNone;
import org.edgexfoundry.test.data.EventData;
import org.edgexfoundry.test.data.ReadingData;
import org.edgexfoundry.test.data.RegistrationData;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

@Category({ RequiresNone.class })
public class ClientMessageSplitterTest {

	private ClientMessageSplitter splitter;
	private Message<Event> message;
	private Event event;

	@Before
	public void setup() throws Exception {
		splitter = new ClientMessageSplitter();
		setRepos(new ExportRegistrationRepositoryMock(true));

		event = EventData.newTestInstance();
		Reading reading = ReadingData.newTestInstance();
		event.addReading(reading);
		message = MessageBuilder.withPayload(event).build();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSplit() {
		List<ExportMessage> messages = (List<ExportMessage>) splitter.splitMessage(message);
		assertEquals("Incorrect number of messages returned from splitter", 1, messages.size());
		assertEquals("Export message does not contain proper event", event, messages.get(0).getEvent());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSplitNoClients() throws Exception {
		setRepos(new ExportRegistrationRepositoryMock(false));
		List<ExportMessage> messages = (List<ExportMessage>) splitter.splitMessage(message);
		assertTrue("No messages should be returned from splitter", messages.isEmpty());
	}

	@Test(expected = Exception.class)
	public void testSplitException() {
		Message<String> badMessage = MessageBuilder.withPayload("test").build();
		splitter.splitMessage(badMessage);
	}

	// use Java reflection to set client (without Spring)
	private void setRepos(ExportRegistrationRepository repos) throws Exception {
		Class<?> clazz = splitter.getClass();
		Field temp = clazz.getDeclaredField("repos");
		temp.setAccessible(true);
		temp.set(splitter, repos);
	}

	public class ExportRegistrationRepositoryMock implements ExportRegistrationRepository {

		private boolean createList = true;

		public ExportRegistrationRepositoryMock(boolean createList) {
			this.createList = createList;
		}

		@Override
		public <S extends ExportRegistration> List<S> save(Iterable<S> entites) {
			return null;
		}

		@Override
		public List<ExportRegistration> findAll() {
			return null;
		}

		@Override
		public List<ExportRegistration> findAll(Sort sort) {
			return null;
		}

		@Override
		public <S extends ExportRegistration> S insert(S entity) {
			return null;
		}

		@Override
		public <S extends ExportRegistration> List<S> insert(Iterable<S> entities) {
			return null;
		}

		@Override
		public Page<ExportRegistration> findAll(Pageable arg0) {
			return null;
		}

		@Override
		public long count() {
			return 0;
		}

		@Override
		public void delete(String arg0) {

		}

		@Override
		public void delete(ExportRegistration arg0) {

		}

		@Override
		public void delete(Iterable<? extends ExportRegistration> arg0) {

		}

		@Override
		public void deleteAll() {

		}

		@Override
		public boolean exists(String arg0) {
			return false;
		}

		@Override
		public Iterable<ExportRegistration> findAll(Iterable<String> arg0) {
			return null;
		}

		@Override
		public ExportRegistration findOne(String arg0) {
			return null;
		}

		@Override
		public <S extends ExportRegistration> S save(S arg0) {
			return null;
		}

		@Override
		public ExportRegistration findByName(String name) {
			return null;
		}

		@Override
		public List<ExportRegistration> findByEnable(boolean enabled) {
			List<ExportRegistration> regs = new ArrayList<>();
			if (createList) {
				ExportRegistration reg = RegistrationData.newTestInstance();
				regs.add(reg);
			}
			return regs;
		}

	}

}
