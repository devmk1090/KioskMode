package com.devkproject.kioskmode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));
    private Button hiddenExitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FLAG_DISMISS_KEYGUARD : 기본잠금화면을 지울 수 있다. 이 플래그가 작동하는 순간 시스템볼륨 음량으로
        //효과음이 발생하면서 기본잠금화면이 없어진다.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        setContentView(R.layout.activity_main);

        //every time enters the kiosk mode, set the flag true
        PrefUtils.setKioskModeActive(true, getApplicationContext());

        hiddenExitButton = (Button) findViewById(R.id.hiddenExitButton);
        hiddenExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Break Out
                PrefUtils.setKioskModeActive(false, getApplicationContext());
                Toast.makeText(getApplicationContext(), "You can leave the app now!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //hasFocus == true (Activity focus O) -> onCreate(), onResume()
    //hasFocus == false(Activity focus X) -> onPause(), onDestroy()
    //window 가 focus 를 잃을 때, system dialog 를 무조건 닫는 broadcast 를 보냄.
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
    }

    //volume button disable
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (blockedKeys.contains(event.getKeyCode())) {
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    //back button disable
    @Override
    public void onBackPressed() {}
}
