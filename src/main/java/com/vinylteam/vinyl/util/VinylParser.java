package com.vinylteam.vinyl.util;

import com.vinylteam.vinyl.entity.Vinyl;

import java.io.IOException;
import java.util.List;

public interface VinylParser {
    List<Vinyl> getAllVinylsFromShopList() throws IOException;
}
