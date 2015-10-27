package com.blackwhitesoftware.pandalight;


import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Fabian on 15.02.2015.
 */
public class ErrorHandling {

    public static JFrame mainframe;

    public static void ShowException(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String exceptionAsString = sw.toString();

        ShowMessage(e.getMessage() + "\n\n" + exceptionAsString);
    }

    public static void ShowMessage(String Message) {
        ShowMessage(Message, "Error");
    }

    public static void ShowMessage(String Message, String title) {
        JOptionPane.showMessageDialog(mainframe, Message, title, JOptionPane.ERROR_MESSAGE);
    }


}
