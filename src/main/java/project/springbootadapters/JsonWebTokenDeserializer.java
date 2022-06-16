package project.springbootadapters;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class JsonWebTokenDeserializer extends StdDeserializer<JsonWebToken> {
    public JsonWebTokenDeserializer() {
        this(null);
    }

    protected JsonWebTokenDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public JsonWebToken deserialize(JsonParser jsonParser, DeserializationContext ctxt)
            throws IOException {
        JsonNode jwtNode = jsonParser.getCodec().readTree(jsonParser);
        String jwt = jwtNode.get("jwt").textValue();
        return new JsonWebToken(jwt);
    }
}
