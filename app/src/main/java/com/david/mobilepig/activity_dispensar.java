package com.david.mobilepig;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class activity_dispensar extends AppCompatActivity {

    private LottieAnimationView lottieDispensar;
    private Button btnDispensar;
    private Button btnRegresarDeDispensar;

    private final String ESP32_IP = "http://192.168.137.167/dispensar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispensar);

        // Inicializar vistas
        lottieDispensar = findViewById(R.id.lottieDispensar);
        btnDispensar = findViewById(R.id.btnDispensar);

        // NO ocultamos la animación. Solo la dejamos pausada al inicio.
        lottieDispensar.pauseAnimation(); // Detenida, pero visible

        // Configuración del botón
        btnDispensar.setOnClickListener(v -> showConfirmDialog());


        btnRegresarDeDispensar = findViewById(R.id.btnregresadedispensar);
        btnRegresarDeDispensar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_dispensar.this, OpcionesActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("¿Dispensar alimento?");
        builder.setMessage("¿Estás seguro de que quieres dispensar comida?");
        builder.setPositiveButton("Sí", (dialog, which) -> {
            lottieDispensar.playAnimation(); // Comienza la animación al confirmar
            sendDispenseRequest();
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void sendDispenseRequest() {
        // Deshabilitar el botón mientras se procesa la solicitud
        btnDispensar.setEnabled(false);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            boolean success = false;
            try {
                URL url = new URL(ESP32_IP);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                int responseCode = connection.getResponseCode();
                success = (responseCode == 200); // Si la respuesta es 200 OK, fue exitoso

            } catch (Exception e) {
                e.printStackTrace();
            }

            boolean finalSuccess = success;
            handler.post(() -> {
                // Pausar la animación cuando la solicitud haya terminado
                //  lottieDispensar.pauseAnimation();

                // Mostrar el cuadro de diálogo de éxito o error
                if (finalSuccess) {
                    // Mostrar el cuadro de diálogo de éxito
                    new AlertDialog.Builder(activity_dispensar.this)
                            .setTitle("Alimento Despachado")
                            .setMessage("El alimento ha sido despachado con éxito.")
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .show();
                } else {
                    Toast.makeText(activity_dispensar.this, "Error al comunicar con ESP32", Toast.LENGTH_SHORT).show();
                }

                // Habilitar nuevamente el botón
                btnDispensar.setEnabled(true);
            });
        });
    }
}