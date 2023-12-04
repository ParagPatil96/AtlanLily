package com.parag.lily.database.tables;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class CrdBooleanDeserializer extends StdDeserializer<Boolean> {
    public CrdBooleanDeserializer() {
        this(null);
    }

    public CrdBooleanDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Boolean deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        return node.asText().equalsIgnoreCase("t") || node.asText().equalsIgnoreCase("true");
    }
}
