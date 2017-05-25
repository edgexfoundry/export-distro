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
package org.edgexfoundry.messaging;

import org.edgexfoundry.domain.meta.Addressable;

import com.microsoft.azure.iothub.DeviceClient;
import com.microsoft.azure.iothub.IotHubClientProtocol;
import com.microsoft.azure.iothub.IotHubEventCallback;
import com.microsoft.azure.iothub.IotHubStatusCode;
import com.microsoft.azure.iothub.Message;

public class AzureMQTTSender {

	private final static String HOST_NAME = "HostName=";
	private final static String DEVICE_ID = ";DeviceId=";
	private final static String SHARED_ACCESS = ";SharedAccessKey=";

	private final static org.edgexfoundry.support.logging.client.EdgeXLogger logger = 
			org.edgexfoundry.support.logging.client.EdgeXLoggerFactory.getEdgeXLogger(AzureMQTTSender.class);

	private DeviceClient client;
	private StringBuffer connectionString;

	protected static class EventCallback implements IotHubEventCallback {
		public void execute(IotHubStatusCode status, Object context) {
			logger.info("IoT Hub responded to message with status " + status.name());
			if (context != null) {
				synchronized (context) {
					context.notify();
				}
			}
		}
	}

	public AzureMQTTSender(Addressable addressable, String deviceId) {
		logger.debug("Creating Azure MQTT Sendor");
		this.connectionString = new StringBuffer(HOST_NAME);
		this.connectionString.append(addressable.getAddress());
		this.connectionString.append(DEVICE_ID);
		if (deviceId != null)
			this.connectionString.append(deviceId.replaceAll("\\s+", "_").replaceAll("\\:+", "_"));
		else
			this.connectionString.append(deviceId);
		this.connectionString.append(SHARED_ACCESS);
		this.connectionString.append(addressable.getPassword());
		logger.debug("Preparing to send to: " + this.connectionString);
	}

	public boolean sendMessage(byte[] messagePayload) {
		logger.debug("Starting IoT Hub distro...");
		logger.debug("Beginning IoT Hub setup.");
		try {
			logger.debug("Azure connect with:  " + connectionString);
			client = new DeviceClient(connectionString.toString(), IotHubClientProtocol.MQTT);
			logger.debug("Successfully created an IoT Hub client.");
			client.open();
			logger.debug("Opened connection to IoT Hub.");
			Message msg = new Message(messagePayload);
			msg.setExpiryTime(5000);
			Object lockobj = new Object();
			EventCallback callback = new EventCallback();
			client.sendEventAsync(msg, callback, lockobj);
			synchronized (lockobj) {
				lockobj.wait();
			}
			client.close();
			logger.debug("Shutting down...");
			return true;
		} catch (Exception e) {
			logger.error("Failure: " + e.toString());
		}
		return false;
	}
}
