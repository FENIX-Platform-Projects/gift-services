package org.fao.gift.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class JwtUtils {

    public static String generateJwt(Map<String, Object> claims, String secret) {
        try {
            Algorithm algorithmHS = Algorithm.HMAC256(secret);
            JWTCreator.Builder builder = JWT.create();
            for (Map.Entry<String, Object> claim : claims.entrySet()) {
                builder.withClaim(claim.getKey(), String.valueOf(claim.getValue()));
            }
            return builder.sign(algorithmHS);

        } catch (UnsupportedEncodingException e) {
            return "ERROR " + e.getMessage();
        }
    }
}
