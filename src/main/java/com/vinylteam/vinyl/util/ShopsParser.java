package com.vinylteam.vinyl.util;

import com.vinylteam.vinyl.entity.Vinyl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShopsParser {
    private List<VinylParser> vinylParserList;

    public ShopsParser(List<VinylParser> vinylParserList) {
        this.vinylParserList = vinylParserList;
    }

    public List<Vinyl> getAllVinyls() throws IOException {
        List<Vinyl> allVinyls = new ArrayList<>();

        for (VinylParser vinylParser : vinylParserList) {
            allVinyls.addAll(vinylParser.getAllVinylsFromShopList());
        }
        return allVinyls;
    }
}
