package com.vinylteam.vinyl.dao;

import java.util.List;
//TODO: Rename.
public interface UniqueVinylAndOfferDao<T> {

    boolean addAll(List<T> list);

    List<T> findAll();

    T findById(long id);

}
