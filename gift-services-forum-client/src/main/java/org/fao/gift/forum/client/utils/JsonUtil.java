package org.fao.gift.forum.client.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static JsonNode resolve(String jsonString, String... nodes) throws IOException {
        JsonNode tempNode = objectMapper.readValue(jsonString, JsonNode.class);
        for (String node : nodes) {
            tempNode = tempNode.get(node);
        }
        return tempNode;
    }
}
