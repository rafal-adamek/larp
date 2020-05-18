package com.up.larp.leaderboard;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.up.larp.R;

import java.util.List;

public class LeaderboardActivity extends AppCompatActivity implements FirebaseUserRepository.Callback {

    FirebaseUserRepository userRepository = new FirebaseUserRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        userRepository.attachCallback(this);
        userRepository.getUsers();
    }

    @Override
    public void usersFetched(List<User> users) {
        populateUsers(users);
    }

    @Override
    public void userUpdated() {

    }

    @Override
    public void userFetched(User user) {

    }

    @Override
    public void requestFailed(String message) {

    }

    private void populateUsers(List<User> users) {
        LinearLayout container = findViewById(R.id.container);

        container.removeAllViews();

        for (User user : users) {
            View view = LayoutInflater.from(this).inflate(R.layout.element_user, null);
            TextView name = view.findViewById(R.id.name);
            TextView points = view.findViewById(R.id.points);

            name.setText(user.getName());
            points.setText("Points: " + user.getPoints());

            container.addView(view);
        }
    }
}
