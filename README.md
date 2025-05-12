# Paragliding/Skydiving Mod for Minecraft based on Fabric

A Minecraft Fabric mod that adds realistic **wind physics** affecting Elytra gliding.

Being a skydiver myself, I noticed similarities between gliding with Elytras
and skydiving with small canopies. I immediately decided to share the experience
with friends.

Later, this mod was born as a simple toy to make Elytra flying a bit more fun.


## ✨ Features

- **Dynamic Wind Direction**: Wind rotates gradually (15° per minute).
- **Variable Wind Speed**: Changes every 60 seconds.
- **Forecast System**: Maintains a queue of 15 future wind changes.
- **Wind Sync**: Server-controlled wind synced to all clients.
- **Command `/wind forecast`**: Shows next 5 wind changes in readable format (e.g., `2 min: SE at 0.015`).
- **Command `/wind again`**: Regenerates the future weather. I wish we have the same in real world!
- **Realistic flight physics**: Headwind slows you, tailwind boosts, crosswind drifts.
- **Faster falling when twisting fast**: By default, with elytra, you can twist around the same place and continue descending safely enough. In real skydiving, if you twist rapidly, you lose speed, thus losing handleability and falling dangerously fast.
- **Disables wind when player is in water**.
- **Wind cone**: You must use command /give @p skydivingmod:windsock (I don't remember exactly)
- **Low altitude wind influence**:
  Wind strength will vary depending on your altitude above the terrain — lower altitudes will have less wind effect.
  - 0-5 blocks - 30% effect
  - 5-10 blocks - 50% effect
  - 10+ blocks - 100% effect
- **Crafting recipe for Wind Sock Block 🏳**:
  Craftable visual indicators to show current wind direction and speed in-world.
- **Settings**. Settings page with a persistent state
- **Thermal Columns & Downwash**

  Different biomes will influence wind direction and intensity (e.g., deserts may have stronger gusts, forests more turbulence).
  Air currents will push the player up or down based on vertical airflows, simulating thermals and sink zones.

  Raise always:
  - Players are above slopes or ridges which facing the wind

  Falling always:
  - Players are above slopes or ridges against the wind

  Effects are most strong when 10-100 blocks above the ground and gradually lose strength above and beyond this range


## 🧪 Upcoming Features

Your help would be appreciated! 💖

1. **Biome-Aware Wind**

   Raise during the daytime:
   - Bare soil, dry grasslands, or any type of stone blocks slopes heat up quickly under the sun.

2. ~~**Weather-Driven Dynamics**~~

   ~~Rain, storms, and other weather conditions will dynamically alter wind behavior.~~

3. **Summon airplane** command

   It would be amazing to either cast a spell or use some radio to appear on the play flying high above.

## 🛠 Setup

This mod requires:

- **Fabric Loader** `0.16.14`
- **Minecraft** `1.21.1`

## Install

Place the .jar file in your Minecraft mods/ folder (client and server).

## In-game 🎮 console commands

| Command                | Description                                                        |
|------------------------|--------------------------------------------------------------------|
| `/wind forecast`       | View upcoming 5 wind changes                                       |
| `/wind again`          | If you don't like the forecast, you can regenerate future weather. |
| `/wind hud true/false` | Show debug hud                                                     |

# Developers

## 🛠 Setup

This mod requires:

- **Fabric Loader** `0.16.14`
- Minecraft `1.21.1`
- Java 21
- Gradle 8


## Build

```bash
./gradlew build
```

The mod .jar will be located in build/libs/.




## 📦 Project Structure

* `SkydivingHandler` – main server logic, tick handler, command registration
* `SkydivingModClient` – client wind application logic
* `WindForecast` – forecast queue and generation logic
* `WindChange` – simple data holder (direction + speed)
* `WindSyncPacket` – server-to-client wind sync
* `SkydivingConfig` – mod constants
* `WindUtils` – helper functions (compass, clamping, etc.)

## Tutorials used

- https://wiki.fabricmc.net/tutorial:blocks
- https://wiki.fabricmc.net/tutorial:blockentity
- https://wiki.fabricmc.net/tutorial:blockentityrenderers
- https://wiki.fabricmc.net/tutorial:custom_model
- https://www.youtube.com/watch?v=qXkduRD61M4
- https://www.youtube.com/watch?v=bOlbBOLfnfk
- ChatGPT, of course

## 🔗 License

MIT – use freely, contribute freely.

Built with ❤️ for future skydivers/paragliders!

## Author

https://github.com/tgskiv