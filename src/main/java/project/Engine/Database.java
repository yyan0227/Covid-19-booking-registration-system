package project.Engine;

import project.springbootadapters.BookingMessage;
import project.Engine.Http.Http;
import project.Engine.Http.PatchAdapter;
import project.Engine.Http.PostAdapter;
import project.Engine.Http.SearchAdapter;
import project.CovidTest.TestSite.HealthcareFacility;
import project.CovidTest.TestSite.HealthcareFacilityDeserializer;
import project.CovidTest.TestSite.TestSite;
import project.booking.Booking;
import project.booking.BookingDeserializer;
import project.springbootadapters.JsonWebToken;
import project.springbootadapters.JsonWebTokenDeserializer;
import project.user.User;
import project.user.UserDeserializer;
import org.json.JSONObject;

import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

/**
 * This class contains the necessary information for the process of registering a test at a
 * healthcare facility. This includes bookings and available test sites. It also helps to create and
 * update bookings.
 */

public class Database {
    private static final String ROOT_URL = "<<PLACEHOLDER FOR API ROOT URL>>";
    private static final String TEST_SITE_ENDPOINT = "<<PLACEHOLDER FOR API ENDPOINT>>";
    private static final String USER_ENDPOINT = "<<PLACEHOLDER FOR API ENDPOINT>>";
    private static final String BOOKING_ENDPOINT = "<<PLACEHOLDER FOR API ENDPOINT>>";
    private static final String POST_BOOKING_ENDPOINT = "<<PLACEHOLDER FOR API ENDPOINT>>";
    private static List<Booking> allBookings;
    private static List<? extends TestSite> allTestSites;
    private static List<User> allUsers;

    /**
     * Retrieves all the available resources from the web service at compile time.
     */

    static {
        String getBookingEndpoint = ROOT_URL + BOOKING_ENDPOINT;
        allBookings = SearchAdapter.getAllAvailableObjectsOnWebService(
                getBookingEndpoint, Booking.class, new BookingDeserializer()
        );

        String getTestSiteEndpoint = ROOT_URL + TEST_SITE_ENDPOINT;
        allTestSites = SearchAdapter.getAllAvailableObjectsOnWebService(
                getTestSiteEndpoint, HealthcareFacility.class, new HealthcareFacilityDeserializer()
        );

        String getUsersEndPoint = ROOT_URL + USER_ENDPOINT + "?fields=bookings";
        allUsers = SearchAdapter.getAllAvailableObjectsOnWebService(
                getUsersEndPoint, User.class, new UserDeserializer()
        );
    }

    /**
     * This method gets all the available bookings in the system
     *
     * @return All bookings made by in the system
     */
    public static List<Booking> getAllBookings() {
        return Collections.unmodifiableList(allBookings);
    }

    /**
     * This method gets all the test sites in the system
     *
     * @return All test sites available in the system
     */
    public static List<? extends TestSite> getAllTestSites() {
        return Collections.unmodifiableList(allTestSites);
    }

    /**
     * This method gets all the users in the system
     *
     * @return All users available in the system
     */
    public static List<User> getAllUsers() {
        return Collections.unmodifiableList(allUsers);
    }

    /**
     * This method creates a new Booking object in the web service and the new Booking object's booking ID
     * to the caller.
     *
     * @param postRequestBody JSONObject
     */
    public static Booking createBooking(JSONObject postRequestBody) {
        String postEndpointURL = ROOT_URL + POST_BOOKING_ENDPOINT;
        HttpResponse<String> response = Http.post(postEndpointURL, postRequestBody);

        Booking newBooking = PostAdapter.convertHttpPostResponse(
                response, Booking.class, new BookingDeserializer()
        );

        // Observer pattern: update the existing list (the observer) only when there is an update
        allBookings.add(newBooking);

        // Observer pattern: update the existing list (the observer) only when there is an update
        String userId = postRequestBody.getString("customerId");
        for (User user : allUsers) {
            if (user.USER_ID.equals(userId)) {
                user.addBookingId(newBooking.BOOKING_ID);
            }
        }
        return newBooking.cloneInstance();
    }

    /**
     * This method gets the specific booking based on the entered bookingID
     *
     * @param bookingID BookingID of a specific booking
     *
     * @return The booking with the entered bookingID
     */
    public static Booking getSpecificBooking(String bookingID) {
        for (Booking booking : allBookings) {
            if (booking.BOOKING_ID.equals(bookingID)) {
                return booking.cloneInstance();
            }
        }
        return null;
    }

    /**
     * This method updates a booking's details when its bookingID is entered
     *
     * @param bookingID The bookingID of the booking that wants to be updated
     * @param updateJSON The JSONObject that contains the updated details
     *
     * @return Booking with updated details
     */
    public static Booking updateSpecificBooking(String bookingID, JSONObject updateJSON) {
        String patchEndPoint = ROOT_URL + BOOKING_ENDPOINT + "/" + bookingID;
        Booking patchedBooking = PatchAdapter.patchResource(
                patchEndPoint, updateJSON, Booking.class, new BookingDeserializer()
        );

        // Observer pattern: update the existing list (the observer) only when there is an update
        for (int i = 0; i < allBookings.size(); i++) {
            if (allBookings.get(i).BOOKING_ID.equals(bookingID)) {
                allBookings.set(i, patchedBooking);
            }
        }

        return patchedBooking.cloneInstance();
    }

    /**
     * This method deletes a specific booking when its bookingID is entered
     *
     * @param bookingID The bookingID of the booking that wants to be deleted
     *
     * @return The booking to be removed
     */
    public static Booking deleteSpecificBooking(String bookingID) {
        String deleteURL = ROOT_URL + BOOKING_ENDPOINT + "/" + bookingID;
        HttpResponse<String> response = Http.delete(deleteURL);

        Booking bookingToBeRemoved = getSpecificBooking(bookingID);
        // Observer pattern: remove the booking object from the observable list
        allBookings.removeIf(booking -> booking.BOOKING_ID.equalsIgnoreCase(bookingID));
        allUsers.forEach(user -> user.removeBookingId(bookingID));
        return bookingToBeRemoved;
    }

    /**
     * This method gets the specific User when entering its UserID
     *
     * @param userId The userID of a user
     *
     * @return Return User with the entered userID
     */
    public static User getSpecificUserById(String userId) {
        for (User user : allUsers) {
            if (user.USER_ID.equals(userId)) {
                return user.cloneInstance();
            }
        }
        return null;
    }

    /**
     * This method gets the specific User when entering its username
     *
     * @param userName The username of a user
     *
     * @return Return User with the entered username
     */
    public static User getSpecificUserByUserName(String userName) {
        for (User user : allUsers) {
            if (user.USERNAME.equals(userName)) {
                return user.cloneInstance();
            }
        }
        return null;
    }

    /**
     * This method verifies the login credentials when users are attempting a login
     *
     * @param userName The entered username by the user
     * @param password The entered password by the user
     *
     * @return jwt
     */
    public static JsonWebToken verifyUserLoginCredentials (String userName, String password) {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("userName", userName);
        jsonObj.put("password", password);

        String userCredVerificationEndpoint = ROOT_URL + USER_ENDPOINT + "/login?jwt=true";
        // calling Post class to make a request and to verify credentials
        HttpResponse<String> response = Http.post(userCredVerificationEndpoint, jsonObj);

        JsonWebToken jwt = null;
        if (response.body().contains("jwt")) {
            jwt = PostAdapter.convertHttpPostResponse(
                    response, JsonWebToken.class, new JsonWebTokenDeserializer()
            );
        }

        return jwt;
    }
}
