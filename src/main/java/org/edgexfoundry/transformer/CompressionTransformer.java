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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;

import org.edgexfoundry.domain.export.ExportCompression;
import org.edgexfoundry.domain.export.ExportString;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;

/**
 * Compress the event message string via client selected compression algorithm.
 * There may be no compression.
 * 
 * @author jim_white
 *
 */
@MessageEndpoint
public class CompressionTransformer {

	private static final String LOCAL_ENCODING = "UTF-8";
	private static final String ISO_ENCODING = "ISO-8859-1";

	// private static final Logger logger =
	// Logger.getLogger(CompressionTransformer.class);
	// replace above logger with EdgeXLogger below
	private final static org.edgexfoundry.support.logging.client.EdgeXLogger logger = org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
			.getEdgeXLogger(CompressionTransformer.class);

	@Transformer(inputChannel = "outbound-eventstring", outputChannel = "outbound-compressed-eventstring")
	public ExportString transform(Message<?> msg) throws Exception {
		try {
			ExportString exportString = (ExportString) msg.getPayload();
			logger.debug("message arrived at compression transformer: " + exportString.getEventId());
			ExportCompression compression = exportString.getRegistration().getCompression();
			if (compression != null)
				switch (compression) {
				case GZIP:
					exportString.setEventString(gzipCompression(exportString.getEventString()));
					break;
				case ZIP:
					exportString.setEventString(zipCompression(exportString.getEventString()));
					break;
				case NONE:
					break;
				}
			logger.debug("message leaving compression transformer " + exportString.getEventId());
			return exportString;
		} catch (Exception e) {
			logger.error("Problem with compression transformation: " + e.getMessage());
			throw e;
		}
	}

	private String gzipCompression(String input) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(input.getBytes(LOCAL_ENCODING));
		gzip.close();
		String gzipString = out.toString(ISO_ENCODING);
		return new String(Base64.getEncoder().encode(gzipString.getBytes(LOCAL_ENCODING)), LOCAL_ENCODING);
	}

	private String zipCompression(String data) throws UnsupportedEncodingException, IOException {
		Deflater zipDeflater = new Deflater();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			zipDeflater.setInput(getBytes(data));
			zipDeflater.finish();
			byte[] buffer = new byte[1024];
			int count = 0;
			while (!zipDeflater.finished()) {
				count = zipDeflater.deflate(buffer);
				stream.write(buffer, 0, count);
			}
			return new String(Base64.getEncoder().encode(stream.toByteArray()), LOCAL_ENCODING);
		} finally {
			stream.close();
			zipDeflater.end();
		}
	}

	private byte[] getBytes(String data) throws UnsupportedEncodingException {
		return data.getBytes(LOCAL_ENCODING);
	}

}
