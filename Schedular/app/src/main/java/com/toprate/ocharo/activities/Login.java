package com.toprate.ocharo.activities;

import android.app.KeyguardManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.toprate.ocharo.R;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

            if (km.isKeyguardSecure()) {
                Intent authIntent = km.createConfirmDeviceCredentialIntent(getString(R.string.dialog_title_auth), getString(R.string.dialog_msg_auth));
                startActivityForResult(authIntent, 1234);
            }
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1234) {
            if (resultCode == RESULT_OK) {
                startActivity(new Intent(this, MainActivity.class));
            }
        }
    }
}
