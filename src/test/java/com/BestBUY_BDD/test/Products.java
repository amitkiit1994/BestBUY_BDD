package com.BestBUY_BDD.test;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.BestBUY_BDD.base.TestBase;
import com.BestBUY_BDD.util.TestUtil;

import io.restassured.internal.http.Status;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;

public class Products extends TestBase {

	TestBase testbase;
	String url;
	String serviceUrl;
	TestUtil testutl;
	@BeforeMethod
	public void setUp() throws URISyntaxException {
		testbase = new TestBase();
		url = prop.getProperty("URL");
		serviceUrl = prop.getProperty("Product_Service");
		url = url + serviceUrl;
//		System.out.println(url);
	}
	@Test
	public void ProductsCount() {
		given().
		when().get(url).
		then().
		assertThat().statusCode(200).
		and().
		body("total", equalTo(51957));
	}
	
	@Test(dataProvider = "sort")
	public void SortProductByPrice(String sort,Float price) {
		given().queryParam("$sort[price]", sort).when().get(url).then().
		assertThat().statusCode(200).and().body("data[0].price", is(price));
	}
	
	@DataProvider(name="sort")
	public Object[][] createTestDataRecords() {
	    return new Object[][] {
	        {"-1",27999.98f},
	        {"1",0.01f}
	    };
	}
	@AfterTest
	public void sendMail() throws IOException {
		testutl=new TestUtil();
		TestUtil.sendMail();
	}

}
