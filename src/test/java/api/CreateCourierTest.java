package api;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CreateCourierTest {

    private Courier courier;
    private Response response;
    private CourierID courierID;
    private final String baseURI = "https://qa-scooter.praktikum-services.ru";
    private final String courierLoginEndpoint = "/api/v1/courier/login/";
    private final String courierEndpoint = "/api/v1/courier/";

    @Before
    public void setUp() {
        RestAssured.baseURI = baseURI;
        courier = new Courier("autotest456", "autotest123", "autotest789");
    }

    @After
    public void deleteCourier() {
        courierID = given().header("Content-type", "application/json").body(courier).post(courierLoginEndpoint).body().as(CourierID.class);
        given().header("Content-type", "application/json").body(courierID).delete("/api/v1/courier/" + courierID.getId());
    }

    @Test
    @DisplayName("Проверяем статус-код и тело ответа при успешном создании курьера")
    public void checkStatusCodeSuccessCreate() {
        createCourier().then().statusCode(201).and().assertThat().body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Проверяем статус-код и тело ответа при попытке создать уже существующий аккаунт курьера")
    public void checkDuplicateCourierCreateStatus() {
        createCourier();
        createCourier().then().statusCode(409).and().body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Проверяем создание курьера без заполненного обязательного поля: логин")
    public void checkCreateCourierWithEmptyLogin() {
        courier.setLogin("");
        createCourier().then().statusCode(400).and().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Проверяем создание курьера без заполненного обязательного поля: пароль")
    public void checkCreateCourierWithEmptyPassword() {
        courier.setPassword("");
        createCourier().then().statusCode(400).and().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Проверяем создание курьера без заполненного обязательного поля: имя")
    public void checkCreateCourierWithEmptyFirstName() {
        courier.setFirstName("");
        createCourier().then().statusCode(201 ).and().assertThat().body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Проверяем создание курьера без обязательного поля: логин")
    public void checkCreateCourierWithoutLoginField() {
        courier.setLogin(null);
        createCourier().then().statusCode(400).and().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Проверяем создание курьера без обязательного поля: пароль")
    public void checkCreateCourierWithoutPasswordField() {
        courier.setPassword(null);
        createCourier().then().statusCode(400).and().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Проверяем создание курьера без обязательного поля: имя")
    public void checkCreateCourierWithoutFirstNameField() {
        courier.setFirstName(null);
        createCourier().then().statusCode(201).and().assertThat().body("ok", equalTo(true));
    }

    @Step("Создаём курьера")
    public Response createCourier() {
        return given().header("Content-type", "application/json").body(courier).post(courierEndpoint);
    }


}
