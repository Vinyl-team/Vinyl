package com.vinylteam.vinyl.dao;

import com.vinylteam.vinyl.entity.Vinyl;

import java.util.List;

public interface VinylDao {
    void saveUniqueVinyls(List<Vinyl> uniqueVinyls);

    void saveVinyls(List<Vinyl> vinyls);

    List<Vinyl> getUniqueVinyls();

    List<Vinyl> getVinyls();

    Vinyl getUniqueVinylById(long id);

    Vinyl getVinylById(long id);
}
