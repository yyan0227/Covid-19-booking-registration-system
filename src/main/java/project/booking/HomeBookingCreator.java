package project.booking;

import java.util.Map;
import java.util.Random;

/**
 * This class creates a home booking for the user with the entered details.
 */

public class HomeBookingCreator extends BookingCreator {
    private boolean requireRatTestKit;

    /**
     * This method creates a home booking with the details entered by the user
     *
     * @param requireRatTestKit A boolean representing if a RatTestKit if required for the home booking made
     */
    public HomeBookingCreator(boolean requireRatTestKit) {
        this.requireRatTestKit = requireRatTestKit;
    }

    @Override
    public Booking createBooking(
            String userID, Map<String, String> optionalProperties, Map<String, String> additionalInfo
    ) {
        String qrCode = generateRandomString(
                "", "", 25, true, false
        );
        String url = generateRandomString(
                "www.", ".com", 8, false, true
        );

        additionalInfo.put("QrCode", qrCode);
        additionalInfo.put("URL", url);
        additionalInfo.put("requireRATTestKit", "true");
        if (!requireRatTestKit) {
            // the patient making the booking doesn't require any RAT test kit
            additionalInfo.put("requireRATTestKit", "false");
        }

        return super.createBooking(userID, optionalProperties, additionalInfo);
    }

    /**
     * This method generates a random string used to mimic a random string for a URL or QRCode
     */
    private String generateRandomString(
            String prefix, String suffix, int targetLength, boolean includeNum,
            boolean lowercaseOnly
    ) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        if (!includeNum) {
            leftLimit = 65; // numeral 'A'
        }

        Random random = new Random();
        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> ((i >= 48 && i <= 57) || (i >= 65 && i <= 90) || (i >= 97 && i <= 122)))
                .limit(targetLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        String result = prefix + generatedString + suffix;
        if (lowercaseOnly) {
            result = result.toLowerCase();
        }
        return result;
    }
}
