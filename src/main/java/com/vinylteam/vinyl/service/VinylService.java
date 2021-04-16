package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.Vinyl;

import java.util.List;
import java.util.Optional;

public interface VinylService {

    void addAllUnique(List<Vinyl> uniqueVinyls);

    void addAll(List<Vinyl> vinyls);

    List<Vinyl> getAllUnique();

    List<Vinyl> getManyRandomUnique(int amount);

    List<Vinyl> getManyFilteredUnique(String matcher);

    List<Vinyl> getAll();

    Vinyl getUniqueById(long id);

    Vinyl getById(long id);

    Optional<Vinyl> getByRelease(String vinylsRelease);

}
