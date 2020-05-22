package com.up.larp.leaderboard;

import android.util.Log;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseUserRepository implements UserRepository {

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private Callback callback;

    public void attachCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void getUser(String name) {
        Log.d("Testoviron", "GetUser");

        firestore.collection("user")
                .document(name)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot snapshot = task.getResult();
                        Log.d("Testoviron", snapshot.getId() + " => " + snapshot.getData());
                        callback.userFetched(new User(snapshot.getData().get("name").toString(), Integer.parseInt(snapshot.getData().get("points").toString())));
                    } else {
                        Log.w("Testoviron", "Error getting documents.", task.getException());
                    }
                });
    }

    @Override
    public void getUsers() {
        Log.d("Testoviron", "GetUsers");

        firestore.collection("user")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<User> userList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("Testoviron", document.getId() + " => " + document.getData());
                            userList.add(new User(document.getData().get("name").toString(), Integer.parseInt(document.getData().get("points").toString())));
                        }
                        callback.usersFetched(userList);
                    } else {
                        Log.w("Testoviron", "Error getting documents.", task.getException());
                        callback.requestFailed(task.getException().toString());
                    }
                });
    }

    @Override
    public void postUser(User user) {
        Log.d("Testoviron", "PostUser");

        Map<String, Object> userMap = new HashMap<>();

        userMap.put("name", user.getName());
        userMap.put("points", user.getPoints());

        firestore.collection("user")
                .document(user.getName())
                .set(userMap, SetOptions.merge())
                .addOnSuccessListener(documentReference -> {
                    callback.userUpdated();
                    Log.d("Testoviron", documentReference.toString());
                })
                .addOnFailureListener(e -> {
                    callback.requestFailed(e.getMessage());
                    Log.d("Testoviron", e.getMessage());
                })
                .addOnCompleteListener(result -> {
                    Log.d("Testoviron", result.toString());
                });
    }

    public interface Callback {
        void usersFetched(List<User> users);

        void userUpdated();

        void userFetched(User user);

        void requestFailed(String message);
    }
}
