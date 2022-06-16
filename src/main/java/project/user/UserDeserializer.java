package project.user;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserDeserializer extends StdDeserializer<User> {
    public UserDeserializer() {
        this(null);
    }

    protected UserDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public User deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode userNode = jsonParser.getCodec().readTree(jsonParser);
        String userId = userNode.get("id").textValue();
        String userName = userNode.get("userName").textValue();
        String givenName = userNode.get("givenName").textValue();
        String familyName = userNode.get("familyName").textValue();
        boolean isCustomer = userNode.get("isCustomer").asBoolean();
        boolean isReceptionist = userNode.get("isReceptionist").asBoolean();
        boolean isHealthcareWorker = userNode.get("isHealthcareWorker").asBoolean();

        List<String> allBookingIds = new ArrayList<>();
        for (Iterator<JsonNode> it = userNode.get("bookings").elements(); it.hasNext(); ) {
            JsonNode bookingNode = it.next();
            allBookingIds.add(bookingNode.get("id").textValue());
        }

        return new User(
                userId, userName, givenName, familyName,
                isCustomer, isReceptionist, isHealthcareWorker, allBookingIds
        );
    }
}
