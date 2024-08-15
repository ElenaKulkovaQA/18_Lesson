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
import static specs.AddBookSpec.*;
import static specs.DeleteAllBooksSpec.deleteAllBooksRequestSpec;
import static specs.DeleteAllBooksSpec.deleteAllBooksResponseSpec204;

@Tag("regression")
@Tag("smoke")
public class AddedBookTest extends TestBase {
    private final Response authResponse = new AuthorizationByAPI().getAuthorizationResponse();


    @Test
    @WithLogin
    @DisplayName("Проверить успешное добавление книг")
    void successfulAddedBookTest() {

        String isbn = "9781449325862";
        List<String> books = List.of(isbn);
        BookDataModel bookData = new BookDataModel(authResponse.path("userId"), books);
        step("Добавить книги в профиль. Успех 201", () -> given(addBookRequestSpec)
                .header("Authorization", "Bearer " + authResponse.path("token"))
                .body(bookData)
                .when()
                .post("BookStore/v1/Books")
                .then()
                .spec(addBookSuccessfulResponseSpec201))
        ;
    }

    @Test
    @WithLogin
    @DisplayName("Проверить, что при вводе неправильного ISBN появляется ошибка")
    void negative400AddBookToCollectionTest() {

        step("Удалить все книги", () -> given(deleteAllBooksRequestSpec)
                .header("Authorization", "Bearer " + authResponse.path("token"))
                .queryParams("UserId", authResponse.path("userId"))
                .when()
                .delete("BookStore/v1/Books")
                .then()
                .spec(deleteAllBooksResponseSpec204));

        String isbn = "9781593275846";
        List<String> books = List.of(isbn);
        BookDataModel bookData = new BookDataModel(authResponse.path("userId"), books);

        step("Добавить книги в профиль при неверно заполненном isbn. Ошибка 400", () -> given(addBookRequestSpec)
                .header("Authorization", "Bearer " + authResponse.path("token"))
                .body(bookData)
                .when()
                .post("BookStore/v1/Books")
                .then()
                .spec(addBookUnsuccessfulResponseSpec400)
                .body("code", is("1205"))
                .body("message", is("ISBN supplied is not available in Books Collection!")));
    }

}