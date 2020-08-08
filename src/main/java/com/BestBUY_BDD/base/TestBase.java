package com.BestBUY_BDD.base;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Hello world!
 *
 */
public class TestBase

{
	public static Properties prop;

	public TestBase() {
		// TODO Auto-generated constructor stub
		try {
			prop = new Properties();
			String propertiesFilePath = System.getProperty("user.dir")
					+ "\\src\\main\\java\\com\\BestBUY_BDD\\resources\\config.properties";
			FileInputStream fis = new FileInputStream(propertiesFilePath);
			;
			prop.load(fis);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

}
