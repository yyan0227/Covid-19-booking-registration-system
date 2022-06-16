package project.booking;


/**
 * This class represents the QR Code that is obtained by the users for registration purposes.
 *
 */
public class QRCode extends Verification {
    private String qrCode;

    public QRCode(String qrCode) {
        this.qrCode = qrCode;
    }

    @Override
    public String getCode() {
        return qrCode;
    }

    @Override
    public Verification cloneInstance() {
        return new QRCode(qrCode);
    }

    @Override
    public String toString() {
        return "QRCode{" +
                "qrCode='" + qrCode + '\'' +
                '}';
    }
}
