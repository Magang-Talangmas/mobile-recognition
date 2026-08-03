package com.example.javatraining;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.javatraining.data.repository.AbsensioRepository;

public class CctvReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("com.example.javatraining.CCTV_CHECK_IN".equals(intent.getAction())) {
            // Simulasi menerima sinyal dari backend via CCTV
            AbsensioRepository repository = new AbsensioRepository((android.app.Application) context.getApplicationContext());
            repository.performCheckIn("CCTV Auto");
            
            Toast.makeText(context, "Sistem mendeteksi kehadiran via CCTV!", Toast.LENGTH_LONG).show();
        }
    }
}
