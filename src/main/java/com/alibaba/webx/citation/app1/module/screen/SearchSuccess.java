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

import com.alibaba.citrus.turbine.Context;

/**
 * 
 * @author xueye.duanxy
 */
public class SearchSuccess {

    /**
     * 
     * 
     * @param request
     * @param context
     */
    public void execute(HttpServletRequest request,  Context context) {
        String titleStr = request.getParameter("title");

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            System.out.print("Class Not Found Exception");
        }
        String url = "jdbc:mysql://10.61.2.147:3306/wos";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = (Connection) DriverManager.getConnection(url, "shen", "123456");
            stmt = (Statement) conn.createStatement();
            String sql = "select title,year,journal,wos_id from papers where match(title) against ("
                         + "\"" + titleStr + "\"" + ") limit 10";
            //System.out.println(sql);
            rs = stmt.executeQuery(sql);
        } catch (Exception e) {
            context.put("message", e.getMessage());
            return;
        }

        //return results
        try {
            List<Paper> rsList = new ArrayList<Paper>();
            rs.next();
            int rowIndex = rs.getRow();
            //System.out.println(rowIndex);
            if (rowIndex == 1) {
                rs.previous();
                //System.out.println(rs.getRow());
                while (rs.next()) {
                    Paper paper = new Paper();
                    paper.setTitle(rs.getString("title").toUpperCase());
                    paper.setYear(rs.getString("year").toUpperCase());
                    paper.setJournal(rs.getString("journal").toUpperCase());
                    paper.setWosId(rs.getString("wos_id").toUpperCase());
                    rsList.add(paper);
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
    public class Paper {
        private String title;
        private String year;
        private String journal;
        private String wosId;

        /**
         * Getter method for property <tt>title</tt>.
         * 
         * @return property value of title
         */
        public String getTitle() {
            return title;
        }

        /**
         * Getter method for property <tt>year</tt>.
         * 
         * @return property value of year
         */
        public String getYear() {
            return year;
        }

        /**
         * Getter method for property <tt>journal</tt>.
         * 
         * @return property value of journal
         */
        public String getJournal() {
            return journal;
        }

        /**
         * Getter method for property <tt>wosId</tt>.
         * 
         * @return property value of wosId
         */
        public String getWosId() {
            return wosId;
        }

        /**
         * Setter method for property <tt>title</tt>.
         * 
         * @param title value to be assigned to property title
         */
        public void setTitle(String title) {
            this.title = title;
        }

        /**
         * Setter method for property <tt>year</tt>.
         * 
         * @param year value to be assigned to property year
         */
        public void setYear(String year) {
            this.year = year;
        }

        /**
         * Setter method for property <tt>journal</tt>.
         * 
         * @param journal value to be assigned to property journal
         */
        public void setJournal(String journal) {
            this.journal = journal;
        }

        /**
         * Setter method for property <tt>wosId</tt>.
         * 
         * @param wosId value to be assigned to property wosId
         */
        public void setWosId(String wosId) {
            this.wosId = wosId;
        }

    }
}
