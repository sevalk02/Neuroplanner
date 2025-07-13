package Neuroplanner.webservices;

import Neuroplanner.domain.Gebruiker;
import Neuroplanner.domain.GebruikerService;
import Neuroplanner.domain.Rol;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.Map;

@Path("auth")
public class AuthResource {

    private GebruikerService gebruikerService = new GebruikerService();

    @POST
    @Path("register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(Map<String, String> gegevens) {
        try {
            String gebruikersnaam = gegevens.get("gebruikersnaam");
            String wachtwoord = gegevens.get("wachtwoord");
            String rol = gegevens.get("rol");
            String voornaam = gegevens.get("voornaam");
            String achternaam = gegevens.get("achternaam");

            if (gebruikersnaam == null || wachtwoord == null || rol == null || voornaam == null || achternaam == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("error", "Verplichte velden ontbreken")).build();
            }

            if (gebruikerService.bestaatGebruiker(gebruikersnaam)) {
                return Response.status(Response.Status.CONFLICT).entity(Map.of("error", "Gebruikersnaam bestaat al")).build();
            }

            gebruikerService.registreerGebruiker(new Gebruiker(gebruikersnaam, wachtwoord, voornaam, achternaam, Rol.valueOf(rol)));
            return Response.ok(Map.of("message", "Registratie gelukt")).build();

        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("error", "Fout bij registratie", "details", e.getMessage())).build();
        }
    }

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(Map<String, String> gegevens) {
        try {
            String gebruikersnaam = gegevens.get("gebruikersnaam");
            String wachtwoord = gegevens.get("wachtwoord");

            if (gebruikersnaam == null || wachtwoord == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("error", "Gebruikersnaam en wachtwoord zijn verplicht")).build();
            }

            Gebruiker gebruiker = gebruikerService.login(gebruikersnaam, wachtwoord);

            if (gebruiker == null) {
                return Response.status(Response.Status.UNAUTHORIZED).entity(Map.of("error", "Ongeldige inloggegevens")).build();
            }

            return Response.ok(Map.of(
                    "message", "Login gelukt",
                    "gebruikersnaam", gebruiker.getGebruikersnaam(),
                    "rol", gebruiker.getRol().name()
            )).build();

        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("error", "Fout bij login", "details", e.getMessage())).build();
        }
    }
}