package ryuzubungeechat.ryuzubungeechat;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class RyuZUBungeeChat extends Plugin implements Listener {
    public static RyuZUBungeeChat RBC;
    private static HashMap<String , List<String>> ServerGroups = new HashMap<>();

    @Override
    public void onEnable() {
        RBC = this;
        reloadConfig();
        getProxy().getPluginManager().registerListener(this, this);
        getProxy().getPluginManager().registerCommand(this , new ryuzubungeechat.ryuzubungeechat.Command("rbc"));
        getProxy().registerChannel("ryuzuchat:ryuzuchat");
        getLogger().info("Bungee版リューズは天才が起動したぞ!");
    }

    @EventHandler
    public void onPluginMessageReceived(PluginMessageEvent event) {
        System.out.println("堂田三鷹");
        if (event.getTag().equals("ryuzuchat:ryuzuchat")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
            String data = in.readUTF();
            Map<String , String> map = (Map<String, String>) jsonToMap(data);
            String name = map.get("ServerName");
            if(!ServerGroups.containsKey(name)) {
                return;
            }
            ServerGroups.get(name).stream().forEach(l -> sendPluginMessage(l , "ryuzuchat:ryuzuchat" , data));
        }
    }

    private void sendPluginMessage(String server, String channel, String data) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(data);

        getProxy().getServerInfo(server).sendData(channel, out.toByteArray());
    }

    public void reloadConfig() {
        ServerGroups.clear();
        Configuration config = null;
        File file = new File(getDataFolder(), "config.yml");
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(config != null) {
            Configuration finalConfig = config;
            config.getKeys().stream().forEach(l -> ServerGroups.put(l , finalConfig.getStringList(l)));
        }
    }

    private String mapToJson(Map<String, String> map) {
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    private Map<String,?> jsonToMap(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Map.class);
    }
}
