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

public class GoRestTodosTest {
    int todosId;
    String todos_Id = "5767756";
    Faker faker=new Faker();
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
    public void getTodos() {

        given()
                .spec(reqSpec)

                .when()
                .get("todos")

                .then()
                //.log().body()
                .statusCode(200)
        ;
    }

    @Test(priority = 2)
    public void createUserTodos() {


        Map<String, String> newTodos = new HashMap<>();
        newTodos.put("title", faker.name().title());
        newTodos.put("due_on", "2023-11-30T00:00:00.000+05:30");
        newTodos.put("status", "completed");



        Response response =
                given()
                        .spec(reqSpec)
                        .body(newTodos)

                        .when()
                        .post("users/" + todos_Id + "/todos")

                        .then()
                        //.log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().response()
                ;
        todosId = response.path("id");

    }

    @Test(dependsOnMethods = "createUserTodos")
    public void getTodosByID() {
        given()
                .spec(reqSpec)

                .when()
                .get("todos/" + todosId)

                .then()
                .log().body()
                .statusCode(200)
        ;
    }

    @Test(dependsOnMethods = "getTodosByID")
    public void updateTodos() {

        Map<String, String> updateTodos = new HashMap<>();
        updateTodos.put("title", faker.programmingLanguage().name());


        given()
                .spec(reqSpec)
                .body(updateTodos)

                .when()
                .put("todos/" + todosId)

                .then()
                .statusCode(200)
                .log().body()
                .contentType(ContentType.JSON)
                .body("id",equalTo(todosId))
        ;
    }

    @Test(dependsOnMethods = "updateTodos")
    public void deleteTodos() {
        given()
                .spec(reqSpec)

                .when()
                .delete("todos/" + todosId)

                .then()
                .log().body()
                .statusCode(204)
        ;
    }

}
