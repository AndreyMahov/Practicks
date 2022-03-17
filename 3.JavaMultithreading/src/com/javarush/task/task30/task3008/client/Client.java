package com.javarush.task.task30.task3008.client;

import com.javarush.task.task30.task3008.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {
    protected Connection connection;
    private volatile boolean clientConnected = false;

    protected String getServerAddress() {

        return ConsoleHelper.readString();
    }

    protected int getServerPort() {

        return ConsoleHelper.readInt();
    }

    protected String getUserName() {
        return ConsoleHelper.readString();
    }

    protected boolean shouldSendTextFromConsole() {
        return true;
    }

    protected SocketThread getSocketThread() {
        return new SocketThread();
    }

    protected void sendTextMessage(String text) {
        try {
            connection.send(new Message(MessageType.TEXT, text));

        } catch (IOException ioException) {

            clientConnected = false;
        }
    }

    public void run() {
        SocketThread socketThread = getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();
        try {
            synchronized (this){
                wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
        while (clientConnected) {
            String text = ConsoleHelper.readString();
            if (text.equalsIgnoreCase("exit")) {
                break;
            }
            if (shouldSendTextFromConsole()) {
                sendTextMessage(text);
            }
        }


    }

    public  class SocketThread extends Thread {
        public void run(){
            try {
                Socket socket = new Socket(getServerAddress(), getServerPort());
                 connection = new Connection(socket);
                clientHandshake();
                clientMainLoop();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                notifyConnectionStatusChanged(false);
            }

            ;
        }



protected void processIncomingMessage(String message){
    ConsoleHelper.writeMessage(message);

}
   protected void informAboutAddingNewUser(String userName){
    ConsoleHelper.writeMessage("участник с именем " + userName +  " присоединился к чату.");
   }

   protected  void informAboutDeletingNewUser(String userName){
       ConsoleHelper.writeMessage("участник с именем " + userName +  " покинул чат.");
   }

   protected void notifyConnectionStatusChanged(boolean clientConnected){
       Client.this.clientConnected = clientConnected;
       synchronized (Client.this){
           Client.this.notify();
       }

   }

   protected void clientHandshake() throws IOException, ClassNotFoundException{
while (true){

    Message message = connection.receive();
    if (message.getType() == MessageType.NAME_REQUEST){
       String name =  getUserName();
        connection.send(new Message(MessageType.USER_NAME, name));

    }
    else if(message.getType() == MessageType.NAME_ACCEPTED){
        notifyConnectionStatusChanged(true);
return;
    }
    else {throw new IOException("Unexpected MessageType");}
    
}
   }

   protected void clientMainLoop() throws IOException, ClassNotFoundException{
    while (true){
        Message message = connection.receive();
        if (message.getType() == MessageType.TEXT){
            processIncomingMessage(message.getData());

        }
        else if (message.getType() == MessageType.USER_ADDED){
            informAboutAddingNewUser(message.getData());

        }
        else if (message.getType()==MessageType.USER_REMOVED){
            informAboutDeletingNewUser(message.getData());

        }
        else {
            throw new IOException("Unexpected MessageType");
        }

    }
   }
}




    public static void main(String[] args) {
Client client = new Client();
client.run();


    }
}