package chat.client;

import chat.Connection;
import chat.ConsoleHelper;
import chat.Message;
import chat.MessageType;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Golem765 on 03.05.2016.
 */
public class Client
{
    protected Connection connection;

    private volatile boolean clientConnected = false;

    public static void main(String[] args)
    {
        Client client = new Client();
        client.run();
    }

    public class SocketThread extends Thread
    {
        @Override
        public void run()
        {
            String address = getServerAddress();
            int port = getServerPort();
            try
            {
                Socket socket = new Socket(address, port);
                connection = new Connection(socket);
                clientHandshake();
                clientMainLoop();
            }
            catch (IOException|ClassNotFoundException e)
            {
                notifyConnectionStatusChanged(false);
            }
        }

        protected void processIncomingMessage(String message)
        {
            ConsoleHelper.writeMessage(message);
        }

        protected void informAboutAddingNewUser(String userName)
        {
            ConsoleHelper.writeMessage(String.format("%s has connected to the chat.",userName));
        }

        protected void informAboutDeletingNewUser(String userName)
        {
            ConsoleHelper.writeMessage(String.format("%s has left the chat.",userName));
        }

        protected void notifyConnectionStatusChanged(boolean clientConnected)
        {
            Client.this.clientConnected = clientConnected;
            synchronized (Client.this)
            {
                Client.this.notify();
            }
        }

        protected void clientHandshake() throws IOException, ClassNotFoundException
        {
            while(true)
            {
                Message m = connection.receive();
                switch (m.getType())
                {
                    case NAME_REQUEST:
                        Message send = new Message(MessageType.USER_NAME, getUserName());
                        connection.send(send);
                        break;
                    case NAME_ACCEPTED:
                        notifyConnectionStatusChanged(true);
                        return;
                    default:
                        throw new IOException("Unexpected MessageType");
                }
            }
        }

        protected void clientMainLoop() throws IOException, ClassNotFoundException
        {
            while(true)
            {
                Message m = connection.receive();
                switch (m.getType())
                {
                    case TEXT:
                        processIncomingMessage(m.getData());
                        break;
                    case USER_ADDED:
                        informAboutAddingNewUser(m.getData());
                        break;
                    case USER_REMOVED:
                        informAboutDeletingNewUser(m.getData());
                        break;
                    default:
                        throw new IOException("Unexpected MessageType");
                }
            }
        }
    }

    public void run()
    {
        SocketThread socketThread = getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();
        try
        {
            synchronized (this)
            {
                wait();
            }
        }
        catch (InterruptedException ie)
        {
            ConsoleHelper.writeMessage("Problem occurred on waiting for notification");
            return;
        }
        if(clientConnected)
        {
            ConsoleHelper.writeMessage("Connection established, for exiting enter 'exit'.");
        }
        else
        {
            ConsoleHelper.writeMessage("Problem occurred on working with server.");
        }
        String s;
        while(clientConnected)
        {
            s = ConsoleHelper.readString();
            if(s.equals("exit"))
                break;
            if(shouldSentTextFromConsole())
                sendTextMessage(s);
        }
    }

    protected String getServerAddress()
    {
        ConsoleHelper.writeMessage("Please, enter ip if you are connecting to remote server or 'localhost' if, well, you are connecting to yourself:");
        return ConsoleHelper.readString();
    }

    protected int getServerPort()
    {
        ConsoleHelper.writeMessage("Enter server's port please:");
        return ConsoleHelper.readInt();
    }

    protected String getUserName()
    {
        ConsoleHelper.writeMessage("Please, enter you name or nickname:");
        return ConsoleHelper.readString();
    }

    /**
     *
     * @return true, always in this class
     * Please be sure to override it if you need another functionality
     */
    protected boolean shouldSentTextFromConsole()
    {
        return true;
    }

    protected SocketThread getSocketThread()
    {
        return new SocketThread();
    }

    protected void sendTextMessage(String text)
    {
        Message send = new Message(MessageType.TEXT, text);
        try
        {
            connection.send(send);
        }
        catch (IOException io)
        {
            ConsoleHelper.writeMessage("Problem occurred on trying to send message");
            clientConnected = false;
        }
    }
}
