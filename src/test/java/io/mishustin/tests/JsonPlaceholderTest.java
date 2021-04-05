package io.mishustin.tests;

import io.mishustin.krypton.TestConfiguration;
import io.mishustin.krypton.TestNgListener;
import io.mishustin.krypton.api.FeignApiClient;
import io.mishustin.krypton.api.ApiResponse;
import io.mishustin.krypton.api.JsonPlaceholderService;
import io.mishustin.krypton.model.Post;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Listeners(TestNgListener.class)
public class JsonPlaceholderTest {

    private JsonPlaceholderService service;

    @BeforeClass
    public void init() {
        service = FeignApiClient.getJsonPlaceholderService(TestConfiguration.jsonHost);
    }

    @Test
    public void getPostShouldReturnRC200() {
        ApiResponse<List<Post>> response = service.getPosts();


        assertThat(response.code, is(200));
        assertThat(response.getObj(), not(emptyIterable()));
    }

    @Test
    public void getPostWithInvalidIdShouldReturn404() {
        ApiResponse response = service.getPost(-2323);

        assertThat(response.code, is(404));
    }
}