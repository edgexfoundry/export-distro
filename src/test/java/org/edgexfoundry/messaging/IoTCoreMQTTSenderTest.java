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
package org.edgexfoundry.messaging;

import static org.edgexfoundry.test.data.RegistrationData.TEST_ADDRESS;
import static org.edgexfoundry.test.data.RegistrationData.TEST_ADDR_NAME;
import static org.edgexfoundry.test.data.RegistrationData.TEST_PASSWORD;
import static org.edgexfoundry.test.data.RegistrationData.TEST_PORT;
import static org.edgexfoundry.test.data.RegistrationData.TEST_PROTOCOL;
import static org.edgexfoundry.test.data.RegistrationData.TEST_PUBLISHER;
import static org.edgexfoundry.test.data.RegistrationData.TEST_TOPIC;
import static org.edgexfoundry.test.data.RegistrationData.TEST_USER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.Protocol;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IoTCoreMQTTSenderTest {

  private final String MSG_PAYLOAD = "this is a test";
  private final String KEYFILE = "/dev/null";
  private final String ALGORITHM = "RS256";
  private final int QOS = 0;
  private final int KEEPALIVE = 60;

  private static final String TEST_STRING = "foobar";
  // standard clientid format for IoT Core
  private static final String TEST_PUBLISHER = "projects/project1/locations/us-central1/registries/registry1/devices/device1";
  // standard topic format for IoT Core
  private static final String TEST_TOPIC = "/devices/device1/events";
  private static final String TEST_USER = "unused";
  private static final String TEST_PASSWORD = "";
  private static final String TEST_ADDRESS = "unused.com";
  private static final int TEST_PORT = 8883;
  
  IotCoreMQTTSender sender;

  @Before
  public void setup() throws Exception {
    Addressable addressable = new Addressable(TEST_STRING, Protocol.OTHER, TEST_ADDRESS, TEST_PORT, TEST_PUBLISHER,
            TEST_USER, TEST_PASSWORD, TEST_TOPIC);
    boolean thrown = false;
    try {
      sender = new IotCoreMQTTSender(addressable, KEYFILE, ALGORITHM, QOS, KEEPALIVE);
    } catch (Exception e) {
      thrown = true;
      assertTrue("Must fail without proper key file", thrown);
    }
  }

  @After
  public void cleanup() {
  }

  @Test
  public void testSendMessage() {
    // connection information not setup to call IoT Core.  It will exercise process, but not get through.
    // This should return false
    boolean thrown = false;
    try {
      assertFalse(sender.sendMessage(new String(MSG_PAYLOAD).getBytes()));
    } catch (Exception e) {
      thrown = true;
      assertTrue("Must fail without proper key file", thrown);
    }
  }
}
