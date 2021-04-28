package com.vinylteam.vinyl.dao;

import com.vinylteam.vinyl.entity.UniqueVinyl;

import java.util.List;

public interface UniqueVinylDao extends UniqueVinylAndOfferDao<UniqueVinyl> {

    List<UniqueVinyl> findManyRandom(int amount);

    List<UniqueVinyl> findManyFiltered(String matcher);

    List<UniqueVinyl> findManyByArtist(String artist);


}
