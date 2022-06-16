package project.booking;

import java.time.LocalDateTime;
import java.util.List;


/**
 * This class represents the required details needed to make a booking.
 *
 */

public class Booking {

    public final String BOOKING_ID;
    public final String USER_ID;
    private String testSiteID;
    private String testDateTime;
    private Verification verificationMethod;
    private static List<Booking> allBookings;

    /**
     * Constructor
     *
     * @param bookingID BookingID of the booking
     * @param userID UserID that belong to the booking
     * @param testDateTime The test date time of the booking
     * @param verificationMethod The type of verification method of the booking
     */
    public Booking(String bookingID, String userID, String testDateTime, Verification verificationMethod) {
        BOOKING_ID = bookingID;
        USER_ID = userID;
        this.testDateTime = testDateTime;
        this.verificationMethod = verificationMethod;
    }

    /**
     * Constructor
     *
     * @param bookingID BookingID of the booking
     * @param userID UserID that belong to the booking
     * @param testSiteID testSiteID of the chosen venue
     * @param testDateTime The test date time of the booking
     * @param verificationMethod The type of verification method of the booking
     */
    public Booking(String bookingID, String userID, String testSiteID, String testDateTime,
                   Verification verificationMethod) {
        this.BOOKING_ID = bookingID;
        this.USER_ID = userID;
        this.testSiteID = testSiteID;
        this.testDateTime = testDateTime;
        this.verificationMethod = verificationMethod;
    }

    /**
     * This method gets the verification code based on the type of booking made (either PIN Code or QRCode)
     */
    public String getVerificationCode() {
        return verificationMethod.getCode();
    }

    /**
     * This method gets the testSiteID of the venue chosen
     */
    public String getTestSiteID() {
        return testSiteID;
    }

    /**
     * This method gets the test date time of the booking
     *
     * @return Returns the date time of the booking
     */
    public String getTestDateTime() {
        // test datetime is of the form: 2022-05-10T14:57:17.425Z
        String dateTime = testDateTime.replaceAll("....Z", "");
        String[] chunks = dateTime.split("T");
        dateTime = chunks[0] + "\n" + chunks[1];
        return dateTime;
    }

    /**
     * This method gets the test date time object of the booking
     */
    public LocalDateTime getTestDateTimeObj() {
        return LocalDateTime.parse(testDateTime.replaceAll("Z", ""));
    }

    /**
     * To string method for the booking
     */

    @Override
    public String toString() {
        return "Booking{" +
                "BOOKING_ID='" + BOOKING_ID + '\'' +
                ", USER_ID='" + USER_ID + '\'' +
                ", testSiteID='" + testSiteID + '\'' +
                ", verificationMethod=" + verificationMethod +
                '}';
    }

    public Booking cloneInstance() {
        return new Booking(
                BOOKING_ID, USER_ID, testSiteID, testDateTime, verificationMethod.cloneInstance()
        );
    }
}
