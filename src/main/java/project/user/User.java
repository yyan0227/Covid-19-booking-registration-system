package project.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class contains the required details of a user. Also allows us to create
 * any new users if required.
 *
 */
public class User {
    public final String USER_ID;
    public final String USERNAME;
    private String givenName;
    private String familyName;
    private boolean isCustomer;
    private boolean isReceptionist;
    private boolean isHealthcareWorker;
    private List<String> allBookingIds;
    private List<String> allTestTaken = new ArrayList<>();  // booking ids

    /**
     * Constructor method of the class
     *
     * @param userId The userID of the user
     * @param userName The username of the user
     * @param givenName The given name of the user
     * @param familyName The family name of the user
     * @param isCustomer A boolean to denote if the user is a customer or not
     * @param isReceptionist A boolean to denote if the user is a receptionist or not
     * @param isHealthcareWorker A boolean to denote if the user is a healthcare worker or not
     * @param allBookingIds Bookings made by the user
     */
    public User(String userId, String userName, String givenName, String familyName,
                Boolean isCustomer, Boolean isReceptionist, Boolean isHealthcareWorker,
                List<String> allBookingIds) {
        this.USER_ID = userId;
        this.USERNAME = userName;
        this.givenName = givenName;
        this.familyName = familyName;
        this.isCustomer = isCustomer != null && isCustomer;
        this.isReceptionist = isReceptionist != null && isReceptionist;
        this.isHealthcareWorker = isHealthcareWorker != null && isHealthcareWorker;
        this.allBookingIds = allBookingIds;
    }

    /**
     * A method to see if a user is a receptionist or a healthcare worker
     *
     * @return True if user is a receptionist or a healthcare worker
     */
    public boolean isAdmin() {
        return isReceptionist || isHealthcareWorker;
    }

    /**
     * This method gets all the bookings made by the user
     *
     * @return Bookings made by the user
     */
    public List<String> getAllBookingIds() {
        return Collections.unmodifiableList(allBookingIds);
    }

    /**
     *
     *
     * @param bookingId
     */
    public void addBookingId(String bookingId) {
        allBookingIds.add(bookingId);
    }

    public void removeBookingId(String bookingIdRemove) {
        allBookingIds.removeIf(bookingId -> bookingId.equals(bookingIdRemove));
    }

    public String getFullName() {
        return givenName + " " + familyName;
    }

    @Override
    public String toString() {
        return "User{" +
                "USER_ID='" + USER_ID + '\'' +
                ", givenName='" + givenName + '\'' +
                ", familyName='" + familyName + '\'' +
                ", isCustomer=" + isCustomer +
                ", isReceptionist=" + isReceptionist +
                ", isHealthcareWorker=" + isHealthcareWorker +
                '}';
    }

    public User cloneInstance() {
        return new User(
                USER_ID, USERNAME, givenName, familyName,
                isCustomer, isReceptionist, isHealthcareWorker,
                Collections.unmodifiableList(allBookingIds)
        );
    }
}
