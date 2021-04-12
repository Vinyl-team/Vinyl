package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.Vinyl;

import java.util.List;

public interface DiscogsService {

    List<Vinyl> getVinylsFromDiscogsWantList(String discogsUserName);

}
