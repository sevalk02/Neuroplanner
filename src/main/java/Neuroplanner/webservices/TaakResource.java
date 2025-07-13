package Neuroplanner.webservices;

import Neuroplanner.domain.Taak;
import Neuroplanner.domain.TaakService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Path("taak")
public class TaakResource {

    private TaakService taakService = new TaakService();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTakenVanGebruiker(@HeaderParam("X-Gebruikersnaam") String gebruikersnaam) {
        if (gebruikersnaam == null || gebruikersnaam.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Gebruikersnaam ontbreekt").build();
        }

        try {
            List<Taak> taken = taakService.getTakenVanGebruiker(gebruikersnaam);
            return Response.ok(taken).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Fout bij ophalen taken: " + e.getMessage()).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response voegTaakToe(Taak taak, @HeaderParam("X-Gebruikersnaam") String gebruikersnaam) {
        if (gebruikersnaam == null || gebruikersnaam.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Gebruikersnaam ontbreekt").build();
        }

        try {
            taakService.voegTaakToe(gebruikersnaam, taak);
            return Response.status(Response.Status.CREATED).entity("Taak toegevoegd").build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Fout bij toevoegen taak: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response verwijderTaak(@PathParam("id") int id, @HeaderParam("X-Gebruikersnaam") String gebruikersnaam) {
        if (gebruikersnaam == null || gebruikersnaam.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Gebruikersnaam ontbreekt").build();
        }

        try {
            boolean verwijderd = taakService.verwijderTaak(id, gebruikersnaam);
            if (verwijderd) {
                return Response.ok("Taak verwijderd").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Taak niet gevonden voor deze gebruiker").build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Fout bij verwijderen taak: " + e.getMessage()).build();
        }
    }
    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTaak(@PathParam("id") int id,
                               Taak taak,
                               @HeaderParam("X-Gebruikersnaam") String gebruikersnaam) {
        try {
            if (taak == null || gebruikersnaam == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Ongeldige invoer").build();
            }

            taakService.updateTaak(id, gebruikersnaam, taak);
            return Response.ok(Map.of("message", "Taak bijgewerkt")).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage())).build();
        }
    }

}