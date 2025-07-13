package Neuroplanner.domain;

import Neuroplanner.persistence.DatabaseConnector;

import java.sql.*;

public class InstellingService {

    public Instelling getInstellingVoorGebruiker(String gebruikersnaam) throws SQLException {
        String sql = "SELECT donkereModus, toonKalender, toonTakenlijst, overzichtWeergave FROM Instelling WHERE gebruikersnaam = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, gebruikersnaam);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Instelling(
                        gebruikersnaam,
                        rs.getBoolean("donkereModus"),
                        rs.getBoolean("toonKalender"),
                        rs.getBoolean("toonTakenlijst"),
                        rs.getString("overzichtWeergave")
                );
            } else {
                return new Instelling(gebruikersnaam, false, true, true, "maand");
            }
        }
    }

    public void slaInstellingOp(Instelling instelling) throws SQLException {
        String selectSql = "SELECT COUNT(*) FROM Instelling WHERE gebruikersnaam = ?";
        String updateSql = "UPDATE Instelling SET donkereModus = ?, toonKalender = ?, toonTakenlijst = ?, overzichtWeergave = ? WHERE gebruikersnaam = ?";
        String insertSql = "INSERT INTO Instelling (gebruikersnaam, donkereModus, toonKalender, toonTakenlijst, overzichtWeergave) VALUES (?, ?, ?, ?, ?)";


        try (Connection conn = DatabaseConnector.getConnection()) {
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setString(1, instelling.getGebruikersnaam());
                ResultSet rs = selectStmt.executeQuery();
                rs.next();
                boolean bestaat = rs.getInt(1) > 0;

                if (bestaat) {
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setBoolean(1, instelling.isDonkereModus());
                        updateStmt.setBoolean(2, instelling.isToonKalender());
                        updateStmt.setBoolean(3, instelling.isToonTakenlijst());
                        updateStmt.setString(4, instelling.getOverzichtWeergave());
                        updateStmt.setString(5, instelling.getGebruikersnaam());
                        updateStmt.executeUpdate();
                    }
                } else {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, instelling.getGebruikersnaam());
                        insertStmt.setBoolean(2, instelling.isDonkereModus());
                        insertStmt.setBoolean(3, instelling.isToonKalender());
                        insertStmt.setBoolean(4, instelling.isToonTakenlijst());
                        insertStmt.setString(5, instelling.getOverzichtWeergave());
                        insertStmt.executeUpdate();
                    }
                }
            }
        }
    }
}
