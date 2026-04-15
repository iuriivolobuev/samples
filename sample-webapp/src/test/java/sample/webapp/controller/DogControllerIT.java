package sample.webapp.controller;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import sample.webapp.dto.DogDto;

import java.util.List;

import static io.qala.datagen.RandomShortApi.alphanumeric;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static sample.webapp.TestUtils.assertDatesEqual;

public class DogControllerIT {
    @Test
    public void createsDog() {
        DogDto newDog = DogDto.random();
        String id = createDog(newDog).getId();

        DogDto loaded = getDog(id);
        assertDogsEqual(loaded, newDog);
    }

    @Test
    public void errs_ifDogIsNotValid_whenCreatingDog() {
        DogDto notValid = DogDto.random().setName(alphanumeric(101));
        createDogWithError(notValid, HttpStatus.BAD_REQUEST, "Name size should be between 1 and 100.");
    }

    @Test
    public void updatesDog() {
        String id = createDog(DogDto.random()).getId();
        DogDto toUpdate = DogDto.random();
        updateDog(id, toUpdate);

        DogDto loaded = getDog(id);
        assertDogsEqual(loaded, toUpdate);
    }

    @Test
    public void errs_ifDogIsNotValid_whenUpdatingDog() {
        String id = createDog(DogDto.random()).getId();
        DogDto notValid = DogDto.random().setName(alphanumeric(101));
        updateDogWithError(id, notValid, HttpStatus.BAD_REQUEST, "Name size should be between 1 and 100.");
    }

    @Test
    public void errs_ifDogDoesNotExist_whenUpdatingDog() {
        updateDogWithError("-1", DogDto.random(), HttpStatus.NOT_FOUND, "Couldn't find object [Dog] with id=[-1].");
    }

    @Test
    public void deletesDog() {
        String id = createDog(DogDto.random()).getId();

        DogDto actual = getDog(id);
        assertNotNull(actual);

        deleteDog(id);
        getDogWithError(id, HttpStatus.NOT_FOUND, "Couldn't find object [Dog] with id=[%s].".formatted(id));
    }

    @Test
    public void getsAllDogs() {
        String id1 = createDog(DogDto.random()).getId();
        String id2 = createDog(DogDto.random()).getId();
        List<String> ids = getAllDogs().stream().map(DogDto::getId).toList();
        assertThat(ids, hasItems(id1, id2));
    }

    private static DogDto getDog(String id) {
        Response response = given().get("/api/dog/{id}", id);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        return response.as(DogDto.class);
    }

    private static void getDogWithError(String id, HttpStatus status, String error) {
        Response response = given().get("/api/dog/{id}", id);
        assertEquals(status.value(), response.getStatusCode());
        assertThat(response.as(String.class), containsString(error));
    }

    private static List<DogDto> getAllDogs() {
        Response response = given().get("/api/dog");
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        return List.of(response.as(DogDto[].class));
    }

    private static DogDto createDog(DogDto dog) {
        Response response = given().contentType(ContentType.JSON).body(dog).when().post("/api/dog");
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        return response.as(DogDto.class);
    }

    private static void createDogWithError(DogDto dog, HttpStatus status, String error) {
        Response response = given().contentType(ContentType.JSON).body(dog).when().post("/api/dog");
        assertEquals(status.value(), response.getStatusCode());
        assertThat(response.as(String.class), containsString(error));
    }

    private static void updateDog(String id, DogDto dog) {
        given().contentType(ContentType.JSON).body(dog).when().put("/api/dog/{id}", id);
    }

    private static void updateDogWithError(String id, DogDto dog, HttpStatus status, String error) {
        Response response = given().contentType(ContentType.JSON).body(dog).when().put("/api/dog/{id}", id);
        assertEquals(status.value(), response.getStatusCode());
        assertThat(response.as(String.class), containsString(error));
    }

    private static void deleteDog(String id) {
        given().when().delete("/api/dog/{id}", id);
    }

    private static void assertDogsEqual(DogDto actual, DogDto expected) {
        assertEquals(expected.getName(), actual.getName());
        assertDatesEqual(expected.getTimeOfBirth(), actual.getTimeOfBirth());
        assertEquals(expected.getHeight(), actual.getHeight());
        assertEquals(expected.getWeight(), actual.getWeight());
    }
}