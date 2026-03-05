package es.iesagora.actividad_de_seguimiento;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import es.iesagora.actividad_de_seguimiento.data.SeguimientoEntidad;
import es.iesagora.actividad_de_seguimiento.databinding.FragmentDetalleSeguimientoBinding;

public class DetalleSeguimientoFragment extends Fragment {

    private FragmentDetalleSeguimientoBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetalleSeguimientoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            SeguimientoEntidad item = (SeguimientoEntidad) getArguments().getSerializable("seguimiento");

            if (item != null) {
                binding.tvDetalleTitulo.setText(item.getTitulo());
                binding.tvDetalleSubtitulo.setText(item.getGeneros());
                binding.tvDetalleFecha.setText(item.getFecha());

                binding.rbDetalle.setRating(item.getPuntuacion());
                binding.rbDetalle.setIsIndicator(true);

                String rutaImagen = item.getRutaImagen();

                if (rutaImagen != null && !rutaImagen.isEmpty()) {
                    Glide.with(this)
                            .load(rutaImagen)
                            .centerCrop()
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .into(binding.ivDetalleHeader);
                }

                boolean esFotoUsuario = rutaImagen != null && rutaImagen.startsWith("content://");

                if (esFotoUsuario) {
                    Glide.with(this)
                            .load(rutaImagen)
                            .into(binding.ivDetalleRecuerdo);
                } else {
                    binding.ivDetalleRecuerdo.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            }
        }
    }
}