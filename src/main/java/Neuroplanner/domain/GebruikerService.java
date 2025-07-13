package Neuroplanner.domain;

import Neuroplanner.persistence.DatabaseConnector;

import java.sql.*;

public class GebruikerService {

    public boolean bestaatGebruiker(String gebruikersnaam) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "SELECT COUNT(*) FROM Gebruiker WHERE gebruikersnaam = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, gebruikersnaam);
                ResultSet rs = stmt.executeQuery();
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public void registreerGebruiker(Gebruiker gebruiker) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "INSERT INTO Gebruiker (gebruikersnaam, wachtwoord, rol, voornaam, achternaam) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, gebruiker.getGebruikersnaam());
                stmt.setString(2, gebruiker.getWachtwoord());
                stmt.setString(3, gebruiker.getRol().name());
                stmt.setString(4, gebruiker.getVoornaam());
                stmt.setString(5, gebruiker.getAchternaam());
                stmt.executeUpdate();
            }
        }
    }

    public Gebruiker login(String gebruikersnaam, String wachtwoord) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "SELECT voornaam, achternaam, rol FROM Gebruiker WHERE gebruikersnaam = ? AND wachtwoord = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, gebruikersnaam);
                stmt.setString(2, wachtwoord);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return new Gebruiker(
                            gebruikersnaam,
                            wachtwoord,
                            rs.getString("voornaam"),
                            rs.getString("achternaam"),
                            Rol.valueOf(rs.getString("rol"))
                    );
                }
                return null;
            }
        }
    }
    public Gebruiker findByGebruikersnaam(String gebruikersnaam) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "SELECT * FROM Gebruiker WHERE gebruikersnaam = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, gebruikersnaam);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return new Gebruiker(
                            gebruikersnaam,
                            rs.getString("wachtwoord"), // of null
                            rs.getString("voornaam"),
                            rs.getString("achternaam"),
                            Rol.valueOf(rs.getString("rol"))
                    );
                } else {
                    return null;
                }
            }
        }
    }
}