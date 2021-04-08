package com.vinylteam.vinyl.util;

import com.vinylteam.vinyl.entity.Vinyl;
import com.vinylteam.vinyl.util.impl.VinylUaParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VinylSorter {

    public Map<String, List<Vinyl>> getMapWithAllAndUniqueLists() throws IOException {
        Map<String, List<Vinyl>> allAndUniqueLists = new HashMap<>();
        ShopsParser shopsParser = new ShopsParser(List.of(new VinylUaParser()));

        List<Vinyl> all = shopsParser.getAllVinyls();
        int[] duplicatesArray = getDuplicatesIds(all);
        List<Vinyl> unique = getUnique(all, duplicatesArray);

        allAndUniqueLists.put("all", all);
        allAndUniqueLists.put("unique", unique);

        return allAndUniqueLists;
    }

    int[] getDuplicatesIds(List<Vinyl> all) {
        String uniqueRelease;
        String uniqueArtist;
        String releaseToCompare;
        String artistToCompare;
        int duplicatesCount = 0;
        int[] duplicates = new int[all.size() * 2];
        for (int i = 0; i < all.size() - 1; i++) {

            uniqueRelease = getParametersForComparison(all.get(i).getRelease());
            uniqueArtist = getParametersForComparison(all.get(i).getArtist());
            for (int j = i + 1; j < all.size(); j++) {

                releaseToCompare = getParametersForComparison(all.get(j).getRelease());
                artistToCompare = getParametersForComparison(all.get(j).getArtist());
                if (uniqueRelease.equals(releaseToCompare) && uniqueArtist.equals(artistToCompare)) {
                    duplicates[duplicatesCount++] = i;
                    duplicates[duplicatesCount++] = j;
                }
            }
        }
        int[] newDuplicates = new int[duplicatesCount];
        System.arraycopy(duplicates, 0, newDuplicates, 0, duplicatesCount);
        return newDuplicates;
    }

    List<Vinyl> getUnique(List<Vinyl> all, int[] duplicatesArray) {
        List<Vinyl> uniqueList = new ArrayList<>();
        boolean flag = false;
        long uniqueId = 1;
        for (int i = 0; i < all.size(); i++) {
            for (int j = 1; j < duplicatesArray.length; j = j + 2) {
                if (i == duplicatesArray[j]) {
                    flag = true;
                    for (Vinyl vinyl : uniqueList) {
                        Vinyl duplicateVinyl = all.get(duplicatesArray[j - 1]);
                        if (duplicateVinyl.equals(vinyl)) {
                            all.get(i).setUniqueVinylId(vinyl.getUniqueVinylId());
                        }
                    }
                    break;
                }
            }
            if (!flag) {
                all.get(i).setUniqueVinylId(uniqueId++);
                uniqueList.add(all.get(i));
            }
            flag = false;
        }
        return uniqueList;
    }

    String getParametersForComparison(String param) {
        String[] paramArray = param.split(" ");
        if (paramArray.length <= 1) {
            return paramArray[0];
        }
        if (paramArray[0].equalsIgnoreCase("the") || paramArray[0].equalsIgnoreCase("a")) {
            paramArray[0] = paramArray[1];
        }
        return paramArray[0];
    }
}
