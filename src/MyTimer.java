/**
 * Created by casde on 13-6-2016.
 */
public class MyTimer extends Thread
{
    private long millisec;
    private boolean isDone;
    public MyTimer(long millis)
    {
        millisec = millis;
        isDone = false;
    }

    public void run()
    {
        while(!isDone)
        {
            millisec--;
            if(millisec <= 0)
            {
                isDone = true;
            }

            try{Thread.sleep(1);}catch(Exception e){e.printStackTrace();}
        }
    }

    public boolean isDone()
    {
        return isDone;
    }

    public long getMillis()
    {
        return millisec;
    }

    public static void main(String[] arg)
    {

    }
}
