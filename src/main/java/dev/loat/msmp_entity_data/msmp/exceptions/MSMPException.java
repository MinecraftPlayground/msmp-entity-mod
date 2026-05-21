package dev.loat.msmp_entity_data.msmp.exceptions;

/**
 * Base exception for all MSMP entity data errors.
 *
 * <p>All method-specific exceptions extend this class, allowing handlers
 * to catch all MSMP errors with a single {@code catch (MSMPException e)} block.</p>
 */
public class MSMPException extends RuntimeException {

    /**
     * Creates a new {@link MSMPException} with the given message.
     *
     * @param message A human-readable description of the error
     */
    public MSMPException(String message) {
        super(message);
    }
}
