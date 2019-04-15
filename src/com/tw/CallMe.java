package com.tw;

import org.json.simple.JSONObject;

public class CallMe {
    public static void main(String[] args) throws Exception {
        Game game = new Game();
        JSONObject jObj = game.start();

        if(jObj != null){
           game.postData(jObj);
        }
    }
}
