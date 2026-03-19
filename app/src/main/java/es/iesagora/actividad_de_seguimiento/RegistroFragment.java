package es.iesagora.actividad_de_seguimiento;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.io.InputStream;
import java.util.regex.Pattern;

import es.iesagora.actividad_de_seguimiento.api_rest.SupabaseClient;
import es.iesagora.actividad_de_seguimiento.api_rest.SupabaseStorageApi;
import es.iesagora.actividad_de_seguimiento.viewModel.AuthViewModel;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroFragment extends Fragment {

    private AuthViewModel viewModel;
    private NavController navController;

    private EditText etNombre, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvGoLogin;
    private ImageView btnBack;
    private ProgressBar progressBar;

    private ImageView ivFotoRegistro;
    private String uriImagenPerfil = null;

    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJlb2ZxZXNvbHdjY255Z3p4cnlmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzM4NjAzNzQsImV4cCI6MjA4OTQzNjM3NH0.EXAzznDtiB1_Q50Yno25HA6K96Xt74jU-rnyrYcoLsI";
    private static final String BUCKET_NAME = "recuerdos";

    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    uriImagenPerfil = uri.toString();
                    ivFotoRegistro.setImageURI(uri);
                }
            }
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registro, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        navController = Navigation.findNavController(view);

        etNombre = view.findViewById(R.id.etNombreRegister);
        etEmail = view.findViewById(R.id.etEmailRegister);
        etPassword = view.findViewById(R.id.etPasswordRegister);
        etConfirmPassword = view.findViewById(R.id.etConfirmPasswordRegister);
        btnRegister = view.findViewById(R.id.btnRegister);
        tvGoLogin = view.findViewById(R.id.tvGoLogin);
        btnBack = view.findViewById(R.id.btnBack);
        progressBar = view.findViewById(R.id.progressBarRegister);

        ivFotoRegistro = view.findViewById(R.id.ivFotoRegistro);

        view.findViewById(R.id.cvFotoRegistro).setOnClickListener(v -> galleryLauncher.launch("image/*"));

        viewModel.getAuthState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            progressBar.setVisibility(state.loading ? View.VISIBLE : View.GONE);
            btnRegister.setEnabled(!state.loading);

            if (state.error != null) {
                mostrarAlerta("Error de Registro", state.error);
                state.error = null;
            }

            if (state.user != null) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        btnRegister.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (validarEntradas(nombre, email, password, confirmPassword)) {

                if (uriImagenPerfil != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    btnRegister.setEnabled(false);
                    subirFotoYRegistrar(nombre, email, password);
                } else {
                    String avatarPorDefecto = "https://beofqesolwccnygzxryf.supabase.co/storage/v1/object/public/recuerdos/avatar_defecto.png.png";
                    viewModel.register(email, password, nombre, avatarPorDefecto);                }
            }
        });

        tvGoLogin.setOnClickListener(v -> navController.navigate(R.id.action_registroFragment_to_iniciarSesionFragment));
        btnBack.setOnClickListener(v -> navController.popBackStack());
    }

    private void subirFotoYRegistrar(String nombre, String email, String password) {
        try {
            Uri uri = Uri.parse(uriImagenPerfil);
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
                                viewModel.register(email, password, nombre, urlPublica);
                            } else {
                                mostrarAlerta("Error", "No se pudo subir la foto de perfil al servidor.");
                                restaurarUI();
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            mostrarAlerta("Fallo de conexión", "Comprueba tu conexión a internet para subir la foto.");
                            restaurarUI();
                        }
                    });
        } catch (Exception e) {
            mostrarAlerta("Error", "Hubo un problema al procesar la imagen seleccionada.");
            restaurarUI();
        }
    }

    private void restaurarUI() {
        progressBar.setVisibility(View.GONE);
        btnRegister.setEnabled(true);
    }

    private boolean validarEntradas(String nombre, String email, String password, String confirmPassword) {
        if (nombre.isEmpty()) {
            etNombre.setError("El nombre es obligatorio.");
            return false;
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("El formato del correo no es válido.");
            return false;
        }
        Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z]).{8,}$");
        if (!passwordPattern.matcher(password).matches()) {
            etPassword.setError("Mínimo 8 caracteres, al menos una letra y un número.");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Las contraseñas no coinciden.");
            return false;
        }
        return true;
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        if (getContext() != null) {
            new AlertDialog.Builder(requireContext())
                    .setTitle(titulo)
                    .setMessage(mensaje)
                    .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                    .show();
        }
    }
}