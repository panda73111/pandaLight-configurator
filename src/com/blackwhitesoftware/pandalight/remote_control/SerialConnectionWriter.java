package com.blackwhitesoftware.pandalight.remote_control;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by sebastian on 09.10.15.
 */
public class SerialConnectionWriter implements Runnable {
    OutputStream out;

    public SerialConnectionWriter ( OutputStream out )
    {
        this.out = out;
    }

    @Override
    public void run ()
    {
        try
        {
            int c = 0;
            while ( ( c = System.in.read()) > -1 )
            {
                this.out.write(c);
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }
}
