package project.springbootadapters;

/**
 * This class represents the description and information of a booking
 *
 */
public class BookingMessage {

    private String residentID;
    private String testSiteID;
    private String notes;
    private String bookingID;
    private String verification;
    private Boolean requireTestKit;
    private String testDateTime;

    public BookingMessage() {}

    /**
     * Constructor (used for home bookings)
     *
     * @param residentID The residentID of the user
     * @param requireTestKit A boolean to indicate whether a test kit is required or not
     */
    public BookingMessage(String residentID, boolean requireTestKit) {
        this.residentID = residentID;
        this.requireTestKit = requireTestKit;
    }

    /**
     * Constructor (used for normal bookings)
     *
     * @param residentID The residentID of the user
     * @param testSiteID The test site ID of the preferred venue
     * @param notes Any extra infomration regarding the booking
     * @param testDateTime The test date time of the booking
     */
    public BookingMessage(String residentID, String testSiteID, String notes, String testDateTime) {
        this.residentID = residentID;
        this.testSiteID = testSiteID;
        this.notes = notes;
        this.testDateTime = testDateTime;
    }

    /**
     * Method to get the resident ID of the user
     *
     * @return Resident ID of the user
     */
    public String getResidentID() {
        return residentID;
    }


    /**
     * Method to get the test site ID of the user
     *
     * @return Test site ID of the user
     */
    public String getTestSiteID() {
        return testSiteID;
    }


    /**
     * Method to get the notes of the booking
     *
     * @return Notes of the booking
     */
    public String getNotes() {
        return notes;
    }


    /**
     * Method to set the resident ID of the user
     */
    public void setResidentID(String residentID) {
        this.residentID = residentID;
    }


    /**
     * Method to set the test site ID of the booking
     */
    public void setTestSiteID(String testSiteID) {
        this.testSiteID = testSiteID;
    }

    /**
     * Method to set the notes of the booking
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }


    /**
     * Method to get the verification of the booking
     *
     * @return Verification of the booking
     */
    public String getVerification() {
        return verification;
    }


    /**
     * Method to set the verification of the booking
     */
    public void setVerification(String verification) {
        this.verification = verification;
    }


    /**
     * Method to get the bookingID of the booking
     *
     * @return Booking ID of the booking
     */
    public String getBookingID() {
        return bookingID;
    }


    /**
     * Method to set the bookingID of the booking
     */
    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }


    /**
     * Method to get the result of requiring a test kit for the home booking
     *
     * @return A boolean of whether a test kit is required
     */
    public boolean isRequireTestKit() {
        return requireTestKit != null && requireTestKit;
    }


    /**
     * Method to set the result of requiring a test kid for the home booking
     */
    public void setRequireTestKit(boolean requireTestKit) {
        this.requireTestKit = requireTestKit;
    }


    /**
     * Method to get the test date time of the booking
     *
     * @return Test date time of the booking
     */
    public String getTestDateTime() {
        return testDateTime;
    }


    /**
     * Method to set the test date time of the booking
     */
    public void setTestDateTime(String testDateTime) {
        this.testDateTime = testDateTime;
    }

}
