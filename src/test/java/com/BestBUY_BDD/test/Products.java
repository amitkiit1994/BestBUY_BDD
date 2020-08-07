package com.BestBUY_BDD.test;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class Products {
	
	@Test
	public void getProducts() {
		given().
		when().get("http://localhost:3030/products").
		then().
		assertThat().statusCode(200).
		and().
		body("total", equalTo(51957));
	}

}
