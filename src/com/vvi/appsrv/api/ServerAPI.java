package com.vvi.appsrv.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.vvi.appsrv.Constants;
import com.vvi.appsrv.Main;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ServerAPI {
    /**
     * Checks whether user with such username has been registered
     * @param username Username
     * @return Returns whether username is available
     */
    public static boolean isUsernameAvailable(String username){
        boolean isAvailable=true;
        try {
            ResultSet result = Main.statement.executeQuery("SELECT * FROM "+ Constants.USER_PASSWORD_TABLE_NAME+" WHERE "+Constants.USERNAME_COLUMN_NAME+"=\""+username+"\";");
            if(result.next())
                isAvailable=false;
            result.close();
        }catch (SQLException e) {
            System.out.println("Error checking username availability" + e.getMessage());
        }
        return isAvailable;
    }

    /**
     * Checks whether the hash equals password hash for this user
     * @param username Username
     * @param hash Password hash
     * @return Returns whether password hash is correct
     */
    public static boolean verifyUser(String username, String hash){
        boolean isRight=false;
        try {
            ResultSet result = Main.statement.executeQuery("SELECT * FROM "+ Constants.USER_PASSWORD_TABLE_NAME+" WHERE "+Constants.USERNAME_COLUMN_NAME+"=\""+username+"\";");
            if(result.next() && result.getString(Constants.PASSWORD_HASH_COLUMN_NAME).equals(hash))
                isRight=true;
            result.close();
        }catch (SQLException e) {
            System.out.println("Error in user verification" + e.getMessage());
        }
        return isRight;
    }

    /**
     * Registers user
     * @param username Username
     * @param hash Password hash
     */
    private static void registerUser(String username, String hash){
        try {
            Main.statement.executeUpdate("INSERT " + Constants.USER_PASSWORD_TABLE_NAME + "("+Constants.USERNAME_COLUMN_NAME + "," +
                    Constants.PASSWORD_HASH_COLUMN_NAME + ") VALUES ('"+username+"','"+hash+"')");
        }catch (SQLException e) {
            System.out.println("Error in user registration" + e.getMessage());
        }
    }

    /**
     * Registers user after some checks
     * @param username Username
     * @param hash Password hash
     * @return returns 0 if user has been registered
     *         returns 1 if such username is in the database already
     *         return 2 if there was another error
     */
    public static int registerUserWithChecks(String username, String hash){
        if(!isUsernameAvailable(username))return 1;
        try {
            registerUser(username, hash);
        }catch (Exception e){
            return 2;
        }
        return 0;
    }

    /**
     * Collects all of the data about user
     * @param username Username
     * @return Returns a json with user's data
     */
    public static JsonObject getUserData(String username){
        JsonObject object = new JsonObject();
        try {
            ResultSet result = Main.statement.executeQuery("SELECT * FROM "+ Constants.USER_DATA_TABLE_NAME+" WHERE "+Constants.USERNAME_COLUMN_NAME+"=\""+username+"\";");
            JsonArray arr_expenses=new JsonArray();
            JsonArray arr_income=new JsonArray();
            while(result.next()) {
                JsonObject obj=new JsonObject();
                obj.add("category",new JsonPrimitive(result.getString(Constants.CATEGORY_COLUMN_NAME)));
                obj.add("amount",new JsonPrimitive(result.getInt(Constants.AMOUNT_COLUMN_NAME)));
                obj.add("date",new JsonPrimitive(result.getString(Constants.DATE_COLUMN_NAME)));
                obj.add("currency",new JsonPrimitive(result.getInt(Constants.CURRENCY_COLUMN_NAME)));
                if(result.getString(Constants.TYPE_COLUMN_NAME).equals("expense"))
                    arr_expenses.add(obj);
                else
                    arr_income.add(obj);
            }
            object.add("expenses",arr_expenses);
            object.add("income",arr_income);
            result.close();
        }catch (SQLException e) {
            System.out.println("Error in getting user data" + e.getMessage());
            object.add("correct",new JsonPrimitive("ERROR"));
        }finally {
            object.add("correct",new JsonPrimitive("OK"));
        }
        return object;
    }
    /**
     * Collects all of the data about user
     * @param username Username
     * @return Returns a json with user's data
     */
    public static void addUserData(String username,JsonObject object){
        try {
            String category= object.get("category").getAsString();
            String type= object.get("type").getAsString();
            int amount= object.get("amount").getAsInt();
            String date= object.get("date").getAsString();
            int currency= object.get("currency").getAsInt();
            Main.statement.executeUpdate("INSERT " + Constants.USER_DATA_TABLE_NAME + "(" + Constants.USERNAME_COLUMN_NAME + "," +
                    Constants.TYPE_COLUMN_NAME + "," + Constants.CATEGORY_COLUMN_NAME + "," + Constants.AMOUNT_COLUMN_NAME + "," + Constants.DATE_COLUMN_NAME + "," +
                    Constants.CURRENCY_COLUMN_NAME + ") VALUES ('" + username + "','" + type + "','" + category + "','" + amount + "','" + date + "','" + currency + "')");
        }catch (SQLException e) {
            System.out.println("Error in getting user data" + e.getMessage());
            object.add("correct",new JsonPrimitive("ERROR"));
        }finally {
            object.add("correct",new JsonPrimitive("OK"));
        }
    }
}
