package com.BestBUY_BDD.test;

import org.apache.commons.io.FileUtils;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.mozilla.javascript.json.JsonParser;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.BestBUY_BDD.base.TestBase;
import com.BestBUY_BDD.util.TestUtil;
import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.apache.commons.io.FileUtils;
import static io.restassured.module.jsv.JsonSchemaValidator.*;
import io.restassured.internal.http.Status;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
		given().when().get(url).then().assertThat().statusCode(200).and().body("total", equalTo(51957));
	}

	@Test(dataProvider = "sort")
	public void SortProductByPrice(String sort, Float price) {
		given().queryParam("$sort[price]", sort).when().get(url).then().assertThat().statusCode(200).and()
				.body("data[0].price", is(price));
	}

	@Test
	public void ProductNameAndDescription() throws FileNotFoundException, IOException, ParseException {
		JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.newBuilder()
				.setValidationConfiguration(
						ValidationConfiguration.newBuilder().setDefaultVersion(SchemaVersion.DRAFTV4).freeze())
				.freeze();
		given().param("$select[]", "name").param("$select[]", "description").param("$limit", 10).when().get(url).then()
				.assertThat().statusCode(200).and().body("data[0]", hasKey("name")).and().assertThat()
				.body("data[0]", hasKey("description")).and().assertThat()
				.body(matchesJsonSchemaInClasspath("ProductNameAndDescription.json").using(jsonSchemaFactory)).and()
				.body("data.name", hasSize(10)).and().body("data.description", hasSize(10));

//		given().param("$select[]", "name").param("$select[]", "description").param("$limit", 10).when().get(url).then().
//		assertThat().statusCode(200).and().body(matchesJsonSchemaInClasspath("ProductNameAndDescription.json").using(jsonSchemaFactory));

	}
	@Test(dataProvider = "category")
	public void ProductCategory(String category, String qty) {
		given().param("category.name", category).when().get(url).then().assertThat().statusCode(200).and().
		body("data.categories.name[0]", hasItem(category)).and().body("total", equalTo(Integer.parseInt(qty)));
	}

	@DataProvider(name = "sort")
	public Object[][] createTestDataRecords_sort() {
		return new Object[][] { { "-1", 27999.98f }, { "1", 0.01f } };
	}
	
	@DataProvider(name = "category")
	public Object[][] createTestDataRecords_category() {
		return new Object[][] { {"Housewares","903"}, {"Household Batteries","206"},{"Connected Home & Housewares","4978"},
			{"4K Ultra HD TVs","119"},{"TVs","229"},{"TV & Home Theater","2076"},{"Alkaline Batteries","81"},
			{"Car Electronics & GPS","2646"},{"Car Audio","623"},{"Car Speakers","144"},{"Specialty Batteries","55"}	
		
		};
	}

	@AfterTest
	public void sendMail() throws IOException {
		testutl = new TestUtil();
		TestUtil.sendMail();
	}

}
