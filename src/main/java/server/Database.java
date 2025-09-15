package server;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class Database {

    private final String url = "jdbc:sqlite:src/main/resources/users.db"; // podaj ścieżkę do pliku SQLite

    public Database() {
        try {
            // Ładujemy sterownik SQLite
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Sprawdzenie loginu i hasła w bazie
    public boolean authenticate(String login, String password) {
        String sql = "SELECT COUNT(*) FROM users WHERE login = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // jeśli jest wiersz, login i hasło się zgadzają
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // brak dopasowania
    }

    // Metoda aktualizująca punkty zwycięzcy i przegranego
    public void updateLeaderboard(String winner, String loser) {
        String addPoint = "UPDATE users SET points = points + 1 WHERE login = ?";
        String removePoint = "UPDATE users SET points = points - 1 WHERE login = ?";

        try (Connection conn = DriverManager.getConnection(url)) {
            conn.setAutoCommit(false); // transakcja, żeby obie operacje były atomowe

            try (PreparedStatement stmtWin = conn.prepareStatement(addPoint);
                 PreparedStatement stmtLose = conn.prepareStatement(removePoint)) {

                stmtWin.setString(1, winner);
                stmtWin.executeUpdate();

                stmtLose.setString(1, loser);
                stmtLose.executeUpdate();

                conn.commit(); // zatwierdzenie zmian
            } catch (SQLException e) {
                conn.rollback(); // w razie błędu wycofanie zmian
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Integer> getLeaderboard() {
        String sql = "SELECT login, points FROM users ORDER BY points DESC";
        Map<String, Integer> leaderboard = new LinkedHashMap<>();

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                leaderboard.put(rs.getString("login"), rs.getInt("points"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return leaderboard;
    }

    public void resetLeaderboard() {
        String sql = "UPDATE users SET points = 0";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
