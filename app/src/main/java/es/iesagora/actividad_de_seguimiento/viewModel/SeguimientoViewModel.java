package es.iesagora.actividad_de_seguimiento.viewModel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;
import es.iesagora.actividad_de_seguimiento.data.SeguimientoEntidad;
import es.iesagora.actividad_de_seguimiento.repository.SeguimientoRepository;

public class SeguimientoViewModel extends AndroidViewModel {
    private es.iesagora.actividad_de_seguimiento.repository.SeguimientoRepository repository;

    public SeguimientoViewModel(@NonNull Application application) {
        super(application);
        repository = new SeguimientoRepository(application);
    }

    public void insertar(SeguimientoEntidad seguimiento) {
        repository.insertar(seguimiento);
    }

    public LiveData<List<SeguimientoEntidad>> obtenerSeguimientos() {
        return repository.obtenerTodos();
    }
}