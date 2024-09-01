package tests;

import api.methods.AuthorizationByAPI;
import helpers.WithLogin;
import io.restassured.response.Response;
import models.BookDataModel;
import models.ResponceBookModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.AddBookSpec.addBookRequestSpec;
import static specs.AddBookSpec.addBookSuccessfulResponseSpec201;
import static specs.DeleteAllBooksSpec.*;

@Tag("regression")
public class DeleteBookTest extends TestBase {
    private final Response authResponse = new AuthorizationByAPI().getAuthorizationResponse();


    @Test
    @WithLogin
    @DisplayName("Проверка успешного удаления книг")
    @Tag("smoke")
    void successfulDeleteBookTest() {

        String isbn = "9781449325862";

        List<BookDataModel.IsbnData> books = List.of(new BookDataModel.IsbnData("9781449325862"));

        BookDataModel bookData = new BookDataModel(authResponse.path("userId"), books);

        step("Добавить книги в профиль. Успех 201", () -> given(addBookRequestSpec)
                .header("Authorization", "Bearer " + authResponse.path("token"))
                .body(bookData)
                .when()
                .post("BookStore/v1/Books")
                .then()
                .spec(addBookSuccessfulResponseSpec201))
        ;

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

        String isbn = "9781449325862";

        List<BookDataModel.IsbnData> books = List.of(new BookDataModel.IsbnData("9781449325862"));

        BookDataModel bookData = new BookDataModel(authResponse.path("userId"), books);

        step("Добавить книги в профиль. Успех 201", () -> given(addBookRequestSpec)
                .header("Authorization", "Bearer " + authResponse.path("token"))
                .body(bookData)
                .when()
                .post("BookStore/v1/Books")
                .then()
                .spec(addBookSuccessfulResponseSpec201))
        ;

        ResponceBookModel responce =
                step("Удалить все книги", () ->
                        given(deleteAllBooksRequestSpec)
                                // .header("Authorization", "Bearer " + authResponse.path("token"))
                                .queryParams("UserId", authResponse.path("userId"))
                                .when()
                                .delete("BookStore/v1/Books")
                                .then()
                                .spec(deleteAllBooksNotAuthResponseSpec401)
                                .extract().as(ResponceBookModel.class));

        step("Проверить,что ответ содержит код 1200 и сообщение с ошибкой User not authorized!", () -> {

            assertThat(responce.getCode()).isEqualTo("1200");
            assertThat(responce.getMessage()).isEqualTo("User not authorized!"); // проверить, что isbn книги совпадает с заданным
        })
        ;
    }
}
