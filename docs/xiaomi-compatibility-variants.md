# MiTV-ASTP0 compatibility variants

This fork contains three separate builds. The device records below concern one Xiaomi
MiTV-ASTP0 (`mulan`), Android 9/API 28, firmware `24.7.4.3982`.

| Build | Purpose | Package and version | SDK |
|---|---|---|---|
| Full compatibility launcher | CleanYourTV Home, native input/settings panels, short MENU handling and boot/standby Home recovery | Tested profile: `com.ts.launcher`, 2.4, code 5 | min 26, target 25 |
| Aerial Home bridge | No-UI HOME entry that opens separately installed official Aerial | `com.ts.launcher`, `2.4-aerial-bridge.1`, code 5 | min 26, target 25 |
| Native Settings-key preview | Focused port of optional `settings_key_service` / `settings_key_action` routing onto CleanYourTV 3.1 | Published standard build: `com.gathin.tvhome`, 3.1, code 10 | min 26, target 34 |

The full compatibility launcher and Aerial bridge use the **same package ID**: installing one
replaces the other when the signature and version allow it. They are alternatives, not two apps
to install together. Official Aerial keeps its separate `app.orchard.tv` identity. The 3.1 preview
contains neither the legacy startup receiver nor the bridge.

## Sources and device records

- **Full launcher:** [tested 2.4 source](https://github.com/hhhwl/cleanyourtv/tree/v2.4-mitv-astp0-controls.1)
  (`98fb5dead06a7e8dd51aa791ec9fd72649bc0a5d`),
  [native controls and owner checks](https://github.com/hhhwl/cleanyourtv/blob/v2.4-mitv-astp0-controls.1/docs/devices/xiaomi-mitv-astp0-controls.md),
  [build/install/recovery record](https://github.com/hhhwl/cleanyourtv/blob/v2.4-mitv-astp0-controls.1/docs/devices/xiaomi-mitv-astp0-mulan.md),
  and [release files](https://github.com/hhhwl/cleanyourtv/releases/tag/v2.4-mitv-astp0-controls.1).
  Pin this tag to reproduce the 2.4 source: the later `codex/xiaomi-str-wake-compat` branch identifies
  itself as 2.5/code 6. The owner's physical MENU, HDMI and standby-wake checks are recorded against 2.4.
- **Aerial bridge:** [source](https://github.com/hhhwl/cleanyourtv/tree/v2.4-mitv-astp0-aerial-bridge.1),
  [installation, recovery and device checks](aerial-xiaomi-astp0.md), and
  [release files](https://github.com/hhhwl/cleanyourtv/releases/tag/v2.4-mitv-astp0-aerial-bridge.1).
  On 2026-10-29, official Aerial 1.67 remained unchanged; the owner confirmed direct entry after
  standby wake and an authorized full Android reboot.
- **3.1 Settings preview:** [source](https://github.com/hhhwl/cleanyourtv/tree/75edbef3234a6aedcc1febba5619a68fda44984f),
  [configuration and fallback behavior](https://github.com/hhhwl/cleanyourtv/blob/75edbef3234a6aedcc1febba5619a68fda44984f/themes/README.md#native-settings-keys),
  and [release/build notes](https://github.com/hhhwl/cleanyourtv/releases/tag/v3.1-native-settings-preview.1).
  This port preserves the 3.1 SDK levels, permissions and input routing. Its local checks passed:
  305 JVM tests, release assembly, lint with zero errors, and the repository suite
  (2,172 passed, 39 skipped).

## Native-panel screenshot

![MiTV-ASTP0 native settings panel opened by the 2.4 compatibility launcher](https://raw.githubusercontent.com/hhhwl/cleanyourtv/75edbef3234a6aedcc1febba5619a68fda44984f/docs/switches/settings_key_service.png)

Captured on 2026-10-21 using the **2.4 compatibility launcher**, with keycode 82 mapped to
`com.xiaomi.mitv.settings/.TvSettingService` and action
`com.xiaomi.intent.action.show.globalsetting`. The image shows Xiaomi's native network, picture,
sound and input settings. Its storage in the later 3.1 source tree does not change that provenance.

## Build and permissions

Use the source/tag for the intended variant, a full JDK 17, Android SDK 34 and Build Tools 34.0.0.
Run from that checkout's `launcher/` directory:

```sh
# Full 2.4 launcher, inspected Xiaomi compatibility profile
./gradlew --no-daemon :app:testReleaseUnitTest :app:lintRelease :app:assembleRelease \
  -PlauncherApplicationId=com.ts.launcher -PlegacyXiaomiWake=true

# Aerial bridge, from its own complete source checkout
./gradlew --no-daemon :aerial-bridge:testReleaseUnitTest :aerial-bridge:lintRelease :aerial-bridge:assembleRelease

# 3.1 native Settings preview, standard package
./gradlew --no-daemon :app:testReleaseUnitTest :app:lintRelease :app:assembleRelease
```

The full 2.4 launcher defaults to `com.gathin.tvhome` / target SDK 34 unless both compatibility
properties above are supplied. Its startup feature adds `RECEIVE_BOOT_COMPLETED`; it retains the
launcher's Wi-Fi state/location access and optional key-accessibility helpers, with no `INTERNET`
permission. The tested foreground MENU path did not require those helpers.

The bridge requests only `RECEIVE_BOOT_COMPLETED`, has no service or network permission, and
reuses the full checkout's startup sources. Aerial's own networking and optional services belong
to the official Aerial app. The 3.1 Settings port adds no permissions or services to its upstream base.

Follow the linked variant-specific installation and recovery records. Back up the existing Home
and check package ID, signing certificate and version before an update; locally rebuilt debug
certificates normally differ from the published device build. Preserve a working Home throughout.
These are records for this firmware, not a claim that other Xiaomi models accept the same HOME setup.
