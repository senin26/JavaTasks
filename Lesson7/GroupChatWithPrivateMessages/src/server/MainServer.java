package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.Vector;

public class MainServer {

    Vector<ClientHandler> clients;

    public MainServer() throws SQLException {

        ServerSocket server = null;
        Socket socket = null;
        clients = new Vector<>();

        try {
            AuthService.connect();

            server = new ServerSocket(8189);
            System.out.println("Сервер запущен!");

            while (true) {
                socket = server.accept();
                System.out.println("Клиент подключился!");
                // создаем нового клиента
               new ClientHandler(this, socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            AuthService.disconnect();
        }

    }
    // метод для рассылки сообщения всем клиентам
    public void broadCastMsg(String msg) {
        for (ClientHandler o: clients) {
            o.sendMsg(msg);
        }
    }

    //метод для рассылки приватного сообщения
    public void privateMsg(String msg, String sender, String recipient) {
        int senderIndex = 0;
        for (ClientHandler o: clients) {
            if (o.getNick().equals(sender)){
                break;
            }
            senderIndex++;
        }

        int recipientIndex = 0;
        for (ClientHandler o: clients) {
            if (o.getNick().equals(recipient)){
                break;
            }
            recipientIndex++;
        }
        clients.get(senderIndex).sendMsg(msg);
        clients.get(recipientIndex).sendMsg(msg);
    }

    // подписываем клиента и добавляем его в список клиентов
    public void subscribe(ClientHandler client) {
        clients.add(client);
    }

    // отписываем клиента и удаляем его из списка клиентов
    public void unsubscribe(ClientHandler client) {
        clients.remove(client);
    }

}
