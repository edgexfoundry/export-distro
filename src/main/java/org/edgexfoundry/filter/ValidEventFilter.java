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

import org.edgexfoundry.controller.ValueDescriptorClient;
import org.edgexfoundry.domain.common.IoTType;
import org.edgexfoundry.domain.common.ValueDescriptor;
import org.edgexfoundry.domain.core.Event;
import org.edgexfoundry.domain.core.Reading;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.Filter;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.core.MessageSelector;
import org.springframework.messaging.Message;

/**
 * Check that reading value is in line with associated value descriptor
 * information. Reject message if not. Bean providing logic for filter that
 * checks for events with bad readings, per value descriptor information
 * 
 * @author Jim
 *
 */
@MessageEndpoint
public class ValidEventFilter implements MessageSelector {

	@Value("${valuedescriptor.check}")
	private boolean conductCheck;

	@Autowired
	private ValueDescriptorClient vdClient;

	// private static final Logger logger =
	// Logger.getLogger(ValidEventFilter.class);
	// replace above logger with EdgeXLogger below
	private final static org.edgexfoundry.support.logging.client.EdgeXLogger logger = 
			org.edgexfoundry.support.logging.client.EdgeXLoggerFactory.getEdgeXLogger(ValidEventFilter.class);

	@SuppressWarnings("unchecked")
	@Override
	@Filter(inputChannel = "export-inboud-events", outputChannel = "valid-inbound-events", discardChannel = "invalid-inbound-events")
	public boolean accept(Message<?> msg) {
		try {
			boolean accepted = false;
			logger.debug("message arrived at valid event filter");
			if (conductCheck) {
				Message<Event> eventMsg = (Message<Event>) msg;
				Event event = eventMsg.getPayload();
				logger.debug("...event entering validation check in valid event filter: " + event.getId());
				accepted = checkValueDescriptors(event);
			} else
				accepted = true;
			logger.debug("message leaving at valid event filter");
			if (!accepted)
				logger.error("Event rejected - readings not matching Value Descriptor expectations.");
			return accepted;
		} catch (Exception e) {
			logger.error("problem checking all readings for valid value descriptor:  " + e.getMessage());
			return false;
			// throw e;
		}
	}

	public void setConductCheck(boolean conductCheck) {
		this.conductCheck = conductCheck;
	}

	private boolean checkValueDescriptors(Event event) {
		boolean readingsApproved = true;
		if (event.getReadings() == null)
			return false;
		for (Reading reading : event.getReadings()) {
			if (!isValidValueDescriptor(reading)) {
				readingsApproved = false;
				break;
			}
		}
		return readingsApproved;
	}

	/**
	 * This task is expensive!!! It is a REST call into core data. Then it must
	 * do a lot of string parsing to insure data meets the required Value
	 * Descriptor parameters. Should only be turned on for dev and test.
	 * 
	 * @param reading
	 *            (from the Event being exported)
	 * @return boolean indicating whether data in the Reading is square with
	 *         expected values per the ValueDescriptor
	 */
	private boolean isValidValueDescriptor(Reading reading) {
		ValueDescriptor vd = vdClient.valueDescriptorByName(reading.getName());
		if (vd == null) {
			logger.error("Reading rejected - " + reading.getValue() + " no value descriptor found for the reading.");
			return false;
		}
		IoTType type = vd.getType();
		switch (type) {
		case B: // boolean
			return validBoolean(reading);
		case F: // floating point
			return validFloat(reading, vd);
		case I: // integer
			return validInteger(reading, vd);
		case S: // string or character data
			return validString(reading);
		case J: // JSON data
			return validJSON(reading);
		}
		// default case
		logger.error("Reading rejected - " + reading.getValue() + " unknown value descriptor type.");
		return false;
	}

	private boolean validBoolean(Reading reading) {
		try {
			Boolean.parseBoolean(reading.getValue());
			return true;
		} catch (Exception e) {
			logger.error("Reading rejected - " + reading.getValue() + " not a boolean as expected.");
			return false;
		}
	}

	private boolean validFloat(Reading reading, ValueDescriptor vd) {
		try {
			double val = Double.parseDouble(reading.getValue());
			if (vd.getMax() != null && vd.getMin() != null) {
				double max = Double.parseDouble(vd.getMax().toString());
				double min = Double.parseDouble(vd.getMin().toString());
				if ((val <= max) && (val >= min))
					return true;
				logger.error("Reading rejected - " + reading.getValue() + " not within min " + min + " and max " + max
						+ " range as expected.");
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.error("Reading rejected - " + reading.getValue() + " not a floating point as expected.");
			return false;
		}
	}

	private boolean validInteger(Reading reading, ValueDescriptor vd) {
		try {
			int val = Integer.parseInt(reading.getValue());
			if (vd.getMax() != null && vd.getMin() != null) {
				int max = Integer.parseInt(vd.getMax().toString());
				int min = Integer.parseInt(vd.getMin().toString());
				if ((val <= max) && (val >= min))
					return true;
				logger.error("Reading rejected - " + reading.getValue() + " not within min " + min + " and max " + max
						+ " range as expected.");
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.error("Reading rejected - " + reading.getValue() + " not an integer as expected.");
			return false;
		}
	}

	private boolean validString(Reading reading) {
		boolean isEmpty = reading.getValue().isEmpty();
		if (isEmpty) {
			logger.error("Reading rejected - " + reading.getValue() + " string value is empty.");
			return false;
		}
		return true;
	}

	private boolean validJSON(Reading reading) {
		try {
			JSONParser parser = new JSONParser();
			parser.parse(reading.getValue());
			return true;
		} catch (Exception e) {
			logger.error("Reading rejected - " + reading.getValue() + " not a valid JSON string as expected.");
			return false;
		}
	}
}
