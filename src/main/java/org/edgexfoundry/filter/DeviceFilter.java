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

import java.util.List;

import org.edgexfoundry.domain.core.Event;
import org.edgexfoundry.domain.export.ExportFilter;
import org.edgexfoundry.domain.export.ExportMessage;
import org.springframework.integration.annotation.Filter;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.core.MessageSelector;
import org.springframework.messaging.Message;

/**
 * Filters events per client based on whether the event is about devices of
 * interest (as represented by device identifier in the event). The event device
 * identifier could be either a device name or id depending on how the device
 * service reported it.
 * 
 * @author jim_white
 *
 */
@MessageEndpoint
public class DeviceFilter implements MessageSelector {

	//private static final Logger logger = Logger.getLogger(DeviceFilter.class);
	//replace above logger with EdgeXLogger below
	private final static org.edgexfoundry.support.logging.client.EdgeXLogger logger = 
			org.edgexfoundry.support.logging.client.EdgeXLoggerFactory.getEdgeXLogger(DeviceFilter.class);
	@Override
	@Filter(inputChannel = "inbound-eventmessages", outputChannel = "device-filtered-eventmessages")
	public boolean accept(Message<?> msg) {
		try {
			boolean accepted = true;
			ExportMessage exportMsg = (ExportMessage) msg.getPayload();
			logger.debug("message arrived at device filter: " + exportMsg.getEvent().getId());
			ExportFilter filter = exportMsg.getRegistration().getFilter();
			if (filter != null && filter.getDeviceIdentifiers() != null && (!filter.getDeviceIdentifiers().isEmpty())) {
				accepted = checkFilters(filter.getDeviceIdentifiers(), exportMsg.getEvent());
			}
			logger.debug("message leaving device filter: " + exportMsg.getEvent().getId());
			return accepted;
		} catch (Exception e) {
			logger.error("Problem with device filter: " + e.getMessage());
			return false;
			//throw e;
		}
	}

	private boolean checkFilters(List<String> deviceIdentifiers, Event event) {
		try {
			String eventDeviceIdentifier = event.getDevice();
			return deviceIdentifiers.contains(eventDeviceIdentifier);
		} catch (Exception e) {
			logger.error("Problem getting event device or registration filters:  " + e.getMessage());
			return false;
		}
	}

}
