package dev.loat.msmp_entity_data.msmp.exceptions;

/**
 * Thrown when a request provides neither {@code id} nor {@code name}
 * for entity lookup.
 */
public class MissingIdentifierException extends MSMPException {

    public MissingIdentifierException() {
        super("Either 'id' or 'name' must be provided");
    }
}
