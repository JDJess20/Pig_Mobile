package com.david.mobilepig;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.text.SimpleDateFormat;

public class CalculaActivity extends AppCompatActivity {

    private DatePicker datePicker;
    private Button btnCalcular;
    private TextInputEditText inputAlias, inputFechaRegistro, inputFechaParto;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Button btnRegresarOpcion;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calcula);

        // Inicializar Firebase Auth y Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Verificar autenticación
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Debes iniciar sesión primero", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Obtener referencias a las vistas
        datePicker = findViewById(R.id.datePicker);
        btnCalcular = findViewById(R.id.btnCalcular);
        inputAlias = findViewById(R.id.input_Alias);
        inputFechaRegistro = findViewById(R.id.InputFechaRegistro);
        inputFechaParto = findViewById(R.id.inputFechaParto);
        btnRegresarOpcion = findViewById(R.id.btnRegresaOpcion);

        // Configurar DatePicker
        datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        actualizarFechas(year, monthOfYear, dayOfMonth);
                    }
                });

        // Configurar botones
        btnCalcular.setOnClickListener(v -> guardarEnFirestore());
        btnRegresarOpcion.setOnClickListener(v -> {
            startActivity(new Intent(CalculaActivity.this, OpcionesActivity.class));
            finish();
        });
    }

    private void actualizarFechas(int year, int month, int day) {
        Calendar calendarioRegistro = Calendar.getInstance();
        calendarioRegistro.set(year, month, day);
        SimpleDateFormat formato = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.getDefault());
        String fechaRegistroStr = formato.format(calendarioRegistro.getTime());

        Calendar calendarioParto = (Calendar) calendarioRegistro.clone();
        calendarioParto.add(Calendar.DAY_OF_YEAR, 114);
        String fechaPartoStr = formato.format(calendarioParto.getTime());

        inputFechaRegistro.setText(fechaRegistroStr);
        inputFechaParto.setText(fechaPartoStr);
    }

    private void guardarEnFirestore() {
        String alias = inputAlias.getText().toString().trim();
        String fechaRegistro = inputFechaRegistro.getText().toString().trim();
        String fechaParto = inputFechaParto.getText().toString().trim();

        if (alias.isEmpty()) {
            inputAlias.setError("Por favor ingresa un alias para la cerdita");
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Sesión no válida", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> cerda = new HashMap<>();
        cerda.put("alias", alias);
        cerda.put("fechaRegistro", fechaRegistro);
        cerda.put("fechaParto", fechaParto);
        cerda.put("userId", user.getUid());
        cerda.put("fechaCreacion", FieldValue.serverTimestamp());

        db.collection("cerdas")
                .add(cerda)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Cerdita registrada exitosamente", Toast.LENGTH_SHORT).show();
                    limpiarCampos();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al registrar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void limpiarCampos() {
        inputAlias.setText("");
        inputFechaRegistro.setText("");
        inputFechaParto.setText("");
    }
}