package com.vinylteam.vinyl.util;

import com.vinylteam.vinyl.entity.RawOffer;

import java.io.IOException;
import java.util.List;

public interface VinylParser {

    List<RawOffer> getRawVinylDataList() throws IOException;

}
