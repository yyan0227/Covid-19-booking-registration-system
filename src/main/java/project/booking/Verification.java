package project.booking;

/**
 * Parent class for the different types of verification; PIN code and QR code.
 */

public abstract class Verification {

    // gets the generated code (be it PIN or QR)
    public abstract String getCode();

    public abstract Verification cloneInstance();
}
