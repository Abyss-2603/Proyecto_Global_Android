package es.iesagora.actividad_de_seguimiento.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;
import es.iesagora.actividad_de_seguimiento.R;
import es.iesagora.actividad_de_seguimiento.model.CombinedCreditsResponse.CombinedCredit;

public class FilmografiaAdapter extends RecyclerView.Adapter<FilmografiaAdapter.FilmografiaViewHolder> {

    private List<CombinedCredit> listaObras = new ArrayList<>();
    private final OnCreditClickListener listener;

    public interface OnCreditClickListener {
        void onCreditClick(CombinedCredit obra);
    }

    public FilmografiaAdapter(OnCreditClickListener listener) {
        this.listener = listener;
    }

    public void setObras(List<CombinedCredit> obras) {
        this.listaObras = obras;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FilmografiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_filmografia, parent, false);
        return new FilmografiaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmografiaViewHolder holder, int position) {
        CombinedCredit obra = listaObras.get(position);

        holder.tvTitulo.setText(obra.getDisplayTitle());

        String urlImagen = "https://image.tmdb.org/t/p/w342" + obra.getPosterPath();
        Glide.with(holder.itemView.getContext())
                .load(urlImagen)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.ivPoster);

        holder.itemView.setOnClickListener(v -> listener.onCreditClick(obra));
    }

    @Override
    public int getItemCount() {
        return listaObras.size();
    }

    static class FilmografiaViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvTitulo;

        public FilmografiaViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivFilmografiaPoster);
            tvTitulo = itemView.findViewById(R.id.tvFilmografiaTitulo);
        }
    }
}