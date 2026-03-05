package es.iesagora.actividad_de_seguimiento.repository;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesRepository {

    private static final String PREFS_NAME = "mis_preferencias";
    private final SharedPreferences sharedPreferences;

    public PreferencesRepository(Context context) {
        sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public int getIdioma() {
        return sharedPreferences.getInt("idioma_pref", 0);
    }

    public void setIdioma(int idioma) {
        sharedPreferences.edit().putInt("idioma_pref", idioma).apply();
    }

    public boolean isWifiOnly() {
        return sharedPreferences.getBoolean("wifi_only", false);
    }

    public void setWifiOnly(boolean wifi) {
        sharedPreferences.edit().putBoolean("wifi_only", wifi).apply();
    }

    public int getTema() {
        return sharedPreferences.getInt("tema_app", 0);
    }

    public void setTema(int tema) {
        sharedPreferences.edit().putInt("tema_app", tema).apply();
    }

    public String getLanguageCode() {
        int idioma = getIdioma();
        switch(idioma) {
            case 1: return "en-US";
            case 2: return "fr-FR";
            case 3: return "de-DE";
            default: return "es-ES";
        }
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}