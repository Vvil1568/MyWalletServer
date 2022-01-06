package com.vvi.appsrv;

import java.sql.*;

public class Main {

    private static String hostname="localhost";
    private static int databasePort =3306;
    private static int serverPort=25565;
    private static String databaseName="appdatabase";
    private static String username="root";
    private static String password="VeryGoodBird2005";
    public static Connection connection;
    public static Statement statement;
    public static void main(String args[]){
        try {
            final String url = "jdbc:mysql://" + hostname + ":" + databasePort + "/" + databaseName;
            connection = DriverManager.getConnection(url, username, password);
            statement=connection.createStatement();
            Server serverInstance = new Server(serverPort);
            serverInstance.startServer();
            //statement.close();
            //connection.close();
        }
        catch (SQLException e) {
            System.out.println("Error using MySQL database" + e.getMessage());
        }
    }
}
