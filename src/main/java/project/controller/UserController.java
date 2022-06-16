package project.controller;

import project.springbootadapters.BookingMessage;
import project.Engine.Database;
import project.springbootadapters.JsonWebToken;
import project.booking.Booking;
import project.booking.BookingCreator;
import project.booking.HomeBookingCreator;
import project.user.User;
import project.Engine.Form.Form;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents the controller for the User's side
 */
@Controller
public class UserController {

    @Autowired
    SimpMessagingTemplate template;
    private static List<Booking> bookings = Database.getAllBookings();
    private static List<User> users = Database.getAllUsers();
    private static final String CREATE_ACTION = "create";

    /**
     * This method verifies the entered details by the user to check if it is a resident as well as displaying
     * the respective bookings made by the user.
     *
     * @param model Represents the interface that defines a holder for model attributes.
     * @param user User using the application
     * @param jwt JsonWebToken for verification of the login
     * @param reqHeader A data structure representing HTTP request or response headers
     *
     * @return Returns to the respective page depending on the success of the login. User page if login is
     *         successful else redirects to the login page.
     */
    @GetMapping("/user")
    public String user(
            Model model, @ModelAttribute("user") User user, @ModelAttribute("jwt") JsonWebToken jwt,
            @RequestHeader HttpHeaders reqHeader
    ) {
        String authorizationField = "Authorization";
        String referrerField = "referer";

        // if the reqHeader is null redirect back to the login page
        if (reqHeader.get(authorizationField) == null ||
                reqHeader.get(authorizationField).get(0).equalsIgnoreCase("null")) {
            if (!reqHeader.containsKey(referrerField) ||
                    !reqHeader.get(referrerField).get(0).contains("/login"))
                return "redirect:/login";
        }
        // if the userID is null and reqHeader is undefined, redirect back to the login page
        else if (user.USER_ID == null &&
                reqHeader.get(authorizationField).get(0).equalsIgnoreCase("undefined"))
            return "redirect:/login";

        if (user.USER_ID == null) {
            String jwtString = reqHeader.get(authorizationField).get(0);
            Map<String, JSONObject> decoded = decodeJwt(jwtString);
            String userId = (String) decoded.get("payload").get("sub");
            user = Database.getSpecificUserById(userId);
        }

        // display bookings made by the user
        List<String> allBookingIds = user.getAllBookingIds();
        List<Booking> allBookings = new ArrayList<>();
        for (String bookingId : allBookingIds) {
            Booking booking = Database.getSpecificBooking(bookingId);
            allBookings.add(booking);
        }
        model.addAttribute("bookings", allBookings);
        model.addAttribute("jwt", jwt);
        model.addAttribute("user", user);
        model.addAttribute("bookingMsg", new BookingMessage());
        model.addAttribute("questionForm", new Form());
        model.addAttribute("currentDateTime", LocalDateTime.now());

        return "user";
    }

    /**
     * This method creates a home booking when requested by the resident
     *
     * @param bookingMsg Represents the booking details of a booking
     * @param model Represents the interface that defines a holder for model attributes.
     * @param reqHeader A data structure representing HTTP request or response headers
     * @param redirectAttributes
     *
     * @return Returns to the user page with the new booking shown at the end of the list of bookings
     */
    @PostMapping(value = "/user")
    public String createBooking(
            @ModelAttribute BookingMessage bookingMsg, Model model, @RequestHeader HttpHeaders reqHeader,
            RedirectAttributes redirectAttributes
    ) {
        initializeModel(model);

        // authorising user that requested to make a booking
        String authorizationField = "Authorization";
        String jwtString = reqHeader.get(authorizationField).get(0);
        Map<String, JSONObject> decoded = decodeJwt(jwtString);
        String userId = (String) decoded.get("payload").get("sub");
        User user = Database.getSpecificUserById(userId);

        // obtaining the entered test date time entered
        Map<String, String> optionalProperties = new HashMap<>();
        optionalProperties.put("startTime", bookingMsg.getTestDateTime());

        // creating the home booking
        BookingCreator creator = new HomeBookingCreator(bookingMsg.isRequireTestKit());
        Booking newBooking = creator.createBooking(
                user.USER_ID, optionalProperties, new HashMap<>()
        );
        broadcastBooking(newBooking, CREATE_ACTION);   // broadcast the new booking to all browser subscribers

        redirectAttributes.addFlashAttribute(
                "jwt", reqHeader.get(authorizationField).get(0)
        );
        return "redirect:/user";
    }

    /**
     * References: https://www.baeldung.com/thymeleaf-list
     *             https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#setting-the-value-of-any-attribute
     *             https://www.thymeleaf.org/doc/tutorials/2.1/thymeleafspring.html#creating-a-form
     *             https://www.baeldung.com/spring-redirect-and-forward
     *
     * This method updates a details of a specific booking
     *
     * @param bookingMsg Represents the booking details of a booking
     * @param model Represents the interface that defines a holder for model attributes.
     *
     * @return Returns to the new redirected view of the admin page
     */
    @PostMapping(value="/user/updatebooking/{id}")
    public RedirectView updateBooking(@ModelAttribute BookingMessage bookingMsg, Model model) {

        // initializing model
        initializeModel(model);

        // obtaining the bookingID
        String bookingId = bookingMsg.getBookingID();

        // obtaining the test date time and test site ID of the booking
        String updatedTime = bookingMsg.getTestDateTime();
        String updatedTestSiteID = bookingMsg.getTestSiteID();

        // creating a new JSON Object with the user's input for test site and test date time
        JSONObject updateJson = new JSONObject();
        updateJson.put("testDateTime", updatedTime);
        updateJson.put("testSiteID", updatedTestSiteID);

        // updating the specific booking
        Database.updateSpecificBooking(bookingId, updateJson);

        return new RedirectView("/user");
    }


    /**
     * This method handles the HTTP GET request to go to the edit booking page when a
     * a request by the user is made.
     *
     * @param bookingID The bookingID of the booking that is currently being edited
     * @param model Represents the interface that defines a holder for model attributes.
     * @return Returns the user to the edit booking page
     */
    @GetMapping(value="/user/editBooking/{id}")
    public String editBooking(@PathVariable String bookingID, Model model) {
        model.addAttribute("booking", Database.getSpecificBooking(bookingID));

        return "edit_booking";
    }


    /**
     * This method initialises the model and its attributes
     *
     * @param model Represents the interface that defines a holder for model attributes.
     */
    private void initializeModel(Model model) {
        model.addAttribute("bookingMsg", new BookingMessage());
        // https://www.baeldung.com/thymeleaf-iteration
        model.addAttribute("bookings", bookings);
        model.addAttribute("name", "user");
        model.addAttribute("questionForm", new Form());
        model.addAttribute("users", new Users());
        model.addAttribute("currentDateTime", LocalDateTime.now());
    }

    /**
     * Reference: https://www.baeldung.com/java-jwt-token-decode
     *
     * This method decodes the jwt token
     *
     * @param jwt A JsonWebToken
     */
    private Map<String, JSONObject> decodeJwt (String jwt){
        String[] chunks = jwt.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));

        JSONObject headerJson = new JSONObject(header);
        JSONObject payloadJson = new JSONObject(payload);
        Map<String, JSONObject> result = new HashMap<>();
        result.put("header", headerJson);
        result.put("payload", payloadJson);
        return result;
    }

    /**
     * This method broadcasts a message to all subscribers (browsers).
     *
     * @param booking The booking that is being broadcasted to all subscribers
     * @param actionType The action that was performed on the booking
     */
    @SendTo("/subscribers/bookings")
    public void broadcastBooking(Booking booking, String actionType) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("actionType", actionType);
        jsonObject.put("BOOKING_ID", booking.BOOKING_ID);
        jsonObject.put("USER_ID", booking.USER_ID);
        jsonObject.put("verification", booking.getVerificationCode());
        template.convertAndSend("/subscribers/bookings", jsonObject.toString());
    }

    /**
     * Helper class for thymeleaf to retrieve a user's full name based on its user ID.
     */
    private class Users {
        public String getFullNameByUserId(String userId) {
            return users.stream()
                    .filter(user -> user.USER_ID.equals(userId))
                    .collect(Collectors.toList())
                    .get(0)
                    .getFullName();
        }
    }
}