package com.hebe.thetvdbapi.models;

import javax.xml.bind.annotation.XmlRootElement;

import org.json.JSONObject;

import json.JSONParser;

@XmlRootElement
public class Token {

	public String token;

	public Token(String token) {
		this.token = token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public String getToken() {
		return this.token;
	}
	
	public String toJSON(){
		return "{ " + JSONParser.single("token", this.token) + " }";
	}
	
	public static Token parse(String string){
		JSONObject obj = new JSONObject(string);
		if(obj.has("token")){
			return new Token(obj.getString("token"));
		}else{
			return null;
		}		
	}
}
