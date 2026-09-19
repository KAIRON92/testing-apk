# Authorized QA Telemetry Contract

The production game adapter must be the authoritative source of runtime state. TestingAPK does not infer entity state from arbitrary process memory.

## Envelope

A telemetry message is a JSON representation of GameState and should be associated with an authenticated QA session.

Required:
- timestampMs
- players
- vehicles
- items

Optional synchronization maps:
- clientPositions
- authoritativePositions

## Player state

Each player can provide:
- id
- displayName
- type: PLAYER or BOT
- position
- distanceMeters
- direction
- movement
- health
- weapon
- item
- vehicleId
- skeleton joints
- screenX / screenY as normalized camera projection

## Vehicles

Provide:
- id
- type
- position
- distanceMeters
- speedMps
- occupantIds
- screenX / screenY

## Items

Provide:
- id
- name
- position
- distanceMeters
- floor
- optional projected screen coordinates

## Anti-cheat telemetry

Anti-cheat remains enabled. The game/server integration should publish a detection event containing:
- QA session ID
- detection category
- detection timestamp

TestingAPK calculates detection latency from the recorded controlled-test start timestamp.

## Trust model

Production deployments should authenticate the QA producer/session and reject unexpected telemetry sources. Keep the WebSocket endpoint on a trusted QA network or use TLS/authentication for non-loopback transport.
