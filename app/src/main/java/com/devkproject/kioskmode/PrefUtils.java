package com.devkproject.kioskmode;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

// 1.SharedPreferences 는 데이터를 키-값(key-value) 형식으로 저장함.
// 2.PreferenceManager.getDefaultSharedPreferences() 함수는 별도의 파일명을 명시하지 않으므로
// 앱의 패키지명을 파일명으로 사용한다.
// 3.데이터를 저장하려면 edit() 함수 사용.
// 4.저장된 데이터를 획득할땐 getter 함수를 이용 ex)getBoolean(String key, boolean defValue) ,getString(String key, String defValue)
// 5.apply() -> 비동기 처리, commit() -> 동기 처리

public class PrefUtils {
    private static final String PREF_KIOSK_MODE = "pref_kiosk_mode";

    public static boolean isKioskModeActive(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_KIOSK_MODE, false);
    }

    public static void setKioskModeActive(final boolean active, final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_KIOSK_MODE, active).apply();
    }
}
