package util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import junit.framework.TestCase;

public class EncodeTest extends TestCase {

	public void test_encode_decode() {
		String str = "联想 YOGA平板3代 8英寸 平板电脑 (高通CPU 2G/16G )板岩黑 850 WIFI版";
		String result = "";
		try {
			result = URLEncoder.encode(str, "utf8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println(result);
	}

}
