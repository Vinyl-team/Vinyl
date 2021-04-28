package com.vinylteam.vinyl.util;

import com.vinylteam.vinyl.entity.RawOffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShopsParser {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public List<RawOffer> getRawVinylDataFromAll(List<VinylParser> vinylParserList) throws IOException {
        List<RawOffer> allRawOffers = new ArrayList<>();
        for (VinylParser vinylParser : vinylParserList) {
            allRawOffers.addAll(vinylParser.getRawVinylDataList());
            logger.debug("Added all vinyls from vinyl parser to list of all vinyls {'vinylParser':{}}", vinylParser);
        }
        logger.debug("Resulting list of all vinyls from all shops is {'allRawOffers':{}}", allRawOffers);
        return allRawOffers;
    }

}
