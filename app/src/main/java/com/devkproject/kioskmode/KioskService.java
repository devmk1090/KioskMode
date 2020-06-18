package com.devkproject.kioskmode;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.concurrent.TimeUnit;

//Service 는 백그라운드에서 오래 실행되는 작업을 수행할 수 있는 애플리케이션 구성 요소이며 사용자 인터페이스를 제공하지 않습니다.
// 다른 애플리케이션 구성 요소가 서비스를 시작할 수 있으며, 이는 사용자가 다른 애플리케이션으로 전환하더라도
// 백그라운드에서 계속해서 실행됩니다. 이외에도, 구성 요소를 서비스에 바인딩하여 서비스와 상호작용할 수 있으며,
// 심지어는 프로세스 간 통신(IPC)도 수행할 수 있습니다. 예를 들어 한 서비스는 네트워크 트랜잭션을 처리하고,
// 음악을 재생하고 파일 I/O를 수행하거나 콘텐츠 제공자와 상호작용할 수 있으며 이 모든 것을 백그라운드에서 수행할 수 있습니다.

//서비스는 그저 백그라운드에서 실행될 수 있는 구성 요소일 뿐입니다. 이는 사용자가 애플리케이션과 상호작용하지 않아도 관계없이 해당됩니다.
// 그러므로 필요한 경우에만 서비스를 사용해야 합니다.
// 사용자가 애플리케이션과 상호작용하는 동안 기본 스레드 밖에서 작업을 수행해야 하는 경우, 새 스레드를 생성해야 합니다.
// 예를 들어 액티비티가 실행되는 중에만 음악을 재생하고자 하는 경우, onCreate() 안에 스레드를 생성하고
// 이를 onStart()에서 실행하기 시작한 다음, onStop()에서 중단하면 됩니다.
// 또한 기존의 Thread 클래스 대신 AsyncTask 또는 HandlerThread 를 사용하는 방안도 고려해야함.
public class KioskService extends Service {

    private static final long INTERVAL = TimeUnit.SECONDS.toMillis(2);
    private static final String TAG = KioskService.class.getSimpleName();

    private Thread t = null;
    private Context context = null;
    private boolean running = false;

    @Override
    public void onDestroy() {
        Log.i(TAG, "Stopping service 'KioskService'");
        running = false;
        super.onDestroy();
    }


//onStartCommand(): 시스템이 이 메서드를 호출하는 것은 또 다른 구성 요소(예: 액티비티)가 서비스를 시작하도록 요청하는 경우입니다.
//이때 startService()를 호출하는 방법을 씁니다. 이 메서드가 실행되면 서비스가 시작되고 백그라운드에서 무한히 실행될 수 있습니다.
//이것을 구현하면 서비스의 작업이 완료되었을 때 해당 서비스를 중단하는 것은 개발자 본인의 책임이며,
// 이때 stopSelf() 또는 stopService()를 호출하면 됩니다. 바인딩만 제공하고자 하는 경우, 이 메서드를 구현하지 않아도 됩니다.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Starting service 'KioskService'");
        running = true;
        context = this;

        //만약 앱이 포어그라운드라면 주기적으로 쓰레드가 시작됨.
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    handleKioskMode();
                    try {
                        Thread.sleep(INTERVAL);
                    } catch (InterruptedException e) {
                        Log.i(TAG, "Thread interrupted: 'KioskService'");
                    }
                } while (running);
                stopSelf();
            }
        });
        t.start();
        return Service.START_NOT_STICKY;
//START_NOT_STICKY: 시스템이 서비스를 onStartCommand() 반환 후에 중단시키면 서비스를 재생성하면 안 됩니다.
//다만 전달할 보류 인텐트가 있는 경우는 예외입니다. 이는 서비스가 불필요하게 실행되는 일을 피할 수 있는 가장 안전한 옵션이며,
//애플리케이션이 완료되지 않은 모든 작업을 단순히 다시 시작할 수 있을 때 유용합니다.
    }

    private void handleKioskMode() {
        //is Kiosk Mode active?
        if (PrefUtils.isKioskModeActive(context)) {
            //is App in background ?
            if (isInBackground()) {
                restoreApp(); //restore
            }
        }
    }

    //getRunningTasks(): 함수를 이용하면 실행 중인 액티비티 정보가 담긴 RunningTaskInfo 객체를 얻을 수 있습니다.
    // RunningTaskInfo 객체에서 최상위 액티비티의 클래스명은 topActivity.getClassName( ) 함수로 구할 수 있으며,
    // 이 문자열을 비교하여 특정 액티비티가 화면을 점유하고 있는지 판단하게 됩니다.
    // getRunningTasks ( ) 함수는 API Level 21(Android 5.0)부터 deprecated 되긴 했지만,
    // 하위 호환성을 목적으로 계속 사용할 수 있습니다. 또한, 앱의 Task 정보를 구하려면 퍼미션이 부여되어 있어야 합니다.
    private boolean isInBackground() {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> taskInfo = activityManager.getRunningTasks(1);
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        return (!context.getApplicationContext().getPackageName().equals(componentInfo.getPackageName()));
    }

    private void restoreApp() {
        //Restart activity
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
