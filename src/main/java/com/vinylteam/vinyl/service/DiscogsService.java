package com.vinylteam.vinyl.service;

import java.util.List;

public interface DiscogsService {

    List<String> getVinylsReleasesFromDiscogsWantList(String discogsUserName);

}
