package es.iesagora.actividad_de_seguimiento.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {PendientesEntidad.class, SeguimientoEntidad.class}, version = 2)
public abstract class PendientesDatabase extends RoomDatabase {

    public abstract PendienteDao pendienteDao();

    public abstract SeguimientoDao seguimientoDao();

    private static PendientesDatabase instance;

    public static PendientesDatabase getInstance(final Context context){
        if (instance == null){
            synchronized (PendientesDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    PendientesDatabase.class,
                                    "pendientes.db"
                            )
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}
