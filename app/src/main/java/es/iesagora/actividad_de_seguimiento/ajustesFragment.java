package es.iesagora.actividad_de_seguimiento;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.InputStream;

import es.iesagora.actividad_de_seguimiento.api_rest.SupabaseClient;
import es.iesagora.actividad_de_seguimiento.api_rest.SupabaseStorageApi;
import es.iesagora.actividad_de_seguimiento.databinding.FragmentAjustesBinding;
import es.iesagora.actividad_de_seguimiento.viewModel.AjustesViewModel;
import es.iesagora.actividad_de_seguimiento.viewModel.AuthViewModel;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ajustesFragment extends Fragment {

    private FragmentAjustesBinding binding;

    private AuthViewModel authViewModel;
    private AjustesViewModel ajustesViewModel;

    private int temaSeleccionado = 0;

    private String uriNuevaImagen = null;
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJlb2ZxZXNvbHdjY255Z3p4cnlmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzM4NjAzNzQsImV4cCI6MjA4OTQzNjM3NH0.EXAzznDtiB1_Q50Yno25HA6K96Xt74jU-rnyrYcoLsI";
    private static final String BUCKET_NAME = "recuerdos";

    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    uriNuevaImagen = uri.toString();
                    binding.ivFotoPerfilAjustes.setPadding(0, 0, 0, 0);
                    Glide.with(this).load(uri).into(binding.ivFotoPerfilAjustes);

                    subirNuevaFotoASupabase();
                }
            }
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAjustesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        ajustesViewModel = new ViewModelProvider(this).get(AjustesViewModel.class);

        String[] idiomas = {"Español (España)", "English (US)", "Français", "Deutsch"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, idiomas);
        binding.spinnerIdioma.setAdapter(adapter);

        cargarPreferencias();

        authViewModel.getNombreUsuario().observe(getViewLifecycleOwner(), nombre -> {
            binding.etNombreUsuario.setText(nombre);
        });
        authViewModel.cargarNombreUsuario();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            binding.tvEmailUsuarioAjustes.setText(user.getEmail());

            if (user.getPhotoUrl() != null) {
                binding.ivFotoPerfilAjustes.setPadding(0, 0, 0, 0);
                Glide.with(this).load(user.getPhotoUrl()).into(binding.ivFotoPerfilAjustes);
            } else {
                String avatarPorDefecto = "https://beofqesolwccnygzxryf.supabase.co/storage/v1/object/public/recuerdos/avatar_defecto.png.png";
                binding.ivFotoPerfilAjustes.setPadding(0, 0, 0, 0);
                Glide.with(this).load(avatarPorDefecto).into(binding.ivFotoPerfilAjustes);
            }
        }

        binding.cvFotoAjustes.setOnClickListener(v -> galleryLauncher.launch("image/*"));

        binding.btnTemaClaro.setOnClickListener(v -> actualizarBotonesTema(0));
        binding.btnTemaOscuro.setOnClickListener(v -> actualizarBotonesTema(1));
        binding.btnGuardar.setOnClickListener(v -> guardarPreferencias());
        binding.btnResetear.setOnClickListener(v -> resetearPreferencias());
        binding.btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
    }

    private void cargarPreferencias() {
        binding.spinnerIdioma.setSelection(ajustesViewModel.getIdioma());
        binding.switchWifi.setChecked(ajustesViewModel.isWifiOnly());

        temaSeleccionado = ajustesViewModel.getTema();
        actualizarBotonesTema(temaSeleccionado);
    }

    private void actualizarBotonesTema(int modo) {
        temaSeleccionado = modo;

        int colorBorde = android.graphics.Color.BLACK;
        int colorSinBorde = android.graphics.Color.TRANSPARENT;

        if (modo == 0) {
            binding.btnTemaClaro.setStrokeColor(android.content.res.ColorStateList.valueOf(colorBorde));
            binding.btnTemaClaro.setStrokeWidth(8);
            binding.btnTemaOscuro.setStrokeColor(android.content.res.ColorStateList.valueOf(colorSinBorde));
            binding.btnTemaOscuro.setStrokeWidth(0);
        } else {
            binding.btnTemaClaro.setStrokeColor(android.content.res.ColorStateList.valueOf(colorSinBorde));
            binding.btnTemaClaro.setStrokeWidth(0);
            binding.btnTemaOscuro.setStrokeColor(android.content.res.ColorStateList.valueOf(colorBorde));
            binding.btnTemaOscuro.setStrokeWidth(8);
        }
    }

    private void guardarPreferencias() {
        String nuevoNombre = binding.etNombreUsuario.getText().toString().trim();
        if (!nuevoNombre.isEmpty()) {
            authViewModel.actualizarNombre(nuevoNombre);
        }

        ajustesViewModel.setIdioma(binding.spinnerIdioma.getSelectedItemPosition());
        ajustesViewModel.setWifiOnly(binding.switchWifi.isChecked());
        ajustesViewModel.setTema(temaSeleccionado);

        if (temaSeleccionado == 0) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Información")
                .setMessage("Los cambios se han guardado correctamente.")
                .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void resetearPreferencias() {
        ajustesViewModel.resetearPreferencias();

        cargarPreferencias();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        mostrarAlerta("Éxito", "Las preferencias han sido reseteadas.");
    }

    private void cerrarSesion() {
        FirebaseAuth.getInstance().signOut();
        GoogleSignIn.getClient(requireContext(), new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()).signOut();

        es.iesagora.actividad_de_seguimiento.viewModel.ExplorarViewModel.resetearBienvenida();

        Intent intent = new Intent(requireActivity(), inicioActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void subirNuevaFotoASupabase() {
        mostrarAlerta("Procesando", "Estamos actualizando tu foto de perfil...");

        try {
            Uri uri = Uri.parse(uriNuevaImagen);
            InputStream is = getContext().getContentResolver().openInputStream(uri);
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            is.close();

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), bytes);
            String nombreArchivo = "perfil_" + System.currentTimeMillis() + ".jpg";
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", nombreArchivo, requestFile);

            SupabaseStorageApi storageApi = SupabaseClient.getClient().create(SupabaseStorageApi.class);
            storageApi.uploadImage("Bearer " + SUPABASE_KEY, BUCKET_NAME, nombreArchivo, body)
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                String urlPublica = "https://beofqesolwccnygzxryf.supabase.co/storage/v1/object/public/" + BUCKET_NAME + "/" + nombreArchivo;
                                actualizarPerfilFirebase(urlPublica);
                            } else {
                                mostrarAlerta("Error", "No se pudo subir la foto al servidor.");
                            }
                        }
                        @Override public void onFailure(Call<Void> call, Throwable t) {
                            mostrarAlerta("Fallo de conexión", "Comprueba tu conexión a internet.");
                        }
                    });
        } catch (Exception e) {
            mostrarAlerta("Error", "Ocurrió un problema al procesar la imagen seleccionada.");
        }
    }

    private void actualizarPerfilFirebase(String urlPublica) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(urlPublica))
                    .build();

            user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseFirestore.getInstance().collection("users").document(user.getUid())
                            .update("fotoUrl", urlPublica);

                    mostrarAlerta("Éxito", "¡Tu foto de perfil se ha actualizado correctamente!");                }
            });
        }
    }
    private void mostrarAlerta(String titulo, String mensaje) {
        new AlertDialog.Builder(requireContext())
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                .show();
    }
}