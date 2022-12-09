package api;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

public class LoginCourierTest {

    private Courier courier;
    private Response response;
    private CourierID courierID;
    private final String baseURI = "https://qa-scooter.praktikum-services.ru";
    private final String courierLoginEndpoint = "/api/v1/courier/login";
    private final String courierEndpoint = "/api/v1/courier/";

    @Before
    public void setUp() {
        RestAssured.baseURI = baseURI;
        courier = new Courier("autotest456", "autotest123");
        response = given().header("Content-type", "application/json").body(courier).post(courierEndpoint);
    }

    @After
    public void deleteCourier() {
        courierID = given().header("Content-type", "application/json").body(courier).post(courierLoginEndpoint).body().as(CourierID.class);
        given().header("Content-Type", "application/json").delete("/api/v1/courier/" + courierID.getId());
    }

    @Test
    @DisplayName("Проверяем авторизацию курьера")
    public void checkLoginCourier() {
        createCourier().then().statusCode(200).and().assertThat().body("id", notNullValue());
    }

    @Test
    @DisplayName("Проверяем авторизацию курьера без заполненного обязательного поля: логин")
    public void checkCourierAuthorisationWithEmptyLogin() {
        courier.setLogin("");
        createCourier().then().statusCode(400).and().assertThat().body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Проверяем авторизацию курьера без заполненного обязательного поля: пароль")
    public void checkCourierAuthorisationWithEmptyPassword() {
        courier.setPassword("");
        createCourier().then().statusCode(400).and().assertThat().body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Проверяем авторизацию курьера с неправильным логином")
    public void checkCourierAuthorisationWithWrongLogin() {
        courier.setLogin("autotest444");
        createCourier().then().statusCode(404).and().assertThat().body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Проверяем авторизацию курьера с неправильным паролем")
    public void checkCourierAuthorisationWithWrongPassword() {
        courier.setPassword("autotest111");
        createCourier().then().statusCode(404).and().assertThat().body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Проверяем авторизацию курьера без обязательного поля: логин")
    public void checkCourierAuthorisationWithoutLoginField() {
        courier.setLogin(null);
        createCourier().then().statusCode(400).and().assertThat().body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Проверяем авторизацию курьера без обязательного поля: пароль")
    public void checkCourierAuthorisationWithoutPasswordField()  {
        courier.setPassword(null);
        createCourier().then().statusCode(400).and().assertThat().body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Проверям авторизацию несуществующим курьером")
    public void checkUnknownCourierAuthorisation() {
        courier.setLogin("autotest123321999");
        createCourier().then().statusCode(404).and().assertThat().body("message", equalTo("Учетная запись не найдена"));
    }

    @Step("Логинимся курьером")
    public Response createCourier() {
        return given().header("Content-type", "application/json").body(courier).when().post(courierLoginEndpoint);
    }

}
