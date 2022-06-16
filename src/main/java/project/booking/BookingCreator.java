package project.booking;

import project.Engine.Database;
import org.json.JSONObject;
import java.util.Map;

/**
 * This class creates a booking at a health care facility for the user with the entered details.
 */
public class BookingCreator {
    /**
     * This method creates the booking with the details entered by a user
     *
     * @param userID Assuming the userID already exists in the web service.
     * @param additionalInfo Key-value pairs. These will be serialized into a JSON object and
     *                       stored under the "additionalInfo" section of the Booking JSON.
     *                       For example: one can store key=TestID, Value=TestID_Value.
     * @return A newly created Booking object.
     */
    public Booking createBooking(
            String userID, Map<String, String> optionalProperties, Map<String, String> additionalInfo
    ) {
        JSONObject json = new JSONObject(additionalInfo);
        JSONObject postRequestBody = new JSONObject();
        postRequestBody.put("customerId", userID);
        postRequestBody.put("additionalInfo", json);    // nested JSON

        for (String property : optionalProperties.keySet()) {
            // optional properties in the Http POST Booking JSON such as testSiteId & notes
            postRequestBody.put(property, optionalProperties.get(property));
        }

        return Database.createBooking(postRequestBody);
    }
}
