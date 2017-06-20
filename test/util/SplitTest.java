package util;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class SplitTest extends TestCase {

	public void test_split(){
		List<List<String>> strList = new ArrayList<List<String>>();
		String line ="王珞丹和白百合对比照|||93";
		List<String> content = new ArrayList<String>();
		String[] lineOb = line.split("\\|\\|\\|");
		content.add(lineOb[0]);
		content.add(lineOb[1]);
		if (lineOb.length > 2) {
			content.add(lineOb[2]);
		}
		strList.add(content);
	}
}
