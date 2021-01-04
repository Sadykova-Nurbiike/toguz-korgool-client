package com.nurbiike.tk.ui;

import android.os.Build;
import android.view.View;
import android.view.WindowManager;

public class UtilMethods {
    public static void setWindowStatus(android.view.Window window) {

        int flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT_WATCH || Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            window.getAttributes().flags |= flags;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int uiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            window.getDecorView().setSystemUiVisibility(uiVisibility);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getAttributes().flags &= ~flags;

            window.setStatusBarColor(android.graphics.Color.parseColor("#66000000"));
        }
    }

    public static void setWindowStatusHidden(android.view.Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int uiVisibility = window.getDecorView().getSystemUiVisibility();
            uiVisibility |= View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            window.getDecorView().setSystemUiVisibility(uiVisibility);
        }
    }

}
