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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.edgexfoundry.domain.export.EncryptionDetails;
import org.edgexfoundry.domain.export.ExportString;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;

/**
 * Encrypt the event message via client selected encryption algorithm. There may
 * be no encryption
 * 
 * @author jim_white
 *
 */
@MessageEndpoint
public class EncryptionTransformer {

	private final static String LOCAL_ENCODING = "UTF-8";
	private final static String MSG_DIGEST_HASH = "SHA-1";
	private final static String ENCRYPTION_ALGORITHM = "AES";
	private final static String CIPHER_TRX = "AES/CBC/PKCS5Padding";

	// private static final Logger logger =
	// Logger.getLogger(EncryptionTransformer.class);
	// replace above logger with EdgeXLogger below
	private final static org.edgexfoundry.support.logging.client.EdgeXLogger logger = org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
			.getEdgeXLogger(EncryptionTransformer.class);

	@Transformer(inputChannel = "outbound-compressed-eventstring", outputChannel = "outbound-encrypted-eventstring")
	public ExportString transform(Message<?> msg) throws Exception {
		try {
			ExportString exportString = (ExportString) msg.getPayload();
			logger.debug("message arrived at encryption transformer: " + exportString.getEventId());
			EncryptionDetails encryptDetails = exportString.getRegistration().getEncryption();
			if (encryptDetails != null) {
				switch (encryptDetails.getEncryptionAlgorithm()) {
				case AES:
					exportString.setEventString(getEncryptedString(exportString.getEventString(),
							encryptDetails.getEncryptionKey(), encryptDetails.getInitializingVector()));
					break;
				case NONE:
					break;
				}
			}
			logger.debug("message leaving encryption transformer " + exportString.getEventId());
			return exportString;
		} catch (Exception e) {
			logger.error("Problem with encryption transformation: " + e.getMessage());
			throw e;
		}
	}

	private String getEncryptedString(String data, String encryptionKey, String vector) throws Exception {
		MessageDigest msgDigest = MessageDigest.getInstance(MSG_DIGEST_HASH);
		SecretKeySpec keySpec = new SecretKeySpec(first128Bits(msgDigest.digest(getBytes(encryptionKey))),
				ENCRYPTION_ALGORITHM);
		IvParameterSpec initVectorSpec = new IvParameterSpec(first128Bits(getBytes(vector)));
		Cipher cipher = Cipher.getInstance(CIPHER_TRX);
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, initVectorSpec);
		return new String(Base64.getEncoder().encode(cipher.doFinal(getBytes(data))), LOCAL_ENCODING);
	}

	private byte[] getBytes(String data) throws UnsupportedEncodingException {
		return data.getBytes(LOCAL_ENCODING);
	}

	private byte[] first128Bits(byte[] array) {
		return Arrays.copyOf(array, 16);
	}
}
