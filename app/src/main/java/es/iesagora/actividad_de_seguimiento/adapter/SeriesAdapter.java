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
import es.iesagora.actividad_de_seguimiento.model.Generos;
import es.iesagora.actividad_de_seguimiento.model.Series;

public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.SeriesViewHolder> {

    private boolean puedeCargarImagenes = true;

    public void setPuedeCargarImagenes(boolean puedeCargar) {
        this.puedeCargarImagenes = puedeCargar;
        notifyDataSetChanged();
    }

    private List<Series> listaSeries = new ArrayList<>();
    private final OnItemClickListener listener;
    private final OnToggleClickListener toggleListener;

    public interface OnToggleClickListener {
        void onToggleClick(Series serie, boolean isChecked);
    }

    public interface OnItemClickListener {
        void onItemClick(Series serie);
    }

    public SeriesAdapter(OnItemClickListener listener, OnToggleClickListener toggleListener) {
        this.listener = listener;
        this.toggleListener = toggleListener;
    }

    public void setLista(List<Series> nuevasSeries) {
        this.listaSeries = nuevasSeries;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SeriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_item, parent, false);
        return new SeriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeriesViewHolder holder, int position) {
        Series serie = listaSeries.get(position);

        String urlImagen = "https://image.tmdb.org/t/p/w500" + serie.getBackdropPath();

        int nTemporadas = serie.getRuntime();
        String tempStr = nTemporadas + (nTemporadas == 1 ? " temporada" : " temporadas");
        if (nTemporadas == 0) tempStr = "1 temporada";

        String generos = Generos.getGeneros(serie.getGenreIds());

        holder.tvTitulo.setText(serie.getTitle());
        holder.tvSubtitulo.setText(tempStr + " • " + generos);
        holder.tvDescripcion.setText(serie.getOverview());

        if (puedeCargarImagenes) {
            Glide.with(holder.itemView.getContext())
                    .load(urlImagen)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.ivPortada);
        } else {
            holder.ivPortada.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.toggleButton.setOnCheckedChangeListener(null);
        holder.toggleButton.setChecked(serie.isSeleccionado());

        holder.toggleButton.setOnClickListener(v -> {
            boolean isChecked = holder.toggleButton.isChecked();
            serie.setSeleccionado(isChecked);
            toggleListener.onToggleClick(serie, isChecked);
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(serie);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaSeries.size();
    }

    static class SeriesViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPortada;
        TextView tvTitulo, tvSubtitulo, tvDescripcion;
        ToggleButton toggleButton;

        public SeriesViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPortada = itemView.findViewById(R.id.ivPortada);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvSubtitulo = itemView.findViewById(R.id.tvSubtitulo);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            toggleButton = itemView.findViewById(R.id.toggleButton);
        }
    }
}