package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.Vinyl;

import java.util.List;
import java.util.Optional;

public interface VinylService {
    void saveAllUnique(List<Vinyl> uniqueVinyls);

    void saveAll(List<Vinyl> vinyls);

    List<Vinyl> getAllUnique();

    List<Vinyl> getAll();

    Vinyl getUniqueById(long id);

    Vinyl getById(long id);

    Optional<Vinyl> getByRelease(String vinylsRelease);
}
