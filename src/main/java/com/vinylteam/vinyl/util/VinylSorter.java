package com.vinylteam.vinyl.util;

import com.vinylteam.vinyl.entity.Vinyl;
import com.vinylteam.vinyl.util.impl.VinylUaParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VinylSorter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Map<String, List<Vinyl>> getMapWithAllAndUniqueLists() throws IOException {
        logger.debug("Start of function VinylSorter.getMapWithAllAndUniqueLists()");
        Map<String, List<Vinyl>> allAndUniqueMap = new HashMap<>();
        logger.debug("Created and initialized Map<String, List<Vinyl>> object for storing " +
                "lists of all vinyls and list of unique vinyls " +
                "{'allAndUniqueMap':{}}", allAndUniqueMap);
        List<VinylParser> vinylParsersList = List.of(new VinylUaParser());
        logger.debug("Created and initialized List<VinylParser> object for storing " +
                "instances of all vinyl parsers " +
                "{'vinylParsersList':{}}", vinylParsersList);
        ShopsParser shopsParser = new ShopsParser(vinylParsersList);
        logger.debug("Created and initialized shopsParser with " +
                "{'shopsParser':{}, 'vinylParsersList':{}}", shopsParser, vinylParsersList);
        List<Vinyl> all = shopsParser.getAllVinyls();
        logger.debug("Created and initialized List<Vinyl> object for storing " +
                "all vinyls from all shops " +
                "{'all':{}}", all);
        int[] duplicatesArray = getDuplicatesIds(all);
        logger.debug("Created and initialized int[] object for storing " +
                "sequence of pairs of duplicate vinyls id-s " +
                "{'duplicatesArray':{}}", duplicatesArray);
        List<Vinyl> unique = getUnique(all, duplicatesArray);
        logger.debug("Created and initialized List<Vinyl> object for storing " +
                "all unique vinyls across all vinyls " +
                "{'unique':{}}", unique);
        allAndUniqueMap.put("all", all);
        allAndUniqueMap.put("unique", unique);
        logger.debug("Resulting map of all vinyls list and unique vinyls list is " +
                "{'allAndUniqueMap':{}}", allAndUniqueMap);
        return allAndUniqueMap;
    }

    int[] getDuplicatesIds(List<Vinyl> all) {
        logger.debug("Start of function VinylSorter.getDuplicatesIds(List<Vinyl> all) with " +
                "{'vinyls':{}}", all);
        String uniqueRelease;
        String uniqueArtist;
        String releaseToCompare;
        String artistToCompare;
        int duplicatesCount = 0;
        int[] duplicatesIds = new int[all.size() * 2];
        logger.debug("Created and initialized int array with length " +
                "{'arrayLength':{}}", duplicatesIds.length);
        for (int i = 0; i < all.size() - 1; i++) {
            uniqueRelease = getParametersForComparison(all.get(i).getRelease());
            uniqueArtist = getParametersForComparison(all.get(i).getArtist());
            logger.debug("{'i':{}, 'uniqueRelease':{}, 'uniqueArtist'{}}", i, uniqueRelease, uniqueArtist);
            for (int j = i + 1; j < all.size(); j++) {
                releaseToCompare = getParametersForComparison(all.get(j).getRelease());
                artistToCompare = getParametersForComparison(all.get(j).getArtist());
                logger.debug("{'i':{}, 'j':{}, 'releaseToCompare':{}, 'artistToCompare'{}}", i, j, releaseToCompare, artistToCompare);
                if (uniqueRelease.equals(releaseToCompare) && uniqueArtist.equals(artistToCompare)) {
                    duplicatesIds[duplicatesCount++] = i;
                    duplicatesIds[duplicatesCount++] = j;
                    logger.debug("Vinyls with these id-s are duplicates " +
                            "{'uniqueId':{}, 'duplicateId':{}, 'duplicatesCount':{}}", i, j, duplicatesCount);
                }
            }
        }
        int[] resultingDuplicatesIds = new int[duplicatesCount];
        logger.debug("Created and initialized int array with length equal to duplicates count " +
                "{'arrayLength':{}, 'duplicatesCount':{}}", resultingDuplicatesIds.length, duplicatesCount);
        System.arraycopy(duplicatesIds, 0, resultingDuplicatesIds, 0, duplicatesCount);
        logger.debug("Resulting array of with id-s of vinyls and their duplicates " +
                "{'duplicatesIds':{}}", resultingDuplicatesIds);
        return resultingDuplicatesIds;
    }

    List<Vinyl> getUnique(List<Vinyl> all, int[] duplicatesArray) {
        logger.debug("Start of function VinylSorter.getUnique(List<Vinyl> all, int[] duplicatesArray) with " +
                "{'vinyls':{}, 'duplicatesIds':{}}", all, duplicatesArray);
        List<Vinyl> unique = new ArrayList<>();
        logger.debug("Created and initialized List<Vinyl> object for storing unique vinyls from all vinyls " +
                "{'unique':{}}", unique);
        boolean flag = false;
        long uniqueId = 1;
        for (int i = 0; i < all.size(); i++) {
            for (int j = 1; j < duplicatesArray.length; j = j + 2) {
                if (i == duplicatesArray[j]) {
                    flag = true;
                    for (Vinyl vinyl : unique) {
                        Vinyl duplicateVinyl = all.get(duplicatesArray[j - 1]);
                        if (duplicateVinyl.equals(vinyl)) {
                            all.get(i).setUniqueVinylId(vinyl.getUniqueVinylId());
                            logger.debug("Set uniqueVinylId for vinyl with id" +
                                    "{'id':{}, 'uniqueVinylId':{}}", i, vinyl.getUniqueVinylId());
                        }
                    }
                    break;
                }
            }
            if (!flag) {
                all.get(i).setUniqueVinylId(uniqueId++);
                unique.add(all.get(i));
            }
            flag = false;
        }
        logger.debug("Resulting list of unique vinyls " +
                "{'unique':{}}", unique);
        return unique;
    }

    String getParametersForComparison(String param) {
        logger.debug("Start of function VinylSorter.getParametersForComparison(String param) with " +
                "{'param':{}}", param);
        String[] paramArray = param.split(" ");
        logger.debug("Split param into param array " +
                "{'param':{}, 'paramArray':{}}", param, paramArray);
        if (paramArray.length > 1 && (paramArray[0].equalsIgnoreCase("the")
                || paramArray[0].equalsIgnoreCase("a")) ) {
            paramArray[0] = paramArray[1];
        }
        logger.debug("Resulting string is " +
                "{'resultParam':{}}", paramArray[0]);
        return paramArray[0];
    }
}
