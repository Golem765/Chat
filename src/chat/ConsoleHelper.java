package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Golem765 on 26.04.2016.
 */
public class ConsoleHelper
{
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void writeMessage(String message)
    {
        System.out.println(message);
    }

    public static String readString()
    {
        boolean success = false;
        String ret = "";
        while(!success)
        {
            try
            {
                ret = reader.readLine();
                success = true;
            }
            catch (IOException ioexception)
            {
                System.out.println("Произошла ошибка при попытке ввода текста. Попробуйте еще раз.");
            }
        }
        return ret;
    }

    public static int readInt()
    {
        boolean success = false;
        int ret = 0;
        while(!success)
        {
            try
            {
                ret = Integer.parseInt(readString());
                success = true;
            }
            catch (NumberFormatException nfe)
            {
                System.out.println("Произошла ошибка при попытке ввода числа. Попробуйте еще раз.");
            }
        }
        return ret;
    }
}
