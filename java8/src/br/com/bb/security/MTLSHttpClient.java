package br.com.bb.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class MTLSHttpClient {

    public static String doRequest(String url, String clientCertificatePEM, String clientPrivateKeyPEM,
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
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        HostnameVerifier trustAllHostnames = new HostnameVerifier() {

            @Override
            public boolean verify(String hostname, SSLSession session) {

                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(trustAllHostnames);
        return executeGET(url, "");
    }

    static String executeGET(String targetURL, String urlParameters) {
        HttpsURLConnection connection = null;

        try {
            // Create connection
            URL url = new URL(targetURL);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.setUseCaches(false);

            // Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
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
