package com.example.gasto.termometroindustrial;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import de.nitri.gauge.Gauge;


public class MainActivity extends AppCompatActivity {

    TextView loTemperatura;
    TextView loTiempo;

    MediaPlayer alarm;
    Handler handler;
    Runnable runnable;
    int iTemperatura;
    Integer iConfTemp;
    Integer iConfTiempo;
    Bundle bundle;
    Intent intent;
    Gauge gauge;
    CountDownTimer timer;
    boolean estadoAlarma = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loTemperatura = (TextView)findViewById(R.id.lo_temperatura);
        loTiempo = (TextView)findViewById(R.id.lo_tiempo);
        gauge = (Gauge)findViewById(R.id.gauge);
        alarm= MediaPlayer.create(MainActivity.this,R.raw.alarm);
        intent = getIntent();





        try {
            bundle = intent.getExtras();
            int intAux = bundle.getInt("confTemp");
            iConfTemp = new Integer(intAux);
            intAux = bundle.getInt("confTiempo");
            iConfTiempo = new Integer(intAux);
            if(!iConfTiempo.equals(0)) {
                setTimer(iConfTiempo * 60 * 1000);
                timer.start();
            }
        }catch (NumberFormatException e){
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "No es posible obtener la configuración", Toast.LENGTH_SHORT).show();
        }

        runnable = new Runnable() {
            @Override
            public void run() {
                iTemperatura = _getTemperaturaActual();
                handler.postDelayed(runnable, 5000);
                gauge.setValue(iTemperatura);

                if(iConfTemp != null && iTemperatura < iConfTemp){
                    estadoAlarma = true;
                    Toast.makeText(MainActivity.this, "La temperatura es muy baja", Toast.LENGTH_SHORT).show();
                }
                loTemperatura.setText("Temperatura actual: "+iTemperatura+"C°");
                //loTiempo.setText("Tiempo restante: "+iConfTiempo+" Minutos");
                if(estadoAlarma)
                    iniciarAlarma();
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

 /*      private int _getTiempoRestante(){
        return tiempoRestante;
    }
*/

    public void onBackPressed() {
        iConfTemp = null; // Cambiar esto.
        estadoAlarma = false;
        try {
            timer.cancel();
        }catch (Exception e){}

        finish();
    }

/*
    public class CountDownT extends CountDownTimer {
        public CountDownT(long milisegundos, long TimeGap){
            super.(milisegundos)
        }

    }
*/


    private void setTimer(Integer milisegundos){
        timer = new CountDownTimer(milisegundos,1000) {
            @Override
            public void onTick(long lMillis) {
                //loTiempo.setText(Long.toString(l));
                String tiempoActual = String.format("%02d:%02d:%02d"
                        , TimeUnit.MILLISECONDS.toHours(lMillis)
                        , TimeUnit.MILLISECONDS.toMinutes(lMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(lMillis))
                        , TimeUnit.MILLISECONDS.toSeconds(lMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(lMillis)));
                loTiempo.setText("Tiempo restante: "+tiempoActual);
            }

            @Override
            public void onFinish() {
                estadoAlarma = true;
                Toast.makeText(MainActivity.this, "El temporizador finalizó", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void iniciarAlarma(){
        alarm.start();
    }
}
