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
import es.iesagora.actividad_de_seguimiento.model.Cast;

public class ActorAdapter extends RecyclerView.Adapter<ActorAdapter.ActorViewHolder> {

    private List<Cast> listaActores = new ArrayList<>();
    private final OnActorClickListener listener;

    public interface OnActorClickListener {
        void onActorClick(Cast actor);
    }

    public ActorAdapter(OnActorClickListener listener) {
        this.listener = listener;
    }

    public void setActores(List<Cast> actores) {
        this.listaActores = actores;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ActorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_actor, parent, false);
        return new ActorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActorViewHolder holder, int position) {
        Cast actor = listaActores.get(position);
        holder.tvNombre.setText(actor.getName());
        holder.tvPersonaje.setText(actor.getCharacter());

        Glide.with(holder.itemView.getContext())
                .load("https://image.tmdb.org/t/p/w185" + actor.getProfilePath())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.ivFoto);

        holder.itemView.setOnClickListener(v -> listener.onActorClick(actor));
    }

    @Override
    public int getItemCount() { return listaActores.size(); }

    static class ActorViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoto;
        TextView tvNombre, tvPersonaje;
        public ActorViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoto = itemView.findViewById(R.id.ivActorFoto);
            tvNombre = itemView.findViewById(R.id.tvActorNombre);
            tvPersonaje = itemView.findViewById(R.id.tvActorPersonaje);
        }
    }
}