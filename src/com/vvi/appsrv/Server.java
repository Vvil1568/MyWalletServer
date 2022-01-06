package com.vvi.appsrv;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.vvi.appsrv.api.DatabaseAPI;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;

public class Server {

    private int port;

    public Server(int port) {
        this.port = port;
    }

    public void startServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", this::requestHandler);
            server.start();
            System.out.println("Successfully started http-server");
        } catch (IOException e) {
            System.out.println("Error while creating an http-server");
            e.printStackTrace();
        }
    }

    public void requestHandler(HttpExchange exchange) {
        try {
            String uri = exchange.getRequestURI().toString();
            //uri.replaceFirst("/","");
            uri = uri.substring(2);
            String[] param_strings = uri.split("&");
            HashMap<String, String> params = new HashMap<>();
            for (String par : param_strings) {
                String[] pair = par.split("=");
                params.put(pair[0], pair[1]);
            }

            String type = params.get("reqType");
            JsonObject response = new JsonObject();

            if (type.equals("verify")) {
                boolean result = DatabaseAPI.verifyUser(params.get("username"), params.get("hash"));
                response.add("result", new JsonPrimitive(result));
            }
            if (type.equals("register")) {
                int result = DatabaseAPI.registerUserWithChecks(params.get("username"), params.get("hash"));
                if (result == 0)
                    response.add("result", new JsonPrimitive("OK"));
                else if (result == 1)
                    response.add("result", new JsonPrimitive("EXISTS"));
                else
                    response.add("result", new JsonPrimitive("ERROR"));
            }
            if (type.equals("getData")) {
                boolean result = DatabaseAPI.verifyUser(params.get("username"), params.get("hash"));
                if (result)
                    response = DatabaseAPI.getUserData(params.get("username"));
                else
                    response.add("result", new JsonPrimitive("PERMISSION DENIED"));
            }
            if (type.equals("addData")) {
                boolean result = DatabaseAPI.verifyUser(params.get("username"), params.get("hash"));
                if (result) {
                    DatabaseAPI.addUserData(params.get("username"), params.get("category"),params.get("type"),
                            Integer.parseInt(params.get("amount")), params.get("date"),Integer.parseInt(params.get("currency")));
                    response.add("result", new JsonPrimitive("OK"));
                } else
                    response.add("result", new JsonPrimitive("PERMISSION DENIED"));
            }
            byte[] reply = response.toString().getBytes();
            exchange.sendResponseHeaders(200, reply.length);
            exchange.getResponseBody().write(reply);
            exchange.close();
        } catch (IOException | IndexOutOfBoundsException e) {
            System.out.println("Error while handling http request with uri " + exchange.getRequestURI());
            try {
                exchange.sendResponseHeaders(400, 0);
                exchange.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
