package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.discogs4j.entity.DiscogsVinylInfo;
import com.vinylteam.vinyl.entity.Vinyl;
import org.json.simple.parser.ParseException;

import java.util.List;
import java.util.Optional;

public interface DiscogsService {

    List<Vinyl> getDiscogsMatchList(String discogsUserName, List<Vinyl> allUniqueVinyl);

    String getDiscogsLink(String artist, String release, String fullName) throws ParseException;
}
