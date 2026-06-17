package com.example.gestortarea;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText etId, etTitulo, etDescripcion, etEstado;
    private Button btnRegistrar, btnBuscar, btnEditar, btnBorrar, btnVerTareas;

    private RecyclerView rvTareas;
    private TareaAdapter adaptador;
    private List<Tarea> listaTareas;

    private FirebaseFirestore db;
    private ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etId = findViewById(R.id.etId);
        etTitulo = findViewById(R.id.etTitulo);
        etDescripcion = findViewById(R.id.etDescripcion);
        etEstado = findViewById(R.id.etEstado);

        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnEditar = findViewById(R.id.btnEditar);
        btnBorrar = findViewById(R.id.btnBorrar);
        btnVerTareas = findViewById(R.id.btnVerTodos);

        rvTareas = findViewById(R.id.rvTareas);
        rvTareas.setLayoutManager(new LinearLayoutManager(this));

        listaTareas = new ArrayList<>();
        adaptador = new TareaAdapter(listaTareas);
        rvTareas.setAdapter(adaptador);

        db = FirebaseFirestore.getInstance();

        btnRegistrar.setOnClickListener(v -> registrarTarea());

        btnBuscar.setOnClickListener(v -> buscarTarea());

        btnEditar.setOnClickListener(v -> modificarTarea());

        btnBorrar.setOnClickListener(v -> borrarTarea());

        btnVerTareas.setOnClickListener(v -> cargarListaTareas());

        cargarListaTareas();
    }

    private void registrarTarea() {

        String titulo = etTitulo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String estadoTexto = etEstado.getText().toString().trim();

        if (titulo.isEmpty() || descripcion.isEmpty() || estadoTexto.isEmpty()) {
            Toast.makeText(this,
                    "Completa todos los campos",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int estado;

        if (estadoTexto.equalsIgnoreCase("completada")) {
            estado = 1;
        } else if (estadoTexto.equalsIgnoreCase("pendiente")) {
            estado = 0;
        } else {
            Toast.makeText(this,
                    "Escribe Pendiente o Completada",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegistrar.setEnabled(false);

        db.collection("tareas")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    int siguienteNumero = queryDocumentSnapshots.size() + 1;

                    Map<String, Object> tarea = new HashMap<>();
                    tarea.put("numero", siguienteNumero);
                    tarea.put("titulo", titulo);
                    tarea.put("descripcion", descripcion);
                    tarea.put("estado", estado);

                    db.collection("tareas")
                            .document(String.valueOf(siguienteNumero))
                            .set(tarea)
                            .addOnSuccessListener(unused -> {

                                btnRegistrar.setEnabled(true);

                                Toast.makeText(MainActivity.this,
                                        "Tarea guardada",
                                        Toast.LENGTH_SHORT).show();

                                limpiarCampos();
                            })
                            .addOnFailureListener(e -> {

                                btnRegistrar.setEnabled(true);

                                Toast.makeText(MainActivity.this,
                                        "Error: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            });

                })
                .addOnFailureListener(e -> {

                    btnRegistrar.setEnabled(true);

                    Toast.makeText(MainActivity.this,
                            "Error obteniendo consecutivo",
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void buscarTarea() {

        String numeroTexto = etId.getText().toString().trim();

        if (numeroTexto.isEmpty()) {
            Toast.makeText(this,
                    "Ingrese el número de la tarea",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int numero = Integer.parseInt(numeroTexto);

        db.collection("tareas")
                .whereEqualTo("numero", numero)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (!queryDocumentSnapshots.isEmpty()) {

                        DocumentSnapshot doc =
                                queryDocumentSnapshots.getDocuments().get(0);

                        etTitulo.setText(doc.getString("titulo"));
                        etDescripcion.setText(doc.getString("descripcion"));

                        Long estado = doc.getLong("estado");

                        if (estado != null && estado == 1) {
                            etEstado.setText("Completada");
                        } else {
                            etEstado.setText("Pendiente");
                        }

                    } else {

                        Toast.makeText(this,
                                "Tarea no encontrada",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void modificarTarea() {

        String numeroTexto = etId.getText().toString().trim();
        String titulo = etTitulo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String estadoTexto = etEstado.getText().toString().trim();

        if (numeroTexto.isEmpty()) {
            Toast.makeText(this,
                    "Ingrese el número de la tarea",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int estado = estadoTexto.equalsIgnoreCase("completada") ? 1 : 0;

        int numero = Integer.parseInt(numeroTexto);

        db.collection("tareas")
                .whereEqualTo("numero", numero)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (!queryDocumentSnapshots.isEmpty()) {

                        String documentId =
                                queryDocumentSnapshots.getDocuments()
                                        .get(0)
                                        .getId();

                        Map<String, Object> datos = new HashMap<>();
                        datos.put("titulo", titulo);
                        datos.put("descripcion", descripcion);
                        datos.put("estado", estado);

                        db.collection("tareas")
                                .document(documentId)
                                .update(datos)
                                .addOnSuccessListener(unused -> {

                                    Toast.makeText(this,
                                            "Actualizado correctamente",
                                            Toast.LENGTH_SHORT).show();

                                    limpiarCampos();
                                });

                    } else {

                        Toast.makeText(this,
                                "Tarea no encontrada",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void borrarTarea() {

        String numeroTexto = etId.getText().toString().trim();

        if (numeroTexto.isEmpty()) {
            Toast.makeText(this,
                    "Ingrese el número de la tarea",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int numero = Integer.parseInt(numeroTexto);

        db.collection("tareas")
                .whereEqualTo("numero", numero)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (!queryDocumentSnapshots.isEmpty()) {

                        String documentId =
                                queryDocumentSnapshots.getDocuments()
                                        .get(0)
                                        .getId();

                        db.collection("tareas")
                                .document(documentId)
                                .delete()
                                .addOnSuccessListener(unused -> {

                                    Toast.makeText(this,
                                            "Tarea eliminada",
                                            Toast.LENGTH_SHORT).show();

                                    limpiarCampos();
                                });

                    } else {

                        Toast.makeText(this,
                                "Tarea no encontrada",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cargarListaTareas() {

        listenerRegistration = db.collection("tareas")
                .addSnapshotListener((value, error) -> {

                    if (error != null) {

                        Toast.makeText(this,
                                "Error al cargar datos",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    listaTareas.clear();

                    if (value != null) {

                        for (DocumentSnapshot doc : value.getDocuments()) {

                            Tarea tarea = doc.toObject(Tarea.class);

                            if (tarea != null) {

                                tarea.setId(doc.getId());

                                listaTareas.add(tarea);
                            }
                        }
                    }

                    adaptador.notifyDataSetChanged();
                });
    }

    private void limpiarCampos() {
        etId.setText("");
        etTitulo.setText("");
        etDescripcion.setText("");
        etEstado.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}