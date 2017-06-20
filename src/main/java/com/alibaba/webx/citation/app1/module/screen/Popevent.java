/**
 * ICT NASC
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.alibaba.webx.citation.app1.module.screen;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.alibaba.citrus.turbine.Context;

/**
 * 
 * @author xueye.duanxy
 */
public class Popevent {

	/**
	 * 
	 * 
	 * @param request
	 * @param context
	 */
	public void execute(HttpServletRequest request, Context context) {
		String startTime = request.getParameter("startTime");
		Boolean emptyFlag = false;
		String sqlTime="";
		if (StringUtils.isBlank(startTime)) {
			startTime = "2017-05-10";//"2017-05-06 00:00:00";
			emptyFlag = true;
		}else{
			sqlTime= startTime.substring(0, 10);
		}
		 
		context.put("startTime", startTime);
		context.put("sqlTime", sqlTime);
		context.put("emptyFlag", emptyFlag);
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			System.out.print("Class Not Found Exception");
		}
		String url = "jdbc:mysql://10.61.2.147:3306/log_analysis";
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = (Connection) DriverManager.getConnection(url, "root", "123");
			stmt = (Statement) conn.createStatement();
			String sql = "";
			if (emptyFlag) {
				sql = "select truncation, ct from log_analysis_full.log_keywords limit 200;";
			} else {
				sql = "select truncation, ct from log_truncation_date t1 where t1.time ='" + sqlTime + "' order by ct desc limit 200;";
			}
			rs = stmt.executeQuery(sql);
		} catch (Exception e) {
			context.put("message", e.getMessage());
			return;
		}

		// return results
		try {
			List<PopEvent> rsList = new ArrayList<PopEvent>();
			rs.next();
			int rowIndex = rs.getRow();
			// System.out.println(rowIndex);
			int index=1;
			if (rowIndex == 1) {
				rs.previous();
				// System.out.println(rs.getRow());
				while (rs.next()) {
					PopEvent paper = new PopEvent();
					paper.setTitle(rs.getString("truncation"));
					paper.setCount(rs.getString("ct"));
					paper.setIndex(index);
					rsList.add(paper);
					index++;
				}
				context.put("resultSet", rsList);
				context.put("message", null);
				return;
			} else {
				context.put("message", "No match for input!");
				return;
			}
		} catch (Exception e) {
			context.put("message", e.getMessage());
		}
	}

	/**
	 * 
	 * 
	 * @author xueye.duanxy
	 */
	public class PopEvent {
		private String title;
		private String count;
		private int index;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getCount() {
			return count;
		}

		public void setCount(String count) {
			this.count = count;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

	}
}
