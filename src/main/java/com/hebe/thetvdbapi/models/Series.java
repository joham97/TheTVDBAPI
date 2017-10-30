package com.hebe.thetvdbapi.models;

public class Series {

	private int id;
	private String seriesName;
	private String status;
	private String firstAired;
	private String network;
	private String overview;
	
	public int getId() {
		return this.id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSeriesName() {
		return this.seriesName;
	}
	public void setSeriesName(String seriesName) {
		this.seriesName = seriesName;
	}
	public String getStatus() {
		return this.status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getFirstAired() {
		return this.firstAired;
	}
	public void setFirstAired(String firstAired) {
		this.firstAired = firstAired;
	}
	public String getNetwork() {
		return this.network;
	}
	public void setNetwork(String network) {
		this.network = network;
	}
	public String getOverview() {
		return this.overview;
	}
	public void setOverview(String overview) {
		this.overview = overview;
	}
	@Override
	public String toString() {
		return "Series [id=" + this.id + ", seriesName=" + this.seriesName + ", status=" + this.status + ", firstAired=" + this.firstAired + ", network=" + this.network + ", overview=" + this.overview + "]";
	}
	
	
	
}
