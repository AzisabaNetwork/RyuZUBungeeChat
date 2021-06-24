package ryuzubungeechat.ryuzubungeechat;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Server;
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
import java.util.stream.Collectors;

public final class RyuZUBungeeChat extends Plugin implements Listener {
    public static RyuZUBungeeChat RBC;
    public static HashMap<String , ChatGroups> ServerGroups = new HashMap<>();

    @Override
    public void onEnable() {
        RBC = this;
        reloadConfig();
        getProxy().getPluginManager().registerListener(this, this);
        getProxy().getPluginManager().registerCommand(this , new ryuzubungeechat.ryuzubungeechat.Command("rbc"));
        getProxy().registerChannel("ryuzuchat:ryuzuchat");
        getLogger().info(ChatColor.GREEN + "RyuZUBungeeChatが起動しました");
    }

    @EventHandler
    public void onPluginMessageReceived(PluginMessageEvent event) {
        if (event.getTag().equals("ryuzuchat:ryuzuchat")) {
            String sendername = null;
            if ( event.getSender() instanceof Server) {
                Server receiver = (Server) event.getSender();
                sendername = receiver.getInfo().getName();
            }
            ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
            String data = in.readUTF();
            Map<String , String> map = (Map<String, String>) jsonToMap(data);
            List<String> system = Arrays.asList("Chat" , "Prefix" , "Suffix" , "SystemMessage");
            if(system.contains(map.get("System"))) {
                map.put("SendServerName" , sendername);
                List<ChatGroups> reciveservers = new ArrayList<>();
                String finalSendername = sendername;
                ServerGroups.keySet().stream().filter(s -> ServerGroups.get(s).servers.contains(finalSendername)).forEach(s -> reciveservers.add(ServerGroups.get(s)));
                if(reciveservers.size() <= 0) { return; }
                reciveservers.forEach(l -> l.servers.forEach(s -> sendPluginMessage(s , "ryuzuchat:ryuzuchat" , setEachServersData(map , l , s))));
            } else if(map.get("System").equals("EditConfig")) {
                switch (map.get("EditTarget")) {
                    case "Format":
                        if (map.get("EditType").equals("set")) {
                            setFormat(map.get("Arg0"), map.get("Arg1"));
                        }
                        break;
                    case "List":
                        if (map.get("EditType").equals("add")) {
                            addServer(map.get("Arg0"), map.get("Arg1"));
                        } else if (map.get("EditType").equals("remove")) {
                            removeServer(map.get("Arg0"), map.get("Arg1"));
                        }
                        break;
                    case "Group":
                        if (map.get("EditType").equals("remove")) {
                            removeGroup(map.get("Arg0"));
                        }
                        break;
                    case "ChannelFormat":
                        if (map.get("EditType").equals("set")) {
                            setChannelFormat(map.get("Arg0"), map.get("Arg1"));
                        }
                        break;
                    case "TellFormat":
                        if (map.get("EditType").equals("set")) {
                            setTellFormat(map.get("Arg0"), map.get("Arg1"));
                        }
                        break;
                }
            }
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
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(config != null) {
            Configuration finalConfig = config;
            config.getKeys().forEach(l -> ServerGroups.put(l , new ChatGroups(finalConfig.getStringList(l + ".Servers") , finalConfig.getString(l + ".Format") , finalConfig.getString(l + ".ChannelFormat") , finalConfig.getString(l + ".TellFormat"))));
        }
    }

    private String setEachServersData(Map<String, String> map , ChatGroups servers , String receive) {
        map.put("Format" , servers.format);
        if(servers.tellformat != null) {map.put("TellFormat" , servers.tellformat);}
        if(servers.channelformat != null) {map.put("ChannelFormat" , servers.channelformat);}
        map.put("ReceiveServerName" , receive);
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    private String mapToJson(Map<String, String> map) {
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    private Map<String,?> jsonToMap(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Map.class);
    }

    public void setFormat(String GroupName , String format) {
        Configuration config = null;
        File file = new File(getDataFolder(), "config.yml");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(config != null) {
            config.set(GroupName + ".Format" , format);
        }
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config , file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setChannelFormat(String GroupName , String format) {
        Configuration config = null;
        File file = new File(getDataFolder(), "config.yml");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(config != null) {
            config.set(GroupName + ".ChannelFormat" , format);
        }
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config , file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setTellFormat(String GroupName , String format) {
        Configuration config = null;
        File file = new File(getDataFolder(), "config.yml");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(config != null) {
            config.set(GroupName + ".TellFormat" , format);
        }
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config , file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addServer(String GroupName , String ServerName) {
        Configuration config = null;
        File file = new File(getDataFolder(), "config.yml");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(config != null) {
            List<String> servers = new ArrayList<>(config.getStringList(GroupName + ".Servers"));
            servers.add(ServerName);
            config.set(GroupName + ".Servers" , servers);
        }
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config , file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeServer(String GroupName , String ServerName) {
        Configuration config = null;
        File file = new File(getDataFolder(), "config.yml");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(config != null) {
            List<String> servers = new ArrayList<>(config.getStringList(GroupName + ".Servers"));
            servers.remove(ServerName);
            config.set(GroupName + ".Servers" , servers);
        }
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config , file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeGroup(String GroupName) {
        Configuration config = null;
        File file = new File(getDataFolder(), "config.yml");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(config != null) {
            config.set(GroupName , null);
        }
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config , file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
