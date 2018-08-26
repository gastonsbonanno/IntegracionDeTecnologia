package com.example.gasto.termometroindustrial;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    TextView loTemperatura;

    MediaPlayer alarm;
    Handler handler;
    Runnable runnable;
    int iTemperatura;
    Integer iConfTemp;
    Bundle bundle;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loTemperatura = (TextView)findViewById(R.id.lo_temperatura);
        alarm= MediaPlayer.create(MainActivity.this,R.raw.alarm);
        intent = getIntent();
        try {
            bundle = intent.getExtras();
            int intAux = bundle.getInt("confTemp");
            iConfTemp = new Integer(intAux);
        }catch (NumberFormatException e){
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "No es posible obtener la configuración", Toast.LENGTH_SHORT).show();
        }

        runnable = new Runnable() {
            @Override
            public void run() {
                iTemperatura = _getTemperaturaActual();
                handler.postDelayed(runnable, 5000);

                if(iConfTemp != null && iTemperatura < iConfTemp){
                    alarm.start();
                    Toast.makeText(MainActivity.this, "La temperatura es muy baja", Toast.LENGTH_SHORT).show();
                }
                loTemperatura.setText("Temperatura actual: "+iTemperatura+"C°");
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 0);
    }


    private int _getTemperaturaActual(){
        Intent bateriaIntent = registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int temperatura = (bateriaIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1))/10;
        return temperatura;
    }

    public void onBackPressed() {
        iConfTemp = -99; // Cambiar esto.
        finish();
    }

}
