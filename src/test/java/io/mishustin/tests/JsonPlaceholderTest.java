package io.mishustin.tests;

import io.mishustin.krypton.TestConfiguration;
import io.mishustin.krypton.TestNgListener;
import io.mishustin.krypton.api.ApiClient;
import io.mishustin.krypton.api.JsonPlaceholderService;
import io.mishustin.krypton.model.Post;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Listeners(TestNgListener.class)
public class JsonPlaceholderTest {

    private JsonPlaceholderService service;

    @BeforeClass
    public void init() {
        service = ApiClient.getJsonPlaceholderService(TestConfiguration.jsonHost);
    }

    @Test
    public void getPostShouldReturnRC200() throws IOException {
        Response<List<Post>> execute = service.getPosts().execute();

        assertThat(execute.code(), is(200));
    }

    @Test
    public void getPostWithInvalidIdShouldReturn404() throws IOException {
        Response<Post> execute = service.getPost(-2323).execute();

        assertThat(execute.code(), is(404));
    }

}
