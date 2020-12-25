package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class Server {

    private static int PORT = 8189;
    ServerSocket server = null;
    Socket socket = null;
    List<ClientHandler> clients;
    private AuthService authService;

    public Server() throws SQLException {
        clients = new Vector<>();

        if(!SQLHandler.connect()) {
            throw new RuntimeException("Не удалось подключится");
        }
        authService = new SimpleAuthService();

        try {
            server = new ServerSocket(PORT);
            System.out.println("Сервер запущен");

            while (true) {
                socket = server.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            SQLHandler.disconnect();
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void broadCastMsg(ClientHandler sender, String msg) {
        SimpleDateFormat formater = new SimpleDateFormat("HH:mm:ss");

        String message = String.format("%s %s : %s", formater.format(new Date()), sender.getNickname(), msg);
        for (ClientHandler client : clients) {
            client.sendMsg(message + "\n");
        }
    }

    public void subscribe(ClientHandler clientHandler){
        clients.add(clientHandler);
        broadClientList();
    }

    public void unsubscribe(ClientHandler clientHandler){
        clients.remove(clientHandler);
        broadClientList();
    }

    public AuthService getAuthService(){
        return authService;
    }

    public void privateCastMsg(ClientHandler sender, String receiver,  String msg) {
        String message = String.format("[%s] private [%s] : %s", sender.getNickname(), receiver, msg);
        for (ClientHandler c : clients) {
            if(c.getNickname().equals(receiver)){
                c.sendMsg(message + "\n");
                if(!c.equals(sender)) {
                    sender.sendMsg(message);
                }
                return;
            }
        }
    }

    public boolean isLoginAuthenticated(String login){
        for (ClientHandler c: clients) {
            if(c.getLogin().equals(login)){
                return true;
            }
        }
        return false;
    }

    public void broadClientList() {
        StringBuilder sb = new StringBuilder("/clientList ");
        for(ClientHandler c: clients){
            sb.append(c.getNickname()).append(" ");
        }
        String msg = sb.toString();
        for (ClientHandler c: clients){
            c.sendMsg(msg);
        }
    }
}
