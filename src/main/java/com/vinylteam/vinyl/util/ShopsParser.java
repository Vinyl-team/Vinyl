package com.vinylteam.vinyl.util;

import com.vinylteam.vinyl.entity.Vinyl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShopsParser {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private List<VinylParser> vinylParserList;

    public ShopsParser(List<VinylParser> vinylParserList) {
        this.vinylParserList = vinylParserList;
    }

    public List<Vinyl> getAllVinyls() throws IOException {
        List<Vinyl> allVinyls = new ArrayList<>();
        for (VinylParser vinylParser : vinylParserList) {
            allVinyls.addAll(vinylParser.getAllVinylsFromShopList());
            logger.debug("Added all vinyls from vinyl parser to list of all vinyls {'vinylParser':{}}", vinylParser);
        }
        logger.debug("Resulting list of all vinyls from all shops is {'allVinyls':{}}", allVinyls);
        return allVinyls;
    }

}
