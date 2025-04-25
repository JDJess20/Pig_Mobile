package com.david.mobilepig;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Button;

import java.util.Calendar;
import java.text.SimpleDateFormat;

public class CalculaActivity extends AppCompatActivity {

    DatePicker datePicker;
    Button btnCalcular;
    TextView tvResultado;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calcula);

        datePicker = findViewById(R.id.datePicker);
        btnCalcular = findViewById(R.id.btnCalcular);
        tvResultado = findViewById(R.id.txtResultado);

        btnCalcular.setOnClickListener(v -> {
            int dia = datePicker.getDayOfMonth();
            int mes = datePicker.getMonth(); // ¡Ojo! Enero = 0
            int anio = datePicker.getYear();

            Calendar calendario = Calendar.getInstance();
            calendario.set(anio, mes, dia);
            calendario.add(Calendar.DAY_OF_YEAR, 114); // Suma 114 días

            SimpleDateFormat formato = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy");
            String fechaParto = formato.format(calendario.getTime());

            tvResultado.setText("Fecha probable de parto: " + fechaParto);
        });
    }
}
