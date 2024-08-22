package tests;

import authorisation.AuthorizationByAPI;
import helpers.WithLogin;
import io.restassured.response.Response;
import models.BookDataModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static specs.DeleteAllBooksSpec.*;


public class DeleteBookTest extends TestBase {
    private final Response authResponse = new AuthorizationByAPI().getAuthorizationResponse();


    @Test
    @WithLogin
    @DisplayName("Проверка успешного удаления книг")
    @Tag("smoke")
    void successfulDeleteBookTest() {

        step("Удалить все книги", () -> given(deleteAllBooksRequestSpec)
                .header("Authorization", "Bearer " + authResponse.path("token"))
                .queryParams("UserId", authResponse.path("userId"))
                .when()
                .delete("BookStore/v1/Books")
                .then()
                .spec(deleteAllBooksResponseSpec204));
    }


    @Test
    @WithLogin
    @DisplayName("Проверить, что невозможно удалить книги неавторизированному пользователю. Сообщение \"User not authorized!\"")
    @Tag("smoke")
    void negative401DeleteBookNotAuthTest() {

        step("Удалить все книги", () -> given(deleteAllBooksRequestSpec)
                // .header("Authorization", "Bearer " + authResponse.path("token"))
                .queryParams("UserId", authResponse.path("userId"))
                .when()
                .delete("BookStore/v1/Books")
                .then()
                .spec(deleteAllBooksNotAuthResponseSpec401))
                .body("code", is("1200"))
                .body("message", is("User not authorized!"));
    }

    @Test
    @WithLogin
    @DisplayName("Проверить, что невозможно удалить книги c неверным isbn")
    @Tag("regression")
    void negative400DeleteBookTest() {

        String isbn = "9781449325862";

        List<BookDataModel.IsbnData> books = List.of(new BookDataModel.IsbnData("NULL"));

        BookDataModel bookData = new BookDataModel(authResponse.path("userId"),books);


        step("Удалить все книги", () -> given(deleteAllBooksRequestSpec)
                .header("Authorization", "Bearer " + authResponse.path("token"))
                .queryParams("UserId", authResponse.path("userId"))
                .when()
                .delete("BookStore/v1/Books")
                .then()
                .spec(deleteAllBooksNotBookResponseSpec400))
                ;
    }

}