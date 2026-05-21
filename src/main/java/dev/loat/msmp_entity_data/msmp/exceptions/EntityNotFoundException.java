package dev.loat.msmp_entity_data.msmp.exceptions;

/**
 * Thrown when an entity cannot be found by the given UUID or player name.
 */
public class EntityNotFoundException extends MSMPException {

    /**
     * @param identifier The UUID or name that was used for the lookup
     */
    public EntityNotFoundException(String identifier) {
        super("Entity not found: " + identifier);
    }
}
