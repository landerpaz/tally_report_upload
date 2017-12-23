package hello;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Util {

	public static String getCurrentdate() {
		
		String str = null;
		try {
			
			//Date date = new java.sql.Date(new java.util.Date().getTime());
			
			//return date;
			
			LocalDateTime date = LocalDateTime.now();
			DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd-hh-mm-ss");
		    str = date.toString(fmt);
			//System.out.println(str);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return str;
				
	}
	
}
