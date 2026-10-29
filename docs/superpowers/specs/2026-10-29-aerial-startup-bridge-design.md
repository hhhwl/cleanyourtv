# Aerial startup bridge for MiTV-ASTP0

The owner wants Aerial's interface/screensaver with CleanYourTV's background cleanup
and direct startup. Remote-button voice and all casting must stay; far-field voice
may be disabled. HDMI, USB playback, video apps, accounts and OTA remain enabled.

## Evidence and approaches

The official Aerial 1.67 APK works, including physical Home, HDMI and aerial films.
With stock Home enabled, standby resumes it before Aerial's guard takes over.
After disabling stock Home and the old compatible Home, selecting `app.orchard.tv`
resolves to FallbackHome; the second real Home command stays there for over ten
seconds. That configuration has been reverted.

Rebuilding Aerial under the accepted package ID would complicate official updates.
Use the existing, signed `com.ts.launcher` Home identity as a tiny, non-rendering
trampoline instead. This is the agent's implementation choice within the owner's
request to combine the projects; Aerial itself stays the official APK.

## Design and constraints

- Add a separate `aerial-bridge` module. Reuse CleanYourTV's tested boot/STR receiver
  and default-Home policy. No original Home UI, AndroidX, network permission,
  services, timers, polling or accessibility service in the bridge.
- Preserve `com.ts.launcher/com.gathin.tvhome.MainActivity`, signing certificate,
  code 5 and target SDK 25. Retaining code 5 permits reinstalling the exact signed
  previous APK without a version downgrade or erasing its data.
- MainActivity uses `Theme.NoDisplay`, explicitly opens `app.orchard.tv/.MainActivity`
  and finishes before onResume. A missing/blocked Aerial opens system Settings.
- The receiver only responds to boot/STR while the bridge is the selected Home;
  incoming broadcasts require DEVICE_POWER. Do not send a global synthetic STR.
- Back up the exact previous APK, verify matching identity/certificate before
  updating, select the bridge after disabling stock Home, and verify native Settings,
  HDMI, repeated Home, standby and an owner-authorized reboot.
- Disable only inspected, recorded package batches; retain a recovery script.
  Publishing household state or changes to GitHub is outside this task.
