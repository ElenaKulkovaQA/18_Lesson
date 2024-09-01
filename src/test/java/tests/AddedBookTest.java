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
import static specs.AddBookSpec.*;
import static specs.DeleteAllBooksSpec.deleteAllBooksRequestSpec;
import static specs.DeleteAllBooksSpec.deleteAllBooksResponseSpec204;


@Tag("regression")
public class AddedBookTest extends TestBase {
    private final Response authResponse = new AuthorizationByAPI().getAuthorizationResponse();


    @Test
    @WithLogin
    @DisplayName("Проверить успешное добавление книг")
    void successfulAddedBookTest() {

        String isbn = "9781449325862";

        List<BookDataModel.IsbnData> books = List.of(new BookDataModel.IsbnData("9781449325862"));

        BookDataModel bookData = new BookDataModel(authResponse.path("userId"), books);

        step("Удалить все книги", () -> given(deleteAllBooksRequestSpec)
                .header("Authorization", "Bearer " + authResponse.path("token"))
                .queryParams("UserId", authResponse.path("userId"))
                .when()
                .delete("BookStore/v1/Books")
                .then()
                .spec(deleteAllBooksResponseSpec204));

        step("Добавить книги в профиль. Успех 201", () -> given(addBookRequestSpec)
                .header("Authorization", "Bearer " + authResponse.path("token"))
                .body(bookData)
                .when()
                .post("BookStore/v1/Books")
                .then()
                .spec(addBookSuccessfulResponseSpec201))
                .extract().as(ResponceBookModel.class);// размаппить ответ и наложить его на класс BookDataModel

        step("Проверить что строка с isbn не пустая", () -> {

            assertThat(books).isNotNull();
            assertThat(books.size()).isEqualTo(1); // проверить, что в списке книг одна книга
            assertThat(books.get(0).getIsbn()).isEqualTo(isbn); // проверить, что isbn книги совпадает с заданным
        })
        ;
    }

    @Test
    @WithLogin
    @DisplayName("Проверить, что при вводе неправильного ISBN появляется ошибка")
    @Tag("smoke")
    void negative400AddBookToCollectionTest() {


        step("Удалить все книги", () -> given(deleteAllBooksRequestSpec)
                .header("Authorization", "Bearer " + authResponse.path("token"))
                .queryParams("UserId", authResponse.path("userId"))
                .when()
                .delete("BookStore/v1/Books")
                .then()
                .spec(deleteAllBooksResponseSpec204));

        String isbn = "NULL";
        List<BookDataModel.IsbnData> books = List.of(new BookDataModel.IsbnData("NULL"));
        BookDataModel bookData = new BookDataModel(authResponse.path("userId"), books);

        ResponceBookModel responce =
                step("Добавить книги в профиль при неверно заполненном isbn. Ошибка 400", () ->
                        given(addBookRequestSpec)
                                .header("Authorization", "Bearer " + authResponse.path("token"))
                                .body(bookData)
                                .when()
                                .post("BookStore/v1/Books")
                                .then()
                                .spec(addBookUnsuccessfulResponseSpec400)
                                .extract().as(ResponceBookModel.class));

        step("Проверить что ответ содержит код с ошибкой", () -> {

            assertThat(responce.getCode()).isEqualTo("1205");
            assertThat(responce.getMessage()).isEqualTo("ISBN supplied is not available in Books Collection!"); // проверить, что isbn книги совпадает с заданным
        })
        ;
    }
}