package foodflow.domain;

/** A delivery address: district and street. Immutable. */
public record DeliveryAddress(String district, String street) {

    public DeliveryAddress {
        district = Texts.requireText(district, "District");
        street = Texts.requireText(street, "Street");
    }

    @Override
    public String toString() {
        return district + ", " + street;
    }
}

