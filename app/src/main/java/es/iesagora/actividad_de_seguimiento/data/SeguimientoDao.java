package es.iesagora.actividad_de_seguimiento.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface SeguimientoDao {
    @Insert
    void insertar(SeguimientoEntidad seguimiento);

    @Query("SELECT * FROM tabla_seguimiento ORDER BY id DESC")
    LiveData<List<SeguimientoEntidad>> obtenerTodos();

    @Query("SELECT COUNT(*) FROM tabla_seguimiento WHERE idApi = :idApi AND tipo = :tipo")
    int existe(int idApi, String tipo);
}