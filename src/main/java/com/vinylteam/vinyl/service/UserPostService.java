package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.UserPost;

public interface UserPostService {
    void processSave(UserPost post);

    boolean save(UserPost post);
}