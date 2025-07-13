package Neuroplanner.webservices;

import Neuroplanner.domain.Instelling;
import Neuroplanner.domain.InstellingService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

@Path("instelling")
public class InstellingResource {

    private InstellingService instellingService = new InstellingService();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInstelling(@HeaderParam("X-Gebruikersnaam") String gebruikersnaam) {
        if (gebruikersnaam == null || gebruikersnaam.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Gebruikersnaam ontbreekt").build();
        }

        try {
            Instelling instelling = instellingService.getInstellingVoorGebruiker(gebruikersnaam);
            return Response.ok(instelling).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Fout bij ophalen instellingen").build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateInstelling(Instelling instelling, @HeaderParam("X-Gebruikersnaam") String gebruikersnaam) {
        if (gebruikersnaam == null || gebruikersnaam.trim().isEmpty() || instelling == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Ongeldige invoer").build();
        }

        try {
            instelling.setGebruikersnaam(gebruikersnaam);
            instellingService.slaInstellingOp(instelling);
            return Response.ok().entity("Instellingen opgeslagen").build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Fout bij opslaan instellingen").build();
        }
    }
}
