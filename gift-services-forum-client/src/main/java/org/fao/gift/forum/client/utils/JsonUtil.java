package org.fao.gift.forum.client.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Retrieve a JsonNode, given the path of its ancestors' names
     *
     * @param jsonString a JSON string
     * @param nodes      the path of its ancestors' ad its own name as the last element (e.g. "payload", "uid")
     * @return the {@link JsonNode} of the specified element
     * @throws IOException in case of deserialization problems
     */
    public static JsonNode resolve(String jsonString, String... nodes) throws IOException {
        JsonNode tempNode = objectMapper.readValue(jsonString, JsonNode.class);
        for (String node : nodes) {
            tempNode = tempNode.get(node);
        }
        return tempNode;
    }
}
