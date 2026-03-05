package es.iesagora.actividad_de_seguimiento.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.firebase.auth.FirebaseUser;

import es.iesagora.actividad_de_seguimiento.repository.AuthRepository;
import es.iesagora.actividad_de_seguimiento.repository.PreferencesRepository;

public class AjustesViewModel extends AndroidViewModel {

    private final PreferencesRepository prefsRepo;

    public AjustesViewModel(@NonNull Application application) {
        super(application);
        prefsRepo = new PreferencesRepository(application);
    }

    public int getIdioma() { return prefsRepo.getIdioma(); }
    public void setIdioma(int idioma) { prefsRepo.setIdioma(idioma); }

    public boolean isWifiOnly() { return prefsRepo.isWifiOnly(); }
    public void setWifiOnly(boolean wifiOnly) { prefsRepo.setWifiOnly(wifiOnly); }

    public int getTema() { return prefsRepo.getTema(); }
    public void setTema(int tema) { prefsRepo.setTema(tema); }

    public void resetearPreferencias() { prefsRepo.clear(); }

}