# Skydiving Mod for Minecraft based on Fabric

A Minecraft Fabric mod that adds realistic **wind physics** affecting only Elytra gliding.

Being a skydiver myself, I noticed similarities between gliding with Elytras
and skydiving with small canopies. I immediately decided to share the experience
with friends.

Later, this mod was born as a simple toy to make Elytra flying a bit more fun.


## ✨ Features

- **Dynamic Wind Direction**: Wind rotates gradually (15° per minute).
- **Variable Wind Speed**: Changes by ±0.002 every 60 seconds (clamped between 0.0–0.02).
- **Forecast System**: Maintains a queue of 15 future wind changes.
- **Wind Sync**: Server-controlled wind synced to all clients.
- **Command `/wind forecast`**: Shows next 5 wind changes in readable format (e.g., `2 min: SE at 0.015`).
- **Realistic flight physics**: Headwind slows you, tailwind boosts, crosswind drifts.
- **Disables wind when player is in water**.
- **Wind cone**: You must use command /give @p skydiving (I don't remember exactly)


## 🧪 Upcoming Features

Your help would be appreciated! 💖

- **Altitude-Based Wind Influence**

  Wind strength will vary depending on your altitude above the terrain — lower altitudes will have less wind effect.

- **Biome-Aware Wind**

  Different biomes will influence wind direction and intensity (e.g., deserts may have stronger gusts, forests more turbulence).

- **Weather-Driven Dynamics**
  
  Rain, storms, and other weather conditions will dynamically alter wind behavior.

- **Thermal Columns & Downwash**

  Air currents will push the player up or down based on vertical airflows, simulating thermals and sink zones.

- **Crafting recipe for Wind Sock Block 🏳**

  Craftable visual indicators to show current wind direction and speed in-world.

- **Faster falling when twisting fast**

  By default, with elytra, you can twist around the same place to be in the same place and descend slowly enough. In real skydiving, if you twist rapidly, you lose speed, thus losing handleability and falling dangerously fast.


## 🛠 Setup

This mod requires:

- **Fabric Loader** `0.14.x`
- **Fabric API**
- Minecraft `1.20.1`

### Build

```bash
./gradlew build
```

The mod .jar will be located in build/libs/.

## Install

Place the .jar file in your Minecraft mods/ folder (client and server).

🎮 Commands

| Command            | Description                           |
|--------------------|---------------------------------------|
| `/wind forecast`   | View upcoming 5 wind changes           |

📦 Project Structure

* `SkydivingHandler` – main server logic, tick handler, command registration
* `SkydivingModClient` – client wind application logic
* `WindForecast` – forecast queue and generation logic
* `WindChange` – simple data holder (direction + speed)
* `WindSyncPacket` – server-to-client wind sync
* `SkydivingConfig` – mod constants
* `WindUtils` – helper functions (compass, clamping, etc.)

## Tutorials used

https://wiki.fabricmc.net/tutorial:blocks
https://wiki.fabricmc.net/tutorial:blockentity
https://wiki.fabricmc.net/tutorial:blockentityrenderers
https://wiki.fabricmc.net/tutorial:custom_model
https://www.youtube.com/watch?v=qXkduRD61M4
https://www.youtube.com/watch?v=bOlbBOLfnfk

## 🔗 License
MIT – use freely, contribute freely.

Built with ❤️ for future skydivers!
