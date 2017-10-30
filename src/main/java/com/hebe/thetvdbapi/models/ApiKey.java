package com.hebe.thetvdbapi.models;

import javax.xml.bind.annotation.XmlRootElement;

import org.json.JSONObject;

import json.JSONParser;

@XmlRootElement
public class ApiKey {

	public String apikey;

	public ApiKey(String apikey) {
		this.apikey = apikey;
	}
	
	public void setApikey(String apikey) {
		this.apikey = apikey;
	}
	
	public String getApikey() {
		return this.apikey;
	}
	
	public String toJSON(){
		return "{ " + JSONParser.single("apikey", this.apikey) + " }";
	}
	
	public static Token parse(String string){
		JSONObject obj = new JSONObject(string);
		if(obj.has("apikey")){
			return new Token(obj.getString("apikey"));
		}else{
			return null;
		}		
	}
}
