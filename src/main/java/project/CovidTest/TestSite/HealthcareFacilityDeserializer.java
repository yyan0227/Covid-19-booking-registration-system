package project.CovidTest.TestSite;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

/**
 * Mapping with custom deserializer.
 * https://www.baeldung.com/jackson-nested-values
 */
public class HealthcareFacilityDeserializer extends StdDeserializer<HealthcareFacility> {

    public HealthcareFacilityDeserializer() {
        this(null);
    }

    protected HealthcareFacilityDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public HealthcareFacility deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        JsonNode facilityNode = jsonParser.getCodec().readTree(jsonParser);
        return new HealthcareFacility(
                facilityNode.get("id").textValue(), facilityNode.get("name").textValue(),
                facilityNode.get("description").textValue(),
                facilityNode.get("address").get("latitude").intValue(),
                facilityNode.get("address").get("longitude").intValue(),
                facilityNode.get("address").get("unitNumber").textValue(),
                facilityNode.get("address").get("street").textValue(),
                facilityNode.get("address").get("street2").textValue(),
                facilityNode.get("address").get("suburb").textValue(),
                facilityNode.get("address").get("state").textValue(),
                facilityNode.get("address").get("postcode").textValue()
        );
    }
}
