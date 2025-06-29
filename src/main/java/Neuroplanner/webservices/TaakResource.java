package Neuroplanner.webservices;
import Neuroplanner.persistence.DatabaseConnector;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

@Path("taak")
public class TaakResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTakenVanGebruiker(@HeaderParam("X-Gebruikersnaam") String gebruikersnaam) //gebruiker komt uit header gebruikersnaam
    {
        if (gebruikersnaam == null || gebruikersnaam.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Gebruikersnaam ontbreekt")).build(); //checkt of gebruikersnaam is meegegeven
        }

        List<Map<String, Object>> taken = new ArrayList<>();
        String query = "SELECT id, titel, type, startTijd, eindTijd, kleur FROM Taak WHERE gebruiker_naam =?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) { //connectie database en preparestatement om sql injectie te voorkomen

            stmt.setString(1, gebruikersnaam); //placeholder word ingevuld met huidige gebruikersnaam
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                taken.add(Map.of(
                        "id", rs.getInt("id"),
                        "titel", rs.getString("titel"),
                        "type", rs.getString("type"),
                        "kleur", rs.getString("kleur"),
                        "startTijd", rs.getTimestamp("startTijd").toString(),
                        "eindTijd", rs.getTimestamp("eindTijd").toString()
                ));
            }
            return Response.ok(taken).build();

                }catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Fout bij ophalen aquaria")).build();
        }
    }
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response voegTaakToe(Map<String, Object> data, @HeaderParam("X-Gebruikersnaam") String gebruikersnaam) {
        if (gebruikersnaam == null || gebruikersnaam.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Gebruikersnaam ontbreekt")).build();
        }

        try {
            String titel = (String) data.get("titel");
            String omschrijving = (String) data.get("omschrijving");
            String type = (String) data.get("type");
            String kleur = (String) data.get("kleur");
            String startTijdStr = (String) data.get("startTijd");  // verwachte formaat: "2025-06-28T10:00"
            String eindTijdStr = (String) data.get("eindTijd");
            LocalDateTime start = LocalDateTime.parse(startTijdStr);
            LocalDateTime eind = LocalDateTime.parse(eindTijdStr);
            Timestamp startTijd = Timestamp.valueOf(start);
            Timestamp eindTijd = Timestamp.valueOf(eind);

            String sql = "INSERT INTO Taak (titel, omschrijving, type, kleur, startTijd, eindTijd, gebruiker_naam) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = DatabaseConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, titel);
                stmt.setString(2, omschrijving);
                stmt.setString(3, type);
                stmt.setString(4, kleur);
                stmt.setTimestamp(5, startTijd);
                stmt.setTimestamp(6, eindTijd);
                stmt.setString(7, gebruikersnaam);

                stmt.executeUpdate();

                return Response.status(Response.Status.CREATED).entity(Map.of("message", "Taak toegevoegd")).build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Databasefout: " + e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Ongeldige invoer: " + e.getMessage())).build();
        }
    }
    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response verwijderTaak(@PathParam("id") int id,
                                  @HeaderParam("X-Gebruikersnaam") String gebruikersnaam) {
        if (gebruikersnaam == null || gebruikersnaam.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Gebruikersnaam ontbreekt")).build();
        }

        String sql = "DELETE FROM Taak WHERE id = ? AND gebruiker_naam = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.setString(2, gebruikersnaam);

            int deleted = stmt.executeUpdate();
            if (deleted > 0) {
                return Response.ok(Map.of("message", "Taak verwijderd")).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "Taak niet gevonden voor deze gebruiker")).build();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Databasefout: " + e.getMessage())).build();
        }
    }



}
