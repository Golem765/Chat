package chat;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by Golem765 on 26.04.2016.
 */
public class Connection implements Closeable
{
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public Connection(Socket socket)throws IOException
    {
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    public void send(Message message)throws IOException
    {
        synchronized (out)
        {
            out.writeObject(message);
        }
    }

    public Message receive()throws IOException, ClassNotFoundException
    {
        Message ret;
        synchronized (in)
        {
            ret = (Message)in.readObject();
        }
        return ret;
    }

    public SocketAddress getRemoteSocketAddress()
    {
        return socket.getRemoteSocketAddress();
    }

    @Override
    public void close() throws IOException
    {
        socket.close();
        in.close();
        out.close();
    }
}
