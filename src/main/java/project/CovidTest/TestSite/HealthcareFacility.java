package project.CovidTest.TestSite;

/**
 * Consider using Singleton pattern for health facilities since it doesn't make sense
 * for a health facility to have multiple instances.
 *
 * This class represents the available healthcare facilities entered by the user.
 *
 */
public class HealthcareFacility implements TestSite {
    private static final String NULL_STR = "NULL";
    public final String ID;
    private String name;
    private String description;
    private Address address = new Address();

    private class Address {
        private int latitude;
        private int longitude;
        private String unitNumber;
        private String street;
        private String street2;
        private String suburb;
        private String state;
        private String postcode;

        public int getLatitude() {
            return latitude;
        }

        public void setLatitude(int latitude) {
            this.latitude = latitude;
        }

        public int getLongitude() {
            return longitude;
        }

        public void setLongitude(int longitude) {
            this.longitude = longitude;
        }

        public String getUnitNumber() {
            return unitNumber;
        }

        public void setUnitNumber(String unitNumber) {
            if (unitNumber != null) {
                this.unitNumber = unitNumber;
            }
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            if (street != null) {
                this.street = street;
            }
        }

        public String getStreet2() {
            return street2;
        }

        public void setStreet2(String street2) {
            if (street2 != null) {
                this.street2 = street2;
            }
        }

        public String getSuburb() {
            return suburb;
        }

        public void setSuburb(String suburb) {
            if (suburb != null) {
                this.suburb = suburb;
            }
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            if (state != null) {
                this.state = state;
            }
        }

        public String getPostcode() {
            return postcode;
        }

        public void setPostcode(String postcode) {
            if (postcode != null) {
                this.postcode = postcode;
            }
        }

        @Override
        public String toString() {
            return "Address{" +
                    "latitude=" + latitude +
                    ", longitude=" + longitude +
                    ", unitNumber='" + unitNumber + '\'' +
                    ", street='" + street + '\'' +
                    ", street2='" + street2 + '\'' +
                    ", suburb='" + suburb + '\'' +
                    ", state='" + state + '\'' +
                    ", postcode='" + postcode + '\'' +
                    '}';
        }
    }

    public HealthcareFacility(String id, String name, String description,
                              int latitude, int longitude, String unitNumber, String street,
                              String street2, String suburb, String state, String postcode) {
        this.ID = id;
        this.name = name;
        setDescription(description);
        address.setLatitude(latitude);
        address.setLongitude(longitude);
        address.setUnitNumber(unitNumber);
        address.setStreet(street);
        address.setStreet2(street2);
        address.setSuburb(suburb);
        address.setState(state);
        address.setPostcode(postcode);
    }

    @Override
    public boolean provideWalkIn() {
        return false;
    }

    @Override
    public boolean provideDriveThrough() {
        return false;
    }

    @Override
    public boolean isClinic() {
        return true;
    }

    @Override
    public boolean isHospital() {
        return false;
    }

    @Override
    public boolean isGP() {
        return false;
    }

    @Override
    public TestSite cloneInstance() {
        return new HealthcareFacility(
                this.ID, this.name, this.description, address.getLatitude(), address.getLongitude(),
                address.getUnitNumber(), address.getStreet(), address.getStreet2(), address.getSuburb(),
                address.getState(), address.getPostcode()
        );
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        if (description != null && !description.equalsIgnoreCase(HealthcareFacility.NULL_STR)) {
            this.description = description;
        }
    }

    @Override
    public String toString() {
        return "HealthcareFacility{" +
                "ID='" + ID + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", address=" + address +
                '}';
    }

    public String getSuburb() {
        return address.getSuburb();
    }
}
