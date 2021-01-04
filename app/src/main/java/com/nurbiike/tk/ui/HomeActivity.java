package com.nurbiike.tk.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.nurbiike.tk.R;

import static com.nurbiike.tk.ui.UtilMethods.setWindowStatus;
import static com.nurbiike.tk.ui.UtilMethods.setWindowStatusHidden;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the status bar, set status bar color to black opaque
        setWindowStatus(getWindow());
        setWindowStatusHidden(getWindow());

        setContentView(R.layout.activity_home);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getY();

        if (y < 20 && event.getAction() == MotionEvent.ACTION_DOWN) {
            setWindowStatus(getWindow());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
            setWindowStatusHidden(getWindow());
        }
        return false;
    }

    public void handleSingleBtn(View view) {
        startActivity(new Intent(HomeActivity.this, SingleOptionsActivity.class));
    }

    public void handleMultiBtn(View view) {
        startActivity(new Intent(HomeActivity.this, MultiOptionsActivity.class));
    }

    public void handleSettingsBtn(View view) {
        startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
    }

}