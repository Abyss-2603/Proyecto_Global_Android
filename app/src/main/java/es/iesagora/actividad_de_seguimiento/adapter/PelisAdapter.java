package es.iesagora.actividad_de_seguimiento.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;
import es.iesagora.actividad_de_seguimiento.R;
import es.iesagora.actividad_de_seguimiento.model.Generos;
import es.iesagora.actividad_de_seguimiento.model.Peliculas;

public class PelisAdapter extends RecyclerView.Adapter<PelisAdapter.ViewHolder> {

    private boolean puedeCargarImagenes = true;

    public void setPuedeCargarImagenes(boolean puedeCargar) {
        this.puedeCargarImagenes = puedeCargar;
        notifyDataSetChanged();
    }

    private List<Peliculas> items = new ArrayList<>();
    private final OnItemClickListener listener;
    private final OnToggleClickListener toggleListener;

    public interface OnToggleClickListener {
        void onToggleClick(Peliculas peli, boolean isChecked);
    }

    public interface OnItemClickListener { void onItemClick(Peliculas peli); }

    public PelisAdapter(OnItemClickListener listener, OnToggleClickListener toggleListener) {
        this.listener = listener;
        this.toggleListener = toggleListener;
    }

    public void setList(List<Peliculas> list) {
        this.items = list;
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
        Peliculas peli = items.get(position);

        String imageUrl = "https://image.tmdb.org/t/p/w500" + peli.getBackdropPath();

        int totalMin = peli.getRuntime();
        String duracionStr;
        if (totalMin > 0) {
            int horas = totalMin / 60;
            int min = totalMin % 60;
            duracionStr = horas + " h " + min + " min";
        } else {
            duracionStr = "2 h 49 min";
        }

        String generos = Generos.getGeneros(peli.getGenreIds());

        holder.tvTitulo.setText(peli.getTitle());
        holder.tvSubtitulo.setText(duracionStr + " • " + generos);
        holder.tvDescripcion.setText(peli.getOverview());

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.ivPortada);

        holder.toggleButton.setOnCheckedChangeListener(null);
        holder.toggleButton.setChecked(peli.isSeleccionado());

        holder.toggleButton.setOnClickListener(v -> {
            boolean isChecked = holder.toggleButton.isChecked();
            peli.setSeleccionado(isChecked);
            toggleListener.onToggleClick(peli, isChecked);
        });

        holder.itemView.setOnClickListener(v -> listener.onItemClick(peli));
    }
    @Override
    public int getItemCount() { return items.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        android.widget.ImageView ivPortada;
        android.widget.TextView tvTitulo, tvSubtitulo, tvDescripcion;
        ToggleButton toggleButton;
        ViewHolder(View v) {
            super(v);
            ivPortada = v.findViewById(R.id.ivPortada);
            tvTitulo = v.findViewById(R.id.tvTitulo);
            tvSubtitulo = v.findViewById(R.id.tvSubtitulo);
            tvDescripcion = v.findViewById(R.id.tvDescripcion);
            toggleButton = v.findViewById(R.id.toggleButton);
        }
    }
}