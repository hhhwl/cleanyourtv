# Aerial Startup Bridge Implementation Plan

> **For agentic workers:** Use superpowers:executing-plans; native execution in the existing isolated workspace.

**Goal:** Open official Aerial through Xiaomi's accepted Home identity and boot/STR path.

**Architecture:** A separate no-UI Android module reuses the existing StartupReceiver
and StartupPolicy. The visible launcher remains the official Aerial APK.

**Tech Stack:** Java 17, Android SDK 34, AGP 8.5.2, JUnit 4.13.2.

**Spec:** ../specs/2026-10-29-aerial-startup-bridge-design.md

## Global constraints

Package/component remain `com.ts.launcher/com.gathin.tvhome.MainActivity`; minSdk 26,
targetSdk 25, code 5, existing signing key. No system uninstall, root, recurring
background work or modifications to the Aerial APK. Preserve remote voice/casting.

## Review focus

- Missing Aerial must fall back to Settings, not an invisible permanent Home.
- NoDisplay activity must finish before onResume.
- Receiver must preserve selected-Home/action checks and DEVICE_POWER protection.
- Equal version code and matching certificate must permit exact rollback.
- Real HDMI and Settings return paths must reach Aerial with stock Home disabled.

## Task 1: Package and verify the startup bridge

**Files:** `launcher/aerial-bridge/build.gradle`, module manifest and MainActivity;
`launcher/settings.gradle`. Reuse app StartupReceiver/Policy and their seven tests.

- [x] Observe failing device baseline: stock/legacy Home disabled, second Home remains FallbackHome.
- [x] Restore usable Home while preparing the bridge and record the nine background disables.
- [x] Build minimal bridge; run all project JVM tests, lint and release assembly.
- [x] Inspect packaged manifest and verify certificate/identity/code against installed Home.
- [x] Save exact installed APK; install bridge with -r and select it after stock disable.
- [x] Verify resolver, three Home returns, native Settings and HDMI1 session.
- [x] Have an independent reviewer check the branch and address findings.
- [x] Ask owner to check remote voice/casting and standby; reboot only with explicit permission.
- [x] Record before/after CPU/memory samples without treating cache/RSS as guaranteed savings.
- [x] Save exact APK/checksums, acceptance outputs and recovery instructions; commit reviewed source.

## Acceptance

Release build and lint pass. The 135 original JVM tests and seven shared startup
policy tests pass; the Python suite passes 57 tests with one environment skip.
An independent source review found no blockers.

The installed bridge is 8,844 bytes, has the same signing certificate and code 5
as the exact previous Home backup, and declares no service or network permission.
Its packaged entries match the final verification rebuild. The delivered APK's
hash is the installed APK's hash; a signed rebuild need not have identical container
bytes even when every ZIP entry and signing certificate match.

Device checks cover the real Aerial Settings and source controls, Settings Back,
three Settings-to-Home returns, HDMI1 input session and Home return. Earlier test
driver failures came from assuming Xiaomi's Settings window title contains a slash
and assuming the generic Settings intent always selects Xiaomi's panel; both
preconditions were corrected without changing the bridge's production routing.
The bridge uses the same ACTION_MAIN and task flags as Aerial's bringBackIntent.

The owner confirmed standby opens Aerial directly and remote voice/casting work.
After explicit owner permission a real reboot incremented the device boot count;
the BOOT_COMPLETED receiver opened the selected Home, and Aerial became foreground
without test-driver input. The owner confirmed no stock desktop or advertisement.
All ten packages disabled during this run remain disabled, as does the advertising
screensaver disabled earlier. Preserved packages and casting processes remain
available. The bridge has no running service.

Memory and CPU snapshots are retained privately. The post-reboot memory snapshot
is not a controlled comparison to the long-running baseline, so no fixed memory
saving or performance percentage is claimed. Changes and recovery are saved with
the household's local deliverables; no source or household logs were published.
