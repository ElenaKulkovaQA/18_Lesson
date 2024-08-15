package authorisation;

import io.restassured.response.Response;
import models.AuthModel;
import tests.TestBase;
import tests.TestBase;

import static io.restassured.RestAssured.given;
import static specs.LoginUserSpec.loginRequestSpec;
import static specs.LoginUserSpec.loginResponseSpec;

public class AuthorizationByAPI extends TestBase {
    public Response getAuthorizationResponse() {
        AuthModel model = new AuthModel(getUserName(), getPassword());

        return given(loginRequestSpec)
                .body(model)
                .when()
                .post("Account/v1/Login")
                .then()
                .spec(loginResponseSpec)
                .extract().response();
    }
}

