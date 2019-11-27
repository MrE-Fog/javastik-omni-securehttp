package com.gustinmi.cryptotest.httpserv;

import static com.gustinmi.cryptotest.Utils.*;
import java.io.FileInputStream;
import java.security.KeyStore;
import javax.net.ssl.*;

/**
 * SSL Client with client-side authentication.
 */
public class SSLClientWithClientAuthTrustExample extends SSLClientExample {

    /**
     * Create an SSL context with both identity and trust store
     */
	static SSLContext createSSLContext() throws Exception {
		// set up a key manager for our local credentials
		KeyManagerFactory mgrFact = KeyManagerFactory.getInstance("SunX509");
		KeyStore clientStore = KeyStore.getInstance("PKCS12");

        clientStore.load(new FileInputStream("client.p12"), CLIENT_PASSWORD);

        mgrFact.init(clientStore, CLIENT_PASSWORD);

		// set up a trust manager so we can recognize the server
		TrustManagerFactory trustFact = TrustManagerFactory.getInstance("SunX509");
		KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(new FileInputStream("trustStore.jks"), TRUST_STORE_PASSWORD);
		trustFact.init(trustStore);

		// create a context and set up a socket factory
		SSLContext sslContext = SSLContext.getInstance("TLS");

		sslContext.init(mgrFact.getKeyManagers(), trustFact.getTrustManagers(), null);

		return sslContext;
	}

	public static void main(String[] args) throws Exception {

		SSLContext sslContext = createSSLContext();
		SSLSocketFactory fact = sslContext.getSocketFactory();
        SSLSocket cSock = (SSLSocket) fact.createSocket(HOST, PORT_NO);

		doProtocol(cSock);
	}
}