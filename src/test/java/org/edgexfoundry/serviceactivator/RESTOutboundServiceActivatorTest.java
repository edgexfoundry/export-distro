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

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import org.edgexfoundry.domain.export.ExportString;
import org.edgexfoundry.serviceactivator.RESTOutboundServiceActivator;
import org.edgexfoundry.test.category.RequiresNone;
import org.edgexfoundry.test.data.ExportStringData;
import org.edgexfoundry.test.data.RegistrationData;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

@Category({ RequiresNone.class })
public class RESTOutboundServiceActivatorTest {

	private RESTOutboundServiceActivator activator;
	private ExportString string;
	private Message<ExportString> message;
	private static final String TEST_STRING = "foobar";
	private static final String TEST_ID = "1234";

	@Before
	public void setup() {
		activator = new RESTOutboundServiceActivator();
		string = ExportStringData.newTestInstance();
		string.setEventString(TEST_STRING);
		string.setEventId(TEST_ID);
		string.getRegistration().getAddressable().setPath("");
		string.getRegistration().getAddressable().setAddress("http://localhost");
		message = MessageBuilder.withPayload(string).build();
	}

	@Test
	public void testRESTOutbound() throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(RegistrationData.TEST_PORT), 0);
		server.createContext("/", new TestHandler());
		server.setExecutor(null);
		server.start();
		assertEquals("Returned event id does not match expected", TEST_ID, activator.restOutbound(message));
	}

	@Test
	public void testRESTOutboundNoAddressable() {
		string.getRegistration().setAddressable(null);
		assertNull("Not returning null when no MQTT addressable is available", activator.restOutbound(message));
	}

	@Test
	public void testRESTOutboundException() {
		string.setRegistration(null, null);
		assertNull("Not returning null when an exception occurs", activator.restOutbound(message));
	}
	
	public class TestHandler implements HttpHandler {

		@Override

		public void handle(HttpExchange he) throws IOException {
			String response = "yep";
			he.sendResponseHeaders(200, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}


}
