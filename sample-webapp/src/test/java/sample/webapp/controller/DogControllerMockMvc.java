package sample.webapp.controller;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import sample.webapp.dto.DogDto;

import java.util.List;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

class DogControllerMockMvc {
    DogControllerMockMvc(MockMvc mvc) {
        RestAssuredMockMvc.mockMvc(mvc);
        RestAssuredMockMvc.requestSpecification = new MockMvcRequestSpecBuilder().setContentType(ContentType.JSON).build();
    }

    DogDto getDog(String id) {
        MockMvcResponse response = given().get("/api/dog/{id}", id);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        return response.as(DogDto.class);
    }

    void getDogWithError(String id, HttpStatus status, String error) {
        MockMvcResponse response = given().get("/api/dog/{id}", id);
        assertEquals(status.value(), response.getStatusCode());
        assertThat(response.as(String.class), containsString(error));
    }

    List<DogDto> getAllDogs() {
        MockMvcResponse response = given().get("/api/dog");
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        return List.of(response.as(DogDto[].class));
    }

    DogDto createDog(DogDto dog) {
        MockMvcResponse response = given().body(dog).post("/api/dog");
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        return response.as(DogDto.class);
    }

    void createDogWithError(DogDto dog, HttpStatus status, String error) {
        MockMvcResponse response = given().contentType(ContentType.JSON).body(dog).when().post("/api/dog");
        assertEquals(status.value(), response.getStatusCode());
        assertThat(response.as(String.class), containsString(error));
    }

    void updateDog(String id, DogDto dog) {
        given().contentType(ContentType.JSON).body(dog).when().put("/api/dog/{id}", id);
    }

    void updateDogWithError(String id, DogDto dog, HttpStatus status, String error) {
        MockMvcResponse response = given().contentType(ContentType.JSON).body(dog).when().put("/api/dog/{id}", id);
        assertEquals(status.value(), response.getStatusCode());
        assertThat(response.as(String.class), containsString(error));
    }

    void deleteDog(String id) {
        given().when().delete("/api/dog/{id}", id);
    }
}
