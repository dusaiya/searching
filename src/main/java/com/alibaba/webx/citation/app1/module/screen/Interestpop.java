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

/**
 * 
 * @author xueye.duanxy
 * @version $Id: ViewShare.java, v 0.1 2016-1-21 4:58:19 Exp $
 */
public class Interestpop {

	/***/
	private static final String DETAIL_QUERY = "python /home/project242/topic061914.py ";
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
			String str=request.getParameter("str");
			if (StringUtils.isBlank(title) || StringUtils.isBlank(startTime)||StringUtils.isBlank(str)) {
				context.put("emptyFlag", 1);
				return;
			}else{
				context.put("emptyFlag", 0);
			}
			String sqlTime = "";
			sqlTime = startTime.substring(0, 10);
			startTime = sqlTime + " 00:00:00";
			context.put("title", title);
			context.put("startTime", startTime);
			context.put("sqlTime", sqlTime);
			List<List<String>> timeList = new ArrayList<List<String>>();
			List<List<String>> area1List = new ArrayList<List<String>>();
			List<List<String>> area2List = new ArrayList<List<String>>();
			List<List<String>> area3List = new ArrayList<List<String>>();
			List<List<String>> area4List = new ArrayList<List<String>>();
			List<List<String>> area5List = new ArrayList<List<String>>();
			List<List<String>> areaAllList = new ArrayList<List<String>>();
			List<List<String>> groupList = new ArrayList<List<String>>();
			List<List<String>> urlList = new ArrayList<List<String>>();
			//
			String args = str + PY_SPLIT + startTime;
			try {
				args = URLEncoder.encode(args, "utf8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			List<String> outputList = getShOutStr(args, DETAIL_QUERY + SPACE, context);

			int typeCt = 0; // 轮数计数
			int timeRoundCt = 0;
			int timeLintCt = 0;
			// StringList Data List
			for (String line : outputList) {
				if (line.startsWith("#")) {
					typeCt++;
					timeRoundCt = 0;
				} else {
					switch (typeCt) {
					case 1:
						if (line.startsWith("$")) {
							timeRoundCt += 1;
							timeLintCt = 0;
							continue;
						} else {
							add5NewLine(timeList, line, timeRoundCt, timeLintCt);
							timeLintCt++;
						}
						break;
					case 2:
						if (line.startsWith("$")) {
							timeRoundCt += 1;
							continue;
						} else {
							switch (timeRoundCt) {
							case 1:
								add4NewLine(area1List, line);
								break;
							case 2:
								add4NewLine(area2List, line);
								break;
							case 3:
								add4NewLine(area3List, line);
								break;
							case 4:
								add4NewLine(area4List, line);
								break;
							case 5:
								add4NewLine(area5List, line);
								break;
							case 6:
								add4NewLine(areaAllList, line);
								break;
							default:
								break;
							}
						}
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

			putTime(timeList, context);
			putMap(area1List, context, "map1List");
			putMap(area2List, context, "map2List");
			putMap(area3List, context, "map3List");
			putMap(area4List, context, "map4List");
			putMap(area5List, context, "map5List");
			putMapMax(areaAllList, context);
			// putGroup(groupList, context);
			context.put("groupList", groupList);
			context.put("urlList", urlList);
			context.put("message", "完整流程跑完，未溢出");
		} catch (Exception e) {
			context.put("message", e.getMessage());
		}
	}

	private void putMapMax(List<List<String>> areaAllList, Context context) {
		int maxCt = 0;
		for (List<String> strLine : areaAllList) {
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

		// TODO Auto-generated method stub

	}

	private void add4NewLine(List<List<String>> strList, String line) {
		List<String> content = new ArrayList<String>();
		String[] lineOb = line.split("\t");
		content.add(lineOb[0]);
		content.add(lineOb[1]);
		if (lineOb.length > 2) {
			content.add(lineOb[2]);
		}
		strList.add(content);

	}

	private void add5NewLine(List<List<String>> strList, String line, int timeCt, int timeLintCt) {
		if (1 == timeCt) {
			List<String> content = new ArrayList<String>();
			String[] lineOb = line.split("\t");
			content.add(lineOb[0]);
			content.add(lineOb[1]);
			strList.add(content);
		} else {
			List<String> content = strList.get(timeLintCt);
			String[] lineOb = line.split("\t");
			content.add(lineOb[1]);
		}

	}

	private void addNewLine(List<List<String>> strList, String line) {
		List<String> content = new ArrayList<String>();
		String[] lineOb = line.split("\t");
		content.add(lineOb[0]);
		content.add(lineOb[1]);
		if (lineOb.length > 2) {
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
				ob.put("data1", timeLine.get(1));
				ob.put("data2", timeLine.get(2));
				ob.put("data3", timeLine.get(3));
				ob.put("data4", timeLine.get(4));
				ob.put("data5", timeLine.get(5));
				ob.put("data6", timeLine.get(6));
				linedata.add(ob);
			} catch (JSONException e) {
				context.put("Message", e.getMessage());
			}

		}
		JSONArray dataList = new JSONArray(linedata);
		context.put("timeList", dataList);

	}

	/**
	 * 
	 * @param line
	 */
	private void putMap(List<List<String>> areaList, Context context, String mapKey) {
		JSONArray dataList = contextJson(areaList, context);
		context.put(mapKey, dataList);
	}

	private void putGroup(List<List<String>> groupList, Context context) {
		JSONArray dataList = contextJson(groupList, context);
		context.put("groupList", dataList);
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
			Boolean startFlag = false;
			while ((line = input.readLine()) != null) {
				if (!startFlag) {
					if (!line.startsWith("#")) {
						continue;
					} else {
						startFlag = true;
					}
				}
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