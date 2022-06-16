package project.booking;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

/**
 * Mapping with custom deserializer.
 * https://www.baeldung.com/jackson-nested-values
 */
public class BookingDeserializer extends StdDeserializer<Booking> {

    public BookingDeserializer() {
        this(null);
    }

    protected BookingDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Booking deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        JsonNode bookingNode = jsonParser.getCodec().readTree(jsonParser);
        Verification verification = null;
        if (bookingNode.get("additionalInfo") != null) {
            if (bookingNode.get("additionalInfo").get("QrCode") != null) {
                verification = new QRCode(
                        bookingNode.get("additionalInfo").get("QrCode").textValue()
                );
            }
        }

        if (verification == null) {
            verification = new PIN(bookingNode.get("smsPin").textValue());
        }

        String testSiteId = null;
        JsonNode testingSiteNode = bookingNode.get("testingSite");
        if (testingSiteNode != null && !testingSiteNode.isNull())
            testSiteId = bookingNode.get("testingSite").get("id").textValue();

        bookingNode.get("startTime").textValue();
        return new Booking(
                bookingNode.get("id").textValue(),
                bookingNode.get("customer").get("id").textValue(),
                testSiteId,
                bookingNode.get("startTime").textValue(),
                verification
        );
    }
}
