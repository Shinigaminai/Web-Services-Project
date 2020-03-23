package UserAPI;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/userdata")
public class GetUserResource {

    @Inject
    GetUserService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/user/{name}")
    public String getUser(@PathParam String name) {
        return toJSON(service.getUser(name));
    }


    public String toJSON(Object o) {
        //Creating the ObjectMapper object
        ObjectMapper mapper = new ObjectMapper();
        //Converting the Object to JSONString
        try {
            return mapper.writeValueAsString(o);
        } catch (Exception e) {
            return null;
        }
    }
}
