package chat.client;

import chat.ConsoleHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Golem765 on 11.05.2016.
 */
public class BotClient extends Client
{

    public static void main(String[] args)
    {
        new BotClient().run();
    }

    public class BotSocketThread extends Client.SocketThread
    {
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException
        {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message)
        {
            ConsoleHelper.writeMessage(message);
            if(message.contains(":"))
            {
                String[] sp = message.split(": ");
                String format;
                switch (sp[1])
                {
                    case "дата":
                        format = "d.MM.YYYY";
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
                        return;
                }
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                Calendar calendar = Calendar.getInstance();
                sendTextMessage(String.format("Информация для %s: %s",sp[0], sdf.format(calendar.getTime())));
            }
        }
    }

    @Override
    protected boolean shouldSentTextFromConsole()
    {
        return false;
    }

    @Override
    protected SocketThread getSocketThread()
    {
        return new BotSocketThread();
    }

    @Override
    protected String getUserName()
    {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return String.format("date_bot_%d", random.nextInt(0,99));
    }
}
