<img src="assets/icon.png" width="64" align="right">

# MSMP Entity Data

A server-side Fabric mod that extends the [Minecraft Server Management Protocol](https://minecraft.wiki/w/Minecraft_Server_Management_Protocol) (MSMP) by providing additional functions for querying and setting entity data.

This mod is designed for tooling, dashboards, automation systems, external monitoring tools, and integrations that need structured access to entity information without relying on command parsing or RCON hacks.

## RPC Methods

The mod currently provides the following MSMP RPC methods. All of these methods are also automatically discoverable through the standard `rpc.discover` MSMP endpoint.

| Method                       | Description                                                             |
| ---------------------------- | ----------------------------------------------------------------------- |
| `entity_data:dimension`      | Returns the dimension/world of an entity.                               |
| `entity_data:dimension/set`  | Changes the dimension of an entity.                                     |
| `entity_data:health`         | Returns the current health of an entity.                                |
| `entity_data:health/set`     | Sets the health value of an entity.                                     |
| `entity_data:inventory`      | Returns the inventory contents of a player or inventory-holding entity. |
| `entity_data:inventory/set`  | Modifies or replaces inventory contents.                                |
| `entity_data:position`       | Returns the current position of an entity.                              |
| `entity_data:position/set`   | Teleports or changes the position of an entity.                         |
| `entity_data:rotation`       | Returns the current entity rotation.                                    |
| `entity_data:rotation/set`   | Updates the entity rotation.                                            |
| `entity_data:saturation`     | Returns the food saturation level of a player.                          |
| `entity_data.saturation/set` | Sets the saturation level of a player.                                  |
| `entity_data:uuid`           | Resolves or returns UUID information for players.                       |

## Installation

1. Download the mod `.jar` and place it in your server's `mods/` folder.
2. Enable the Management Server in `server.properties`:
   ```properties
   management-server-enabled=true
   ```
3. Start the server. The Management Server will listen on `localhost:25576` by default.

## License

[LGPL-3.0](LICENSE)
