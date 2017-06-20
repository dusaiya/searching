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

import com.alibaba.citrus.turbine.Context;

/**
 * 
 * @author xueye.duanxy
 * @version $Id: ViewShare.java, v 0.1 2016-1-21 4:58:19 Exp $
 */
public class Topicstart {

	/***/
	private static final String DETAIL_QUERY = "python /home/project242/topic_fetch.py ";
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
			if (StringUtils.isBlank(title) || StringUtils.isBlank(startTime)) {
				context.put("emptyFlag", 1);
				return;
			} else {
				context.put("emptyFlag", 0);
			}
			String sqlTime = "";
			sqlTime = startTime.substring(0, 10);
			startTime = sqlTime + " 00:00:00";
			context.put("title", title);
			context.put("startTime", startTime);
			context.put("sqlTime", sqlTime);
			List<List<String>> urlList = new ArrayList<List<String>>();
			//
			String args = title;// + PY_SPLIT + startTime;
			try {
				args = URLEncoder.encode(args, "utf8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			List<String> outputList = getShOutStr(args, DETAIL_QUERY + SPACE, context);

			int typeCt = 0; // 轮数计数
			// StringList Data List
			for (String line : outputList) {
				if (line.startsWith("$")) {
					typeCt++;
				} else {
					switch (typeCt) {
					case 1:
						addNewLine(urlList, line);
						break;
					default:
						break;
					}
				}
			}

			// putGroup(groupList, context);
			context.put("urlList", urlList);
			context.put("message", "完整流程跑完，未溢出");
		} catch (Exception e) {
			context.put("message", e.getMessage());
		}
	}

	private void addNewLine(List<List<String>> strList, String line) {
		List<String> content = new ArrayList<String>();
		String[] lineOb = line.split("\\|\\|\\|");
		content.add(lineOb[0]);
		content.add(lineOb[1]);
		if (lineOb.length > 2) {
			content.add(lineOb[2]);
		}
		strList.add(content);

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
					if (!line.startsWith("$")) {
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