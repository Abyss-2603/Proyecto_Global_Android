package es.iesagora.actividad_de_seguimiento;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.iesagora.actividad_de_seguimiento.api_rest.RetrofitClient;
import es.iesagora.actividad_de_seguimiento.api_rest.TMDB_API;
import es.iesagora.actividad_de_seguimiento.data.SeguimientoEntidad;
import es.iesagora.actividad_de_seguimiento.databinding.FragmentAnadirSeguimientoBinding;
import es.iesagora.actividad_de_seguimiento.model.Generos;
import es.iesagora.actividad_de_seguimiento.model.PelisAllResponse;
import es.iesagora.actividad_de_seguimiento.model.Peliculas;
import es.iesagora.actividad_de_seguimiento.model.Series;
import es.iesagora.actividad_de_seguimiento.model.SeriesAllResponse;
import es.iesagora.actividad_de_seguimiento.viewModel.SeguimientoViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class anadirSeguimientoFragment extends Fragment {

    private FragmentAnadirSeguimientoBinding binding;
    private TMDB_API apiService;
    private SeguimientoViewModel viewModel;

    private List<Peliculas> resultadosPelis = new ArrayList<>();
    private List<Series> resultadosSeries = new ArrayList<>();

    private Object seleccionActual = null;

    private String uriImagenSeleccionada = null;

    private ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    uriImagenSeleccionada = uri.toString();
                    binding.ivRecuerdo.setImageURI(uri);
                    binding.ivRecuerdo.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            }
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAnadirSeguimientoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = RetrofitClient.getClient().create(TMDB_API.class);
        viewModel = new ViewModelProvider(this).get(SeguimientoViewModel.class);

        binding.etFecha.setOnClickListener(v -> mostrarCalendario());
        binding.btnSubirImagen.setOnClickListener(v -> galleryLauncher.launch("image/*"));
        binding.btnBuscarApi.setOnClickListener(v -> buscarEnApi());
        binding.btnGuardarSeguimiento.setOnClickListener(v -> guardarEnBD());

        binding.cardResultado.setVisibility(View.GONE);

        if (getArguments() != null){
            String tituloPre = getArguments().getString("tituloPreCargado");
            String tipoPre = getArguments().getString("tipoPreCargado");

            if (tipoPre != null) {
                if (tipoPre.equalsIgnoreCase("PELICULA") || tipoPre.equalsIgnoreCase("MOVIE")) {
                    binding.rbPelicula.setChecked(true);
                } else if (tipoPre.equalsIgnoreCase("SERIE") || tipoPre.equalsIgnoreCase("TV")) {
                    binding.rbSerie.setChecked(true);
                }
            }

            if (tituloPre != null && !tituloPre.isEmpty()) {
                binding.etBusqueda.setText(tituloPre);
                buscarEnApi();
            }
        }
    }

    private void mostrarCalendario() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (view, year, month, day) -> {
            String fecha = day + "/" + (month + 1) + "/" + year;
            binding.etFecha.setText(fecha);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void buscarEnApi() {
        String query = binding.etBusqueda.getText().toString();
        if (query.isEmpty()) return;

        if (binding.rbPelicula.isChecked()) {
            apiService.searchMovies(query).enqueue(new Callback<PelisAllResponse>() {
                @Override
                public void onResponse(Call<PelisAllResponse> call, Response<PelisAllResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        resultadosPelis = response.body().getResults();
                        mostrarDialogoSeleccion(true);
                    }
                }
                @Override public void onFailure(Call<PelisAllResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Error buscando películas", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            apiService.searchSeries(query).enqueue(new Callback<SeriesAllResponse>() {
                @Override
                public void onResponse(Call<SeriesAllResponse> call, Response<SeriesAllResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        resultadosSeries = response.body().getResults();
                        mostrarDialogoSeleccion(false);
                    }
                }
                @Override public void onFailure(Call<SeriesAllResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Error buscando series", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void mostrarDialogoSeleccion(boolean esPelicula) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Resultados encontrados");

        String[] opciones;
        if (esPelicula) {
            if (resultadosPelis.isEmpty()) { Toast.makeText(getContext(), "No hay resultados", Toast.LENGTH_SHORT).show(); return; }
            opciones = new String[resultadosPelis.size()];
            for (int i = 0; i < resultadosPelis.size(); i++) opciones[i] = resultadosPelis.get(i).getTitle();
        } else {
            if (resultadosSeries.isEmpty()) { Toast.makeText(getContext(), "No hay resultados", Toast.LENGTH_SHORT).show(); return; }
            opciones = new String[resultadosSeries.size()];
            for (int i = 0; i < resultadosSeries.size(); i++) opciones[i] = resultadosSeries.get(i).getTitle();
        }

        builder.setItems(opciones, (dialog, which) -> {
            if (esPelicula) seleccionActual = resultadosPelis.get(which);
            else seleccionActual = resultadosSeries.get(which);

            actualizarTarjetaVisual();
        });
        builder.show();
    }

    private void actualizarTarjetaVisual() {
        if (seleccionActual == null) return;

        binding.cardResultado.setVisibility(View.VISIBLE);

        String titulo = "";
        String info = "";
        String posterPath = "";

        if (seleccionActual instanceof Peliculas) {
            Peliculas p = (Peliculas) seleccionActual;
            titulo = p.getTitle();
            info = "Película";
            posterPath = p.getPosterPath();
        } else if (seleccionActual instanceof Series) {
            Series s = (Series) seleccionActual;
            titulo = s.getTitle();
            info = "Serie";
            posterPath = s.getPosterPath();
        }

        binding.tvResultadoTitulo.setText(titulo);
        binding.tvResultadoInfo.setText(info);

        Glide.with(this)
                .load("https://image.tmdb.org/t/p/w200" + posterPath)
                .into(binding.ivResultadoPoster);
    }

    private void guardarEnBD() {
        if (seleccionActual == null || binding.etFecha.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Selecciona título y fecha", Toast.LENGTH_SHORT).show();
            return;
        }

        int idApi = 0;
        String titulo = "";
        String tipo = binding.rbPelicula.isChecked() ? "PELICULA" : "SERIE";
        String generos = "";
        String desc = "";
        String posterPath = "";

        if (seleccionActual instanceof Peliculas) {
            Peliculas p = (Peliculas) seleccionActual;
            idApi = p.getId();
            titulo = p.getTitle();
            desc = p.getOverview();
            generos = Generos.getGeneros(p.getGenreIds());
            posterPath = p.getPosterPath();
        } else {
            Series s = (Series) seleccionActual;
            idApi = s.getId();
            titulo = s.getTitle();
            desc = s.getOverview();
            generos = Generos.getGeneros(s.getGenreIds());
            posterPath = s.getPosterPath();
        }

        String imagenFinal;
        if (uriImagenSeleccionada != null) {
            imagenFinal = uriImagenSeleccionada;
        } else {
            if (posterPath != null && !posterPath.isEmpty()) {
                imagenFinal = "https://image.tmdb.org/t/p/w500" + posterPath;
            } else {
                imagenFinal = "";
            }
        }

        SeguimientoEntidad nuevo = new SeguimientoEntidad(
                idApi, titulo, tipo,
                binding.etFecha.getText().toString(),
                binding.ratingBar.getRating(),
                imagenFinal,
                desc, generos
        );

        viewModel.insertar(nuevo);
        Toast.makeText(getContext(), "Seguimiento guardado", Toast.LENGTH_SHORT).show();
        NavHostFragment.findNavController(this).popBackStack();
    }
}