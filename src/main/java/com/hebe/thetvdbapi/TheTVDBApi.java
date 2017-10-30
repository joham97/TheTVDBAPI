package com.hebe.thetvdbapi;

import java.io.IOException;
import java.util.ArrayList;
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
	}

	/*
	 * Search For Series
	 */
	
	public static List<Series> searchForSeries(String keywords) throws ClientProtocolException, IOException{
		keywords = keywords.replace(" ", "%20");
		
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet("https://api.thetvdb.com/search/series?name="+keywords);
        request.addHeader("Accept", "application/json");
        request.addHeader("Authorization", "Bearer " + token);
        
        HttpResponse result = httpClient.execute(request);
        return parseSeriesList(EntityUtils.toString(result.getEntity(), "UTF-8"));
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
	
	public static Series searchForSeriesById(String id) throws ClientProtocolException, IOException{
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet("https://api.thetvdb.com/series/"+id);
        request.addHeader("Accept", "application/json");
        request.addHeader("Authorization", "Bearer " + token);
        
        HttpResponse result = httpClient.execute(request);
        return parseSingleSeries(EntityUtils.toString(result.getEntity(), "UTF-8"));
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
	
	public static String getThumbnailPathById(String id) throws ClientProtocolException, IOException{
		 return "https://www.thetvdb.com/banners/_cache/" + requestCoverPathById(id);
	}
	
	public static String getCoverPathById(String id) throws ClientProtocolException, IOException{
		 return "https://www.thetvdb.com/banners/" + requestCoverPathById(id);
	}
	
	private static String requestCoverPathById(String id) throws ClientProtocolException, IOException{
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet("https://api.thetvdb.com/series/"+id+"/images/query?keyType=poster");
        request.addHeader("Accept", "application/json");
        request.addHeader("Authorization", "Bearer " + token);
        
        HttpResponse result = httpClient.execute(request);
        return parseCoverPath(EntityUtils.toString(result.getEntity(), "UTF-8"));
	}
	
	private static String parseCoverPath(String json){
		JSONArray jsonArray = new JSONObject(json).getJSONArray("data");
		
		if(jsonArray != null){
			return jsonArray.getJSONObject(0).getString("fileName");
		}
		
		return null;
	}
	
}
