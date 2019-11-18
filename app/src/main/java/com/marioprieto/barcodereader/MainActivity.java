package com.marioprieto.barcodereader;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    EditText txtResultado;
    Button btnEscanear, btnSalir;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtResultado = findViewById(R.id.resoult);
        btnEscanear = findViewById(R.id.scanner);
        btnSalir = findViewById(R.id.exit);

        btnEscanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escanear();
            }
        });

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

    }

    protected void escanear() {
        IntentIntegrator integrador = new IntentIntegrator(this);
        integrador.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);

        integrador.setPrompt("Escanear Código");
        integrador.setCameraId(0);
        integrador.setBeepEnabled(true);
        integrador.setBarcodeImageEnabled(true);
        integrador.initiateScan();
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show();
            } else {
                txtResultado.setText(result.getContents());
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getContents()));
                    startActivity(browserIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "Ninguna aplicacion puede tratar este escaneo"
                            + " por favor, instala una aplicacion que soporte el escaneo.",  Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
