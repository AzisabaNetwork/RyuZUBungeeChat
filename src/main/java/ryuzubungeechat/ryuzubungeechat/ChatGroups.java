package ryuzubungeechat.ryuzubungeechat;

import com.google.gson.Gson;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.MessageDeleteEvent;
import discord4j.core.object.entity.channel.MessageChannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatGroups {
    public List<String> servers;
    public String format;
    public String channelformat;
    public String tellformat;
    public ChatLogBot adminbot;
    public ChatLogBot memberbot;

    public ChatGroups(List<String> servers, String format , String channelformat , String tellformat) {
        this.servers = servers;
        this.format = format;
        this.channelformat = channelformat;
        this.tellformat = tellformat;
    }

    public ChatGroups(List<String> servers, String format , String channelformat , String tellformat , ChatLogBot adminbot) {
        this.servers = servers;
        this.format = format;
        this.channelformat = channelformat;
        this.tellformat = tellformat;
        this.adminbot = adminbot;

        adminbot.gateway.on(MessageCreateEvent.class).subscribe(event -> {
            if(adminbot.channel.getId().asLong() != event.getMessage().getChannelId().asLong() || event.getMessage().getAuthor().get().isBot()) {return;}
            MessageChannel channel1 = event.getMessage().getChannel().block();
            if(channel1 == null) {return;}
            if (channel1.equals(adminbot.channel)) {
                Map<String , String> map = new HashMap<>();
                Gson gson = new Gson();
                map.put("System" , "Chat");
                map.put("Message" , event.getMessage().getContent());
                map.put("Discord" , event.getMessage().getAuthor().get().getUsername());
                servers.forEach(s -> RyuZUBungeeChat.RBC.sendPluginMessage(s , "ryuzuchat:ryuzuchat" , gson.toJson(map)));
            }
        });
    }

    public ChatGroups(List<String> servers, String format , String channelformat , String tellformat , ChatLogBot adminbot , ChatLogBot memberbot) {
        this.servers = servers;
        this.format = format;
        this.channelformat = channelformat;
        this.tellformat = tellformat;
        this.adminbot = adminbot;
        this.memberbot = memberbot;
        adminbot.gateway.on(MessageCreateEvent.class).subscribe(event -> {
            if(adminbot.channel.getId().asLong() != event.getMessage().getChannelId().asLong() || event.getMessage().getAuthor().get().isBot()) {return;}
            MessageChannel channel1 = event.getMessage().getChannel().block();
            if(channel1 == null) {return;}
            if (channel1.equals(adminbot.channel)) {
                Map<String , String> map = new HashMap<>();
                Gson gson = new Gson();
                map.put("System" , "Chat");
                map.put("Message" , event.getMessage().getContent());
                map.put("Discord" , event.getMessage().getAuthor().get().getUsername());
                servers.forEach(s -> RyuZUBungeeChat.RBC.sendPluginMessage(s , "ryuzuchat:ryuzuchat" , gson.toJson(map)));
            }
        });
        memberbot.gateway.on(MessageCreateEvent.class).subscribe(event -> {
            if(memberbot.channel.getId().asLong() != event.getMessage().getChannelId().asLong() || event.getMessage().getAuthor().get().isBot()) {return;}
            MessageChannel channel1 = event.getMessage().getChannel().block();
            if(channel1 == null) {return;}
            if (channel1.equals(memberbot.channel)) {
                Map<String , String> map = new HashMap<>();
                Gson gson = new Gson();
                map.put("System" , "Chat");
                map.put("Message" , event.getMessage().getContent());
                map.put("Discord" , event.getMessage().getAuthor().get().getUsername());
                servers.forEach(s -> RyuZUBungeeChat.RBC.sendPluginMessage(s , "ryuzuchat:ryuzuchat" , gson.toJson(map)));
                adminbot.sendLogMessage(map , ChatLogBot.SendType.Discord);
            }
        });
    }

    public void sendLogMessage(Map<String , String> map) {
        if(adminbot == null) {return;}
        if(map.containsKey("ReceivePlayerName")) {return;}
        if(map.containsKey("ReceivedPlayerName")) {
            adminbot.sendLogMessage(map , ChatLogBot.SendType.Private);
            return;
        }
        if(map.containsKey("ChannelName")) {
            adminbot.sendLogMessage(map , ChatLogBot.SendType.Channel);
            return;
        }
        adminbot.sendLogMessage(map , ChatLogBot.SendType.Global);
        if(memberbot == null) {return;}
        memberbot.sendLogMessage(map , ChatLogBot.SendType.Global);
    }
}
