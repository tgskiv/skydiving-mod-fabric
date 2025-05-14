# Changelog

All notable changes to this project will be documented in this file.


## [1.0.104] - 2025-05-14
### Added
- **Smooth Wind Transitions**: Wind now rotates gradually using interpolated movement.
- **Command `/wind again`** to regenerate upcoming weather conditions.
- **Slope-Based Updrafts and Downdrafts**: Wind now rises or sinks based on terrain slope and wind direction.
- **In-Game HUD**: Displays vertical airflows and wind data.
- **Faster Falling with Twisting**: Simulates real-world canopy loss of lift during rapid spins.

### Fixed
- Improved how slope and wind interact — reduced noise from small terrain variations.

---

## [1.0.0] - 2025-05-11
### Added
- **Persistent Config**: Settings are now saved between sessions.
- **Settings Menu**: Basic config UI added (session-only for now).

### Improved
- Realistic wind speed units (now shown in m/s).
- Enhanced versioning and ground effect logic.

---

## [0.1.1] - 2025-05-08
### Added
- **Windsock Block**: Visual wind indicator with full model and rotation.
- **Crafting Recipe** for windsock.
- **Low Altitude Wind Effect**: Wind strength now varies with height.

### Fixed
- Textures and rendering issues on windsock.
- Windsock disappearing at a distance.

---

## [0.1.0] - 2025-05-04
### Added
- **Initial Wind Simulation**: Dynamic wind direction and speed synced across server/client.
- **Wind Forecast System**: View upcoming changes via `/wind forecast`.
- **Wind Effects on Elytra Flight**: Tailwind boosts, headwind slows, crosswind drifts.
- **Command to Regenerate Forecast**: `/wind again`.
- **Speed Display in m/s** for realism.

---

## [0.0.1] - 2025-04-29
### Added
- First commit and initial project setup.
- Basic wind vector logic and weather forecast system.
