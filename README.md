# TestingAPK — Multiplayer Game Security QA

Authorized internal Android QA application for an owned multiplayer game.

## Scope

TestingAPK is a developer/security-team harness that consumes an authenticated game instrumentation/test-state interface. It visualizes runtime state, diagnoses synchronization issues, records authorized anti-cheat detection telemetry, and exports test-session reports.

It deliberately does **not** implement arbitrary RAM scanning, offset hunting, stealth hooks, code injection, anti-cheat bypass/evasion, or third-party-game cheat functionality.

## Architecture

- **UI** — dashboard, test instances, sessions, settings
- **Telemetry** — replaceable `TelemetryProvider` contract
- **Models** — PlayerState, BotState, VehicleState, ItemState, GameState
- **Overlay** — foreground service + transparent renderer + floating controls
- **Diagnostics** — synchronization and movement anomaly analysis
- **Anti-cheat** — passive authorized detection-event monitor
- **Sessions** — persistent session metadata and event history
- **Reports** — JSON export for engineering/security review

## Runtime contract

The production adapter should receive data from the game's authenticated QA/debug channel, including:

- player/bot identity and 3D position
- movement state and direction
- skeleton/body joints
- health and other debug state
- weapons/items
- vehicles and occupants
- client/server/authoritative positions
- interpolation/jitter/latency diagnostics
- anti-cheat detection events and timestamps

The overlay consumes `GameState`; it does not know how the game obtains that data.

## Development fallback

A clearly isolated `MockTelemetryProvider` is included for UI/renderer development. It is deterministic sample data only and is never presented as real game telemetry.

## Build

Install JDK 17 and Android SDK 34. From the repository root:

```bash
./gradlew :app:assembleDebug
```

If a Gradle installation is used instead:

```bash
gradle :app:assembleDebug
```

Debug APK:

`app/build/outputs/apk/debug/app-debug.apk`

## QA session flow

1. Add/import an authorized QA game build.
2. Create/select a test instance.
3. Launch the registered test package.
4. Connect the game's authenticated telemetry adapter.
5. Start the floating QA overlay.
6. Run synchronization and anti-cheat test scenarios.
7. Stop the session.
8. Export the session JSON report.

## Telemetry adapter

Implement `TelemetryProvider` for the real game. The adapter should validate a QA session token and reject untrusted producers. Keep the adapter in its own package so the UI, overlay and diagnostics remain game-agnostic.
