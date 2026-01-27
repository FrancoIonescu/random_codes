import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.FileReader;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

public class CertificateGenerator {
    public static void main(String[] args) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        // 1. Incarcă Cheia Publică din pubISM.pem
        PublicKey publicKey = loadPublicKey("pubISM.pem");
        
        // NOTĂ: Pentru a semna un certificat, ai nevoie de cheia PRIVATĂ pereche
        // Aici generez una temporară doar pentru exemplul de semnare
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        PrivateKey privateKey = keyGen.generateKeyPair().getPrivate();

        // 2. Detalii Certificat
        X500Name issuer = new X500Name("CN=ISM-CA, O=Facultate, L=Bucuresti, C=RO");
        X500Name subject = new X500Name("CN=UtilizatorISM, O=Student, L=Bucuresti, C=RO");
        BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());
        Date notBefore = new Date();
        Date notAfter = new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000); // 1 an

        // 3. Construiește Certificatul
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                issuer, serial, notBefore, notAfter, subject, publicKey);

        // 4. Semnează Certificatul
        ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSA").build(privateKey);
        X509CertificateHolder holder = certBuilder.build(signer);

        // 5. Convertește în obiect X509Certificate standard
        X509Certificate cert = new JcaX509CertificateConverter().getCertificate(holder);

        System.out.println("Certificat generat cu succes pentru: " + cert.getSubjectDN());
        System.out.println("Valid până la: " + cert.getNotAfter());
    }

    private static PublicKey loadPublicKey(String filename) throws Exception {
        try (PemReader reader = new PemReader(new FileReader(filename))) {
            PemObject pemObject = reader.readPemObject();
            X509EncodedKeySpec spec = new X509EncodedKeySpec(pemObject.getContent());
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(spec);
        }
    }
}
