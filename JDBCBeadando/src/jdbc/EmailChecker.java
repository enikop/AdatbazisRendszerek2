package jdbc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailChecker {
	private static String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
	
	public static boolean isValid(String s) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(s);
        return (matcher.matches() ? true  : false );
	}
}
