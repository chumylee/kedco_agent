package com.wareproz.mac.kedco;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import java.util.Date;

public class BaseActivity extends AppCompatActivity {

    // Session Manager Class
    SessionManagement session;
    long lastActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // Session Manager
        session = new SessionManagement(getApplicationContext());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        lastActivity = new Date().getTime();
        return super.onTouchEvent(event);
    }


    @Override
    public void onResume() {
        super.onResume();
        long now = new Date().getTime();
        if (lastActivity != 0){
            if (now - lastActivity > 120000) {
                // startActivity and force logon
                if(session.isLoggedIn()){
                    //open home page
                    Intent mIntent = new Intent(this, Unlocker.class);
                    startActivity(mIntent);
                }
            }
        }
    }

}
