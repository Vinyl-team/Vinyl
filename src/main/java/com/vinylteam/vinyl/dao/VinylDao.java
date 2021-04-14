package com.vinylteam.vinyl.dao;

import com.vinylteam.vinyl.entity.Vinyl;

import java.util.List;
import java.util.Optional;

public interface VinylDao {

    void addAllUnique(List<Vinyl> uniqueVinyls);

    void addAll(List<Vinyl> vinyls);

    List<Vinyl> getAllUnique();

    List<Vinyl> getAll();

    Vinyl getUniqueById(long id);

    Vinyl getById(long id);

    Optional<Vinyl> getByRelease(String vinylsRelease);

}
