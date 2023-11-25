import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import netscape.security.UserDialogHelper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class GoRestUserTest {
    Faker faker = new Faker();
    int userId;


    RequestSpecification reqSpec;

    @BeforeClass
    public void setup() {
        baseURI = "https://gorest.co.in/public/v2/users";

        reqSpec = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer d25bdf13239fc114534412d3f2688450e201c94e77ef3bc63e4592b54e86a353")
                .setContentType(ContentType.JSON)
                .setBaseUri(baseURI)
                .build();
    }

    @Test
    public void createUserClass() {
        String rndFullName = faker.name().fullName();
        String rndEmail = faker.internet().emailAddress();

        Map<String, String> newUser = new HashMap<>();
        newUser.put("name", rndFullName);
        newUser.put("email", rndEmail);
        newUser.put("gender", "male");
        newUser.put("status", "active");

        Response response =
                given()
                        .spec(reqSpec)
                        .body(newUser)

                        .when()
                        .post("")

                        .then()
                        //.log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().response()
                ;

        userId = response.path("id");

    }

    @Test(dependsOnMethods = "createUserClass")
    public void getUserById() {
        given()
                .spec(reqSpec)

                .when()
                .get("" + userId)

                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(userId))
        ;
    }

    @Test(dependsOnMethods = "getUserById")
    public void updateUser() {
        String rndFullName = faker.name().fullName();

        Map<String, String> updateUser = new HashMap<>();
        updateUser.put("name", rndFullName);

        given()
                .spec(reqSpec)
                .body(updateUser)

                .when()
                .put("" + userId)

                .then()
                .log().body()
                .statusCode(200)
                .body("id", equalTo(userId))
                .body("name", equalTo(rndFullName))
        ;

    }

    @Test(dependsOnMethods = "updateUser")
    public void deleteUser() {
        given()
                .spec(reqSpec)

                .when()
                .delete("" + userId)

                .then()
                .log().body()
                .statusCode(204)
        ;
    }

}
