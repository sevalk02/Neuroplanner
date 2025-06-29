package Neuroplanner.webservices;

import Neuroplanner.persistence.DatabaseConnector;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.util.Map;


@Path("auth")
public class AuthResource {


    @POST
    @Path("register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(Map<String, String> gegevens) { //json response omzetten in map met sleutels-waarden
        String gebruikersnaam = gegevens.get("gebruikersnaam");
        String wachtwoord = gegevens.get("wachtwoord");
        String rol = gegevens.get("rol");
        String voornaam = gegevens.get("voornaam");
        String achternaam = gegevens.get("achternaam");

        if (gebruikersnaam == null || wachtwoord == null || rol == null || voornaam == null || achternaam == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Gebruikersnaam, wachtwoord rol, voornaam en achternaam zijn verplicht"))
                    .build(); //velden mogen niet leeg zijn
        }

        try (Connection conn = DatabaseConnector.getConnection()) {
            System.out.println("Registratiegegevens:");
            System.out.println("gebruikersnaam: " + gebruikersnaam);
            System.out.println("wachtwoord: " + wachtwoord);
            System.out.println("rol: " + rol);
            System.out.println("voornaam: " + voornaam);
            System.out.println("achternaam: " + achternaam);
            String checkQuery = "SELECT COUNT(*) FROM Gebruiker WHERE gebruikersnaam = ?"; //checken of gebruikersnaam al bestaat
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, gebruikersnaam); //checkt aantal gebruikers met gebruikersnaam
                ResultSet rs = checkStmt.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0) { //meer dan nul betekent dat gebruikersnaam al bestaat
                    return Response.status(Response.Status.CONFLICT)
                            .entity(Map.of("error", "Gebruikersnaam bestaat al"))
                            .build();
                }
            }

            String insertSql = "INSERT INTO Gebruiker (gebruikersnaam, wachtwoord, rol, voornaam, achternaam) VALUES (?, ?, ?, ?, ?)";
            //in gebruiker tabel zetten
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, gebruikersnaam);
                insertStmt.setString(2, wachtwoord);
                insertStmt.setString(3, rol);
                insertStmt.setString(4, voornaam);
                insertStmt.setString(5, achternaam);
                insertStmt.executeUpdate();
            }

            return Response.ok(Map.of("message", "Registratie gelukt")).build(); //alles gaat goed

        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Fout in registratie", "details", e.getMessage())).build(); //er is iets fouts gegaan
        }
    }

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(Map<String, String> gegevens) { //gebruikersnaam, wachtwoord als json
        if (gegevens == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Leeg verzoek")).build(); //mag niet leeg zijn
        }

        System.out.println("gegevens ontvangen: " + gegevens);
        String gebruikersnaam = gegevens.get("gebruikersnaam");
        String wachtwoord = gegevens.get("wachtwoord");

        if (gebruikersnaam == null || wachtwoord == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Gebruikersnaam en wachtwoord zijn verplicht")) //een veld mag niet leeg zijn
                    .build();
        }


        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "SELECT rol FROM Gebruiker WHERE gebruikersnaam = ? AND wachtwoord = ?"; //checkt of gebruiker met ww bestaat
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, gebruikersnaam);
                stmt.setString(2, wachtwoord);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String rol = rs.getString("rol");
                    return Response.ok(Map.of(
                            "message", "Login gelukt",
                            "gebruikersnaam", gebruikersnaam,
                            "rol", rol
                    )).build(); //login gelukt
                } else {
                    return Response.status(Response.Status.UNAUTHORIZED)
                            .entity(Map.of("error", "Ongeldige inloggegevens")) //er is iets fouts gegaan
                            .build();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Fout in login")).build();
        }
    }
    @GET
    @Path("test")
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        return "AuthResource bereikbaar";
    }

}
