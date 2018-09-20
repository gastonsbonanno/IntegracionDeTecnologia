package com.example.gasto.termometroindustrial;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConfigActivity extends AppCompatActivity{

    EditText loConfTemp;
    Button loBotonComenzar;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        loConfTemp = (EditText) findViewById(R.id.lo_confTemp);
        loBotonComenzar = (Button) findViewById(R.id.lo_botonComenzar);
        loBotonComenzar.setOnClickListener(new onClickClass());
    }


    public class onClickClass implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            try{
                intent = new Intent(ConfigActivity.this, MainActivity.class);
                intent.putExtra("confTemp",new Integer(loConfTemp.getText().toString()));
                startActivity(intent);
            }
            catch (Exception e) {
                Toast.makeText(ConfigActivity.this, "Debe seleccionar una temperatura valida", Toast.LENGTH_SHORT).show();
            }
        }

    }


}
