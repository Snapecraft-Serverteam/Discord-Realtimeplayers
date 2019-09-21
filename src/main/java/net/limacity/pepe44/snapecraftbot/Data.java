package net.limacity.pepe44.snapecraftbot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Data {
    static int targetlenght = 30;
    public static void refreshData() {
        if(Main.isSetup == false) { return; }
        String data = null;
        EmbedBuilder output = new EmbedBuilder();
        output.setTitle("Unsere Server:");
        output.setColor(Color.GREEN);
        try {
             data = readStringFromURL("http://snapecraft.net:8000/playerlist");
            System.out.println(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(data == null) { return; }

        JSONObject json = new JSONObject(data);
        int full = 0;
        for (String key: json.keySet()) {
            JSONArray array = json.getJSONArray(key);
            full = full + array.length();
            output.addField(key, "Online: " + array.length(), true);
            //output = output + key + ":" + array.length() + " Spieler Online.\n";
        }
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss dd. MM. yyyy");
        output.setFooter("Zuletzt aktuallisiert: " + format.format(new Date()));
        output.setTitle("Unsere Server: " + full + " Spieler online");
        Main.msg.editMessage(output.build()).queue();
    }




    public static String readStringFromURL(String requestURL) throws IOException
    {
        try (Scanner scanner = new Scanner(new URL(requestURL).openStream(),
                StandardCharsets.UTF_8.toString()))
        {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }

    public static void save() {
        System.out.println("SAVE!");
        if(Main.isSetup == false) { return; }
        String msg = Main.msg.getId();
        String channel = Main.msg.getChannel().getId();
        String guild = Main.msg.getGuild().getId();
        JSONObject json = new JSONObject();
        json.put("guild", guild);
        json.put("channel", channel);
        json.put("msg", msg);
        write(json.toString());
    }

    private static void write(String data) {
        try {
            Files.write(Paths.get("config.json"), data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadData(JDA jda) {
        System.out.println("LOAD!");
        if(Main.config.exists()) {
            String content = null;
            try {
                 content = readFile(Main.config.getPath(), Charset.defaultCharset());
            } catch (IOException e) {
                return;
            }
            if(content == null) { return; }


            Message target = null;
            JSONObject data = new JSONObject();
            try {
                 data = new JSONObject(content);
            } catch (JSONException e) {
               e.printStackTrace();
            }




            String guildid = data.getString("guild");
            String channelid = data.getString("channel");
            String msgid = data.getString("msg");

            if(guildid == null) { return; }
            if(channelid == null) { return; }
            if(msgid == null) { return; }
            System.out.println(guildid);
            Guild guild = jda.getGuildById(guildid);

            if(guild == null) {
                System.out.println("Guild 0"); return; }

            TextChannel channel = guild.getTextChannelById(channelid);

            if(channel == null) { return; }

            MessageHistory history = new MessageHistory(channel);

            if(history == null) { return; }



            List<Message> messages = history.retrievePast(history.size()).complete();

            if(messages.size() == 0) {
                System.out.println("Size 0"); return; }


            for (Message msg : messages) {
                if(msg.getId() == msgid) {
                    target = msg;
                    break;
                }
            }
            if(target == null) { return; }
            System.out.println("FOUND!");
            Main.msg = target;
            Main.isSetup = true;

        }

    }

    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
