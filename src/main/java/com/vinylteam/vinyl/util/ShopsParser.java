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

    public List<Vinyl> getAllProducts() throws IOException {
        List<Vinyl> allVinyls = new ArrayList<>();

        for (VinylParser vinylParser : vinylParserList) {
            allVinyls.addAll(vinylParser.getDataProduct());
        }
        return allVinyls;
    }
}
