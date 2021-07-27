package ryuzubungeechat.ryuzubungeechat;

import com.github.ucchyocean.lc.lib.org.jetbrains.annotations.NonNls;
import com.google.gson.Gson;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.MessageChannel;
import net.md_5.bungee.api.ChatColor;
import reactor.util.annotation.NonNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatLogBot {
    public enum SendType{Global,Channel,Private}
    public GatewayDiscordClient gateway;
    public MessageChannel channel;

    public ChatLogBot(@NonNull String token , @NonNull Long channnel) {
        DiscordClient client = DiscordClient.create(token);
        gateway = client.login().block();
        channel = (MessageChannel) gateway.getChannelById(Snowflake.of(channnel)).block();;
    }

    public void sendLogMessage(Map<String , String> map , SendType type) {
        String msg = setFormat(map);
        if(type.equals(SendType.Global)) {
            msg = "(" + map.get("SendServerName") + ")" +  " --> " + msg;
        } else if(type.equals(SendType.Private)) {
            msg = "(" + map.get("SendServerName") + ")" +  " --> " + msg + " --> " +
                    map.getOrDefault("ReceivedPlayerLuckPermsPrefix", "") +
                    map.getOrDefault("ReceivedPlayerRyuZUMapPrefix", "") +
                    (map.getOrDefault("ReceivedPlayerDisplayName", map.getOrDefault("ReceivedPlayerName", ""))) +
                    map.getOrDefault("ReceivedPlayerLuckPermsSuffix", "") +
                    map.getOrDefault("ReceivedPlayerRyuZUMapSuffix", "") +
                    " --> " + "(" +  map.get("ReceiveServerName") + ")";
        } else {
            msg = "(" + map.get("SendServerName") + ")" + "[" + map.get("ChannelName") + "]" +  " --> " + msg;
        }

        channel.createMessage(msg).block();
    }

    private String setFormat(Map<String , String> map) {
        String msg = map.get("Format");
        msg = msg.replace("[LuckPermsPrefix]", map.getOrDefault("LuckPermsPrefix", ""))
                .replace("[LunaChatPrefix]", map.getOrDefault("LunaChatPrefix", ""))
                .replace("[RyuZUMapPrefix]", map.getOrDefault("RyuZUMapPrefix", ""))
                .replace("[SendServerName]", map.getOrDefault("SendServerName", ""))
                .replace("[ReceiveServerName]", map.getOrDefault("ReceiveServerName", ""))
                .replace("[PlayerName]", (map.getOrDefault("PlayerDisplayName", map.getOrDefault("PlayerName", ""))))
                .replace("[RyuZUMapSuffix]", map.getOrDefault("RyuZUMapSuffix", ""))
                .replace("[LunaChatSuffix]", map.getOrDefault("LunaChatSuffix", ""))
                .replace("[LuckPermsSuffix]", map.getOrDefault("LuckPermsSuffix", ""));
        msg = setColor(msg);
        msg = msg.replace("[PreReplaceMessage]", (Boolean.parseBoolean(map.get("CanJapanese")) ? "(" + map.get("PreReplaceMessage") + ")" : ""))
                .replace("[Message]", map.getOrDefault("Message", ""))
                .replace("§" , "");
        return msg;
    }

    private String setColor(String msg) {
        String replaced = msg;
        replaced = replaceToHexFromRGB(replaced);
        return ChatColor.stripColor(replaced);
    }

    private String replaceToHexFromRGB(String text) {
        String regex = "\\{.+?}";
        List<String> RGBcolors = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            RGBcolors.add(matcher.group());
        }
        for(String hexcolor : RGBcolors) {
            text = text.replace(hexcolor , "");
        }
        return text;
    }
}
