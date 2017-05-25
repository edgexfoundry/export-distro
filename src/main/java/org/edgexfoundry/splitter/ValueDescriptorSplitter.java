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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.edgexfoundry.domain.core.Event;
import org.edgexfoundry.domain.core.Reading;
import org.edgexfoundry.domain.export.ExportFilter;
import org.edgexfoundry.domain.export.ExportMessage;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Splitter;
import org.springframework.integration.splitter.AbstractMessageSplitter;
import org.springframework.messaging.Message;

/**
 * Splitter/quasi-filter bean that removes all readings from the event that
 * don't match a client defined list of value descriptors of interest. The value
 * descriptors, as provided by the client registrant, should be value descriptor
 * names (not database generated ids).
 * 
 * Splitter/quasi-filter bean and configuration that removes all readings from
 * the event that don't match client defined list of value descriptors
 * 
 */
@MessageEndpoint
public class ValueDescriptorSplitter extends AbstractMessageSplitter {

	//private static final Logger logger = Logger.getLogger(ValueDescriptorSplitter.class);
	//replace above logger with EdgeXLogger below
	private final static org.edgexfoundry.support.logging.client.EdgeXLogger logger = org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
			.getEdgeXLogger(ValueDescriptorSplitter.class);
	
	@Override
	@Splitter(inputChannel = "device-filtered-eventmessages", outputChannel = "reading-filtered-eventmessages")
	public Object splitMessage(Message<?> msg) {
		try {
			ExportMessage exportMsg = (ExportMessage) msg.getPayload();
			logger.debug("message arrived at value description filter/splitter: " + exportMsg.getEvent().getId());
			ExportFilter filter = exportMsg.getRegistration().getFilter();
			if (filter != null && filter.getValueDescriptorIdentifiers() != null
					&& (!filter.getValueDescriptorIdentifiers().isEmpty())) {
				return filterReadings(exportMsg, filter.getValueDescriptorIdentifiers());
			}
			logger.debug("message leaving value description filter/splitter: " + exportMsg.getEvent().getId());
			return exportMsg;
		} catch (Exception e) {
			logger.error("problem removing non-relevant readings based on value descriptor:  " + e.getMessage());
			throw e;
		}
	}

	private ExportMessage filterReadings(ExportMessage exportMsg, List<String> vdIdentifiers) {
		if (hasMatching(exportMsg.getEvent(), vdIdentifiers)) {
			return stripNonApplicableReadings(exportMsg, vdIdentifiers);
		}
		return null; // no matching readings so just return null
	}

	private boolean hasMatching(Event event, List<String> vdIdentifiers) {
		try {
			return !Collections.disjoint(vdIdentifiers,
					event.getReadings().stream().map(r -> r.getName()).collect(Collectors.toList()));
		} catch (Exception e) {
			logger.error(
					"Problem getting event readings or associated value descriptor and checking against registration filters:  "
							+ e.getMessage());
			return false;
		}
	}

	private ExportMessage stripNonApplicableReadings(ExportMessage exportMsg, List<String> vdIdentifiers) {
		List<Reading> matchingReadings = exportMsg.getEvent().getReadings().stream()
				.filter(r -> vdIdentifiers.contains(r.getName())).collect(Collectors.toList());
		exportMsg.getEvent().setReadings(matchingReadings);
		return exportMsg;
	}

}
