package com.blackwhitesoftware.pandalight.spec;

/**
 * Created by Fabian on 01.04.2015.
 */
public class SshCommand {
    public String command;

    public SshCommand() {
        command = "";
    }

    public SshCommand(String string) {
        command = string;
    }

    public String getCommand() {
        return command;
    }

}
