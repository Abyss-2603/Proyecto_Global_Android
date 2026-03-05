package es.iesagora.actividad_de_seguimiento.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import es.iesagora.actividad_de_seguimiento.data.PendientesDatabase;
import es.iesagora.actividad_de_seguimiento.data.SeguimientoDao;
import es.iesagora.actividad_de_seguimiento.data.SeguimientoEntidad;

public class SeguimientoRepository {
    private SeguimientoDao seguimientoDao;
    private Executor executor;

    public SeguimientoRepository(Application application) {
        seguimientoDao = PendientesDatabase.getInstance(application).seguimientoDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public void insertar(SeguimientoEntidad seguimiento) {
        executor.execute(() -> {
            int cantidad = seguimientoDao.existe(seguimiento.getIdApi(), seguimiento.getTipo());

            if (cantidad == 0) {
                seguimientoDao.insertar(seguimiento);
            }
        });
    }

    public LiveData<List<SeguimientoEntidad>> obtenerTodos() {
        return seguimientoDao.obtenerTodos();
    }
}