package com.devkproject.kioskmode;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;

//application component 사이에서 공동으로 멤버들을 사용할 수 있게 해주는 편리한 공유 클래스를 제공.
//어디서든 context 를 이용한 접근이 가능하다.
//manifests 에 application class name 을 추가해야함.

public class AppContext extends Application {
    private AppContext instance;
    private PowerManager.WakeLock wakeLock;
    private OnScreenOffReceiver onScreenOffReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        registerKioskModeScreenOffReceiver();
        startKioskService();
    }

    private void registerKioskModeScreenOffReceiver() {
        //스크린 끄기 receiver 등록
        final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        onScreenOffReceiver = new OnScreenOffReceiver();
        registerReceiver(onScreenOffReceiver, filter);
    }

    //화면과 관련된 wake lock 은 배터리 소모 이슈가 많아 정책이 변경되면서 현재 depreciated 됨.
    public PowerManager.WakeLock getWakeLock() {
        if(wakeLock == null) {
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Tag: partial wake lock");
        }
        return wakeLock;
    }

    private void startKioskService() {
        startService(new Intent(this, KioskService.class));
    }
}
