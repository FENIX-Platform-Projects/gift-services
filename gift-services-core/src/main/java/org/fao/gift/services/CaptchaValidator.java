package org.fao.gift.services;

import org.fao.gift.commons.dto.MainConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;

@Stateless
public class CaptchaValidator {

    private static final Logger log = LoggerFactory.getLogger(CaptchaValidator.class);

    private static final String USER_AGENT = "Mozilla/5.0";

    @Inject
    MainConfig config;

    @Lock(LockType.READ)
    public boolean verify(String gRecaptchaResponse) {
        if (log.isDebugEnabled()) log.debug("verify - START - {}", gRecaptchaResponse);
        else log.info("verify - START");

        if (gRecaptchaResponse == null || gRecaptchaResponse.isEmpty()) {
            return false;
        }

        try {
            final String VERIFY_URL = config.get("gift.disclaimer.captcha.url");
            final String SECRET = config.get("gift.disclaimer.captcha.secret");

            log.info("Sending 'POST' request to URL: {}", VERIFY_URL);

            URL obj = new URL(VERIFY_URL);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            // Request header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            String postParams = "secret=" + SECRET + "&response=" + gRecaptchaResponse;

            // Send post request
            con.setDoOutput(true);
            StringBuffer response;

            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.writeBytes(postParams);
                wr.flush();
            }

            if (log.isDebugEnabled()) {
                log.debug("Sent 'POST' request to URL: {}, params: {}, response: {}", VERIFY_URL, postParams, con.getResponseCode());
            }

            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                response = new StringBuffer();

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

            log.info("Response: {}", response);

            //parse JSON response and return 'success' value
            try (JsonReader jsonReader = Json.createReader(new StringReader(response.toString()))) {
                JsonObject jsonObject = jsonReader.readObject();
                return jsonObject.getBoolean("success");
            }

        } catch (Exception e) {
            log.error("verify - ERROR", e);
            return false;
        }
    }
}
