package com.vvi.appsrv;

import com.google.gson.JsonObject;
import com.vvi.appsrv.api.ServerAPI;
import jdk.internal.dynalink.beans.StaticClass;

import java.sql.*;

public class Main {

    private static String hostname="localhost";
    private static int port=3306;
    private static String databaseName="appdatabase";
    private static String username="root";
    private static String password="VeryGoodBird2005";
    public static Connection connection;
    public static Statement statement;
    public static void main(String args[]){
        try {
            final String url = "jdbc:mysql://" + hostname + ":" + port + "/" + databaseName;
            connection = DriverManager.getConnection(url, username, password);
            statement=connection.createStatement();

            statement.close();
            connection.close();
        }
        catch (SQLException e) {
            System.out.println("Error using MySQL database" + e.getMessage());
        }
    }
}
