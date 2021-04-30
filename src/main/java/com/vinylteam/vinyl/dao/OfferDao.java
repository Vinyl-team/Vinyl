package com.vinylteam.vinyl.dao;

import com.vinylteam.vinyl.entity.Offer;

import java.util.List;

public interface OfferDao {

    List<Offer> findManyByUniqueVinylId(long uniqueVinylId);

    //int[] updateUniqueVinylsAddAll(List<UniqueVinyl> uniqueVinyls, List)

}
