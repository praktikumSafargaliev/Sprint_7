package api;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest {

    private Order order;

    private Response response;

    private OrderTrackNumber orderTrackNumber;

    private final String baseURI = "https://qa-scooter.praktikum-services.ru";

    private final List<String> colors;

    public CreateOrderTest (List<String> color) {
        this.colors = color;
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = baseURI;

        order = new Order ("Autotest", "Autotest", "Ufa, Test st.", "3", "+79991213333", 5, "2022-12-12", "Autotest", colors);
        response = given().header("Content-type", "application/json").body(order).post("/api/v1/orders/");
        orderTrackNumber = response.body().as(OrderTrackNumber.class);
    }

    @After
    public void cancelOrder() {
        given().header("Content-type", "application/json").body(orderTrackNumber).put("/api/v1/orders/cancel/?track=" + orderTrackNumber.getTrack());
    }

    @Parameterized.Parameters
    public static Object[][] getColors() {
            return new Object[][] {
                    {Arrays.asList("BLACK", "GREY")},
                    {Arrays.asList("BLACK")},
                    {Arrays.asList("GREY")},
                    {Arrays.asList("")},
            };
    }

    @Test
    @DisplayName("Проверяем успешное создание заказа + наличие трек номера")
    public void checkCreateOrder() {
        response.then().statusCode(201).and().body("track", notNullValue());
        Assert.assertTrue(orderTrackNumber.getTrack() > 0);
    }

}
