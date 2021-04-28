package com.vinylteam.vinyl.dao;

import com.vinylteam.vinyl.entity.Offer;

import java.util.List;

public interface OfferDao extends UniqueVinylAndOfferDao<Offer> {

    List<Offer> findManyByUniqueVinylId(long uniqueVinylId);

}
