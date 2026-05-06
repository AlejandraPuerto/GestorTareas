package com.example.gestortarea;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText etId, etTitulo, etDescripcion, etEstado;
    private Button btnRegistrar, btnBorrar, btnEditar, btnBuscar, btnVerTareas;

    private RecyclerView rvTareas;
    private TareaAdapter adaptador;
    private List<Tarea> listaTareas;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etId = findViewById(R.id.etId);
        etTitulo = findViewById(R.id.etTitulo);
        etDescripcion = findViewById(R.id.etDescripcion);
        etEstado = findViewById(R.id.etEstado);

        rvTareas = findViewById(R.id.rvTareas);

        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnEditar = findViewById(R.id.btnEditar);
        btnBorrar = findViewById(R.id.btnBorrar);
        btnVerTareas = findViewById(R.id.btnVerTodos);


        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarTarea();
            }
        });

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarTarea();
            }
        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modificarTarea();
            }
        });

        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrarTarea();
            }
        });

        btnVerTareas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { cargarListaTareas(); }
        });

        rvTareas.setLayoutManager(new LinearLayoutManager(this));
    }

    private void registrarTarea(){
        String id = etId.getText().toString();
        String titulo = etTitulo.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        String estado = etEstado.getText().toString();

        if (!id.isEmpty() && !titulo.isEmpty() && !descripcion.isEmpty() && !estado.isEmpty()){

            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "tareas.db", null, 1);
            SQLiteDatabase baseDeDatos = admin.getWritableDatabase();

            ContentValues registro = new ContentValues();
            registro.put("id", id);
            registro.put("titulo", titulo);
            registro.put("descripcion", descripcion);
            registro.put("estado", estado);


            baseDeDatos.insert("tareas", null, registro);

            //Cerrar conexion a la base de datos
            baseDeDatos.close();

            etId.setText("");
            etTitulo.setText("");
            etDescripcion.setText("");
            etEstado.setText("");

            Toast.makeText(this, "Tarea registrada exitosamente", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void buscarTarea(){
        String id = etId.getText().toString();

        if (!id.isEmpty()){
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "tareas.db", null, 1);
            SQLiteDatabase baseDeDatos = admin.getReadableDatabase();

            android.database.Cursor fila = baseDeDatos.rawQuery("SELECT titulo, descripcion, estado FROM tareas WHERE id = " + id, null);

            if (fila.moveToFirst()){
                etTitulo.setText(fila.getString(0));
                etDescripcion.setText(fila.getString(1));
                etEstado.setText(fila.getString(2));
                Toast.makeText(this, "Tarea encontrada", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No existe la tarea con ese id", Toast.LENGTH_SHORT).show();
                etTitulo.setText("");
                etDescripcion.setText("");
                etEstado.setText("");
            }

            baseDeDatos.close();
            fila.close();
        } else {
            Toast.makeText(this, "Debes ingresar el id de la tarea a buscar", Toast.LENGTH_SHORT).show();
        }
    }

    private void borrarTarea(){
        String id = etId.getText().toString();

        if (!id.isEmpty()){
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "tareas.db", null, 1);
            SQLiteDatabase baseDeDatos = admin.getWritableDatabase();

            int eliminado = baseDeDatos.delete("tareas", "id=" + id, null);

            baseDeDatos.close();

            etTitulo.setText("");
            etDescripcion.setText("");
            etEstado.setText("");


            if (eliminado == 1){
                Toast.makeText(this, "Tarea eliminada exitosamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "La tarea no existe", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "Ingrese el id de la tarea a eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    private void modificarTarea(){
        String id = etId.getText().toString();
        String titulo = etTitulo.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        String estado = etEstado.getText().toString();

        if (!id.isEmpty() && !titulo.isEmpty() && !descripcion.isEmpty() && !estado.isEmpty()){
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "tareas.db", null, 1);
            SQLiteDatabase baseDeDatos = admin.getWritableDatabase();

            ContentValues datosNuevos = new ContentValues();
            datosNuevos.put("titulo", titulo);
            datosNuevos.put("descripcion", descripcion);
            datosNuevos.put("estado", estado);

            int actualizado = baseDeDatos.update("tareas", datosNuevos, "id=" + id, null);

            baseDeDatos.close();

            etTitulo.setText("");
            etDescripcion.setText("");
            etEstado.setText("");

            if (actualizado == 1){
                Toast.makeText(this, "Actualizado correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No existe tarea para actualizar", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarListaTareas(){
        listaTareas = new ArrayList<>();

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "tareas.db", null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        android.database.Cursor fila = db.rawQuery("SELECT id, titulo, descripcion, estado FROM tareas", null);

        while (fila.moveToNext()){
            int id = fila.getInt(0);
            String titulo = fila.getString(1);
            String descripcion = fila.getString(2);
            int estado = fila.getInt(3);

            listaTareas.add(new Tarea(id, titulo, descripcion, estado));
        }

        db.close();
        fila.close();

        adaptador = new TareaAdapter(listaTareas);

        rvTareas.setAdapter(adaptador);
    }


}