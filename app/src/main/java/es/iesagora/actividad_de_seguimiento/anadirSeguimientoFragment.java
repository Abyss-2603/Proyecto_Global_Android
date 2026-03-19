package es.iesagora.actividad_de_seguimiento;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJlb2ZxZXNvbHdjY255Z3p4cnlmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzM4NjAzNzQsImV4cCI6MjA4OTQzNjM3NH0.EXAzznDtiB1_Q50Yno25HA6K96Xt74jU-rnyrYcoLsI";
    private static final String BUCKET_NAME = "recuerdos";

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
                    mostrarAlerta("Error", "Ocurrió un error al buscar películas en la API.");
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
                    mostrarAlerta("Error", "Ocurrió un error al buscar series en la API.");
                }
            });
        }
    }

    private void mostrarDialogoSeleccion(boolean esPelicula) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Resultados encontrados");

        String[] opciones;
        if (esPelicula) {
            if (resultadosPelis.isEmpty()) {
                mostrarAlerta("Sin resultados", "No se han encontrado películas con ese título.");
                return;
            }
            opciones = new String[resultadosPelis.size()];
            for (int i = 0; i < resultadosPelis.size(); i++) opciones[i] = resultadosPelis.get(i).getTitle();
        } else {
            if (resultadosSeries.isEmpty()) {
                mostrarAlerta("Sin resultados", "No se han encontrado series con ese título.");
                return;
            }
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
            mostrarAlerta("Campos incompletos", "Por favor, selecciona un título de la API y una fecha de visualización.");
            return;
        }

        if (uriImagenSeleccionada != null) {
            subirImagenASupabase();
        } else {
            String posterPath = "";
            if (seleccionActual instanceof Peliculas) posterPath = ((Peliculas) seleccionActual).getPosterPath();
            else posterPath = ((Series) seleccionActual).getPosterPath();

            String imagenFinal = (posterPath != null && !posterPath.isEmpty()) ? "https://image.tmdb.org/t/p/w500" + posterPath : "";
            finalizarGuardado(imagenFinal);
        }
    }

    private void subirImagenASupabase() {
        mostrarAlerta("Procesando", "Guardando tu recuerdo... Por favor, espera un momento.");
        try {
            android.net.Uri uri = android.net.Uri.parse(uriImagenSeleccionada);
            java.io.InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();

            okhttp3.RequestBody requestFile = okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/jpeg"), bytes);
            String nombreArchivo = System.currentTimeMillis() + ".jpg";
            okhttp3.MultipartBody.Part body = okhttp3.MultipartBody.Part.createFormData("file", nombreArchivo, requestFile);

            es.iesagora.actividad_de_seguimiento.api_rest.SupabaseStorageApi storageApi =
                    es.iesagora.actividad_de_seguimiento.api_rest.SupabaseClient.getClient().create(es.iesagora.actividad_de_seguimiento.api_rest.SupabaseStorageApi.class);

            Call<Void> call = storageApi.uploadImage("Bearer " + SUPABASE_KEY, BUCKET_NAME, nombreArchivo, body);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        String urlPublica = "https://befqfexsolwccnygzxryf.supabase.co/storage/v1/object/public/" + BUCKET_NAME + "/" + nombreArchivo;
                        finalizarGuardado(urlPublica);
                    } else {
                        mostrarAlerta("Error en servidor", "No se pudo subir la imagen al almacenamiento remoto (Error: " + response.code() + ").");
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    mostrarAlerta("Fallo de conexión", "No se ha podido subir la imagen. Comprueba tu conexión a internet.");
                }
            });
        } catch (Exception e) {
            mostrarAlerta("Error local", "Hubo un problema al procesar la imagen seleccionada.");
        }
    }

    private void finalizarGuardado(String urlImagen) {
        int idApi = 0; String titulo = ""; String tipo = binding.rbPelicula.isChecked() ? "PELICULA" : "SERIE";
        String generos = ""; String desc = "";

        if (seleccionActual instanceof Peliculas) {
            Peliculas p = (Peliculas) seleccionActual;
            idApi = p.getId(); titulo = p.getTitle(); desc = p.getOverview();
            generos = Generos.getGeneros(p.getGenreIds());
        } else {
            Series s = (Series) seleccionActual;
            idApi = s.getId(); titulo = s.getTitle(); desc = s.getOverview();
            generos = Generos.getGeneros(s.getGenreIds());
        }

        SeguimientoEntidad nuevo = new SeguimientoEntidad(
                idApi, titulo, tipo, binding.etFecha.getText().toString(),
                binding.ratingBar.getRating(), urlImagen, desc, generos
        );

        viewModel.insertar(nuevo);

        new AlertDialog.Builder(requireContext())
                .setTitle("¡Hecho!")
                .setMessage("El seguimiento se ha guardado correctamente.")
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    NavHostFragment.findNavController(this).popBackStack();
                })
                .setCancelable(false)
                .show();
    }
    private void mostrarAlerta(String titulo, String mensaje) {
        new AlertDialog.Builder(requireContext())
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                .show();
    }
}