<img src="assets/icon.png" width="64" align="right">

# MSMP Entity

A server-side Fabric mod that extends the [Minecraft Server Management Protocol](https://minecraft.wiki/w/Minecraft_Server_Management_Protocol) (MSMP) by providing additional functions for getting and setting entity data.

This mod is designed for tooling, dashboards, automation systems, external monitoring tools, and integrations that need structured access to entity information without relying on command parsing or RCON hacks.

## RPC Methods

The mod currently provides the following MSMP RPC methods. All of these methods are also automatically discoverable through the standard `rpc.discover` MSMP endpoint.

| Method                       | Description                                                             |
| ---------------------------- | ----------------------------------------------------------------------- |
| `entity:dimension`           | Returns the dimension/world of an entity.                               |
| `entity:dimension/set`       | Changes the dimension of an entity.                                     |
| `entity:dimension/changed`   | Subscribe to dimension change notifications.                            |
| `entity:dimension/changed/add`   | Subscribe to entity-enters-dimension notifications.                 |
| `entity:dimension/changed/remove` | Subscribe to entity-leaves-dimension notifications.                |
| `entity:health`              | Returns the current and maximum health of a LivingEntity.               |
| `entity:health/set`          | Sets the health value of an entity.                                     |
| `entity:items`               | Returns the inventory contents of a player or inventory-holding entity. |
| `entity:items/set`           | Modifies or replaces inventory contents.                                |
| `entity:position`            | Returns the current position of an entity.                              |
| `entity:position/set`        | Teleports or changes the position of an entity.                         |
| `entity:rotation`            | Returns the current entity rotation.                                    |
| `entity:rotation/set`        | Updates the entity rotation.                                            |
| `entity:saturation`          | Returns the food level and saturation of a player.                      |
| `entity:saturation/set`      | Sets the food level and saturation of a player.                         |
| `entity:uuid`                | Resolves or returns UUID information for players.                       |

## Installation

1. Download the mod `.jar` and place it in your server's `mods/` folder.
2. Enable the Management Server in `server.properties`:
   ```properties
   management-server-enabled=true
   ```
3. Start the server. The Management Server will listen on `localhost:25576` by default.

## License

[LGPL-3.0](LICENSE)
