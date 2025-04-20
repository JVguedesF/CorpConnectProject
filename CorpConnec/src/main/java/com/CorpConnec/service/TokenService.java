package com.CorpConnec.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.CorpConnec.exception.KeyLoadingException;
import com.CorpConnec.model.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Service
public class TokenService {

    private static final String KEY_HEADER = "-----BEGIN PRIVATE KEY-----";
    private static final String KEY_FOOTER = "-----END PRIVATE KEY-----";
    private static final String RSA_ALGORITHM = "RSA";

    private final RSAPrivateKey privateKey;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${security.jwt.token.expire-length}")
    private long expirationMillis;

    public TokenService(@Value("${jwt.private.key}") String privateKeyPath) {
        this.privateKey = loadPrivateKey(privateKeyPath);
    }

    public String generateAccessToken(User user) {

        Algorithm algorithm = Algorithm.RSA256(null, privateKey);
        Instant now = Instant.now();

        return JWT.create()
                .withSubject(user.getId().toString())
                .withIssuer(issuer)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusMillis(expirationMillis)))
                .withClaim("userId", user.getId().toString())
                .withClaim("username", user.getUsername())
                .withClaim("email", user.getEmail())
                .withClaim("role", user.getRole().name())
                .sign(algorithm);
    }

    private RSAPrivateKey loadPrivateKey(String path) {
        try {
            String keyContent = readKeyContent(path);
            PKCS8EncodedKeySpec keySpec = createKeySpec(keyContent);
            return createPrivateKey(keySpec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new KeyLoadingException("Falha na inicialização do serviço de tokens: " + e.getMessage(), e);
        }
    }

    private String readKeyContent(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return new String(resource.getInputStream().readAllBytes())
                .replace(KEY_HEADER, "")
                .replace(KEY_FOOTER, "")
                .replaceAll("\\s", "");
    }

    private PKCS8EncodedKeySpec createKeySpec(String keyContent) {
        return new PKCS8EncodedKeySpec(Base64.getDecoder().decode(keyContent));
    }

    private RSAPrivateKey createPrivateKey(PKCS8EncodedKeySpec keySpec) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }
}