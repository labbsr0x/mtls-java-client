import java.net.http.HttpResponse;

import br.com.bb.security.MTLSHttpClient;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Java 11");

        var clientCertificatePEM = "-----BEGIN CERTIFICATE-----\nMIIBnjCCAUSgAwIBAgIUFONqtKB9w4E3iMUHv94n03hGy9swCgYIKoZIzj0EAwIw\nEzERMA8GA1UEAxMIbXRscy5kZXYwHhcNMjEwMzE3MTc0NTAwWhcNMjIwMzE3MTc0\nNTAwWjAUMRIwEAYDVQQDEwlsb2NhbGhvc3QwWTATBgcqhkjOPQIBBggqhkjOPQMB\nBwNCAATtsHTYeVE705oaoXaiZ64/nTJ9l7k9eCA3Y58BgbcUleYzJtxAztbbF1xK\nidZUbDdECVtNHpzo+lnEUo/KEy7Yo3UwczAOBgNVHQ8BAf8EBAMCBaAwEwYDVR0l\nBAwwCgYIKwYBBQUHAwIwDAYDVR0TAQH/BAIwADAdBgNVHQ4EFgQUfmVic1TWBX4+\nZ3gw4v5ZnYH2mE4wHwYDVR0jBBgwFoAU0iIAebLrjpXUxqUXD4p8Xm1TAbUwCgYI\nKoZIzj0EAwIDSAAwRQIgX4pZ96+2Xnu/YkCI2cOTMY2VrEyPKaikS7WWOPsQztIC\nIQDHA8GfJitFy/U7CRFKf6IKUDvHmbDbDlwJX9XBoDOOVQ==\n-----END CERTIFICATE-----";
        var clientPrivateKeyPEM = "-----BEGIN PRIVATE KEY-----\nMIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQglrsSXTZk9EL6wWkg\n70WRPhlRJkN0dI2jKMnITbzlWr6hRANCAATtsHTYeVE705oaoXaiZ64/nTJ9l7k9\neCA3Y58BgbcUleYzJtxAztbbF1xKidZUbDdECVtNHpzo+lnEUo/KEy7Y\n-----END PRIVATE KEY-----";
        var clientePrivateKeyPassphrase = "";
        var serverCA = "-----BEGIN CERTIFICATE-----\nMIIBwDCCAWagAwIBAgIUMMN7D/SMJN0G1MTNBtMabI1WGSEwCgYIKoZIzj0EAwIw\nEzERMA8GA1UEAxMIbXRscy5kZXYwHhcNMjEwMzE3MTc0NTAwWhcNMjIwMzE3MTc0\nNTAwWjAUMRIwEAYDVQQDEwlsb2NhbGhvc3QwWTATBgcqhkjOPQIBBggqhkjOPQMB\nBwNCAARa/qfz8XDD88fKqy3XTeOIjyDt5HWVNNQXeTaAKpyjiuG2PCu0wtZgt8JU\nlDwDNu0qfZlB/mqsflH8x+3Om9/9o4GWMIGTMA4GA1UdDwEB/wQEAwIFoDATBgNV\nHSUEDDAKBggrBgEFBQcDATAMBgNVHRMBAf8EAjAAMB0GA1UdDgQWBBTeEvt8HSyn\nq04FNBqPS/tXhBh+czAfBgNVHSMEGDAWgBTSIgB5suuOldTGpRcPinxebVMBtTAe\nBgNVHREEFzAVgglsb2NhbGhvc3SCCG10bHMuZGV2MAoGCCqGSM49BAMCA0gAMEUC\nIQDRcT/RBc+kQMkpr7GFOD6PR0L2Rm/C3uIEsHVsgJiTuwIgWy4vaQO0/V//BM1m\nRqSDzUrTeSUc24s6aivBM7Y+STU=\n-----END CERTIFICATE-----";

        String url = "";
        if (args.length > 0 && args[0].contains("--url")) {
            url = args[0].split("=")[1];
        } else {
            System.out.println("You must provide de --url flag");
            System.exit(3);
        }
        boolean localhostEnabled = args.length > 1 && args[1].equalsIgnoreCase("--localhostEnabled");
        HttpResponse<String> resp = MTLSHttpClient.doRequest(url, clientCertificatePEM, clientPrivateKeyPEM,
                clientePrivateKeyPassphrase, serverCA, localhostEnabled);

        System.out.println(resp.body());
    }
}
