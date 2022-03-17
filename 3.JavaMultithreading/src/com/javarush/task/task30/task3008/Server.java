package com.javarush.task.task30.task3008;

import javax.sound.sampled.Port;
import java.io.IOException;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static class Handler extends Thread{
       Socket socket;
        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {

            while (true) {
                connection.send(new Message(MessageType.NAME_REQUEST));
                Message message = connection.receive();
                if (message.getType() != MessageType.USER_NAME){
                    ConsoleHelper.writeMessage("Получено сообщение от " + socket.getRemoteSocketAddress() + ". Тип сообщения не соответствует протоколу.");
                    continue;
                }
               String userName = message.getData();

                if (userName.isEmpty()){
                    ConsoleHelper.writeMessage("Попытка подключения к серверу с пустым именем от " + socket.getRemoteSocketAddress());
                    continue;
                }
                if (connectionMap.containsKey(userName)){
                    ConsoleHelper.writeMessage("Попытка подключения к серверу с уже используемым именем от " + socket.getRemoteSocketAddress());

                    continue;
                }

                connectionMap.put(userName,connection);
                connection.send(new Message(MessageType.NAME_ACCEPTED));
                return userName;

            }
        }


        @Override
        public void run() {
            super.run();
         ConsoleHelper.writeMessage( "установлено новое соедение с адресом" + socket.getRemoteSocketAddress().toString());
          try (Connection connection = new Connection(socket);) {
             String userName =  serverHandshake(connection);
             sendBroadcastMessage(new Message(MessageType.USER_ADDED,userName));
              notifyUsers(connection,userName);
              serverMainLoop(connection,userName);
              if (userName != null){
              connectionMap.remove(userName);
              sendBroadcastMessage(new Message(MessageType.USER_REMOVED,userName));
              }

              ConsoleHelper.writeMessage("Соедение с удаленным адресом " + connection.getRemoteSocketAddress() + " закрыто");

          }catch (IOException  ioException){

          }
          catch (ClassNotFoundException classNotFoundException){

          }

        }

        public Handler(Socket socket){
           this.socket = socket;


       }
private void notifyUsers(Connection connection, String userName) throws IOException{

       for (String name: connectionMap.keySet()){
if (name.equals(userName)) continue;
connection.send(new Message(MessageType.USER_ADDED,name));
       }


}
private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException{
    while (true) {
        Message message = connection.receive();

        if (message.getType() == MessageType.TEXT) {
            String data = message.getData();
            sendBroadcastMessage(new Message(MessageType.TEXT, userName + ": " + data));
        } else {
            ConsoleHelper.writeMessage("Получено сообщение от " + socket.getRemoteSocketAddress() + ". Тип сообщения не соответствует протоколу.");
        }
    }
}


    }
    private static  Map<String, Connection> connectionMap = new ConcurrentHashMap<>();
    public static void sendBroadcastMessage(Message message){
        for (String key :connectionMap.keySet()){
            try {
                connectionMap.get(key).send(message);
            } catch (IOException e) {
                ConsoleHelper.writeMessage("Отправка сообщения не удалась");
            }
        }

    }

    public static void main(String[] args) throws IOException {
ConsoleHelper.writeMessage("Введите имя порта");
        int serverPort = ConsoleHelper.readInt();

        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            while (true){
                Socket socket = serverSocket.accept();
               new  Handler(socket).start();
            } }catch (Exception e){
            ConsoleHelper.writeMessage("Произошла ошибка при запуске или работе сервера.");
            }


        }
    }

