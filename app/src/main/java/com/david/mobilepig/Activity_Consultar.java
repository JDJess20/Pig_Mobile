package com.david.mobilepig;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity_Consultar extends AppCompatActivity {

    private Spinner spinnerCerdas;
    private TextView tvAlias, tvFechaRegistro, tvFechaParto;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private List<String> listaCerdas = new ArrayList<>();
    private Map<String, DocumentSnapshot> cerdasMap = new HashMap<>(); // Para guardar los datos completos
    private Button btnRegresardeConsulta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar);

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Verificar autenticación
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Debes iniciar sesión primero", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Referencias a las vistas
        spinnerCerdas = findViewById(R.id.spinnerCerdas);
        tvAlias = findViewById(R.id.tvAlias);
        tvFechaRegistro = findViewById(R.id.tvFechaRegistro);
        tvFechaParto = findViewById(R.id.tvFechaParto);

        // Configurar el Spinner con un item inicial
        listaCerdas.add("Selecciona una cerda");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, listaCerdas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCerdas.setAdapter(adapter);

        // Cargar las cerdas del usuario
        cargarCerdasUsuario();

        // Configurar el listener del Spinner
        spinnerCerdas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // Ignorar el primer item (hint)
                    String aliasSeleccionado = listaCerdas.get(position);
                    mostrarInformacionCerda(aliasSeleccionado);
                } else {
                    // Limpiar campos cuando se selecciona el hint
                    tvAlias.setText("");
                    tvFechaRegistro.setText("");
                    tvFechaParto.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada
            }
        });

        btnRegresardeConsulta = findViewById(R.id.btnRegresardeConsulta);
        btnRegresardeConsulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Consultar.this, OpcionesActivity.class);
                startActivity(intent);
            }
        });
    }

    private void cargarCerdasUsuario() {
        String userId = mAuth.getCurrentUser().getUid();
        Log.d("Firestore", "Cargando cerdas para usuario: " + userId);

        db.collection("cerdas")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firestore", "Documentos encontrados: " + task.getResult().size());

                        // Limpiar la lista excepto el primer item
                        if (listaCerdas.size() > 1) {
                            listaCerdas.subList(1, listaCerdas.size()).clear();
                        }
                        cerdasMap.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String alias = document.getString("alias");
                            if (alias != null && !alias.isEmpty()) {
                                listaCerdas.add(alias);
                                cerdasMap.put(alias, document); // Guardar documento completo
                                Log.d("Firestore", "Añadida cerda: " + alias);
                            }
                        }

                        // Notificar al adaptador que los datos cambiaron
                        ((ArrayAdapter)spinnerCerdas.getAdapter()).notifyDataSetChanged();

                        if (listaCerdas.size() == 1) {
                            Toast.makeText(this, "No tienes cerdas registradas", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("Firestore", "Error al cargar cerdas", task.getException());
                        Toast.makeText(Activity_Consultar.this,
                                "Error al cargar datos: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void mostrarInformacionCerda(String alias) {
        DocumentSnapshot document = cerdasMap.get(alias);
        if (document != null) {
            String fechaRegistro = document.getString("fechaRegistro");
            String fechaParto = document.getString("fechaParto");

            tvAlias.setText(alias);
            tvFechaRegistro.setText(fechaRegistro != null ? fechaRegistro : "No disponible");
            tvFechaParto.setText(fechaParto != null ? fechaParto : "No calculada");
        }
    }
}