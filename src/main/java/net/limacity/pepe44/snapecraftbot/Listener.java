package net.limacity.pepe44.snapecraftbot;


import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.utils.Checks;

import java.util.List;
import java.util.TimerTask;

public class Listener extends ListenerAdapter {
    String krasseLine = "Snapecraft Status";

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        System.out.println("Message Received!");


        if (event.getAuthor().isBot()){

            if(event.getAuthor().getId().equals("615224075769479191")) {

                if(event.getMessage().getContentDisplay().equals(krasseLine)) {
                    Main.msg = event.getMessage();
                    Main.isSetup = true;
                    Data.save();
                    Data.refreshData();
                    startTimer();
                }

            }

        }
        if (event.getMessage().getContentDisplay().startsWith("?setup")) {
            Role r = getHighestFrom(event.getGuild().getMember(event.getAuthor()));
            if(r == null) {
                event.getChannel().sendMessage("Dazu hast du keine Rechte!").queue();
            }
            if(r.hasPermission(Permission.ADMINISTRATOR)) {

                event.getChannel().sendMessage(krasseLine).queue();


            } else {
                event.getChannel().sendMessage("Dazu hast du keine Rechte!").queue();
            }
        }
        return;

    }

    public static void startTimer() {
        Main.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Data.refreshData();
                startTimer();
            }
        }, 10*1000);
    }


    public static Role getHighestFrom(Member member) {
        Checks.notNull(member, "Member object can not be null");

        List<Role> roles = member.getRoles();
        if (roles.isEmpty()) {
            return null;
        }

        return roles.stream().sorted((first, second) -> {
            if (first.getPosition() == second.getPosition()) {
                return 0;
            }
            return first.getPosition() > second.getPosition() ? -1 : 1;
        }).findFirst().get();
    }



}
