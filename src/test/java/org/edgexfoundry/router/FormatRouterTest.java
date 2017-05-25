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

import org.edgexfoundry.domain.export.ExportFormat;
import org.edgexfoundry.domain.export.ExportMessage;
import org.edgexfoundry.router.FormatRouter;
import org.edgexfoundry.test.category.RequiresNone;
import org.edgexfoundry.test.data.ExportMessageData;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ RequiresNone.class })
public class FormatRouterTest {

	private FormatRouter router;
	private ExportMessage export;

	@Before
	public void setup() {
		export = ExportMessageData.newTestInstance();
		router = new FormatRouter();
	}

	@Test
	public void testRouteJSON() throws Exception {
		export.getRegistration().setFormat(ExportFormat.JSON);
		assertEquals("Route for JSON format not routing properly", FormatRouter.OUT_JSON, router.route(export));
	}

	@Test
	public void testRouteXML() throws Exception {
		export.getRegistration().setFormat(ExportFormat.XML);
		assertEquals("Route for JSON format not routing properly", FormatRouter.OUT_XML, router.route(export));
	}
	
	@Test
	public void testRouteSerialized() throws Exception {
		export.getRegistration().setFormat(ExportFormat.SERIALIZED);
		assertEquals("Route for Serialized format not routing properly", FormatRouter.OUT_SERIALIZED, router.route(export));
	}

	@Test
	public void testRouteAzure() throws Exception {
		export.getRegistration().setFormat(ExportFormat.AZURE_JSON);
		assertEquals("Route for AZURE format not routing properly", FormatRouter.OUT_AZURE, router.route(export));
	}

	@Test(expected = Exception.class)
	public void testRouteCSV() throws Exception {
		export.getRegistration().setFormat(ExportFormat.CSV);
		router.route(export);
	}

	@Test(expected = Exception.class)
	public void testException() throws Exception {
		export.getRegistration().setFormat(null);
		router.route(export);
	}

}
