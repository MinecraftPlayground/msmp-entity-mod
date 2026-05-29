package dev.loat.msmp_entity.msmp.exceptions;

/**
 * Thrown when a method requires a {@link net.minecraft.world.entity.player.Player}
 * but the resolved entity is not one.
 */
public class EntityNotPlayerException extends MSMPException {

    /**
     * @param uuid The UUID of the entity that was found but is not a Player
     */
    public EntityNotPlayerException(String uuid) {
        super("Entity is not a Player: " + uuid);
    }
}
