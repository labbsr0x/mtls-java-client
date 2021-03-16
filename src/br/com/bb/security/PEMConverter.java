package br.com.bb.security;

import java.io.ByteArrayInputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class PEMConverter {

    public static X509Certificate generateCertificateFromDER(String pemCertificate) throws CertificateException {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");

        return (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(pemCertificate.getBytes()));
    }

    public static RSAPrivateKey generatePrivateKeyFromDER(String pemKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        // PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(pemKey.getBytes());

        // KeyFactory factory = KeyFactory.getInstance("RSA");

        // return (RSAPrivateKey) factory.generatePrivate(spec);

        String privateKeyPEM = pemKey.replace("-----BEGIN PRIVATE KEY-----", "").replaceAll(System.lineSeparator(), "")
                .replace("-----END PRIVATE KEY-----", "");

        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }
}
