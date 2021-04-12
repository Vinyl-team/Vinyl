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
        logger.debug("Starting constructor ShopsParser(List<VinylParser> vinylParserList) " +
                "with {'vinylParserList':{}}", vinylParserList);
        this.vinylParserList = vinylParserList;
        logger.debug("Initialized this.vinylParserList " +
                "{'vinylParserList':{}}", this.vinylParserList);
    }

    public List<Vinyl> getAllVinyls() throws IOException {
        logger.debug("Start of function ShopsParser.getAllVinyls()");
        List<Vinyl> allVinyls = new ArrayList<>();
        logger.debug("Created and initialized List<Vinyl> object for storing vinyls from all shops " +
                "{'allVinyls':{}}", allVinyls);
        for (VinylParser vinylParser : vinylParserList) {
            allVinyls.addAll(vinylParser.getAllVinylsFromShopList());
            logger.debug("Added all vinyls from vinylParser.getAllVinylsFromShopList() " +
                    "to list of all vinyls " +
                    "{'vinylParser':{}, 'allVinyls':{}}", vinylParser, allVinyls);
        }
        logger.debug("Resulting list of all vinyls from all shops is " +
                "{'allVinyls':{}}", allVinyls);
        return allVinyls;
    }
}
