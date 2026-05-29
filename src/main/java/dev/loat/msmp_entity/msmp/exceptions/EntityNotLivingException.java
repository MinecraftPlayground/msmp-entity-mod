package dev.loat.msmp_entity.msmp.exceptions;

/**
 * Thrown when a method requires a {@link net.minecraft.world.entity.LivingEntity}
 * but the resolved entity is not one.
 */
public class EntityNotLivingException extends MSMPException {

    /**
     * @param uuid The UUID of the entity that was found but is not a LivingEntity
     */
    public EntityNotLivingException(String uuid) {
        super("Entity is not a LivingEntity: " + uuid);
    }
}
