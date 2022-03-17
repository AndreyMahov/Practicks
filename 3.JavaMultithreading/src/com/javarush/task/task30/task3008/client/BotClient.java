package com.javarush.task.task30.task3008.client;

import com.javarush.task.task30.task3008.Message;
import com.javarush.task.task30.task3008.MessageType;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class BotClient  extends Client{


    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    @Override
    protected String getUserName() {
        int randome = (int) (Math.random() * 100);

        String name = "date_bot_" + randome  ;

        return name;
    }

    public class BotSocketThread extends SocketThread{

        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");


            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message) {
            super.processIncomingMessage(message);
            String[] words = message.split(": ");
            if (words.length != 2) return;

           String format = null;

            switch (words[1]){
                case "дата":
                    format = "d.MM.YYYY" ;
                    break;
                case "день":
                    format = "d";
                    break;
                case "месяц":
                    format = "MMMM";
                    break;
                case "год":
                    format = "YYYY";
                    break;
                case "время":
                    format = "H:mm:ss";
                    break;
                case "час":
                    format = "H";
                    break;
                case "минуты":
                    format = "m";
                    break;
                case "секунды":
                    format = "s";
                    break;
                default:
            }

            if (format != null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                Calendar calendar = new GregorianCalendar();
                BotClient.this.sendTextMessage( "Информация для " + words[0] + ": " + simpleDateFormat.format(calendar.getTime()));

            }


        }
    }

    public static void main(String[] args) {
        BotClient botClient = new BotClient();
        botClient.run();
    }
}
