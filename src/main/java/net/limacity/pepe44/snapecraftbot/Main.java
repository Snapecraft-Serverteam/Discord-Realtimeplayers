package net.limacity.pepe44.snapecraftbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.Timer;

public class Main {
    public static Timer timer = new Timer();
    public static boolean isSetup = false;
    public static Message msg;
    public static File config = new File("config.json");
    public static void main(String[] args) throws LoginException {
        if(!config.exists()) {
            try {
                config.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        JDABuilder builder = new JDABuilder(args[0]);
        builder.addEventListeners(new Listener());
        JDA bot = builder.build();
        Data.loadData(bot);
        if(isSetup) {
            Data.refreshData();
            Listener.startTimer();
        }
    }
}