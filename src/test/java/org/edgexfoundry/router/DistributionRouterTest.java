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
package org.edgexfoundry.router;

import static org.junit.Assert.assertEquals;

import org.edgexfoundry.domain.export.ExportDestination;
import org.edgexfoundry.domain.export.ExportString;
import org.edgexfoundry.router.DistributionRouter;
import org.edgexfoundry.test.category.RequiresNone;
import org.edgexfoundry.test.data.ExportStringData;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ RequiresNone.class })
public class DistributionRouterTest {

	private DistributionRouter router;
	private ExportString string;

	@Before
	public void setup() {
		router = new DistributionRouter();
		string = ExportStringData.newTestInstance();
	}

	@Test
	public void testRouteRESTENDPOINT() throws Exception {
		string.getRegistration().setDestination(ExportDestination.REST_ENDPOINT);
		assertEquals("Route of data for REST ENDPOINT incorrectly routed", DistributionRouter.OUT_REST, router.route(string));
	}

	@Test
	public void testRouteMQTT() throws Exception {
		string.getRegistration().setDestination(ExportDestination.MQTT_TOPIC);
		assertEquals("Route of data for MQTT Topic incorrectly routed", DistributionRouter.OUT_MQTT, router.route(string));
	}

	@Test
	public void testRouteZMQ() throws Exception {
		string.getRegistration().setDestination(ExportDestination.ZMQ_TOPIC);
		assertEquals("Route of data for ZMQ TOPIC incorrectly routed", DistributionRouter.OUT_ZMQ, router.route(string));
	}
	
	@Test
	public void testRouteAzure() throws Exception {
		string.getRegistration().setDestination(ExportDestination.AZURE_MQTT);
		assertEquals("Route of data for AZURE MQTT TOPIC incorrectly routed", DistributionRouter.OUT_AZURE, router.route(string));
	}

	@Test(expected = Exception.class)
	public void testRouteNoDestination() throws Exception {
		string.getRegistration().setDestination(null);
		router.route(string);
	}

}
