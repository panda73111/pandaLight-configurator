package com.blackwhitesoftware.pandalight.remote_control;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sebastian on 09.10.15.
 */
public class SerialConnectionReader implements Runnable {
    InputStream in;

    public SerialConnectionReader ( InputStream in )
    {
        this.in = in;
    }

    @Override
    public void run ()
    {
        byte[] buffer = new byte[1024];
        int len;
        try
        {
            while ( ( len = this.in.read(buffer)) > -1 )
            {
                System.out.print(new String(buffer,0,len));
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }
}
