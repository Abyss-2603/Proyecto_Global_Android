package es.iesagora.actividad_de_seguimiento.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PendienteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertar(PendientesEntidad pendiente);

    @Update
    void actualizar(PendientesEntidad pendiente);

    @Delete
    void eliminar(PendientesEntidad pendiente);

    @Query("DELETE FROM tabla_pendientes WHERE idApi = :idApi AND tipo = :tipo AND user_id = :userId")
    void eliminarPorIdApi(int idApi, String tipo, String userId);

    @Query("SELECT * FROM tabla_pendientes WHERE user_id = :userId ORDER BY id DESC")
    LiveData<List<PendientesEntidad>> obtenerTodos(String userId);

    @Query("SELECT * FROM tabla_pendientes WHERE user_id = :userId ORDER BY RANDOM() LIMIT 1")
    PendientesEntidad obtenerAleatorio(String userId);
}
