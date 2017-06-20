/**
 * ICT NASC
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.alibaba.webx.citation.app1.module.screen;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.citrus.turbine.Context;

/**
 * 
 * @author xueye.duanxy
 * @version $Id: ViewShare.java, v 0.1 2016-1-21 4:58:19  Exp $
 */
public class ViewShare {

    /***/
    private static final String AUTHOR_LIST    = "bash /usr/local/tomcat/citation/run.sh";
    /***/
    private static final String PREDICT_RUN_SH = "bash /usr/local/tomcat/citation/predict.sh";

    /**
     * 
     * 
     * @param request
     * @param context
     */
    public void execute(HttpServletRequest request, Context context) {
        try {

            String titleStr = request.getParameter("title");
            String titleId = request.getParameter("titleId");
            String citationCount = "1";
            String authorCount = "1";
            List<List<String>> shareList = new ArrayList<List<String>>();
            List<String> rawList = null;
            List<String> nameList = new ArrayList<String>();
            List<String> totalList = new ArrayList<String>();
            //
            List<String> outputList = getShOutStr(titleId, AUTHOR_LIST);
            String outputResult = "";
            int authorC = 0;
            //StringList Data List
            for (String line : outputList) {

                if (line.startsWith("Citations")) {
                    citationCount = line.split("\t")[1];
                } else if (line.startsWith("Author")) {
                    authorCount = line.split("\t")[1];
                    authorC = Integer.parseInt(authorCount);
                } else if (line.startsWith("Name")) {
                    //line.replace("Name\t", "");
                    String[] nameLineList = line.split("\t");
                    for (int i = 0; i < authorC; i++) {
                        nameList.add(nameLineList[i + 1]);
                    }
                } else if (line.startsWith("#")) {
                    rawList = new ArrayList<String>();
                    for (String item : line.split("\t")) {
                        rawList.add(item);
                    }
                    shareList.add(rawList);
                } 
            }
            context.put("outputResult", outputResult);
            context.put("title", titleStr);
            context.put("citationCount", citationCount);
            context.put("authorCount", authorCount);
            context.put("nameList", nameList);
            context.put("shareList", shareList);
            context.put("totalList", totalList);
            dataMsg(context, outputList);
            //¡ÌÕ‚“ª’≈Õº
            predictList(context,titleId);
        } catch (Exception e) {
            context.put("message", e.getMessage());
        }
    }

    /**
     * 
     * @param context
     * @param strList
     * @throws Exception
     */
    private void dataMsg(Context context, List<String> strList) throws Exception {
        List<JSONArray> yearDataList = new ArrayList<JSONArray>();
        List<JSONObject> nameJSList = new ArrayList<JSONObject>();
        for (String str : strList) {
            if (str.startsWith("Citations")) {
                continue;
            } else if (str.startsWith("Author")) {
                continue;
            } else if (str.startsWith("Name")) {
                JSONObject jso = null;
                String[] nameLineList = str.split("\t");
                for (int i = 1; i < nameLineList.length; i++) {
                    jso = new JSONObject();
                    jso.put("name", nameLineList[i]);
                    nameJSList.add(jso);
                }
                continue;
            } else if (str.startsWith("#")) {
                continue;
            } else {
                //yearDataList.add(str);
                String[] yearData = str.split("\t");

                List<JSONObject> linedata = new ArrayList<JSONObject>();
                for (String data : yearData) {
                    JSONObject ob = new JSONObject();
                    ob.put("a", data);
                    linedata.add(ob);
                }
                JSONArray dataList = new JSONArray(linedata);
                yearDataList.add(dataList);
            }
        }
        JSONArray dataList = new JSONArray(yearDataList);
        context.put("dataList", dataList);
        context.put("nameJSList", nameJSList.toString());

    }

    /**
     * 
     * @param titleId
     * @param shell
     * @return
     * @throws Exception 
     */
    private List<String> getShOutStr(String titleId, String shell) throws Exception {
        List<String> outputList = new ArrayList<String>();
        try {
            Process process = null;
            process = Runtime.getRuntime().exec(shell + " " + titleId);
            BufferedReader input = new BufferedReader(new InputStreamReader(
                process.getInputStream()));

            String line = "";
            while ((line = input.readLine()) != null) {
                outputList.add(line);
            }

            input.close();

        } catch (Exception e) {
            throw e;
        }
        return outputList;
    }
    /**
     * 
     * @param context
     * @param titleId
     * @throws Exception
     * @throws JSONException
     */
    private void predictList(Context context, String titleId) throws Exception, JSONException {
        List<String> outputList = getShOutStr(titleId, PREDICT_RUN_SH);
        List<JSONObject> predictResultList = new ArrayList<JSONObject>();
        for (String line : outputList) {
            if (line.startsWith("This")) {
                context.put("predictMsg", "No predict result");
                return;
            } else {
                JSONObject citationCount = new JSONObject();
                citationCount.put("year", line.split("\t")[0]);
                citationCount.put("count", line.split("\t")[1]);
                predictResultList.add(citationCount);
            }
        }
        context.put("predictResult", new JSONArray(predictResultList).toString());
    }


    

}