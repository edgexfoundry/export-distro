/*******************************************************************************
 * Copyright 2017 Schlumberger
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 * @microservice: export-distro
 * @author: Bernard Van Haecke, Schlumberger
 * @version: 1.0.0
 *******************************************************************************/
package org.edgexfoundry.serviceactivator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.edgexfoundry.domain.export.ExportString;
import org.edgexfoundry.serviceactivator.IotCoreMQTTOutboundServiceActivator;
import org.edgexfoundry.test.category.RequiresNone;
import org.edgexfoundry.test.data.ExportStringData;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

@Category({RequiresNone.class})
public class IotCoreMQTTOutboundServiceActivatorTest {

  private IotCoreMQTTOutboundServiceActivator activator;
  private ExportString string;
  private Message<ExportString> message;
  private static final String TEST_STRING = "foobar";
  private static final String TEST_ID = "1234";

  @Before
  public void setup() throws IllegalAccessException {
    activator = new IotCoreMQTTOutboundServiceActivator();
    string = ExportStringData.newTestInstance();
    string.setEventString(TEST_STRING);
    string.setEventId(TEST_ID);
    message = MessageBuilder.withPayload(string).build();
    FieldUtils.writeField(activator, "privateKeyFile", "rsa_private_pkcs8", true);
    FieldUtils.writeField(activator, "algorithm", "RS256", true);
  }

//  @Test
//  public void testMqttOutbound() {
//    assertEquals("Returned event id does not match expected", TEST_ID,
//        activator.mqttOutbound(message));
//  }

  @Test
  public void testMqttOutboundNoAddressable() {
    string.getRegistration().setAddressable(null);
    assertNull("Not returning null when no MQTT addressable is available",
        activator.mqttOutbound(message));
  }

  @Test
  public void testMqttOutboundException() {
    string.setRegistration(null, null);
    assertNull("Not returning null when an exception occurs", activator.mqttOutbound(message));
  }

}
