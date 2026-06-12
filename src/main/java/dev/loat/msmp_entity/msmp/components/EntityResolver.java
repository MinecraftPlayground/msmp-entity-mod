package dev.loat.msmp_entity.msmp.components;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;
import java.util.UUID;


/**
 * Utility class for resolving entities from MSMP request parameters.
 *
 * <p>Works with any request implementing {@link EntityLookup}, centralizing
 * the lookup logic shared across all entity methods.</p>
 */
public final class EntityResolver {

    private EntityResolver() {}

    /**
     * Resolves any {@link Entity} from the given {@link EntityLookup}.
     *
     * @param server  The running {@link MinecraftServer} instance
     * @param lookup  Any request implementing {@link EntityLookup}
     * @return The resolved {@link Entity}
     * @throws IllegalArgumentException if neither field is provided, the UUID is malformed,
     * or no matching entity is found
     */
    public static Entity resolveEntity(MinecraftServer server, EntityLookup lookup) {
        if (lookup.id().isEmpty() && lookup.name().isEmpty()) {
            throw new IllegalArgumentException("Either 'id' or 'name' must be provided");
        }

        Entity entity = null;

        if (lookup.id().isPresent()) {
            UUID uuid;
            try {
                uuid = UUID.fromString(lookup.id().get());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid UUID: " + lookup.id().get());
            }

            // Fast path: try player list first
            entity = server.getPlayerList().getPlayer(uuid);

            // Fall back to all loaded levels for non-player entities
            if (entity == null) {
                for (ServerLevel level : server.getAllLevels()) {
                    Entity found = level.getEntity(uuid);
                    if (found != null) {
                        entity = found;
                        break;
                    }
                }
            }
        }

        // Name fallback: players only
        if (entity == null && lookup.name().isPresent()) {
            entity = server.getPlayerList().getPlayerByName(lookup.name().get());
        }

        if (entity == null) {
            String identifier = lookup.id().orElseGet(() -> lookup.name().get());
            throw new IllegalArgumentException("Entity not found: " + identifier);
        }

        return entity;
    }

    /**
     * Resolves any {@link Entity} by its UUID directly.
     *
     * <p>Lookup order:</p>
     * <ol>
     *   <li>By UUID via {@code server.getPlayerList().getPlayer(UUID)} (players only, fast path)</li>
     *   <li>By UUID via level entity lookup across all loaded levels</li>
     * </ol>
     *
     * @param server The running {@link MinecraftServer} instance
     * @param uuid The UUID of the entity to resolve
     * @return The resolved {@link Entity}
     * @throws IllegalArgumentException if no matching entity is found
     */
    public static Entity resolveEntityByUUID(MinecraftServer server, UUID uuid) {
        return EntityResolver.resolveEntity(server, new EntityRequest(Optional.of(uuid.toString()), Optional.empty()));
    }

    /**
     * Resolves any {@link Entity} by its UUID directly.
     *
     * @param server The running {@link MinecraftServer} instance
     * @param uuid The UUID of the entity to resolve
     * @return The resolved {@link Entity}
     * @throws IllegalArgumentException if no matching entity is found
     */
    public static Entity resolveEntityByUUID(MinecraftServer server, String uuid) {
        return EntityResolver.resolveEntity(server, new EntityRequest(Optional.of(uuid), Optional.empty()));
    }

    /**
     * Resolves a {@link LivingEntity} from the given {@link EntityLookup}.
     *
     * @param server  The running {@link MinecraftServer} instance
     * @param lookup  Any request implementing {@link EntityLookup}
     * @return The resolved {@link LivingEntity}
     * @throws IllegalArgumentException if the entity is not a {@link LivingEntity}
     */
    public static LivingEntity resolveLivingEntity(MinecraftServer server, EntityLookup lookup) {
        Entity entity = resolveEntity(server, lookup);
        if (!(entity instanceof LivingEntity living)) {
            throw new IllegalArgumentException(
                "Entity %s is not a LivingEntity".formatted(entity.getUUID())
            );
        }
        return living;
    }

    /**
     * Resolves a {@link Player} from the given {@link EntityLookup}.
     *
     * @param server  The running {@link MinecraftServer} instance
     * @param lookup  Any request implementing {@link EntityLookup}
     * @return The resolved {@link Player}
     * @throws IllegalArgumentException if the entity is not a {@link Player}
     */
    public static Player resolvePlayer(MinecraftServer server, EntityLookup lookup) {
        Entity entity = resolveEntity(server, lookup);
        if (!(entity instanceof Player player)) {
            throw new IllegalArgumentException(
                "Entity %s is not a Player".formatted(entity.getUUID())
            );
        }
        return player;
    }

    /**
     * Builds an {@link EntityRef} from a resolved {@link Entity}.
     * Includes the player name if the entity is a {@link Player}.
     *
     * @param entity The resolved entity
     * @return The corresponding {@link EntityRef}
     */
    public static EntityRef toEntityRef(Entity entity) {
        Optional<String> name = entity instanceof Player p
            ? Optional.of(p.getName().getString())
            : Optional.empty();
        return new EntityRef(entity.getUUID().toString(), name);
    }
}
