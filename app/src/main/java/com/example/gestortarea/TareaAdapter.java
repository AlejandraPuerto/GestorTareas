package com.example.gestortarea;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.TareaViewHolder> {

    private List<Tarea> listaTareas;

    public TareaAdapter(List<Tarea> listaTareas){
        this.listaTareas = listaTareas;
    }

    @NonNull
    @Override
    public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tarea, parent, false);
        return new TareaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TareaViewHolder holder, int posicion){
        Tarea tareaActual = listaTareas.get(posicion);

        holder.tvId.setText(String.valueOf(tareaActual.getId()));
        holder.tvTitulo.setText(tareaActual.getTitulo());
        holder.tvDescripcion.setText(tareaActual.getDescripcion());

        if(tareaActual.getEstado() == 1){
            holder.tvEstado.setText("Completada");
        } else {
            holder.tvEstado.setText("Pendiente");
        }
    }

    @Override
    public int getItemCount(){return listaTareas.size();
    }

    public static class TareaViewHolder extends RecyclerView.ViewHolder {

        TextView tvId, tvTitulo, tvDescripcion, tvEstado;

        public TareaViewHolder(@NonNull View itemView){
            super(itemView);

            tvId = itemView.findViewById(R.id.tvId);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvEstado = itemView.findViewById(R.id.tvEstado);
        }
    }
}