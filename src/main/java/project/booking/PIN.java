package project.booking;

/**
 * This class represents the PIN code that is obtained by the users for registration purposes.
 *
 */
public class PIN extends Verification {
    private String pinCode;

    public PIN(String pinCode) {
        this.pinCode = pinCode;
    }

    @Override
    public String getCode() {
        return pinCode;
    }

    @Override
    public String toString() {
        return "PIN{" +
                "pinCode='" + pinCode + '\'' +
                '}';
    }

    @Override
    public Verification cloneInstance() {
        return new PIN(pinCode);
    }
}
