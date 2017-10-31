package com.hebe.thetvdbapi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.hebe.thetvdbapi.models.ApiKey;
import com.hebe.thetvdbapi.models.Episode;
import com.hebe.thetvdbapi.models.Season;
import com.hebe.thetvdbapi.models.Series;
import com.hebe.thetvdbapi.models.Token;

public abstract class TheTVDBApi {

	private static String token = null;
	
	/*
	 * Token
	 */
	
	public static String requestNewToken(String apiKey) throws ClientProtocolException, IOException {	
		ApiKey apikey = new ApiKey(apiKey);
		
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost request = new HttpPost("https://api.thetvdb.com/login");
        StringEntity params = new StringEntity(apikey.toJSON());
        request.addHeader("Content-Type", "application/json");
        request.addHeader("Accept", "application/json");
        request.setEntity(params);
        
        HttpResponse result = httpClient.execute(request);
        Token token = Token.parse(EntityUtils.toString(result.getEntity(), "UTF-8"));
		
		return token.token;
	}
	
	public static void useToken(String token) {
		TheTVDBApi.token = token;
		System.out.println("Use Token: " + token);
	}

	public static boolean badToken(String json){
		JSONObject rootNode = new JSONObject(json);
		return rootNode.has("Error") && rootNode.getString("Error").equals("Not authorized");
	}
	
	/*
	 * Search For Series
	 */
	
	public static List<Series> searchForSeries(String keywords) throws ClientProtocolException, IOException, BadTokenException{
		keywords = keywords.replace(" ", "%20");
		
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet("https://api.thetvdb.com/search/series?name="+keywords);
        request.addHeader("Accept", "application/json");
        request.addHeader("Authorization", "Bearer " + token);
        
        HttpResponse result = httpClient.execute(request);
        String json = EntityUtils.toString(result.getEntity(), "UTF-8");
        if(badToken(json)) throw new BadTokenException();
        return parseSeriesList(json);
	}
	
	private static List<Series> parseSeriesList(String json){
		List<Series> series = new ArrayList<Series>();
		
		JSONObject rootNode = new JSONObject(json);
		if(rootNode.has("data")){
			rootNode.getJSONArray("data").forEach((obj) -> {
				JSONObject jsonObject = (JSONObject) obj;
				Series singleSeries = new Series();
				if(!jsonObject.isNull("id")) singleSeries.setId(jsonObject.getInt("id"));
				if(!jsonObject.isNull("seriesName")) singleSeries.setSeriesName(jsonObject.getString("seriesName"));
				if(!jsonObject.isNull("status")) singleSeries.setStatus(jsonObject.getString("status"));
				if(!jsonObject.isNull("firstAired")) singleSeries.setFirstAired(jsonObject.getString("firstAired"));
				if(!jsonObject.isNull("network")) singleSeries.setNetwork(jsonObject.getString("network"));
				if(!jsonObject.isNull("overview")) singleSeries.setOverview(jsonObject.getString("overview"));
				series.add(singleSeries);
			});	
		}
		
		return series;
	}

	/*
	 * Search For Series By ID
	 */
	
	public static Series searchForSeriesById(String id) throws ClientProtocolException, IOException, BadTokenException{
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet("https://api.thetvdb.com/series/"+id);
        request.addHeader("Accept", "application/json");
        request.addHeader("Authorization", "Bearer " + token);
        
        HttpResponse result = httpClient.execute(request);
        String json = EntityUtils.toString(result.getEntity(), "UTF-8");
        if(badToken(json)) throw new BadTokenException();
        return parseSingleSeries(json);
	}
	
	private static Series parseSingleSeries(String json){
		JSONObject jsonObject = new JSONObject(json).getJSONObject("data");
		
		Series series = new Series();
		if(!jsonObject.isNull("id")) series.setId(jsonObject.getInt("id"));
		if(!jsonObject.isNull("seriesName")) series.setSeriesName(jsonObject.getString("seriesName"));
		if(!jsonObject.isNull("status")) series.setStatus(jsonObject.getString("status"));
		if(!jsonObject.isNull("firstAired")) series.setFirstAired(jsonObject.getString("firstAired"));
		if(!jsonObject.isNull("network")) series.setNetwork(jsonObject.getString("network"));
		if(!jsonObject.isNull("overview")) series.setOverview(jsonObject.getString("overview"));
		
		return series;
	}
	
	/*
	 * Cover Path
	 */
	
	public static String getThumbnailPathById(String id) throws ClientProtocolException, IOException, BadTokenException{
		String subPath = requestCoverPathById(id);
		if(subPath!=null){
			return "https://www.thetvdb.com/banners/_cache/" + subPath;
		}
		return null; 
	}
	
	public static String getCoverPathById(String id) throws ClientProtocolException, IOException, BadTokenException{
		String subPath = requestCoverPathById(id);
		if(subPath!=null){
			return "https://www.thetvdb.com/banners/" + subPath;
		}
		return null; 
	}
	
	private static String requestCoverPathById(String id) throws ClientProtocolException, IOException, BadTokenException{
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet("https://api.thetvdb.com/series/"+id+"/images/query?keyType=poster");
        request.addHeader("Accept", "application/json");
        request.addHeader("Authorization", "Bearer " + token);
        
        HttpResponse result = httpClient.execute(request);
        String json = EntityUtils.toString(result.getEntity(), "UTF-8");
        if(badToken(json)) throw new BadTokenException();
        return parseCoverPath(json);
	}
	
	private static String parseCoverPath(String json){
		JSONObject jsonObject = new JSONObject(json);
		if(jsonObject.has("data")){
			JSONArray jsonArray = jsonObject.getJSONArray("data");
			
			if(jsonArray != null){
				return jsonArray.getJSONObject(0).getString("fileName");
			}
		}
		return null;
	}

	/*
	 * Seasons
	 */
	
	public static List<Season> getSeasons(String id) throws ClientProtocolException, IOException, BadTokenException{
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet("https://api.thetvdb.com/series/" + id + "/episodes");
        request.addHeader("Accept", "application/json");
        request.addHeader("Authorization", "Bearer " + token);
        
        HttpResponse result = httpClient.execute(request);
        String json = EntityUtils.toString(result.getEntity(), "UTF-8");
        if(badToken(json)) throw new BadTokenException();
        return parseSeasons(json);
	}
	
	private static List<Season> parseSeasons(String json){
		HashMap<Integer, Season> seasonsMap = new HashMap<Integer, Season>();
		List<Season> seasons = new ArrayList<Season>();
		
		JSONArray jsonArray = new JSONObject(json).getJSONArray("data");
		
		if(jsonArray != null){
			jsonArray.forEach((o) -> {
				JSONObject jsonObj = ((JSONObject) o);
				if(jsonObj.has("airedSeason") && jsonObj.has("airedEpisodeNumber") && jsonObj.has("episodeName")){
					int airedSeason = jsonObj.getInt("airedSeason");
					
					if(!seasonsMap.containsKey(airedSeason)){
						seasonsMap.put(airedSeason, new Season(airedSeason));
						seasonsMap.get(airedSeason).setAiredSeasonID(jsonObj.getInt("airedSeasonID"));
					}
					
					Episode episode = new Episode();
					episode.setAiredEpisodeNumber(jsonObj.getInt("airedEpisodeNumber"));
					episode.setEpisodeName(jsonObj.getString("episodeName"));
					episode.setId(jsonObj.getInt("id"));
					seasonsMap.get(airedSeason).addEpisodes(episode);
				}
			});
		}
		
		seasonsMap.values().forEach((s) -> seasons.add(s));
		seasons.sort(Comparator.comparingInt(Season::getAiredSeason));
		
		return seasons;
	}

	/*
	 * Episode Image By ID
	 */

	public static String getEpisodeThumbnailPathById(String id) throws ClientProtocolException, IOException, BadTokenException{
		 return "https://www.thetvdb.com/banners/_cache/" + getEpisodeImage(id);
	}
	
	public static String getEpisodeCoverPathById(String id) throws ClientProtocolException, IOException, BadTokenException{
		 return "https://www.thetvdb.com/banners/" + getEpisodeImage(id);
	}
	
	private static String getEpisodeImage(String id) throws ClientProtocolException, IOException, BadTokenException{
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet("https://api.thetvdb.com/episodes/"+id);
        request.addHeader("Accept", "application/json");
        request.addHeader("Authorization", "Bearer " + token);
        
        HttpResponse result = httpClient.execute(request);
        String json = EntityUtils.toString(result.getEntity(), "UTF-8");
        if(badToken(json)) throw new BadTokenException();
        return parseEpisodeImage(json);
	}
	
	private static String parseEpisodeImage(String json){
		JSONObject jsonObject = new JSONObject(json).getJSONObject("data");
		
		if(!jsonObject.isNull("filename")) {
			return jsonObject.getString("filename");
		}
		
		return null;
	}

	
}
