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
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class MTLSHttpClient {

    public static HttpResponse<String> doRequest(String url, String clientCertificatePEM, String clientPrivateKeyPEM,
            String clientePrivateKeyPassphrase, String serverCA, boolean localhostEnabled)
            throws IOException, InterruptedException, KeyStoreException, KeyManagementException,
            NoSuchAlgorithmException, CertificateException, InvalidKeySpecException, UnrecoverableKeyException {

        // Client Auth Certs
        PrivateKey clientPrivateKey;
        try {
            clientPrivateKey = PEMConverter.generateECPrivateKeyFromDER(clientPrivateKeyPEM);
        } catch (InvalidKeySpecException ignore) {
            clientPrivateKey = PEMConverter.generateRSAPrivateKeyFromDER(clientPrivateKeyPEM);
        }
        X509Certificate clientCertificate = PEMConverter.generateCertificateFromDER(clientCertificatePEM);
        KeyStore keyStoreClient = KeyStore.getInstance("JKS");
        keyStoreClient.load(null, null); // won't load from a file
        keyStoreClient.setKeyEntry("client-key", clientPrivateKey, clientePrivateKeyPassphrase.toCharArray(),
                new X509Certificate[] { clientCertificate });
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
        SelfCATrustManager stm = new SelfCATrustManager((X509TrustManager) trustManagers[0], localhostEnabled);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, new TrustManager[] { stm }, null);

        HttpClient httpClient = HttpClient.newBuilder().sslContext(sslContext).build();
        HttpRequest requestBuilder = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

        return httpClient.send(requestBuilder, HttpResponse.BodyHandlers.ofString()); // sends the request

    }

    private static class SelfCATrustManager implements X509TrustManager {

        private final X509TrustManager tm;
        private boolean runLocal;

        SelfCATrustManager(X509TrustManager tm, boolean localhostEnabled) {
            this.tm = tm;
            this.runLocal = localhostEnabled;
        }

        public X509Certificate[] getAcceptedIssuers() {
            if (this.runLocal) {
                return new X509Certificate[0];
            }
            return tm.getAcceptedIssuers();
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            throw new UnsupportedOperationException();
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            if (!this.runLocal) {
                tm.checkServerTrusted(chain, authType);
            }
        }
    }
}
