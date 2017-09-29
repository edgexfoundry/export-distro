package org.edgexfoundry.messaging;

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory; 
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory; 

public class EdgeXSecurityManager {
	private KeyStore keystore = null;
	private KeyManagerFactory keyManagerFactory = null;
	private TrustManagerFactory trustManagerFactory = null;
	private Exception failedInitializationCause = null;
	public EdgeXSecurityManager() throws IllegalStateException {
		super();
		
		try {
			keystore = KeyStore.getInstance("JKS");
			InputStream jksInputStream = EdgeXSecurityManager.class.getClassLoader().getResourceAsStream("/mqttkeystore.jks"); 
			keystore.load(jksInputStream, "storepassword".toCharArray()); 

			keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(keystore, "keypassword".toCharArray()); 

			trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()); 
            trustManagerFactory.init(this.keystore);

		} catch (Exception e) {
			failedInitializationCause = e;
			keystore = null;
			keyManagerFactory = null;
			trustManagerFactory = null;
			throw new IllegalStateException("SecurityManager failed to initialize.", failedInitializationCause);
		}
	}
	
	public KeyManager[] getKeyManagers() throws IllegalStateException {
		if (keyManagerFactory == null) {
			throw new IllegalStateException("SecurityManager failed to initialize.", failedInitializationCause);
		}
		return keyManagerFactory.getKeyManagers();
	}
	
	public TrustManager[] getTrustManagers() throws IllegalStateException {
		if (trustManagerFactory == null) {
			throw new IllegalStateException("SecurityManager failed to initialize.", failedInitializationCause);
		}
        return trustManagerFactory.getTrustManagers();
	}
}
