package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.service.ShopService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ShopServlet extends HttpServlet {

    ShopService shopService;

    public ShopServlet(ShopService shopService) {
        this.shopService = shopService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var shopsList = shopService.getAll();
    }
}
