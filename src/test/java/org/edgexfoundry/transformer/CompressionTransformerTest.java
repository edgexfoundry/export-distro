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

import java.lang.reflect.Method;

import org.edgexfoundry.domain.export.ExportCompression;
import org.edgexfoundry.domain.export.ExportString;
import org.edgexfoundry.test.category.RequiresNone;
import org.edgexfoundry.test.data.ExportStringData;
import org.edgexfoundry.transformer.CompressionTransformer;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

@Category({ RequiresNone.class })
public class CompressionTransformerTest {

	private CompressionTransformer transformer;
	private ExportString string;
	private Message<ExportString> message;

	private static final String TEST_STRING = "foobar";

	@Before
	public void setup() throws Exception {
		transformer = new CompressionTransformer();
		string = ExportStringData.newTestInstance();
		string.setEventString(TEST_STRING);
		message = MessageBuilder.withPayload(string).build();
	}

	@Test
	public void testTransformCompressionGZIP() throws Exception {
		string.getRegistration().setCompression(ExportCompression.GZIP);
		String compressedString = getGZIPEncriptionString(TEST_STRING);
		assertEquals("Transformed string of message does matched expected GZIP compressed value", compressedString,
				transformer.transform(message).getEventString());
	}

	@Test
	public void testTransformCompressionZIP() throws Exception {
		string.getRegistration().setCompression(ExportCompression.ZIP);
		String compressedString = getZIPEncriptionString(TEST_STRING);
		assertEquals("Transformed string of message does matched expected ZIP compressed value", compressedString,
				transformer.transform(message).getEventString());
	}

	@Test
	public void testTransformNoCompression() throws Exception {
		string.getRegistration().setCompression(null);
		assertEquals("Transformed string of message does matched expected ZIP encrypted value", TEST_STRING,
				transformer.transform(message).getEventString());
	}

	@Test(expected = Exception.class)
	public void testTransformException() throws Exception {
		string.setRegistration(null, null);
		transformer.transform(message);
	}

	private String getGZIPEncriptionString(String string) throws Exception {
		Class<?> clazz = transformer.getClass();
		Method method = clazz.getDeclaredMethod("gzipCompression", String.class);
		method.setAccessible(true);
		return (String) method.invoke(transformer, string);
	}

	private String getZIPEncriptionString(String string) throws Exception {
		Class<?> clazz = transformer.getClass();
		Method method = clazz.getDeclaredMethod("zipCompression", String.class);
		method.setAccessible(true);
		return (String) method.invoke(transformer, string);
	}
}
