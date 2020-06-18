package com.devkproject.kioskmode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent kioskIntent = new Intent(context, MainActivity.class);
        //FLAG_ACTIVITY_NEW_TASK : 새로운 태스크를 생성하여 그 태스크안에 액티비트를 추가할때 사용
        //기존에 존재하는 태스크들중에 생성하려는 액티비티와 동일한 affinity 를 가지고 있는 태스크가 있다면
        //그곳으로 새 액티비티가 들어감.
        kioskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(kioskIntent);
    }
}
