package com.hebe.thetvapi.test;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.hebe.thetvdbapi.TheTVDBApi;

public class TestMain {

	public static void main(String[] args) {
		try {			
			String token = TheTVDBApi.requestNewToken("B2FABF16E0600F2C");
			TheTVDBApi.useToken(token);

			TheTVDBApi.searchForSeries("diasndiosnoidnoasi").forEach((e)-> System.out.println(e.getSeriesName()));
			System.out.println(TheTVDBApi.searchForSeriesById("121361"));
			System.out.println(TheTVDBApi.getThumbnailPathById("121361"));
			System.out.println(TheTVDBApi.getCoverPathById("121361"));			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
