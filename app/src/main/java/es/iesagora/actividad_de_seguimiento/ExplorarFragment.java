package es.iesagora.actividad_de_seguimiento;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayoutMediator;

import es.iesagora.actividad_de_seguimiento.data.PendientesEntidad;
import es.iesagora.actividad_de_seguimiento.databinding.FragmentExplorarBinding;
import es.iesagora.actividad_de_seguimiento.viewModel.ExplorarViewModel;

public class ExplorarFragment extends Fragment {

    private FragmentExplorarBinding binding;
    private ExplorarViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExplorarBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        establecerAdaptadorViewPager();
        vincularTabLayoutConViewPager();
        configurarLogicaBienvenida();
    }

    private void establecerAdaptadorViewPager() {
        binding.viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    default:
                    case 0: return new SeriesFragment();
                    case 1: return new PelisFragment();
                }
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        });
    }

    private void vincularTabLayoutConViewPager() {
        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Series");
                            break;
                        case 1:
                            tab.setText("Películas");
                            break;
                    }
                }).attach();
    }


    private void configurarLogicaBienvenida() {
        viewModel = new ViewModelProvider(this).get(ExplorarViewModel.class);

        viewModel.getAccionDialogo().observe(getViewLifecycleOwner(), accion -> {
            if (accion != null) {
                if (accion == 1) {
                    mostrarDialogoSinNombre();
                    viewModel.resetearDialogo();
                } else if (accion == 2) {
                    PendientesEntidad p = viewModel.getPendienteEncontrado().getValue();
                    if (p != null) {
                        mostrarDialogoConPendiente(p);
                        viewModel.resetearDialogo();
                    }
                }
            }
        });

        viewModel.getPendienteEncontrado().observe(getViewLifecycleOwner(), p -> {
            Integer accion = viewModel.getAccionDialogo().getValue();
            if (accion != null && accion == 2 && p != null) {
                mostrarDialogoConPendiente(p);
                viewModel.resetearDialogo();
            }
        });

        viewModel.comprobarBienvenida();
    }

    private void mostrarDialogoSinNombre() {
        new AlertDialog.Builder(requireContext())
                .setTitle("¡Hola!")
                .setMessage("Para personalizar tu experiencia, puedes configurar tu nombre en los ajustes. ¿Quieres hacerlo ahora?") // [cite: 157]
                .setPositiveButton("Ir a Ajustes", (dialog, which) -> {
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_explorarFragment_to_ajustesFragment);
                })
                .setNegativeButton("Más tarde", null)
                .show();
    }

    private void mostrarDialogoConPendiente(PendientesEntidad p) {

        String nombre = viewModel.getNombreUsuario().getValue();
        String nombreFinal = (nombre != null && !nombre.isEmpty()) ? nombre : "Usuario";
        String mensaje = "Hola, " + nombreFinal + ". ¿Has visto ya tu pendiente " + p.getTitulo() + "?";

        new AlertDialog.Builder(requireContext())
                .setTitle("Bienvenido")
                .setMessage(mensaje)
                .setPositiveButton("Ir a Seguimiento", (dialog, which) -> {
                    Bundle args = new Bundle();
                    args.putString("tituloPreCargado", p.getTitulo());
                    args.putString("tipoPreCargado", p.getTipo());

                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_explorarFragment_to_anadirSeguimientoFragment, args);
                })
                .setNegativeButton("Aún no", null)
                .show();
    }


}