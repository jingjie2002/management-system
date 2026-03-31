package com.ch.managementsystem.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Base64;
import java.util.function.Function;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        long now = System.currentTimeMillis();
        long exp = now + expiration;
        try {
            Map<String, Object> header = new HashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");

            Map<String, Object> payload = new HashMap<>(claims);
            payload.put("sub", subject);
            payload.put("iat", now / 1000);
            payload.put("exp", exp / 1000);

            String headerEncoded = base64UrlEncode(objectMapper.writeValueAsBytes(header));
            String payloadEncoded = base64UrlEncode(objectMapper.writeValueAsBytes(payload));
            String content = headerEncoded + "." + payloadEncoded;
            String signature = sign(content, secret);
            return content + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("Token generation failed", e);
        }
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new RuntimeException("Invalid token");
            }

            String content = parts[0] + "." + parts[1];
            String signature = sign(content, secret);
            if (!signature.equals(parts[2])) {
                throw new RuntimeException("Invalid signature");
            }

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            Map<String, Object> payload = objectMapper.readValue(payloadJson, new TypeReference<Map<String, Object>>() {});
            Claims claims = new Claims();
            claims.setSubject((String) payload.get("sub"));
            claims.setIssuedAt(new Date(((Number) payload.get("iat")).longValue() * 1000));
            claims.setExpiration(new Date(((Number) payload.get("exp")).longValue() * 1000));
            payload.forEach(claims::putIfAbsent);
            return claims;
        } catch (Exception e) {
            throw new RuntimeException("Token parse failed", e);
        }
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private String sign(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        return base64UrlEncode(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    private String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static class Claims extends HashMap<String, Object> {
        public String getSubject() {
            return (String) get("sub");
        }

        public Date getExpiration() {
            Object value = get("exp");
            if (value instanceof Number number) {
                return new Date(number.longValue() * 1000);
            }
            return null;
        }

        public void setSubject(String subject) {
            put("sub", subject);
        }

        public void setIssuedAt(Date issuedAt) {
            put("iat", issuedAt.getTime() / 1000);
        }

        public void setExpiration(Date expiration) {
            put("exp", expiration.getTime() / 1000);
        }
    }
}
