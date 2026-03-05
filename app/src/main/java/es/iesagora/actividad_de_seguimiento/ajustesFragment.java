package es.iesagora.actividad_de_seguimiento;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import es.iesagora.actividad_de_seguimiento.databinding.FragmentAjustesBinding;
import es.iesagora.actividad_de_seguimiento.viewModel.AjustesViewModel;
import es.iesagora.actividad_de_seguimiento.viewModel.AuthViewModel;

public class ajustesFragment extends Fragment {

    private FragmentAjustesBinding binding;

    private AuthViewModel authViewModel;
    private AjustesViewModel ajustesViewModel;

    private int temaSeleccionado = 0;

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

        Toast.makeText(getContext(), "Cambios guardados", Toast.LENGTH_SHORT).show();
    }

    private void resetearPreferencias() {
        ajustesViewModel.resetearPreferencias();

        cargarPreferencias();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Toast.makeText(getContext(), "Preferencias reseteadas", Toast.LENGTH_SHORT).show();
    }

    private void cerrarSesion() {
        FirebaseAuth.getInstance().signOut();
        GoogleSignIn.getClient(requireContext(), new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()).signOut();

        Intent intent = new Intent(requireActivity(), inicioActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }


}