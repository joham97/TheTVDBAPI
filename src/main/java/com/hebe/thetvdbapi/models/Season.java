package com.hebe.thetvdbapi.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Season {

	private int airedSeasonID;
	private int airedSeason;
	private List<Episode> episodes;
	
	public Season(int airedSeason) {
		this.airedSeason = airedSeason;
		this.episodes = new ArrayList<>();
	}
	
	public int getAiredSeason() {
		return this.airedSeason;
	}
	
	public void setAiredSeason(int airedSeason) {
		this.airedSeason = airedSeason;
	}
	
	public List<Episode> getEpisodes() {
		return this.episodes;
	}
	
	public void addEpisodes(Episode episode) {
		this.episodes.add(episode);
		this.episodes.sort(Comparator.comparingInt(Episode::getAiredEpisodeNumber));
	}
	
	public int getAiredSeasonID() {
		return this.airedSeasonID;
	}
	
	public void setAiredSeasonID(int airedSeasonID) {
		this.airedSeasonID = airedSeasonID;
	}
	
	
}
