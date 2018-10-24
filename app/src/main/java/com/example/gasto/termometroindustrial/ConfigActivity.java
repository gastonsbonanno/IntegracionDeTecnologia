package com.example.gasto.termometroindustrial;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ConfigActivity extends AppCompatActivity{

    TextView loTemperatura;
    EditText loConfTemp;
    EditText loConfTempMax;
    EditText loConfTiempo;
    Button loBotonComenzar;
    Intent intent;
    Runnable runnable;
    int iTemperaturaActual;
    Handler handler;
    ImageView loFondo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        getSupportActionBar().hide();

        loTemperatura = (TextView)findViewById(R.id.lo_temperatura);
        loConfTemp = (EditText) findViewById(R.id.lo_confTemp);
        loConfTempMax = (EditText) findViewById(R.id.lo_confTempMax);
        loConfTiempo = (EditText) findViewById(R.id.lo_confTiempo);
        loBotonComenzar = (Button) findViewById(R.id.lo_botonComenzar);
        loFondo = (ImageView) findViewById(R.id.lo_fondo);
        loBotonComenzar.setOnClickListener(new onClickClass());
        loFondo.setOnClickListener(new onClickFondo());

        runnable = new Runnable() {
            @Override
            public void run() {
                iTemperaturaActual = _getTemperaturaActual();
                handler.postDelayed(runnable, 5000);
                loTemperatura.setText("Temperatura actual: "+iTemperaturaActual+"°C");
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 0);
    }


    public class onClickClass implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            try{
                if(validarPermiteModificar())
                    startActivity(intent);
            }
            catch (Exception e) {
                Toast.makeText(ConfigActivity.this, "Debe seleccionar una temperatura valida", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public class onClickFondo implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            try{
                hideKeyboard(ConfigActivity.this);
            }
            catch (Exception e) {
            }
        }

    }

    public boolean validarPermiteModificar(){
        intent = new Intent(ConfigActivity.this, MainActivity.class);

        if(!loConfTemp.getText().toString().equals("") && !loConfTempMax.getText().toString().equals("")) {
            int iTemp = new Integer(loConfTemp.getText().toString());
            int iTempMax = new Integer(loConfTempMax.getText().toString());
            if (iTemp > iTempMax){
                Toast.makeText(ConfigActivity.this, "La temperatura mínima debe ser menor a la temperatura máxima", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if(!loConfTemp.getText().toString().equals("") || !loConfTempMax.getText().toString().equals("")){
            if(!loConfTemp.getText().toString().equals(""))
                intent.putExtra("confTemp",new Integer(loConfTemp.getText().toString()));
            if(!loConfTempMax.getText().toString().equals(""))
                intent.putExtra("confTempMax",new Integer(loConfTempMax.getText().toString()));
        }else{
            Toast.makeText(ConfigActivity.this, "Debe seleccionar una temperatura", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!loConfTiempo.getText().toString().equals("")){
            String sAux = loConfTiempo.getText().toString();
            if(Integer.parseInt(sAux)> 0){
                intent.putExtra("confTiempo",new Integer(loConfTiempo.getText().toString()));
            }else{
                Toast.makeText(ConfigActivity.this, "Debe seleccionar una valor de tiempo válido", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    private int _getTemperaturaActual(){
        Intent bateriaIntent = registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int temperatura = (bateriaIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1))/10;
        return temperatura;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

}
