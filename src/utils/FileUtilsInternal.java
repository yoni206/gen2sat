package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;


public class FileUtilsInternal {
	public static String getFileAsString(String absolutePath) throws IOException {
		File f = new File(absolutePath);
		String str = FileUtils.readFileToString(f);
//		InputStream in = FileUtilsInternal.class.getResourceAsStream(relativePath);
//		String str = fromStream(in);
		return str;
	}	
	
	public static String fromStream(InputStream in) throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder out = new StringBuilder();
		String newLine = System.getProperty("line.separator");
		String line;
		while ((line = reader.readLine()) != null) {
			out.append(line);
			out.append(newLine);
		}
		return out.toString();
	}

	
	public static void main(String[] args) throws IOException {
		String path = "/predefined.txt";
		String content = getFileAsString(path);
		System.out.println(content);
	}
}
