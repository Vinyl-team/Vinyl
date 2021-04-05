package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.Vinyl;

import java.util.List;

public interface VinylService {
    void saveAllUnique(List<Vinyl> uniqueVinyls);

    void saveAll(List<Vinyl> vinyls);

    List<Vinyl> getAllUnique();

    List<Vinyl> getAll();

    Vinyl getUniqueById(long id);

    Vinyl getById(long id);
}
