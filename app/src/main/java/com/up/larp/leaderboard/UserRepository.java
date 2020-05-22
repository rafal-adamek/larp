package com.up.larp.leaderboard;

import java.util.List;

public interface UserRepository {
    void getUser(String name);
    void getUsers();
    void postUser(User user);

    interface Response<T> {
        void onSuccess(T data);
        void onFailed();
    }
}
