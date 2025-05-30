
package com.david.mobilepig;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPref; // Nuevo: Para persistencia

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Verificar sesión ANTES de mostrar la vista
        sharedPref = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        if (sharedPref.getBoolean("is_logged_in", false)) {
            redirigirACalculadora();
            return; // Si hay sesión, termina el onCreate aquí
        }

        setContentView(R.layout.activity_main);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Vincular vistas
        tilEmail = findViewById(R.id.textInputLayout);
        tilPassword = findViewById(R.id.textInputLayout2);
        etEmail = tilEmail.findViewById(R.id.textCorreo);
        etPassword = tilPassword.findViewById(R.id.textpasswd);
        Button btnLogin = findViewById(R.id.button);
        TextView tvRegistro = findViewById(R.id.textView3);

        // Listeners
        btnLogin.setOnClickListener(v -> loginUser());
        tvRegistro.setOnClickListener(v -> {
            startActivity(new Intent(this, RegistroActivity.class));
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!validarCampos(email, password)) return;

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Guardar sesión en SharedPreferences
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("is_logged_in", true);
                        editor.putString("user_email", email); // Opcional
                        editor.apply();

                        redirigirACalculadora();
                    } else {
                        mostrarError(task.getException());
                    }
                });
    }

    private boolean validarCampos(String email, String password) {
        if (email.isEmpty()) {
            tilEmail.setError("Correo requerido");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Correo no válido");
            return false;
        }
        if (password.isEmpty()) {
            tilPassword.setError("Contraseña requerida");
            return false;
        }
        if (password.length() < 6) {
            tilPassword.setError("Mínimo 6 caracteres");
            return false;
        }
        return true;
    }

    private void redirigirACalculadora() {
        startActivity(new Intent(this, OpcionesActivity.class));
        finish(); // Evita volver atrás con el botón
    }

    private void mostrarError(Exception exception) {
        String error = exception.getMessage();
        if (error.contains("password")) {
            tilPassword.setError("Contraseña incorrecta");
        } else if (error.contains("user")) {
            tilEmail.setError("Usuario no registrado");
        }
        Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
    }
}
