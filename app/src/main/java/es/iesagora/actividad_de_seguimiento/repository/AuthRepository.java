package es.iesagora.actividad_de_seguimiento.repository;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.internal.bind.ObjectTypeAdapter;

import java.util.HashMap;
import java.util.Map;

public class AuthRepository {
    private final FirebaseAuth auth;

    public AuthRepository() {
        auth = FirebaseAuth.getInstance();
    }

    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onError(String message);
    }

    public void login(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> callback.onSuccess(auth.getCurrentUser()))
                .addOnFailureListener(e -> callback.onError(mapError(e)));
    }

    public void register(String email, String password, String nombre, String fotoUrl, AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        com.google.firebase.auth.UserProfileChangeRequest.Builder profileUpdates =
                                new com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                        .setDisplayName(nombre);
                        if (fotoUrl != null) {
                            profileUpdates.setPhotoUri(android.net.Uri.parse(fotoUrl));
                        }
                        user.updateProfile(profileUpdates.build());
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("displayName", nombre);
                        userData.put("email", email);
                        if (fotoUrl != null) userData.put("fotoUrl", fotoUrl);
                        userData.put("createdAt", FieldValue.serverTimestamp());

                        FirebaseFirestore.getInstance().collection("users").document(user.getUid())
                                .set(userData)
                                .addOnSuccessListener(aVoid -> callback.onSuccess(user))
                                .addOnFailureListener(e -> callback.onError("Cuenta creada, pero error al guardar perfil: " + e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> callback.onError(mapError(e)));
    }

    public void resetPassword(String email, AuthCallback callback) {
        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(v -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(mapError(e)));
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public void logout() {
        auth.signOut();
    }

    private String mapError(Exception e) {
        if (e == null || e.getMessage() == null) return "Error desconocido.";
        return e.getMessage();
    }

    public void loginWithGoogle(String idToken, AuthCallback callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnSuccessListener(result -> {
                    callback.onSuccess(auth.getCurrentUser());
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public interface NameCallback {
        void onSuccess(String nombre);
        void onError(String message);
    }

    public void getNombreUsuario(NameCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            FirebaseFirestore.getInstance().collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String nombre = documentSnapshot.getString("displayName");
                            callback.onSuccess(nombre);
                        } else {
                            callback.onError("No se encontraron datos del usuario.");
                        }
                    })
                    .addOnFailureListener(e -> callback.onError(e.getMessage()));
        } else {
            callback.onError("No hay ningún usuario conectado.");
        }
    }

    public void actualizarNombreUsuario(String nuevoNombre, AuthCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("displayName", nuevoNombre);

            FirebaseFirestore.getInstance().collection("users").document(user.getUid())
                    .set(data, com.google.firebase.firestore.SetOptions.merge())
                    .addOnSuccessListener(aVoid -> callback.onSuccess(user))
                    .addOnFailureListener(e -> callback.onError(e.getMessage()));
        }
    }

}
