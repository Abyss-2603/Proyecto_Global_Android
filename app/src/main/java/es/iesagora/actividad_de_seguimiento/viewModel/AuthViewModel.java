package es.iesagora.actividad_de_seguimiento.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;

import es.iesagora.actividad_de_seguimiento.model.AuthState;
import es.iesagora.actividad_de_seguimiento.repository.AuthRepository;

public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository repo;

    private final MutableLiveData<AuthState> authState = new MutableLiveData<>();

    private final MutableLiveData<String> nombreUsuario = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        repo = new AuthRepository();
    }

    public MutableLiveData<AuthState> getAuthState() {
        return authState;
    }

    public MutableLiveData<String> getNombreUsuario() {
        return nombreUsuario;
    }

    public FirebaseUser getCurrentUser() {
        return repo.getCurrentUser();
    }

    public void logout() {
        repo.logout();
    }

    public void login(String email, String password) {
        authState.setValue(AuthState.loading());
        repo.login(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                authState.postValue(AuthState.success(user));
            }

            @Override
            public void onError(String message) {
                authState.postValue(AuthState.error(message));
            }
        });
    }


    public void register(String email, String password, String nombre, String fotoUrl) {
        authState.setValue(AuthState.loading());

        repo.register(email, password, nombre, fotoUrl, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                authState.postValue(AuthState.success(user));
            }
            @Override
            public void onError(String message) {
                authState.postValue(AuthState.error(message));
            }
        });
    }

    public void resetPassword(String email) {
        repo.resetPassword(email, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                authState.postValue(AuthState.success(null));
            }

            @Override
            public void onError(String message) {
                authState.postValue(AuthState.error(message));
            }
        });
    }

    public void loginWithGoogle(String idToken) {
        authState.setValue(AuthState.loading());
        repo.loginWithGoogle(idToken, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                authState.postValue(AuthState.success(user));
            }
            @Override
            public void onError(String message) {
                authState.postValue(AuthState.error(message));
            }
        });
    }

    public void cargarNombreUsuario() {
        repo.getNombreUsuario(new AuthRepository.NameCallback() {
            @Override
            public void onSuccess(String nombre) {
                nombreUsuario.postValue(nombre);
            }

            @Override
            public void onError(String message) {
                nombreUsuario.postValue("Usuario");
            }
        });
    }

    public void actualizarNombre(String nuevoNombre) {
        repo.actualizarNombreUsuario(nuevoNombre, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                nombreUsuario.postValue(nuevoNombre);
            }

            @Override
            public void onError(String message) {
            }
        });
    }
}
