package server;

import java.sql.*;

public class SQLHandler {
    private static Connection connection;
    private static PreparedStatement psGetNickname;
    private static PreparedStatement psRegistration;

    private static void prepareAllStatements() throws SQLException {
        psGetNickname = connection.prepareStatement("SELECT nickname FROM users WHERE login = ? AND password = ?;");
        psRegistration = connection.prepareStatement("INSERT INTO users(login, password, nickname) VALUES (?, ?, ?);");
    }

    public static boolean connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:users.db");
            prepareAllStatements();
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            return false;
        }
    }

    public static String getNicknameByLoginAndPassword(String login, String password)  {
        String nick = null;
        try {
            psGetNickname.setString(1, login);
            psGetNickname.setString(2, password);
            ResultSet rs = psGetNickname.executeQuery();
            if (rs.next()) {
                nick = rs.getString(1);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return nick;
    }

    public static boolean registration(String nickname, String login, String password)  {
        try {
            psRegistration.setString(1, nickname);
            psRegistration.setString(2, login);
            psRegistration.setString(3, password);
            psRegistration.executeUpdate();
            return true;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public static void disconnect() throws SQLException {
        psRegistration.close();
        psGetNickname.close();
        connection.close();
    }

}


