package project.controller;

import project.springbootadapters.BookingMessage;
import project.Engine.Database;
import project.Engine.Form.Form;
import project.springbootadapters.JsonWebToken;
import project.booking.Booking;
import project.booking.BookingCreator;
import project.user.User;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

/**
 * Reference: https://spring.io/guides/gs/serving-web-content/
 *
 * This class represents the controller for the Admin's side
 */
@Controller
public class AdminController {

    @Autowired
    SimpMessagingTemplate template;
    private static List<Booking> bookings = Database.getAllBookings();
    private static List<User> users = Database.getAllUsers();
    private static final String CREATE_ACTION = "create";
    private static final String DELETE_ACTION = "delete";

    /**
     * This method handles the verification part of the login to determine if the details entered belong to
     * an admin or not.
     *
     * @param model Represents the interface that defines a holder for model attributes.
     * @param jwt JsonWebToken for verification of the login
     * @param reqHeader A data structure representing HTTP request or response headers
     *
     * @return Returns to the respective page (admin or redirects back to login page) depending on the
     *         success of the login
     */
    @GetMapping("/admin")
    public String admin(
            Model model, @ModelAttribute("jwt") JsonWebToken jwt, @RequestHeader HttpHeaders reqHeader
    ) {
        initializeModel(model);
        model.addAttribute("jwt", jwt);
        String authorizationField = "Authorization";
        String referrerField = "referer";

        if (reqHeader.containsKey(referrerField)) {
            if (reqHeader.get(referrerField).get(0).contains("/login")){
                return "admin";
            }
        }

        if (reqHeader.get(authorizationField) == null ||
                reqHeader.get(authorizationField).get(0).equalsIgnoreCase("null")) {
            return "redirect:/login";
        }
        return "admin";
    }

    /**
     * Reference: https://spring.io/guides/gs/handling-form-submission/
     *
     * This method handles the requests made by admins when creating a booking for a resident.
     *
     * @param bookingMsg Represents the booking details of a booking
     * @param model Represents the interface that defines a holder for model attributes.
     *
     * @return Returns to the admin page
     */
    @PostMapping(value = "/admin")
    public String createBooking(@ModelAttribute BookingMessage bookingMsg, Model model) {
        initializeModel(model);

        // obtaining details entered by the user
        Map<String, String> optionalProperties = new HashMap<>();
        optionalProperties.put("testingSiteId", bookingMsg.getTestSiteID());
        optionalProperties.put("notes", bookingMsg.getNotes());
        optionalProperties.put("startTime", bookingMsg.getTestDateTime());

        // creates a new booking with the obtained details
        BookingCreator creator = new BookingCreator();
        Booking newBooking = creator.createBooking(bookingMsg.getResidentID(), optionalProperties, new HashMap<>());
        broadcastBooking(newBooking, CREATE_ACTION);   // broadcast the new booking to all browser subscribers
        return "admin";
    }

    /**
     * References: https://www.baeldung.com/thymeleaf-list
     *             https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#setting-the-value-of-any-attribute
     *             https://www.thymeleaf.org/doc/tutorials/2.1/thymeleafspring.html#creating-a-form
     *             https://www.baeldung.com/spring-redirect-and-forward
     *
     *
     * This method updates a details of a specific booking and maps the HTTP POST request made for it.
     *
     * @param form Represents a form that is required to be filled up by a user
     * @param bookingMsg Represents the booking details of a booking
     * @param model Represents the interface that defines a holder for model attributes.
     *
     * @return Returns to the new redirected view of the admin page
     */
    @PostMapping(value="/admin/updatebooking")
    public RedirectView updateBooking(@ModelAttribute Form form, @ModelAttribute BookingMessage bookingMsg, Model model) {
        initializeModel(model);

        // gets the booking id for a specific booking
        String bookingId = bookingMsg.getBookingID();

        // gets the answers of the form entered by the user
        Map<String, String> qna = form.getQuestionAnswer();

        // puts answers into a new JSONObject
        JSONObject qnaJson = new JSONObject(qna);
        JSONObject updateJson = new JSONObject();
        updateJson.put("additionalInfo", qnaJson);

        // updates the booking according to the answers obtained
        Database.updateSpecificBooking(bookingId, updateJson);
        return new RedirectView("/admin");
    }

    /**
     * References: https://www.javaguides.net/2018/11/spring-getmapping-postmapping-putmapping-deletemapping-patchmapping.html
     *             https://www.sourcecodeexamples.net/2019/10/deletemapping-spring-boot-example.html
     *
     * This method deletes a booking when specified with its booking ID.
     *
     * @param bookingId Represents the bookingID of the booking that is deleted
     * @param model Represents the interface that defines a holder for model attributes.
     *
     * @return Returns to the admin page
     */
    @DeleteMapping("/admin/deletebooking/{id}")
    public String deleteBooking(@PathVariable(value= "id") String bookingId, Model model) {

        // removing the booking with specified bookingID
        Booking removedBooking = Database.deleteSpecificBooking(bookingId);
        initializeModel(model);

        // broadcasts message to all browsers that the specified booking has been deleted
        broadcastBooking(removedBooking, DELETE_ACTION);
        return "admin";
    }

    /**
     * This method processes the a booking whenever it is made
     *
     * @param booking Represents the booking to be processed
     *
     * @return Returns the booking details of the booking made
     */
    @MessageMapping("/booking")
    @SendTo("/subscribers/bookings")
    public BookingMessage processBooking(Booking booking) throws Exception {
        BookingMessage bookingMsg = new BookingMessage();
        bookingMsg.setBookingID(booking.BOOKING_ID);
        bookingMsg.setResidentID(booking.USER_ID);
        bookingMsg.setVerification(booking.getVerificationCode());
        return bookingMsg;
    }

    /**
     * Reference: https://stackoverflow.com/questions/46867302/broadcast-messages-with-websockets-and-springboot
     *
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
     * This method initialises the model and its attributes
     *
     * @param model Model to be initialised
     */
    private void initializeModel(Model model) {
        model.addAttribute("bookingMsg", new BookingMessage());
        // https://www.baeldung.com/thymeleaf-iteration
        model.addAttribute("bookings", bookings);
        model.addAttribute("name", "admin");
        model.addAttribute("questionForm", new Form());
        model.addAttribute("users", new Users());
        model.addAttribute("currentDateTime", LocalDateTime.now());
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

