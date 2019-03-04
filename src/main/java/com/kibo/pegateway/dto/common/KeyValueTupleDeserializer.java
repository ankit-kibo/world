package com.kibo.pegateway.dto.common;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Overrides the KeyValueTuple serializer
 * to convert the values into a map.
 */
public class KeyValueTupleDeserializer extends JsonDeserializer<HashMap<String, Object>> {
    @Override
    public HashMap<String, Object> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        HashMap<String, Object> ret = new HashMap<>();
        ObjectCodec codec = jsonParser.getCodec();
        JsonNode node = codec.readTree(jsonParser);
        Iterator<JsonNode> it = node.iterator();
        while (it.hasNext()) {
            JsonNode mapNode = it.next();
            JsonNode value = mapNode.get("value");
            Object o = null;
            if(value.isArray()) {
                Iterator<JsonNode> subit = value.iterator();
                List<String> subitList = new ArrayList();
                while(subit.hasNext()) {
                    subitList.add(subit.next().asText());
                }
                o = subitList;
            } else {
                o = value.asText();
            }
            ret.put(mapNode.get("key").asText(), o);
        }
        return ret;
    }
}
