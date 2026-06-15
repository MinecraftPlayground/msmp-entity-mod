<img src="assets/icon.png" width="64" align="right">

# MSMP Entity

A server-side Fabric mod that extends the [Minecraft Server Management Protocol](https://minecraft.wiki/w/Minecraft_Server_Management_Protocol) (MSMP) by providing additional functions for getting and setting entity data.

This mod is designed for tooling, dashboards, automation systems, external monitoring tools, and integrations that need structured access to entity information without relying on command parsing or RCON hacks.

## RPC Methods

The mod currently provides the following MSMP RPC methods. All of these methods are also automatically discoverable through the standard `rpc.discover` MSMP endpoint.

| Method                            | Description                                                                                   |
| --------------------------------- | --------------------------------------------------------------------------------------------- |
| `entity:dimension`                | Returns the current dimension of any loaded entity by UUID, or a player by name               |
| `entity:dimension/set`            | Transfers any loaded entity to the given dimension, keeping its current position and rotation |
| `entity:dimension/changed`        | Returns a list of all tracked entities for the dimension changed event                        |
| `entity:dimension/changed/add`    | Add entities to the dimension change notification tracker                                     |
| `entity:dimension/changed/remove` | Remove entities from the dimension change notification tracker                                |
| `entity:health`                   | Returns the current and maximum health of any LivingEntity                                    |
| `entity:health/set`               | Partially updates the health and/or maximum health of any LivingEntity                        |
| `entity:health/changed`           | Returns a list of all tracked entities for the health changed event                           |
| `entity:health/changed/add`       | Add entities to the health change notification tracker                                        |
| `entity:health/changed/remove`    | Remove entities from the health change notification tracker                                   |
| `entity:items`                    | Returns all occupied inventory slots of an online player in Vanilla NBT format                |
| `entity:items/set`                | Partially updates an online player's inventory using a diff approach                          |
| `entity:position`                 | Returns the current position of any loaded entity                                             |
| `entity:position/set`             | Teleports any loaded entity to the given position within its current dimension                |
| `entity:position/changed`         | Returns a list of all tracked entities for the position changed event                         |
| `entity:position/changed/add`     | Add entities to the position change notification tracker                                      |
| `entity:position/changed/remove`  | Remove entities from the position change notification tracker                                 |
| `entity:rotation`                 | Returns the current rotation of any loaded entity by UUID, or a player by name                |
| `entity:rotation/set`             | Sets the rotation of any loaded entity, preserving its position and dimension                 |
| `entity:saturation`               | Returns the current food level and saturation of an online player                             |
| `entity:saturation/set`           | Partially updates the food level and/or saturation of an online player                        |
| `entity:uuid`                     | Returns the UUID of an online player by name                                                  |

## RPC Notifications

The mod also provides the following MSMP RPC notifications that clients can subscribe to:

| Method                                  | Description                                   |
| --------------------------------------- | --------------------------------------------- |
| `entity:notification/dimension/changed` | Fires when a tracked entity changes dimension |

## Installation

1. Download the mod `.jar` and place it in your server's `mods/` folder.
2. Enable the Management Server in `server.properties`:
   ```properties
   management-server-enabled=true
   ```
3. Start the server. The Management Server will listen on `localhost:25576` by default.

## License

[LGPL-3.0](LICENSE)
