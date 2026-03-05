package es.iesagora.actividad_de_seguimiento.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.iesagora.actividad_de_seguimiento.R;
import es.iesagora.actividad_de_seguimiento.model.Comentario;

public class ComentarioAdapter extends RecyclerView.Adapter<ComentarioAdapter.ViewHolder> {
    private List<Comentario> mComentarios = new ArrayList<>();

    public void setComentarios(List<Comentario> comentarios) {
        this.mComentarios = comentarios;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comentario, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comentario c = mComentarios.get(position);


        holder.tvNombre.setText(c.getAuthorName());
        holder.tvTexto.setText(c.getText());


        if (c.getAuthorName() != null && !c.getAuthorName().isEmpty()) {
            holder.tvAvatar.setText(c.getAuthorName().substring(0, 1).toUpperCase());
        }

        if (c.getCreatedAt() != null) {
            if (c.getCreatedAt() instanceof com.google.firebase.Timestamp) {
                Date fecha = ((com.google.firebase.Timestamp) c.getCreatedAt()).toDate();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                holder.tvFecha.setText(sdf.format(fecha));
            } else {
                holder.tvFecha.setText("Hace un momento");
            }
        }    }

    @Override
    public int getItemCount() { return mComentarios.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvTexto, tvAvatar, tvFecha;
        ViewHolder(View v) {
            super(v);
            tvNombre = v.findViewById(R.id.tvNombreUsuario);
            tvTexto = v.findViewById(R.id.tvContenidoComentario);
            tvAvatar = v.findViewById(R.id.tvAvatar);
            tvFecha = v.findViewById(R.id.tvFecha);
        }
    }
}