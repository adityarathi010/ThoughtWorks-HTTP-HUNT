package com.tw;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Game {
    private static int stage = 1;

    public JSONObject start() throws Exception {
        //stage = getStage(Constants.URL);
        String response = getResponseByUri(Constants.CHALLENGE_INPUT_URI);
        JSONParser parser = new JSONParser();
        JSONArray jArr = (JSONArray) parser.parse(response);
        JSONObject jObj = new JSONObject();

        switch (stage) {
            case 1:
                stageOne(jObj, jArr);
                break;
            case 2:
                stageTwo(jObj, jArr);
                break;
            case 3:
                stageThree(jObj, jArr);
                break;
            case 4:
                stageFour(jObj, jArr);
                break;
            default:
                jObj = new JSONObject();
        }
        return jObj;
    }

/*    private int getStage(String uri) throws Exception {
        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            connection.setRequestMethod("GET");
            connection.setRequestProperty("userid", Constants.userId);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        int responseCode = connection.getResponseCode();
        StringBuffer response = new StringBuffer();
        if (responseCode != 200)
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        else {
            BufferedReader in = new BufferedReader(L
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        }
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(response.toString());
        stage = Integer.parseInt(String.valueOf(obj.get("stage")).substring(0, 1));
        System.out.println(String.valueOf(obj.get("stage")).charAt(0));

        connection.disconnect();
        return stage;
    }*/

    private void stageOne(JSONObject jObj, JSONArray jArr) {
        JSONObject obj = new JSONObject();
        obj.put("count", jArr.size());
        jObj.put("output", obj);
    }

    public void stageTwo(JSONObject jObj, JSONArray jArr) {

        JSONObject obj = new JSONObject();
        int count = 0;
        JSONObject temp;
        for (Object arrObj : jArr) {
            if (isActive((JSONObject) arrObj)) {
                count++;
            }
        }
        obj.put("count", count);
        jObj.put("output", obj);

    }

    public void stageThree(JSONObject jObj, JSONArray jArr) {
        JSONObject obj = new JSONObject();
        for (Object arrObj : jArr) {
            if (isActive((JSONObject) arrObj)) {
                String category = (String) ((JSONObject) arrObj).get("category");
                if (obj.containsKey(category)) {
                    obj.put(category, Integer.parseInt(String.valueOf(obj.get(category))) + 1);
                } else {
                    obj.put(category, 1);
                }
            }
        }
        jObj.put("output", obj);
    }

    public void stageFour(JSONObject jObj, JSONArray jArr) {

        JSONObject obj = new JSONObject();
        Long totalValue = 0L;
        JSONObject temp;
        for (Object arrObj : jArr) {
            if (isActive((JSONObject) arrObj)) {
                totalValue += (Long) ((JSONObject) arrObj).get("price");
            }
        }
        obj.put("totalValue", totalValue);
        jObj.put("output", obj);

    }

    private boolean isActive(JSONObject arrObj) {
        String endDate = (String) (arrObj).get("endDate");
        String startDate = (String) (arrObj).get("startDate");

        try {
            Date today = new Date();
            Date start = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
            Date end = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);
            if (today.compareTo(end) <= 0 && today.compareTo(start) >= 0) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void postData(JSONObject jObj) throws Exception {
        URL url = new URL(Constants.CHALLENGE_OUTPUT_URI);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            connection.setRequestMethod("POST");
            connection.setRequestProperty("userid", Constants.userId);
            connection.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON);
            //connection.setRequestProperty("output", String.valueOf((jObj.get("output"))));
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(jObj.toJSONString());
            wr.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(jObj);
        int status = connection.getResponseCode();
        String resMes = connection.getResponseMessage();
        if (status == 200) {
            stage++;
        }
        System.out.println(status + " : " + resMes);
    }

    private String getResponseByUri(String inputUri) throws Exception {
        URL url = new URL(inputUri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            connection.setRequestMethod("GET");
            connection.setRequestProperty("userid", Constants.userId);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        int responseCode = connection.getResponseCode();
        StringBuffer response = new StringBuffer();
        if (responseCode != 200)
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        else {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        }
        connection.disconnect();
        return response.toString();
    }
}
