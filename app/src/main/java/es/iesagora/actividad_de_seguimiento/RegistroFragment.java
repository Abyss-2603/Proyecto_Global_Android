package es.iesagora.actividad_de_seguimiento;

import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.regex.Pattern;

import es.iesagora.actividad_de_seguimiento.viewModel.AuthViewModel;

public class RegistroFragment extends Fragment {

    private AuthViewModel viewModel;
    private NavController navController;

    // Vistas
    private EditText etNombre,etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvGoLogin;
    private ImageView btnBack;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

        viewModel.getAuthState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            progressBar.setVisibility(state.loading ? View.VISIBLE : View.GONE);
            btnRegister.setEnabled(!state.loading);

            if (state.error != null) {
                Toast.makeText(getContext(), state.error, Toast.LENGTH_SHORT).show();
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
                viewModel.register(email, password, nombre);
            }
        });

        tvGoLogin.setOnClickListener(v -> {
            navController.navigate(R.id.action_registroFragment_to_iniciarSesionFragment);
        });

        btnBack.setOnClickListener(v -> {
            navController.popBackStack();
        });
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
}