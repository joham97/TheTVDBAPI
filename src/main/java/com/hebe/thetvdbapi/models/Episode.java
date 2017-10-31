package com.hebe.thetvdbapi.models;

public class Episode {

	private int id;
	private int airedEpisodeNumber;
	private String episodeName;
	
	public int getAiredEpisodeNumber() {
		return this.airedEpisodeNumber;
	}
	public void setAiredEpisodeNumber(int airedEpisodeNumber) {
		this.airedEpisodeNumber = airedEpisodeNumber;
	}
	public String getEpisodeName() {
		return this.episodeName;
	}
	public void setEpisodeName(String episodeName) {
		this.episodeName = episodeName;
	}
	public int getId() {
		return this.id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
}
