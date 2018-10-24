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

    TextView loConfTemp;
    TextView loConfTempMax;
    TextView loTemperatura;
    TextView loTiempo;

    MediaPlayer alarm;
    Handler handler;
    Runnable runnable;
    int iTemperaturaActual;
    Integer iConfTemp;
    Integer iConfTempMax;
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
        getSupportActionBar().hide();

        loConfTemp = (TextView)findViewById(R.id.lo_conftemp);
        loConfTempMax = (TextView)findViewById(R.id.lo_conftempMax);
        loTemperatura = (TextView)findViewById(R.id.lo_temperatura);
        loTiempo = (TextView)findViewById(R.id.lo_tiempo);
        gauge = (Gauge)findViewById(R.id.gauge);
        alarm= MediaPlayer.create(MainActivity.this,R.raw.alarm);
        intent = getIntent();





        try {
            bundle = intent.getExtras();
            /*
            int intAux = bundle.getInt("confTemp");
            iConfTemp = new Integer(intAux);
            intAux = bundle.getInt("confTempMax");
            iConfTempMax = new Integer(intAux);
            intAux = bundle.getInt("confTiempo");
            iConfTiempo = new Integer(intAux);
            */
            String sAux = bundle.getString("confTemp",null);
            if(sAux != null) {
                iConfTemp = Integer.parseInt(sAux);
                loConfTemp.setText("Temperatura mínima: "+iConfTemp+"°C");
            }

            sAux = bundle.getString("confTempMax",null);
            if(sAux != null) {
                iConfTempMax = Integer.parseInt(sAux);
                loConfTempMax.setText("Temperatura máxima: "+iConfTempMax+"°C");
            }

            sAux = bundle.getString("confTiempo",null);
            if(sAux != null) {
                iConfTiempo = Integer.parseInt(sAux);
                if(!iConfTiempo.equals(0))
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
                iTemperaturaActual = _getTemperaturaActual();
                handler.postDelayed(runnable, 5000);
                gauge.setValue(iTemperaturaActual);
                loTemperatura.setText("Temperatura actual: "+iTemperaturaActual+"°C");

                if(iConfTemp != null && iTemperaturaActual < iConfTemp){
                    estadoAlarma = true;
                    Toast.makeText(MainActivity.this, "La temperatura es muy baja", Toast.LENGTH_SHORT).show();
                }
                if(iConfTempMax != null  && iTemperaturaActual > iConfTempMax){
                    estadoAlarma = true;
                    Toast.makeText(MainActivity.this, "La temperatura es muy alta", Toast.LENGTH_SHORT).show();
                }

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


    public void onBackPressed() {
        iConfTemp = null;
        iConfTempMax = null;
        estadoAlarma = false;
        try {
            timer.cancel();
        }catch (Exception e){}

        finish();
    }


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
