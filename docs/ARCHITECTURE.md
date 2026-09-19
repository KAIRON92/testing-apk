# TestingAPK Architecture

app/src/main/java/com/kairon/testingapk/

- MainActivity.kt — application entry point
- ui/ — dashboard and settings UI
- registry/ — authorized test-instance metadata
- model/ — game-state and session contracts
- telemetry/ — production adapter plus isolated development mock
- overlay/ — renderer, configuration and floating controls
- diagnostics/ — synchronization analysis
- anticheat/ — passive detection telemetry and latency measurement
- session/ — session persistence
- report/ — JSON report export

## Dependency direction

UI -> registry/session/report
UI -> overlay
Telemetry -> model
Diagnostics -> model
Overlay -> model + diagnostics
Anti-cheat -> model

The overlay never imports a game SDK. A production telemetry adapter is the only component that knows the transport/protocol of the game.

## Production adapter

WebSocketTelemetryProvider is a transport example. Replace or configure the endpoint to match the owned game's authenticated QA channel. The game should publish authoritative state rather than requiring the Android client to inspect process memory.

## Development provider

MockTelemetryProvider exists only to exercise the renderer and diagnostics when the real game is unavailable. It is explicitly selected through Telemetry Settings and should be disabled in production QA builds.

## Session lifecycle

Start session -> launch QA build -> connect telemetry -> render/diagnose -> collect detection events -> stop session -> export report.

## Security

No generic process-memory reader, offset scanner, stealth hook, code injection, anti-cheat bypass or concealment mechanism belongs in this project.
