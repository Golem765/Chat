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
            sendTextMessage("Hi chat. I am bot. Understand commands: date, day, month, year, time, hour, minutes, seconds.");
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
                    case "date":
                        format = "d.MM.YYYY";
                        break;
                    case "day":
                        format = "d";
                        break;
                    case "month":
                        format = "MMMM";
                        break;
                    case "year":
                        format = "YYYY";
                        break;
                    case "time":
                        format = "H:mm:ss";
                        break;
                    case "hour":
                        format = "H";
                        break;
                    case "minutes":
                        format = "m";
                        break;
                    case "seconds":
                        format = "s";
                        break;
                    default:
                        return;
                }
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                Calendar calendar = Calendar.getInstance();
                sendTextMessage(String.format("Information for %s: %s",sp[0], sdf.format(calendar.getTime())));
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
