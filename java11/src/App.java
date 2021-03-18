import java.net.http.HttpResponse;

import br.com.bb.security.MTLSHttpClient;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Java 11");

        String clientCertificatePEM = "-----BEGIN CERTIFICATE-----\nMIIBnTCCAUKgAwIBAgIUChyT2Cfn6UzQ46O3z8j9zo/vr5gwCgYIKoZIzj0EAwIw\nEzERMA8GA1UEAxMIbXRscy5kZXYwHhcNMjEwMzE4MTgwMzAwWhcNMjIwMzE4MTgw\nMzAwWjASMRAwDgYDVQQDEwdTYW5kbWFuMFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcD\nQgAEA8B5onZvmwT0Lsnxo4vbflKF79MP4xw0GvQa8TX6M675Uk09FTsqWxSdJGOb\n1ZLC6rzwa5DUblYBjCR6q0sVF6N1MHMwDgYDVR0PAQH/BAQDAgWgMBMGA1UdJQQM\nMAoGCCsGAQUFBwMCMAwGA1UdEwEB/wQCMAAwHQYDVR0OBBYEFP6Z0m7+ey59Aq12\npH73hyBJyTHtMB8GA1UdIwQYMBaAFAGdIruEIxp1V0wffo9YoK62HtvVMAoGCCqG\nSM49BAMCA0kAMEYCIQCgYv4A1Gq3f9DTNY3CUO0DP3LiMRZ/8fTbh1/g3Mm34AIh\nAMmzEkSMNzBDr99WNls0t0cZJWHW8c2844uxJcEbCNjN\n-----END CERTIFICATE-----";
        String clientPrivateKeyPEM = "-----BEGIN PRIVATE KEY-----\nMIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgT1k7IzW/nRg4JoLq\nGdnsjzyqTkD3A58/tgAQ1WWKA3WhRANCAAQDwHmidm+bBPQuyfGji9t+UoXv0w/j\nHDQa9BrxNfozrvlSTT0VOypbFJ0kY5vVksLqvPBrkNRuVgGMJHqrSxUX\n-----END PRIVATE KEY-----";
        String clientePrivateKeyPassphrase = "";
        String serverCertificate = "-----BEGIN CERTIFICATE-----\nMIIB4DCCAYegAwIBAgIUDJOyF5i6qWe6nF3tWZ9qGgLnCyMwCgYIKoZIzj0EAwIw\nEzERMA8GA1UEAxMIbXRscy5kZXYwHhcNMjEwMzE4MTgwMzAwWhcNMjIwMzE4MTgw\nMzAwWjAkMSIwIAYDVQQDExlzaWRlY2FyLm10bHMubGFiYnMuY29tLmJyMFkwEwYH\nKoZIzj0CAQYIKoZIzj0DAQcDQgAE5i9g8JuZljybB1h082MAHMdAEuJAl2K634hK\nwrCRhtMu+JmUB5ygG46TZBV6nDcY9uUuLkOocy+KsIiSzG0+3qOBpzCBpDAOBgNV\nHQ8BAf8EBAMCBaAwEwYDVR0lBAwwCgYIKwYBBQUHAwEwDAYDVR0TAQH/BAIwADAd\nBgNVHQ4EFgQURAtvgTT3/GY2vV1e4etWL4fCV38wHwYDVR0jBBgwFoAUAZ0iu4Qj\nGnVXTB9+j1igrrYe29UwLwYDVR0RBCgwJoIJbG9jYWxob3N0ghlzaWRlY2FyLm10\nbHMubGFiYnMuY29tLmJyMAoGCCqGSM49BAMCA0cAMEQCIF3EO8rYG+gP/XlptMmf\nLypVK0Rxqdm92wn6v1Xu9xzfAiB/TwMmJB5sA7yPboshaj/FweBSxntsC4QC5Lgl\n3eZUrQ==\n-----END CERTIFICATE-----";

        String url = "";
        if (args.length > 0 && args[0].contains("--url")) {
            url = args[0].split("=")[1];
        } else {
            System.out.println("You must provide de --url flag");
            System.exit(3);
        }
        boolean localhostEnabled = args.length > 1 && args[1].equalsIgnoreCase("--localhostEnabled");
        System.out.println("Target Endpoint:" + url);
        HttpResponse<String> resp = MTLSHttpClient.doRequest(url, clientCertificatePEM, clientPrivateKeyPEM,
                clientePrivateKeyPassphrase, serverCertificate, localhostEnabled);

        System.out.println(resp.body());
    }
}
