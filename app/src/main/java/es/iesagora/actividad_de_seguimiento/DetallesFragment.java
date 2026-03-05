package es.iesagora.actividad_de_seguimiento;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

import es.iesagora.actividad_de_seguimiento.adapter.ComentarioAdapter;
import es.iesagora.actividad_de_seguimiento.data.PendientesEntidad;
import es.iesagora.actividad_de_seguimiento.databinding.FragmentDetallesBinding;
import es.iesagora.actividad_de_seguimiento.model.Comentario;
import es.iesagora.actividad_de_seguimiento.model.Generos;
import es.iesagora.actividad_de_seguimiento.model.Peliculas;
import es.iesagora.actividad_de_seguimiento.model.Series;
import es.iesagora.actividad_de_seguimiento.viewModel.AuthViewModel;
import es.iesagora.actividad_de_seguimiento.viewModel.DetallesViewModel;
import es.iesagora.actividad_de_seguimiento.api_rest.Resource;

public class DetallesFragment extends Fragment {
    private FragmentDetallesBinding binding;
    private DetallesViewModel viewModel;
    private AuthViewModel authViewModel;
    private ComentarioAdapter adapter;
    private String peliculaId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetallesBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(DetallesViewModel.class);

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        adapter = new ComentarioAdapter();
        binding.rvComentarios.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
        binding.rvComentarios.setAdapter(adapter);

        if (getArguments() != null) {
            if (getArguments().containsKey("peli")) {
                Peliculas peli = (Peliculas) getArguments().getSerializable("peli");
                configurarPelicula(peli);
                viewModel.fetchTrailer(peli.getId(), true);
            } else if (getArguments().containsKey("serie")) {
                Series serie = (Series) getArguments().getSerializable("serie");
                configurarSerie(serie);
                viewModel.fetchTrailer(serie.getId(), false);
            } else if (getArguments().containsKey("pendiente_db")) {
                PendientesEntidad item = (PendientesEntidad) getArguments().getSerializable("pendiente_db");
                configurarDesdePendiente(item);

                boolean esPeli = "PELICULA".equalsIgnoreCase(item.getTipo());
                viewModel.fetchTrailer(item.getIdAPI(), esPeli);
            }
        }

        binding.btnEnviarComentario.setOnClickListener(v -> {
            String mensaje = binding.etNuevoComentario.getText().toString().trim();
            if (!mensaje.isEmpty()) {
                guardarComentario(mensaje);
            }
        });

        viewModel.getTrailerKey().observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS) {
                binding.btnVerTrailer.setOnClickListener(v -> abrirYoutube(resource.data));
            }
        });

        return binding.getRoot();
    }

    private void guardarComentario(String mensaje) {
        authViewModel.getNombreUsuario().observe(getViewLifecycleOwner(), nombre -> {
            String uid = authViewModel.getCurrentUser().getUid();

            Comentario nuevo = new Comentario(
                    uid,
                    nombre,
                    mensaje,
                    com.google.firebase.firestore.FieldValue.serverTimestamp()
            );

            FirebaseFirestore.getInstance()
                    .collection("multimedia").document(peliculaId)
                    .collection("comments")
                    .add(nuevo)
                    .addOnSuccessListener(ref -> binding.etNuevoComentario.setText(""));
        });
    }

    private void escucharComentarios() {
        FirebaseFirestore.getInstance()
                .collection("multimedia").document(peliculaId)
                .collection("comments")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        List<Comentario> lista = value.toObjects(Comentario.class);
                        adapter.setComentarios(lista);

                        binding.lblComentarios.setText("Comentarios (" + lista.size() + ")");
                        if (lista.isEmpty()) {
                            binding.containerSinComentarios.setVisibility(View.VISIBLE);
                            binding.rvComentarios.setVisibility(View.GONE);
                        } else {
                            binding.containerSinComentarios.setVisibility(View.GONE);
                            binding.rvComentarios.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void configurarDesdePendiente(PendientesEntidad item) {
        this.peliculaId = String.valueOf(item.getIdAPI());

        binding.tvDetalleTitulo.setText(item.getTitulo());
        binding.tvSinopsis.setText(item.getDescripcion());

        binding.tvDetalleSubtituloHeader.setText(item.getInfo());
        binding.tvInfoMetrica.setText(item.getInfo());

        boolean esPeli = "PELICULA".equalsIgnoreCase(item.getTipo());
        binding.tvInfoTipo.setText(esPeli ? "Película" : "Serie de TV");
        binding.lblMetrica.setText(esPeli ? "Duración" : "Temporadas");

        Glide.with(this)
                .load("https://image.tmdb.org/t/p/w780" + item.getPosterPath())
                .into(binding.ivDetallePoster);

        binding.cgGeneros.removeAllViews();
        com.google.android.material.chip.Chip chip = new com.google.android.material.chip.Chip(getContext());
        chip.setText(item.getGeneros());
        binding.cgGeneros.addView(chip);
        escucharComentarios();
    }

    private void configurarPelicula(Peliculas peli) {
        this.peliculaId = String.valueOf(peli.getId());
        binding.tvDetalleTitulo.setText(peli.getTitle());

        binding.tvDetalleTitulo.setText(peli.getTitle());
        binding.tvSinopsis.setText(peli.getOverview());

        String duracion = (peli.getRuntime() > 0) ? peli.getRuntime() + " min" : "2 h 49 min";
        binding.tvDetalleSubtituloHeader.setText(duracion);

        binding.tvInfoTipo.setText("Película");
        binding.lblMetrica.setText("Duración");
        binding.tvInfoMetrica.setText(duracion);

        cargarImagenYGeneros(peli.getPosterPath(), peli.getGenreIds());
        escucharComentarios();
    }

    private void configurarSerie(Series serie) {
        this.peliculaId = String.valueOf(serie.getId());
        binding.tvDetalleTitulo.setText(serie.getTitle());

        binding.tvDetalleTitulo.setText(serie.getTitle());
        binding.tvSinopsis.setText(serie.getOverview());

        int temporadas = (serie.getRuntime() > 0) ? serie.getRuntime() : 4;
        binding.tvDetalleSubtituloHeader.setText(temporadas + " temporadas • Netflix");

        binding.tvInfoTipo.setText("Serie de TV");
        binding.lblMetrica.setText("Temporadas");
        binding.tvInfoMetrica.setText(String.valueOf(temporadas));

        cargarImagenYGeneros(serie.getPosterPath(), serie.getGenreIds());
        escucharComentarios();
    }

    private void cargarImagenYGeneros(String path, List<Integer> ids) {
        Glide.with(this).load("https://image.tmdb.org/t/p/w780" + path).into(binding.ivDetallePoster);

        binding.cgGeneros.removeAllViews();
        List<String> nombresGeneros = Generos.getGenerosList(ids);
        for (String nombre : nombresGeneros) {
            Chip chip = new Chip(getContext());
            chip.setText(nombre);
            chip.setChipBackgroundColorResource(android.R.color.transparent);
            chip.setChipStrokeWidth(2f);
            chip.setChipStrokeColorResource(android.R.color.darker_gray);
            binding.cgGeneros.addView(chip);
        }
    }

    private void abrirYoutube(String key) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + key));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }


}