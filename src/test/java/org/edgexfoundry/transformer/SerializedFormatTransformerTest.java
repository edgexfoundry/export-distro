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

import org.edgexfoundry.domain.export.ExportMessage;
import org.edgexfoundry.test.category.RequiresNone;
import org.edgexfoundry.test.data.ExportMessageData;
import org.edgexfoundry.transformer.SerializedFormatTransformer;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

@Category({ RequiresNone.class })
public class SerializedFormatTransformerTest {

	private static final String EXPECTED_SERIALIZED_STRING = "rO0ABXNyACJvcmcuZWRnZXhmb3VuZHJ5LmRvbWFpbi5jb3JlLkV2ZW50gyv7XbhoXaYCAANKAAZwdXNoZWRMAAZkZXZpY2V0ABJMamF2YS9sYW5nL1N0cmluZztMAAhyZWFkaW5nc3QAEExqYXZhL3V0aWwvTGlzdDt4cgApb3JnLmVkZ2V4Zm91bmRyeS5kb21haW4uY29tbW9uLkJhc2VPYmplY3T8ogM99zuWWQIABEoAB2NyZWF0ZWRKAAhtb2RpZmllZEoABm9yaWdpbkwAAmlkcQB+AAF4cAAAAAAAAAAAAAAAAAAAAAAAAAAAB1vNFXAAAAAAAAAAAHQAE2xpdmluZ3Jvb210aGVybW9zYXRw";

	private SerializedFormatTransformer transformer;
	private ExportMessage export;
	private Message<ExportMessage> message;

	@Before
	public void setup() {
		export = ExportMessageData.newTestInstance();
		message = MessageBuilder.withPayload(export).build();
		transformer = new SerializedFormatTransformer();
	}

	@Test
	public void testTransform() throws Exception {
		System.out.println(transformer.transform(message).getEventString());
		assertEquals("Serialized tranformed string not the same as the expected string", EXPECTED_SERIALIZED_STRING,
				transformer.transform(message).getEventString());
	}

	@Test(expected = Exception.class)
	public void testTransformException() throws Exception {
		message.getPayload().setEvent(null);
		transformer.transform(message);
	}

}
