package com.david.mobilepig;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class OpcionesActivity extends AppCompatActivity {

    private Button btnAgregarCerdita;
    private Button btnConsultarCedita;
    private Button btnIraConsultar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones);

        Button btnLogout = findViewById(R.id.btnCerrarSesion);

        btnLogout.setOnClickListener(v -> {
            // 1. Cerrar sesión en Firebase
            FirebaseAuth.getInstance().signOut();

            // 2. Limpiar SharedPreferences
            SharedPreferences sharedPref = getSharedPreferences("user_session", Context.MODE_PRIVATE);
            sharedPref.edit().clear().apply();

            // 3. Redirigir al Login y limpiar historial
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        btnAgregarCerdita = findViewById(R.id.btnOpcionAgregar);
        btnAgregarCerdita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OpcionesActivity.this, CalculaActivity.class);
                startActivity(intent);
            }
        });


        btnConsultarCedita = findViewById(R.id.btnConsultaCerdita);
        btnConsultarCedita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OpcionesActivity.this, Activity_Consultar.class);
                startActivity(intent);
            }
        });


        btnIraConsultar = findViewById(R.id.button2);
        btnIraConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OpcionesActivity.this, activity_dispensar.class);
                startActivity(intent);
            }
        });


    }
}