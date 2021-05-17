package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.UserPostDao;
import com.vinylteam.vinyl.entity.UserPost;
import com.vinylteam.vinyl.service.UserPostService;
import com.vinylteam.vinyl.util.MailSender;

public class DefaultUserPostService implements UserPostService {
    private final UserPostDao userPostDao;
    private final MailSender mailSender;

    public DefaultUserPostService(UserPostDao userPostDao, MailSender mailSender) {
        this.userPostDao = userPostDao;
        this.mailSender = mailSender;
    }

    @Override
    public void processSave(UserPost post) {
        userPostDao.save(post);
        mailSender.sendMail(post.getEmail(), post.getTheme(), post.getMessage());
    }

    @Override
    public boolean save(UserPost post) {
        return userPostDao.save(post);
    }
}