package services;

import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.internal.RequestSpecificationImpl;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class BookingTest {

    String bookingId ;
    String token;


    @BeforeTest
    @DataProvider(name = "tokenProvider")
    public Object[][] postCreateToken(){
        Header contentType = new Header("Content-Type","application/json");

        String postData = "{\n" +
                "    \"username\" : \"admin\",\n" +
                "    \"password\" : \"password123\"\n" +
                "}";

        String token = given().log().all().header(contentType)
                .body(postData)
                .contentType(ContentType.JSON)
                .log().all()
                .when().post("https://restful-booker.herokuapp.com/auth")
                .then().statusCode(200).log().all().extract().path("token").toString();

        System.out.println("token : " + token);
        this.token = token;

        return new Object[][]{
                {token}
        };


    }



    @Test(priority = 1)
    public void postCreateBooking(){

        Header contentType = new Header("Content-Type","application/json");
        String postData = "{\n" +
                "    \"firstname\" : \"Jane\",\n" +
                "    \"lastname\" : \"Doe\",\n" +
                "    \"totalprice\" : 111,\n" +
                "    \"depositpaid\" : true,\n" +
                "    \"bookingdates\" : {\n" +
                "        \"checkin\" : \"2018-01-01\",\n" +
                "        \"checkout\" : \"2019-01-01\"\n" +
                "    },\n" +
                "    \"additionalneeds\" : \"Breakfast\"\n" +
                "}";

        bookingId = given().log().all().header(contentType)
                .body(postData)
                .contentType(ContentType.JSON)
                .log().all()
                .when().post("https://restful-booker.herokuapp.com/booking")
                .then().statusCode(200).log().all().extract().path("bookingid").toString();

        System.out.println("bookingid" + bookingId);


    }



    @Test(dataProvider = "tokenProvider",priority = 2)
    public void putUpdateBooking(String token){
        System.out.println("bookingid2" + bookingId);

        Header contentType = new Header("Content-Type","application/json/");


        String postData = "{\n" +
                "    \"firstname\" : \"James\",\n" +
                "    \"lastname\" : \"Brown\",\n" +
                "    \"totalprice\" : 111,\n" +
                "    \"depositpaid\" : true,\n" +
                "    \"bookingdates\" : {\n" +
                "        \"checkin\" : \"2018-01-01\",\n" +
                "        \"checkout\" : \"2019-01-01\"\n" +
                "    },\n" +
                "    \"additionalneeds\" : \"Breakfast\"\n" +
                "}";

        given().log().all().header(contentType).cookie("token",token)
                .body(postData)
                .contentType(ContentType.JSON)
                .log().all()
                .when().put("https://restful-booker.herokuapp.com/booking/" + bookingId)
                .then().statusCode(200).log().all();

    }

    @Test(priority = 3)
    public void getBookingDetail(){

        String baseUrl="https://restful-booker.herokuapp.com/booking/" + bookingId;


        given().log().all()
                .when().get(baseUrl)
                .then().statusCode(200).log().all();

        RequestSpecification requestSpecification = RestAssured.given()
                .header("getBookingDetail","Test")
                .log().all();

        Response response = requestSpecification.get(baseUrl);
        attachment(requestSpecification,baseUrl,response);
        Assert.assertEquals(response.getStatusCode(),200);


    }




    @AfterTest
    public void deleteBooking(){
        System.out.println("bookingid2" + bookingId);

        Header contentType = new Header("Content-Type","application/json/");


        given().log().all().header(contentType).cookie("token",token)
                .when().delete("https://restful-booker.herokuapp.com/booking/" + bookingId)
                .then().statusCode(201).log().all();


    }


    public String attachment(RequestSpecification httpRequest, String baseUrl, Response response){

        String html = "Url = " + baseUrl + "\n\n" +
                "request header ="  +((RequestSpecificationImpl) httpRequest).getHeaders() + "\n\n" +
                "request body =" +((RequestSpecificationImpl) httpRequest).getBody() + "\n\n" +
                "response body =" + response.getBody().asString();

        Allure.addAttachment("request detail",html);
        return html;


    }

}
