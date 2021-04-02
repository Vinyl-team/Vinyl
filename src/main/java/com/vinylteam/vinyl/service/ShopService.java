package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.Vinyl;

import java.io.IOException;
import java.util.List;

public interface ShopService {

    List<Vinyl> getDataProduct() throws IOException;

}
