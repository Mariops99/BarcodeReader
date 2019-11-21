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

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    EditText txtResultado;
    Button btnEscanear, btnGenerate;
    String texto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtResultado = findViewById(R.id.get);
        btnEscanear = findViewById(R.id.scanner);
        btnGenerate = findViewById(R.id.generate);


        btnEscanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escanear();
            }
        });

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtResultado.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"El conteido no puede estar vacio", Toast.LENGTH_SHORT).show();
                } else {
                    generateQr();
                }
            }
        });
    }

    protected void generateQr() {
        Bundle bundle = new Bundle();
        String textoapasar = txtResultado.getText().toString();
        bundle.putString("texto", textoapasar);
        Intent intent = new Intent(MainActivity.this, QRGenerated.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void escanear() {
        IntentIntegrator integrador = new IntentIntegrator(this);

        integrador.setPrompt("Escanear Código");
        integrador.setCameraId(0);
        integrador.setBarcodeImageEnabled(true);

        if (IntentIntegrator.QR_CODE.equals(integrador)) {
            integrador.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        } else if (IntentIntegrator.PRODUCT_CODE_TYPES.equals(integrador)) {
            integrador.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES);
        }

        integrador.initiateScan();
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show();
            } else {
                try {
                    if(result.getFormatName().equals("QR_CODE")) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getContents()));
                        startActivity(browserIntent);
                    } else {  //Comprobar BD

                    }
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
