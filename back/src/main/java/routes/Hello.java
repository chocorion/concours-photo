package routes;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import services.AbstractImageService;

@Path("helloworld/")
public class Hello {
    @Inject public AbstractImageService imageService;

    @GET
    public String helloWorld() throws Exception {
        return "Hello world !";
    }
}
