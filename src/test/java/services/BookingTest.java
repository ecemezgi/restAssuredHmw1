package services;

import io.restassured.http.ContentType;
import io.restassured.http.Header;
import org.testng.annotations.*;

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



    @Test
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



    @Test(dataProvider = "tokenProvider")
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

    @AfterTest
    public void deleteBooking(){
        System.out.println("bookingid2" + bookingId);

        Header contentType = new Header("Content-Type","application/json/");


        given().log().all().header(contentType).cookie("token",token)
                .when().delete("https://restful-booker.herokuapp.com/booking/" + bookingId)
                .then().statusCode(201).log().all();


    }

}
