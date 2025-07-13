package Neuroplanner.domain;

import Neuroplanner.persistence.DatabaseConnector;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaakService {

    public List<Taak> getTakenVanGebruiker(String gebruikersnaam) throws SQLException {
        List<Taak> taken = new ArrayList<>();
        String sql = "SELECT id, titel, omschrijving, type, kleur, startTijd, eindTijd FROM Taak WHERE gebruiker_naam = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, gebruikersnaam);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                GebruikerService gebruikerService = new GebruikerService();
                Gebruiker gebruiker = gebruikerService.findByGebruikersnaam(gebruikersnaam);

                Taak taak = new Taak(
                        rs.getString("titel"),
                        rs.getString("omschrijving"),
                        TaakType.valueOf(rs.getString("type")),
                        TaakKleur.valueOf(rs.getString("kleur")),
                        gebruiker
                );
                taak.setStartTijd(rs.getTimestamp("startTijd").toLocalDateTime());
                taak.setEindTijd(rs.getTimestamp("eindTijd").toLocalDateTime());
                taak.setId(rs.getInt("id"));
                taken.add(taak);
            }
        }
        return taken;
    }

    public void voegTaakToe(String gebruikersnaam, Taak taak) throws SQLException {
        String sql = "INSERT INTO Taak (titel, omschrijving, type, kleur, startTijd, eindTijd, gebruiker_naam) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, taak.getTitel());
            stmt.setString(2, taak.getOmschrijving());
            stmt.setString(3, taak.getType().name());
            stmt.setString(4, taak.getKleur().name());
            stmt.setTimestamp(5, Timestamp.valueOf(taak.getStartTijd()));
            stmt.setTimestamp(6, Timestamp.valueOf(taak.getEindTijd()));
            stmt.setString(7, gebruikersnaam);

            stmt.executeUpdate();
        }
    }

    public boolean verwijderTaak(int id, String gebruikersnaam) throws SQLException {
        String sql = "DELETE FROM Taak WHERE id = ? AND gebruiker_naam = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.setString(2, gebruikersnaam);

            return stmt.executeUpdate() > 0;
        }
    }
    public void updateTaak(int id, String gebruikersnaam, Taak taak) throws SQLException {
        String sql = "UPDATE Taak SET titel=?, omschrijving=?, type=?, kleur=?, startTijd=?, eindTijd=? WHERE id=? AND gebruiker_naam=?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, taak.getTitel());
            stmt.setString(2, taak.getOmschrijving());
            stmt.setString(3, taak.getType().name());
            stmt.setString(4, taak.getKleur().name());
            stmt.setTimestamp(5, Timestamp.valueOf(taak.getStartTijd()));
            stmt.setTimestamp(6, Timestamp.valueOf(taak.getEindTijd()));
            stmt.setInt(7, id);
            stmt.setString(8, gebruikersnaam);
            stmt.executeUpdate();
        }
    }
}