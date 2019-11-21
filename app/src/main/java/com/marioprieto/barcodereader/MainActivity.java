package com.marioprieto.barcodereader;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.*;

public class MainActivity extends AppCompatActivity {

    EditText txtResultado;
    Button btnEscanear, btnGenerate;
    JSONObject obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtResultado = findViewById(R.id.get);
        btnEscanear = findViewById(R.id.scanner);
        btnGenerate = findViewById(R.id.generate);

        try {
            obj = new JSONObject("./Database.jon");
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
                        boolean isValid = URLUtil.isValidUrl( result.getContents());
                        if (isValid){ //Si es una URL
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getContents()));
                            startActivity(browserIntent);
                        } else { //Ya veré que hago si no es una URL

                        }

                    } else {  //Comprobar BD
                        Boolean flag = null;

                        for(int i = 0 ; i<=obj.length(); i++){
                            if(obj.getString("code").equals(result.getContents())){
                                flag = true; //Encontré un producto con ese código
                            }
                        }

                        if(flag){ //Si tengo ese producto
                            Bundle bundle = new Bundle();
                            bundle.putString("code", result.getContents());
                            bundle.putString("description", obj.getString("description"));
                            bundle.putString("stock", String.valueOf(obj.getInt("stock")));
                            Intent intent = new Intent(MainActivity.this, Reultado.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else { //No tengo ese producto
                            Bundle bundle = new Bundle();
                            bundle.putString("code", result.getContents());
                            Intent intent = new Intent(MainActivity.this, NoResoult.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }

                    }
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "Ninguna aplicacion puede tratar este escaneo"
                            + " por favor, instala una aplicacion que soporte el escaneo.",  Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
