package com.BestBUY_BDD.test;

import org.apache.commons.io.FileUtils;

import org.json.simple.parser.ParseException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


import com.BestBUY_BDD.base.TestBase;
import com.BestBUY_BDD.util.TestUtil;
import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;



import static io.restassured.module.jsv.JsonSchemaValidator.*;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;


import java.io.File;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.util.ArrayList;


public class Products extends TestBase {

	TestBase testbase;
	String url;
	String serviceUrl;
	TestUtil testutl;
	int productCount=52070;
	int productId;
	String productName;
	String productType;
	String productUpc;
	Float productPrice;
	String productModel;
	String productDesc;
	
	ArrayList<Integer> productIds= new ArrayList<Integer>();
	ArrayList<String> productNames= new ArrayList<String>();
	ArrayList<String> productUpcs= new ArrayList<String>();
	ArrayList<String> productModels= new ArrayList<String>();
	ArrayList<String> productDescs= new ArrayList<String>();
	ArrayList<String> productTypes= new ArrayList<String>();
	ArrayList<Float> productPrices= new ArrayList<Float>();
	
	@BeforeMethod
	public void setUp() throws URISyntaxException {
		testbase = new TestBase();
		url = prop.getProperty("URL");
		serviceUrl = prop.getProperty("Product_Service");
		url = url + serviceUrl;
//		System.out.println(url);
	}

	@Test(dependsOnGroups = {"single product","multiple product"})
	public void ProductsCount() {
		given().when().get(url).then().assertThat().statusCode(200).and().body("total", equalTo(productCount));
		System.out.println("Products Count");
	}

	@Test(dataProvider = "sort")
	public void SortProductByPrice(String sort, Float price) {
		given().queryParam("$sort[price]", sort).when().get(url).then().assertThat().statusCode(200).and()
				.body("data[0].price", is(price));
		System.out.println("Sort By Price");
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
		System.out.println("Product Name and desc");
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
		System.out.println("Category filter");
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
	
	@Test(groups = {"single product"})
	public void createProduct() throws IOException {
		String benchMark=FileUtils.readFileToString(new File(System.getProperty("user.dir")+"\\src\\test\\java\\com\\BestBUY_BDD\\inputFiles\\createProduct_POST.json"), "UTF-8");
		JsonPath jsonPath= new JsonPath(benchMark);
		productName=jsonPath.getString("name");
		productType=jsonPath.getString("type");
		productUpc=jsonPath.getString("upc");
		productPrice=jsonPath.getFloat("price");
		productModel=jsonPath.getString("model");
		productDesc=jsonPath.getString("description");
		
		Response response=given().contentType("application/json").body(benchMark).when().post(url);
		assertEquals(response.statusCode(),201);

		assertEquals(productName,response.jsonPath().get("name"));
		assertEquals(productType,response.jsonPath().get("type"));
		assertEquals(productUpc,response.jsonPath().get("upc"));
		assertEquals(productPrice,response.jsonPath().get("price"));
		assertEquals(productModel,response.jsonPath().get("model"));
		assertEquals(productDesc,response.jsonPath().get("description"));
		productId=response.jsonPath().get("id");
		System.out.println("Create Single Product");
		
	}
	
	@Test(dependsOnMethods = {"createProduct"},groups = {"single product"})
	public void getProductById() {
		given().pathParam("productId", productId).when().get(url+"/{productId}").then().assertThat().
		statusCode(200).and().body("name", equalTo(productName)).and().body("type", equalTo(productType)).
		and().body("upc", equalTo(productUpc)).and().body("price", equalTo(productPrice)).
		and().body("model", equalTo(productModel)).and().body("description", equalTo(productDesc));
		System.out.println("Get single product");
	}
	
	@Test(dependsOnMethods = {"getProductById"},groups = {"single product"})
	public void updateProduct() throws IOException
	{
		String benchMark=FileUtils.readFileToString(new File(System.getProperty("user.dir")+"\\src\\test\\java\\com\\BestBUY_BDD\\inputFiles\\patchProduct_POST.json"), "UTF-8");
		JsonPath jsonPath= new JsonPath(benchMark);
		productName=jsonPath.getString("name");
		productType=jsonPath.getString("type");
		productUpc=jsonPath.getString("upc");
		productPrice=jsonPath.getFloat("price");
		productModel=jsonPath.getString("model");
		productDesc=jsonPath.getString("description");
		
		Response response=given().pathParam("productId", productId).contentType("application/json").body(benchMark).when().patch(url+"/{productId}");
		assertEquals(response.statusCode(),200);

		assertEquals(productName,response.jsonPath().get("name"));
		assertEquals(productType,response.jsonPath().get("type"));
		assertEquals(productUpc,response.jsonPath().get("upc"));
		assertEquals(productPrice,response.jsonPath().get("price"));
		assertEquals(productModel,response.jsonPath().get("model"));
		assertEquals(productDesc,response.jsonPath().get("description"));
		System.out.println("Update single product");
	}
	
	@Test(dependsOnMethods = {"updateProduct"},groups = {"single product"})
	public void deleteProductById() {
		given().pathParam("productId", productId).when().delete(url+"/{productId}").then().assertThat().
		statusCode(200);
		System.out.println("Delete single product");
	}
	
	@Test(dependsOnGroups = {"single product"}, dataProvider = "product",groups = {"multiple product"})
	public void createMultipleProducts(String name, String type, Float price, String model, String upc, String desc) {
		Response response=given().body("{"
		        + "\"name\": \""+name+"\","
		        + "\"type\": \""+type+"\","
		        + "\"upc\": \""+upc+"\","
		        + "\"price\": "+price+","
		        + "\"description\": \""+desc+"\","
		        + "\"model\": \""+model+"\""
		        + "}"
		    ).contentType("application/json").
		when().post(url);
		assertEquals(201,response.statusCode());
		
		productName=name;
		productType=type;
		productUpc=upc;
		productPrice=price;
		productModel=model;
		productDesc=desc;
		assertEquals(productName,response.jsonPath().get("name"));
		assertEquals(productType,response.jsonPath().get("type"));
		assertEquals(productUpc,response.jsonPath().get("upc"));
		assertEquals(productPrice,response.jsonPath().get("price"));
		assertEquals(productModel,response.jsonPath().get("model"));
		assertEquals(productDesc,response.jsonPath().get("description"));
		productId=response.jsonPath().get("id");
		System.out.println("Product Id: "+productId);
		System.out.println("Creating multiple product id: "+productId);
		
		productIds.add(productId);
		productNames.add(productName);
		productTypes.add(productType);
		productUpcs.add(productUpc);
		productPrices.add(productPrice);
		productModels.add(productModel);
		productDescs.add(productDesc);
//		given().pathParam("productId", productId).when().get(url+"/{productId}").then().assertThat().
//		statusCode(200).and().body("name", equalTo(productName)).and().body("type", equalTo(productType)).
//		and().body("upc", equalTo(productUpc)).and().body("price", equalTo(productPrice)).
//		and().body("model", equalTo(productModel)).and().body("description", equalTo(productDesc));
		
//		given().pathParam("productId", productId).when().delete(url+"/{productId}").then().assertThat().
//		statusCode(200);
		
		
	}
	
	@Test (dependsOnMethods = {"createMultipleProducts"},dataProvider = "productInfo",groups = {"multiple product"})
	public void getMultipleProductsById(int productIds, String productNames, String productTypes, Float productPrices, String productModels, String productUpcs, String productDescs) {
			given().pathParam("productId", productIds).when().get(url+"/{productId}").then().assertThat().
			statusCode(200).and().body("name", equalTo(productNames)).and().body("type", equalTo(productTypes)).
			and().body("upc", equalTo(productUpcs)).and().body("price", equalTo(productPrices)).
			and().body("model", equalTo(productModels)).and().body("description", equalTo(productDescs));
			
			System.out.println("Get Multiple Products ID: "+productIds);
			
		
	}
	
	@Test (dependsOnMethods = {"getMultipleProductsById"},dataProvider = "productInfo",groups = {"multiple product"})
	public void updateMultipleProducts(int productIds, String productNames, String productTypes, Float productPrices, String productModels, String productUpcs, String productDescs) {
		Response response=given().pathParam("productId", productIds).body("{"
		        + "\"name\": \""+productNames+"\","
		        + "\"type\": \""+productTypes+"\","
		        + "\"upc\": \""+productUpcs+"\","
		        + "\"price\": "+productPrices+","
		        + "\"description\": \""+productDescs+"\","
		        + "\"model\": \""+productModels+"\""
		        + "}"
		    ).contentType("application/json").
		when().patch(url+"/{productId}");
		assertEquals(200,response.statusCode());
		
		assertEquals(productNames,response.jsonPath().get("name"));
		assertEquals(productTypes,response.jsonPath().get("type"));
		assertEquals(productUpcs,response.jsonPath().get("upc"));
		assertEquals(productPrices,response.jsonPath().get("price"));
		assertEquals(productModels,response.jsonPath().get("model"));
		assertEquals(productDescs,response.jsonPath().get("description"));
		
		System.out.println("Patch product id: "+productIds);
	}
	
	@Test (dependsOnMethods = {"updateMultipleProducts"},dataProvider = "productIds",groups = {"multiple product"})
	public void deleteMultipleProducts(int prodId) {
			given().pathParam("productId", prodId).when().delete(url+"/{productId}").then().assertThat().
			statusCode(200);
			System.out.println("Deleting ProdID: "+prodId);
		
	}
	
	@DataProvider(name="productIds")
	public Object[][] getAllProdIds()
	{
		return new Object[][] {
			{productIds.get(0)},
			{productIds.get(1)},
			{productIds.get(2)},
			{productIds.get(3)},
			{productIds.get(4)},
			{productIds.get(5)},
			{productIds.get(6)},
			{productIds.get(7)},
			{productIds.get(8)},
			{productIds.get(9)},
			{productIds.get(10)}
		};
	}
	
	
	@DataProvider(name="productInfo")
	public Object[][] getAllProdInfo(){
		return new Object[][] {
			{productIds.get(0),productNames.get(0),productTypes.get(0),productPrices.get(0),productModels.get(0),productUpcs.get(0),productDescs.get(0)},
			{productIds.get(1),productNames.get(1),productTypes.get(1),productPrices.get(1),productModels.get(1),productUpcs.get(1),productDescs.get(1)},
			{productIds.get(2),productNames.get(2),productTypes.get(2),productPrices.get(2),productModels.get(2),productUpcs.get(2),productDescs.get(2)},
			{productIds.get(3),productNames.get(3),productTypes.get(3),productPrices.get(3),productModels.get(3),productUpcs.get(3),productDescs.get(3)},
			{productIds.get(4),productNames.get(4),productTypes.get(4),productPrices.get(4),productModels.get(4),productUpcs.get(4),productDescs.get(4)},
			{productIds.get(5),productNames.get(5),productTypes.get(5),productPrices.get(5),productModels.get(5),productUpcs.get(5),productDescs.get(5)},
			{productIds.get(6),productNames.get(6),productTypes.get(6),productPrices.get(6),productModels.get(6),productUpcs.get(6),productDescs.get(6)},
			{productIds.get(7),productNames.get(7),productTypes.get(7),productPrices.get(7),productModels.get(7),productUpcs.get(7),productDescs.get(7)},
			{productIds.get(8),productNames.get(8),productTypes.get(8),productPrices.get(8),productModels.get(8),productUpcs.get(8),productDescs.get(8)},
			{productIds.get(9),productNames.get(9),productTypes.get(9),productPrices.get(9),productModels.get(9),productUpcs.get(9),productDescs.get(9)},
			{productIds.get(10),productNames.get(10),productTypes.get(10),productPrices.get(10),productModels.get(10),productUpcs.get(10),productDescs.get(10)},
		};
		}
	
	
	

	@DataProvider(name = "product")
	public Object[][] createTestDataRecords_product() {
		return new Object[][] {
			{"Prod1","type1",123.12f,"model1","upc1","desc1"},
			{"Prod2","type2",12.12f,"model2","upc2","desc2"},
			{"Prod3","type3",125.12f,"model3","upc3","desc3"},
			{"Prod4","type4",126.12f,"model4","upc4","desc4"},
			{"Prod5","type5",127.12f,"model5","upc5","desc5"},
			{"Prod6","type6",128.12f,"model6","upc6","desc6"},
			{"Prod7","type7",129.12f,"model7","upc7","desc7"},
			{"Prod8","type8",121.12f,"model8","upc8","desc8"},
			{"Prod9","type9",121.12f,"model9","upc9","desc9"},
			{"Prod10","type10",12.12f,"model10","upc10","desc10"},
			{"Prod11","type11",121.12f,"model11","upc11","desc11"}
		};
		}
	@AfterTest
	public void sendMail() throws IOException {
		System.out.println("Total Number of products Created: "+productIds.size());
		System.out.println("Created: "+productIds);
//		testutl = new TestUtil();
//		TestUtil.sendMail();
	}

}
