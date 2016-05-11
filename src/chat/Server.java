package chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Golem765 on 26.04.2016.
 */
public class Server
{
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();
    public static void main(String[] args)
    {
        ConsoleHelper.writeMessage("Enter port number please:");
        int port = ConsoleHelper.readInt();
        try(ServerSocket serverSocket = new ServerSocket(port))
        {
            ConsoleHelper.writeMessage("chat.Server has started");
            while (true)
            {
                new Handler(serverSocket.accept()).start();
            }
        }
        catch (IOException io)
        {
            ConsoleHelper.writeMessage(io.toString());
        }
    }

    public static void sendBroadcastMessage(Message message)
    {
        for(Connection connection : connectionMap.values())
        {
            try
            {
                connection.send(message);
            }
            catch (IOException io)
            {
                ConsoleHelper.writeMessage(String.format("Couldn't send message to connection:%s", connection.toString()));
            }
        }
    }

    private static class Handler extends Thread
    {
        private Socket socket;
        public Handler(Socket socket)
        {
            this.socket = socket;
        }

        @Override
        public void run()
        {
            String name = null;
            try(Connection connection = new Connection(socket))
            {
                SocketAddress socketAddress = connection.getRemoteSocketAddress();
                ConsoleHelper.writeMessage(String.format("Established connection with: %s",socketAddress));
                name = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, name));
                sendListOfUsers(connection, name);
                serverMainLoop(connection, name);
            }
            catch (IOException | ClassNotFoundException e)
            {
                ConsoleHelper.writeMessage("Error on exchanging data with remote address");
            }
            if(name != null&&!(name.isEmpty()))
            {
                connectionMap.remove(name);
                sendBroadcastMessage(new Message(MessageType.USER_REMOVED, name));
            }
            ConsoleHelper.writeMessage("chat.Connection to remote address closed.");
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException
        {
            boolean success = false;
            String name = null;
            while(!success)
            {
                connection.send(new Message(MessageType.NAME_REQUEST));
                Message wrk = connection.receive();
                if (wrk.getType() == MessageType.USER_NAME)
                {
                    name = wrk.getData();
                    if (name != null && !name.isEmpty() && !(connectionMap.containsKey(name)))
                    {
                        connectionMap.put(name, connection);
                        connection.send(new Message(MessageType.NAME_ACCEPTED));
                        success = true;
                    }
                }
            }
            return name;
        }

        private void sendListOfUsers(Connection connection, String userName) throws IOException
        {
            for(Map.Entry<String, Connection> pair : connectionMap.entrySet())
            {
                if(!(pair.getKey().equals(userName)))
                {
                    connection.send(new Message(MessageType.USER_ADDED, pair.getKey()));
                }
            }
        }
        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException
        {
            while(true)
            {
                Message msg = connection.receive();
                if (msg.getType() == MessageType.TEXT)
                {
                    String data = msg.getData();
                    StringBuilder sb = new StringBuilder(userName);
                    sb.append(": ");
                    sb.append(data);
                    sendBroadcastMessage(new Message(MessageType.TEXT, sb.toString()));
                } else
                {
                    ConsoleHelper.writeMessage("Error");
                }
            }
        }


    }
}
