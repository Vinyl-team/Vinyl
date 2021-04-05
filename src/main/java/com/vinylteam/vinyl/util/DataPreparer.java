package com.vinylteam.vinyl.util;

import com.vinylteam.vinyl.entity.Vinyl;
import com.vinylteam.vinyl.util.impl.VinylUaParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataPreparer {

    public Map<String, List<Vinyl>> getPreparedData() throws IOException {
        Map<String, List<Vinyl>> preparedData = new HashMap<>();
        ShopsParser shopsParser = new ShopsParser(List.of(new VinylUaParser()));

        List<Vinyl> allVinyls = shopsParser.getAllProducts();
        int[] duplicateVinylArray = getDuplicateVinylIds(allVinyls);
        List<Vinyl> uniqueVinyls = getUniqueVinyls(allVinyls, duplicateVinylArray);

        preparedData.put("allProducts", allVinyls);
        preparedData.put("allUniqueProducts", uniqueVinyls);

        return preparedData;
    }

    private int[] getDuplicateVinylIds(List<Vinyl> allVinyls) {
        String release;
        String artist;
        String releaseForCompare;
        String artistForCompare;
        int duplicateCount = 0;
        int[] duplicateVinyls = new int[allVinyls.size() * 2];
        for (int i = 0; i < allVinyls.size(); i++) {
            if (i == allVinyls.size() - 1) {
                break;
            }
            release = allVinyls.get(i).getRelease();
            artist = allVinyls.get(i).getArtist();

            release = getParamForEqual(release);
            artist = getParamForEqual(artist);
            for (int j = i + 1; j < allVinyls.size(); j++) {
                releaseForCompare = allVinyls.get(j).getRelease();
                artistForCompare = allVinyls.get(j).getArtist();

                releaseForCompare = getParamForEqual(releaseForCompare);
                artistForCompare = getParamForEqual(artistForCompare);
                if (release.equals(releaseForCompare) && artist.equals(artistForCompare)) {
                    duplicateVinyls[duplicateCount++] = i;
                    duplicateVinyls[duplicateCount++] = j;
                }
            }
        }
        int[] newDuplicateVinyls = new int[duplicateCount];
        System.arraycopy(duplicateVinyls, 0, newDuplicateVinyls, 0, duplicateCount);
        return newDuplicateVinyls;
    }

    private List<Vinyl> getUniqueVinyls(List<Vinyl> allVinyls, int[] duplicateVinylArray) {
        List<Vinyl> uniqueVinylList = new ArrayList<>();
        boolean flag = false;
        long uniqueId = 1;
        for (int i = 0; i < allVinyls.size(); i++) {
            for (int j = 1; j < duplicateVinylArray.length; j = j + 2) {
                if (i == duplicateVinylArray[j]) {
                    flag = true;
                    for (Vinyl vinyl : uniqueVinylList) {
                        Vinyl duplicateVinyl = allVinyls.get(duplicateVinylArray[j - 1]);
                        if (duplicateVinyl.equals(vinyl)) {
                            allVinyls.get(i).setUniqueVinylId(vinyl.getUniqueVinylId());
                        }
                    }
                    break;
                }
            }
            if (!flag) {
                allVinyls.get(i).setUniqueVinylId(uniqueId++);
                uniqueVinylList.add(allVinyls.get(i));
            }
            flag = false;
        }
        return uniqueVinylList;
    }

    private String getParamForEqual(String param) {
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
