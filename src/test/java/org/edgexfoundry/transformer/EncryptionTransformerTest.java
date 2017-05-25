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

import org.edgexfoundry.domain.export.ExportString;
import org.edgexfoundry.test.category.RequiresNone;
import org.edgexfoundry.test.data.ExportStringData;
import org.edgexfoundry.test.data.RegistrationData;
import org.edgexfoundry.transformer.EncryptionTransformer;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

@Category({ RequiresNone.class })
public class EncryptionTransformerTest {

	private EncryptionTransformer transformer;

	private ExportString string;
	private Message<ExportString> message;

	private static final String TEST_STRING = "foobar";

	@Before
	public void setup() throws Exception {
		transformer = new EncryptionTransformer();
		string = ExportStringData.newTestInstance();
		string.setEventString(TEST_STRING);
		message = MessageBuilder.withPayload(string).build();
	}

	@Test
	public void testTransformEncryption() throws Exception{
		String encryptedString = getEncryptedString(TEST_STRING, RegistrationData.TEST_KEY, RegistrationData.TEST_VECTOR);
		assertEquals("Transform of string with no encryption on still changing event string", encryptedString,
				transformer.transform(message).getEventString());
	}

	@Test
	public void testTransformNoEncryption() throws Exception {
		string.getRegistration().setEncryption(null);
		assertEquals("Transform of string with no encryption on still changing event string", TEST_STRING,
				transformer.transform(message).getEventString());
	}

	@Test(expected = Exception.class)
	public void testTransformException() throws Exception {
		string.setRegistration(null, null);
		transformer.transform(message);
	}

	private String getEncryptedString(String input, String encryptionKey, String initializingVector) throws Exception {
		Class<?> clazz = transformer.getClass();
		Method method = clazz.getDeclaredMethod("getEncryptedString", String.class, String.class, String.class);
		method.setAccessible(true);
		return (String) method.invoke(transformer, input, encryptionKey, initializingVector);
	}
}
