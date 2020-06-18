package com.devkproject.kioskmode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public class OnScreenOffReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            AppContext appContext = (AppContext) context.getApplicationContext();
            //is kiosk mode active ?
            if(PrefUtils.isKioskModeActive(appContext)) {
                wakeUpDevice(appContext);
            }
        }
    }

    private void wakeUpDevice(AppContext appContext) {
        PowerManager.WakeLock wakeLock = appContext.getWakeLock();
        if(wakeLock.isHeld()) {
            //Wake lock 해제. 배터리 소모를 피하기 위해 앱이 종료되자마자 이 메서드를 사용하여
            //wake lock 을 해제하는 것이 중요.
            wakeLock.release();
        }
        wakeLock.acquire();

        wakeLock.release();
    }
}
