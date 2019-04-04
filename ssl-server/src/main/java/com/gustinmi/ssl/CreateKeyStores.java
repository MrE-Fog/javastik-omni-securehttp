package com.gustinmi.ssl;

import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import javax.security.auth.x500.X500PrivateCredential;
import com.gustinmi.cryptotest.Utils;

/**
 * Create the various credentials for an SSL session
 * Refactored class to use undeprecated librarires
 */
public class CreateKeyStores {

    public static X500PrivateCredential createRootCredential() throws Exception {
        X500PrivateCredential rootCredential = Utils.createRootCredential();
        // server credentials
        KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
        keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null, null);
        keyStore.setKeyEntry(Utils.SERVER_NAME, rootCredential.getPrivateKey(), Utils.SERVER_PASSWORD, new Certificate[] { rootCredential.getCertificate() });
        keyStore.store(new FileOutputStream(Utils.SERVER_NAME + ".jks"), Utils.SERVER_PASSWORD);
        return rootCredential;
    }

    public static X500PrivateCredential createIntermediateCredential(X500PrivateCredential rootCredential) throws Exception {
        X500PrivateCredential interCredential = Utils.createIntermediateCredential(rootCredential.getPrivateKey(), rootCredential.getCertificate());
        return interCredential;
    }

    public static X500PrivateCredential createEndentityCredential(X500PrivateCredential interCredential) throws Exception {
        X500PrivateCredential endCredential = Utils.createEndEntityCredential(interCredential.getPrivateKey(), interCredential.getCertificate());
        return endCredential;
    }

    public static void createAll() throws Exception {

        X500PrivateCredential rootCredential = createRootCredential();
        X500PrivateCredential interCredential = createIntermediateCredential(rootCredential);
        X500PrivateCredential endCredential = createEndentityCredential(interCredential);

        // client credentials
        KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
        keyStore.load(null, null);
        keyStore.setKeyEntry(Utils.CLIENT_NAME, endCredential.getPrivateKey(), Utils.CLIENT_PASSWORD,
                new Certificate[] {
                    endCredential.getCertificate(),
                    interCredential.getCertificate(),
                    rootCredential.getCertificate() });

        keyStore.store(new FileOutputStream(Utils.CLIENT_NAME + ".p12"), Utils.CLIENT_PASSWORD);

        // trust store for client
        keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null, null);
        keyStore.setCertificateEntry(Utils.SERVER_NAME, rootCredential.getCertificate());
        keyStore.store(new FileOutputStream(Utils.TRUST_STORE_NAME + ".jks"), Utils.TRUST_STORE_PASSWORD);

        //        // server credentials
        //        keyStore = KeyStore.getInstance("JKS");
        //        keyStore.load(null, null);
        //        keyStore.setKeyEntry(Utils.SERVER_NAME, rootCredential.getPrivateKey(), Utils.SERVER_PASSWORD, new Certificate[] { rootCredential.getCertificate() });
        //        keyStore.store(new FileOutputStream(Utils.SERVER_NAME + ".jks"), Utils.SERVER_PASSWORD);
    }

    public static void main(String[] args) throws Exception {
        createAll();
    }
}
