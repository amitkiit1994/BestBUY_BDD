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

	@Test(dataProvider = "filter")
	public void ProductCategoryFilter(String category, String lt, String gt, String qty) {
		given().param("category.name", category).param("price[$lt]", lt).param("price[$gt]", gt).param("shipping[$eq]","0").
		when().get(url).then().assertThat().statusCode(200).and().body("data.price[0]", lessThan(Float.parseFloat(lt))).
		and().body("data.price[0]", greaterThan(Float.parseFloat(gt))).and().body("total", equalTo(Integer.parseInt(qty)));
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
	
	
	@DataProvider(name = "filter")
	public Object[][] createTestDataRecords_filter() {
		return new Object[][] {
			{"Housewares","800","500","79"},
			{"Household Batteries","800","200","1"},
			{"Connected Home & Housewares","800","500","163"},
			{"4K Ultra HD TVs","800","500","20"},
			{"TVs","800","500","29"},
			{"TV & Home Theater","800","500","157"},
			{"Alkaline Batteries","10","5","30"},
			{"Car Electronics & GPS","800","500","55"},
			{"Car Audio","800","500","21"},
			{"Car Speakers","800","500","1"},
			{"Specialty Batteries","10","5","17"},
			{"Housewares","700","600","27"},
			{"Household Batteries","10","5","66"},
			{"Connected Home & Housewares","700","600","52"},
			{"4K Ultra HD TVs","700","600","4"},
			{"TVs","700","600","6"},
			{"TV & Home Theater","700","600","48"},
			{"Alkaline Batteries","10","5","30"},
			{"Car Electronics & GPS","700","600","19"},
			{"Car Audio","700","600","9"},
			{"Car Speakers","700","600","1"},
			{"Specialty Batteries","20","10","8"}
		
		};
		
	}

	@AfterTest
	public void sendMail() throws IOException {
		testutl = new TestUtil();
		TestUtil.sendMail();
	}

}
