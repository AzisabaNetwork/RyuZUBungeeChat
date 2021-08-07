package ryuzubungeechat.ryuzubungeechat;

import com.google.gson.Gson;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.MessageDeleteEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import reactor.core.Disposable;

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
    public List<ChannelBot> ChannelBots = new ArrayList<>();

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
    }

    public ChatGroups(List<String> servers, String format , String channelformat , String tellformat , ChatLogBot adminbot , ChatLogBot memberbot) {
        this.servers = servers;
        this.format = format;
        this.channelformat = channelformat;
        this.tellformat = tellformat;
        this.adminbot = adminbot;
        this.memberbot = memberbot;
    }

    public ChatGroups(List<String> servers, String format , String channelformat , String tellformat , ChatLogBot adminbot , ChatLogBot memberbot , List<ChannelBot> channelbots) {
        this.servers = servers;
        this.format = format;
        this.channelformat = channelformat;
        this.tellformat = tellformat;
        this.adminbot = adminbot;
        this.memberbot = memberbot;
        this.ChannelBots = channelbots;
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
