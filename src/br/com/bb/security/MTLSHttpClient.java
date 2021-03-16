package br.com.bb.security;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class MTLSHttpClient {

    public static HttpResponse<String> doRequest(String url, String clientCertificatePEM, String clientPrivateKeyPEM,
            String clientePrivateKeyPassphrase, String serverCA)
            throws IOException, InterruptedException, KeyStoreException, KeyManagementException,
            NoSuchAlgorithmException, CertificateException, InvalidKeySpecException, UnrecoverableKeyException {

        // Client Auth Certs
        X509Certificate clientCertificate = PEMConverter.generateCertificateFromDER(clientCertificatePEM);
        RSAPrivateKey clientPrivateKey = PEMConverter.generatePrivateKeyFromDER(clientPrivateKeyPEM);
        KeyStore keyStoreClient = KeyStore.getInstance("JKS");
        keyStoreClient.load(null, null); // won't load from a file
        keyStoreClient.setCertificateEntry("client-cert", clientCertificate);
        keyStoreClient.setKeyEntry("client-key", clientPrivateKey, clientePrivateKeyPassphrase.toCharArray(), null);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStoreClient, clientePrivateKeyPassphrase.toCharArray());
        KeyManager[] keyManagers = kmf.getKeyManagers();

        // Server Trust Certs
        X509Certificate serverCertificate = PEMConverter.generateCertificateFromDER(serverCA);
        KeyStore keyStoreServer = KeyStore.getInstance("JKS");
        keyStoreServer.load(null, null); // won't load from a file
        keyStoreServer.setCertificateEntry("server-cert", serverCertificate);

        TrustManagerFactory trustManagerFactory = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStoreServer);

        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustManagers, null);

        HttpClient httpClient = HttpClient.newBuilder().sslContext(sslContext).build();
        HttpRequest requestBuilder = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

        return httpClient.send(requestBuilder, HttpResponse.BodyHandlers.ofString()); // sends the request

    }
}
