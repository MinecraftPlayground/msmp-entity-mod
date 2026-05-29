package dev.loat.msmp_entity.msmp.exceptions;

/**
 * Thrown when an entity is missing an expected attribute,
 * such as {@link net.minecraft.world.entity.ai.attributes.Attributes#MAX_HEALTH}.
 */
public class MissingAttributeException extends MSMPException {

    /**
     * @param uuid      The UUID of the entity
     * @param attribute The name of the missing attribute
     */
    public MissingAttributeException(String uuid, String attribute) {
        super("Entity %s has no %s attribute".formatted(uuid, attribute));
    }
}
