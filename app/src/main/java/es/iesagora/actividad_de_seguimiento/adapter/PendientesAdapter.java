package es.iesagora.actividad_de_seguimiento.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import es.iesagora.actividad_de_seguimiento.R;
import es.iesagora.actividad_de_seguimiento.data.PendientesEntidad;

public class PendientesAdapter extends RecyclerView.Adapter<PendientesAdapter.ViewHolder> {

    private List<PendientesEntidad> lista = new ArrayList<>();

    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEliminarClick(PendientesEntidad pendiente);
        void onVerDetalleClick(PendientesEntidad pendiente);
    }

    public PendientesAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setLista(List<PendientesEntidad> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PendientesEntidad item = lista.get(position);

        holder.tvTitulo.setText(item.getTitulo());
        holder.tvDescripcion.setText(item.getDescripcion());

        if (item.getInfo() != null && item.getGeneros() != null) {
            String subtitulo = item.getInfo() + " • " + item.getGeneros();
            holder.tvSubtitulo.setText(subtitulo);
        }

        String url = "https://image.tmdb.org/t/p/w500" + item.getPosterPath();
        Glide.with(holder.itemView.getContext()).load(url).into(holder.ivPortada);

        holder.toggleButton.setOnCheckedChangeListener(null);
        holder.toggleButton.setChecked(true);

        holder.toggleButton.setOnClickListener(v -> {
            listener.onEliminarClick(item);
        });

        holder.itemView.setOnClickListener(v -> {
            listener.onVerDetalleClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPortada;
        TextView tvTitulo, tvDescripcion, tvSubtitulo;
        ToggleButton toggleButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPortada = itemView.findViewById(R.id.ivPortada);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvSubtitulo = itemView.findViewById(R.id.tvSubtitulo);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            toggleButton = itemView.findViewById(R.id.toggleButton);
        }
    }
}