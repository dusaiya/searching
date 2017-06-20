/**
 * ICT NASC
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.alibaba.webx.citation.app1.module.screen;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.webx.citation.app1.util.DateTimeUtil;

/**
 * 
 * @author xueye.duanxy
 * @version $Id: ViewShare.java, v 0.1 2016-1-21 4:58:19 Exp $
 */
public class Websitedetail {

	/***/
	private static final String DETAIL_QUERY = "python /home/project242/web061714.py ";
	/****/
	private static final String SPACE = " ";

	private static final String QUATATION = "\"";

	private static final String QUATATION_REPLACE = "\\\"";
	/****/
	private static final String PY_SPLIT = "___";

	/**
	 * 
	 * 
	 * @param request
	 * @param context
	 */
	public void execute(HttpServletRequest request, Context context) {
		try {
			String title = request.getParameter("title");
			String startTime = request.getParameter("startTime");
			if (StringUtils.isBlank(startTime) || startTime.length() < 10) {
				startTime = "2017-05-06 00:00:00";
			} else {
				startTime = startTime.substring(0, 10) + " 00:00:00";
			}
			String sqlTime = startTime.substring(0, 10);
			String endTime = DateTimeUtil.getNextDay(startTime, 1);
			List<List<String>> timeList = new ArrayList<List<String>>();
			List<List<String>> areaList = new ArrayList<List<String>>();
			List<List<String>> groupList = new ArrayList<List<String>>();
			List<List<String>> urlList = new ArrayList<List<String>>();
			//
			String args = title + PY_SPLIT + startTime;
			try {
				args = URLEncoder.encode(args, "utf8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			List<String> outputList = getShOutStr(args, DETAIL_QUERY + SPACE, context);

			int typeCt = 0; // 轮数计数
			// StringList Data List
			for (String line : outputList) {
				if (line.startsWith("#")) {
					typeCt++;
				} else {
					switch (typeCt) {
					case 1:
						addNewLine(timeList, line);
						break;
					case 2:
						addNewLine(areaList, line);
						break;
					case 3:
						addNewLine(groupList, line);
						break;
					case 4:
						addNewLine(urlList, line);
						break;
					default:
						break;
					}
				}
			}
			context.put("title", title);
			context.put("startTime", startTime);
			context.put("endTime", endTime);
			context.put("sqlTime", sqlTime);
			putTime(timeList, context);
			putMap(areaList, context);
			//putGroup(groupList,context);
			context.put("groupList", groupList);
			context.put("urlList", urlList);
			context.put("message", "完整流程跑完，未溢出");
		} catch (Exception e) {
			context.put("message", e.getMessage());
		}
	}

	private void addNewLine(List<List<String>> strList, String line) {
		List<String> content = new ArrayList<String>();
		String[] lineOb = line.split("\t");
		content.add(lineOb[0]);
		content.add(lineOb[1]);
		if(lineOb.length>2){
			content.add(lineOb[2]);
		}
		strList.add(content);

	}
	/**
	 * 
	 * @param line
	 */
	private void putTime(List<List<String>> timeList, Context context) {
		List<JSONObject> linedata = new ArrayList<JSONObject>();
		for (List<String> timeLine : timeList) {
			JSONObject ob = new JSONObject();
			try {
				ob.put("name", timeLine.get(0));
				ob.put("data", timeLine.get(1));
				linedata.add(ob);
			} catch (JSONException e) {
				context.put("TimeMessage", e.getMessage());
			}

		}
		JSONArray dataList = new JSONArray(linedata);
		context.put("timeList", dataList);

	}
	/**
	 * 
	 * @param line
	 */
	private void putMap(List<List<String>> areaList, Context context) {
		int maxCt = 0;
		for (List<String> strLine : areaList) {
			String hotCtStr = strLine.get(1);
			Integer hotCt = new Integer(0);
			try {
				hotCt = Integer.parseInt(hotCtStr);
				maxCt = maxCt > hotCt.intValue() ? maxCt : hotCt.intValue();
			} catch (Exception e) {
				hotCt = new Integer(0);
			}
		}
		maxCt = ((int) (maxCt / 100) + 1) * 100;
		context.put("areaMaxCt", maxCt);
		
		
		JSONArray dataList = contextJson(areaList, context);
		context.put("mapList", dataList);
	}

	private JSONArray contextJson(List<List<String>> strList, Context context) {
		List<JSONObject> linedata = new ArrayList<JSONObject>();
		for (List<String> strLine : strList) {
			JSONObject ob = new JSONObject();
			try {
				ob.put("name", strLine.get(0));
				ob.put("value", strLine.get(1));
				linedata.add(ob);
			} catch (JSONException e) {
				context.put("ErrorMessage", e.getMessage());
			}

		}
		JSONArray dataList = new JSONArray(linedata);
		return dataList;
	}

	/**
	 * 
	 * @param titleId
	 * @param shell
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private List<String> getShOutStr(String titleId, String shell, Context context) throws Exception {
		List<String> outputList = new ArrayList<String>();
		try {
			Process process = null;
			String command = shell + SPACE + titleId.replace(QUATATION, QUATATION_REPLACE);
			context.put("py", command);
			context.put("outputResult", "准备开始跑shell脚本!");
			process = Runtime.getRuntime().exec(command);
			/***
			 * Cannot run program "python /root/event060514.py ": error=2,
			 * 没有那个文件或目录 String[]commands=new String[]{shell,
			 * titleId.replace(QUATATION, QUATATION_REPLACE)}; process =
			 * Runtime.getRuntime().exec(commands); process.waitFor();
			 ***/
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line = "";
			String outputResult = "";
			while ((line = input.readLine()) != null) {
				outputList.add(line);
				outputResult = outputResult + line + ";";
			}
			context.put("outputResult", outputResult);
			input.close();

		} catch (Exception e) {
			throw e;
		}
		return outputList;
	}

}