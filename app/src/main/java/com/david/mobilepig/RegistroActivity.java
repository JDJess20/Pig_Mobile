package com.david.mobilepig;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Button btnRegresarDelRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicializar Firebase Auth y Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Vincular vistas
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        findViewById(R.id.btnRegister).setOnClickListener(v -> registrarUsuario());


        btnRegresarDelRegistro = findViewById(R.id.btnRegresaRegistro);
        btnRegresarDelRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registrarUsuario() {
        // Obtener valores de los campos
        String nombre = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validaciones
        if (nombre.isEmpty()) {
            etName.setError("Nombre requerido");
            etName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Correo requerido");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Correo no válido");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Contraseña requerida");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("La contraseña debe tener al menos 6 caracteres");
            etPassword.requestFocus();
            return;
        }

        // Crear usuario en Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registro exitoso, guardar datos adicionales en Firestore
                        FirebaseUser user = mAuth.getCurrentUser();

                        // Crear mapa con datos del usuario
                        Map<String, Object> usuario = new HashMap<>();
                        usuario.put("nombre", nombre);
                        usuario.put("email", email);
                        usuario.put("fechaRegistro", System.currentTimeMillis());

                        // Guardar en Firestore
                        db.collection("usuarios").document(user.getUid())
                                .set(usuario)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(RegistroActivity.this, "Registro exitoso!", Toast.LENGTH_SHORT).show();
                                    finish(); // Cierra la actividad y vuelve al login
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(RegistroActivity.this, "Error al guardar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // Si falla el registro
                        Toast.makeText(RegistroActivity.this, "Error al registrarse: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}