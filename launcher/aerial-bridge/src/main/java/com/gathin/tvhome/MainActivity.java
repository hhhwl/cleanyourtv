package com.gathin.tvhome;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

/** A non-rendering HOME entry for the Xiaomi package allowlist. Aerial owns the visible UI. */
public final class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            startActivity(new Intent(Intent.ACTION_MAIN)
                    .setComponent(new ComponentName("app.orchard.tv", "app.orchard.tv.MainActivity"))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                            | Intent.FLAG_ACTIVITY_NO_ANIMATION));
        } catch (ActivityNotFoundException | SecurityException e) {
            Log.e("AerialHomeBridge", "Aerial unavailable; opening system Settings for recovery", e);
            try {
                startActivity(new Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            } catch (ActivityNotFoundException | SecurityException recoveryError) {
                Log.e("AerialHomeBridge", "System Settings unavailable", recoveryError);
            }
        } finally {
            // Theme.NoDisplay activities must finish before onResume on this Android version.
            finish();
            overridePendingTransition(0, 0);
        }
    }
}
