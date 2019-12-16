package com.marioprieto.barcodereader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.MultiFormatWriter;

public class QR_Resoult extends AppCompatActivity {

    Button goback;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr__resoult);

        goback = (Button) findViewById(R.id.goback);
        text = (TextView) findViewById(R.id.text);

        Bundle bundle2 = this.getIntent().getExtras();
        String texto = bundle2.getString("text");
        String textoguardado = texto;
        text.setText(textoguardado);


        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QR_Resoult.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
