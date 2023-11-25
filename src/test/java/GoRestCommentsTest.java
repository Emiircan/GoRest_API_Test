import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class GoRestCommentsTest {

    int commentsId;
    String comments_Id = "85953";
    Faker faker = new Faker();
    RequestSpecification reqSpec;

    @BeforeClass
    public void setup() {
        baseURI = "https://gorest.co.in/public/v2/";

        reqSpec = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer d25bdf13239fc114534412d3f2688450e201c94e77ef3bc63e4592b54e86a353")
                .setContentType(ContentType.JSON)
                .setBaseUri(baseURI)
                .build();
    }

    @Test(priority = 1)
    public void getComments() {

        given()
                .spec(reqSpec)

                .when()
                .get("comments")

                .then()
                //.log().body()
                .statusCode(200)
        ;
    }

    @Test(priority = 2)
    public void createUserComments() {

        String rndName = faker.name().fullName();
        String rndEmail = faker.internet().emailAddress();
        String rndMessage = faker.lorem().sentence();

        Map<String, String> newComment = new HashMap<>();
        newComment.put("name", rndName);
        newComment.put("email", rndEmail);
        newComment.put("body", rndMessage);


        Response response =
                given()
                        .spec(reqSpec)
                        .body(newComment)

                        .when()
                        .post("posts/" + comments_Id + "/comments")

                        .then()
                        //.log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().response()
                ;
        commentsId = response.path("id");

    }

    @Test(dependsOnMethods = "createUserComments")
    public void getCommentByID() {
        given()
                .spec(reqSpec)

                .when()
                .get("comments/" + commentsId)

                .then()
                .log().body()
                .statusCode(200)
        ;
    }

    @Test(dependsOnMethods = "getCommentByID")
    public void updateComment() {

        Map<String, String> updateComment = new HashMap<>();
        updateComment.put("name", "Hi GoRest");


        given()
                .spec(reqSpec)
                .body(updateComment)

                .when()
                .put("comments/" + commentsId)

                .then()
                .statusCode(200)
                .log().body()
                .contentType(ContentType.JSON)
                .body("id",equalTo(commentsId))
        ;
    }

    @Test(dependsOnMethods = "updateComment")
    public void deleteComment() {
        given()
                .spec(reqSpec)

                .when()
                .delete("comments/" + commentsId)

                .then()
                .log().body()
                .statusCode(204)
        ;
    }

}
