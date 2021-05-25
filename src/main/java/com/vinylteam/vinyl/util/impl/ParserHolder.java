package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.util.VinylParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParserHolder {

    private final List<VinylParser> parsers;

    public ParserHolder() {
        parsers = new ArrayList<>();
    }

    public ParserHolder(List<VinylParser> parsers) {
        this();
        this.parsers.addAll(parsers);
    }

    public Optional<VinylParser> getShopParser(long shopId){
        return parsers.stream()
                .filter(parser -> parser.getShopId() == shopId)
                .findFirst();
    }

}
