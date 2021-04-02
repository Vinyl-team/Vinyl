package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.Vinyl;

import java.util.List;

public interface VinylService {
    void saveUniqueVinyls(List<Vinyl> uniqueVinyls);

    void saveVinyls(List<Vinyl> vinyls);

    List<Vinyl> getUniqueVinyls();

    List<Vinyl> getVinyls();

    Vinyl getUniqueVinylById(long id);

    Vinyl getVinylById(long id);
}
