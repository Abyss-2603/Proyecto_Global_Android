package es.iesagora.actividad_de_seguimiento.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.iesagora.actividad_de_seguimiento.R;
import es.iesagora.actividad_de_seguimiento.data.SeguimientoEntidad;

public class SeguimientoAdapter extends RecyclerView.Adapter<SeguimientoAdapter.SeguimientoViewHolder> {

    private List<SeguimientoEntidad> lista = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(SeguimientoEntidad seguimiento);
    }

    public SeguimientoAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public SeguimientoAdapter() {
    }

    public void setLista(List<SeguimientoEntidad> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SeguimientoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_seguimiento, parent, false);
        return new SeguimientoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SeguimientoViewHolder holder, int position) {
        SeguimientoEntidad item = lista.get(position);

        holder.tvTitulo.setText(item.getTitulo());
        holder.tvFecha.setText(item.getFecha());

        if (item.getRutaImagen() != null && !item.getRutaImagen().isEmpty()) {
            com.bumptech.glide.Glide.with(holder.itemView.getContext())
                    .load(item.getRutaImagen())
                    .centerCrop()
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_delete)
                    .into(holder.ivFoto);
        } else {
            holder.ivFoto.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class SeguimientoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoto;
        TextView tvTitulo, tvFecha;

        public SeguimientoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoto = itemView.findViewById(R.id.ivSeguimientoFoto);
            tvTitulo = itemView.findViewById(R.id.tvSeguimientoTitulo);
            tvFecha = itemView.findViewById(R.id.tvSeguimientoFecha);
        }
    }
}