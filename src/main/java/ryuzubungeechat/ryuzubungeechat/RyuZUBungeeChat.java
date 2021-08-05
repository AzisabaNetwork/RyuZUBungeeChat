package ryuzubungeechat.ryuzubungeechat;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM2;
import discord4j.core.event.domain.message.MessageCreateEvent;
import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.manager.UUIDManager;
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

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        try {
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
                    if(map.containsKey("PlayerName") && PunishmentManager.get().isMuted(UUIDManager.get().getUUID(map.get("PlayerName")))) {return;}
                    map.put("SendServerName" , sendername);
                    List<ChatGroups> reciveservers = new ArrayList<>();
                    String finalSendername = sendername;
                    ServerGroups.keySet().stream().filter(s -> ServerGroups.get(s).servers.contains(finalSendername)).forEach(s -> reciveservers.add(ServerGroups.get(s)));
                    if(reciveservers.size() <= 0) { return; }
                    reciveservers.forEach(l -> {
                        map.put("Format" , l.format);
                        if(l.tellformat != null) {map.put("TellFormat" , l.tellformat);}
                        if(l.channelformat != null) {
                            map.put("ChannelFormat" , l.channelformat);
                        }
                        l.servers.forEach(s -> {
                            map.put("ReceiveServerName" , s);
                            Gson gson = new Gson();
                            sendPluginMessage(s , "ryuzuchat:ryuzuchat" , gson.toJson(map));
                        });
                        if(map.get("System").equalsIgnoreCase("Chat")) { l.sendLogMessage(map); }
                    });
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
        } catch (Exception i) {
            i.printStackTrace();
        }
    }

    public void sendPluginMessage(String server, String channel, String data) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(data);
        if(getProxy().getServerInfo(server) == null) {return;}
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
            config.getKeys().forEach(l -> {
                List<String> servers = finalConfig.getStringList(l + ".Servers");
                String format = finalConfig.getString(l + ".Format");
                String channelformat =  finalConfig.getString(l + ".ChannelFormat");
                String tellformat = finalConfig.getString(l + ".TellFormat");
                String admintoken = finalConfig.getString(l + ".Bot.Admin.Token" , null);
                Long adminchannelid = finalConfig.getLong(l + ".Bot.Admin.ChannelID");
                String token = finalConfig.getString(l + ".Bot.Member.Token" , null);
                Long channelid = finalConfig.getLong(l + ".Bot.Member.ChannelID");
                if(admintoken == null) {
                    ServerGroups.put(l , new ChatGroups(servers , format , channelformat , tellformat));
                } else {
                    if(token == null) {
                        ServerGroups.put(l , new ChatGroups(servers , format , channelformat , tellformat , new ChatLogBot(admintoken , adminchannelid)));
                    } else {
                        ServerGroups.put(l , new ChatGroups(servers , format , channelformat , tellformat , new ChatLogBot(admintoken , adminchannelid) , new ChatLogBot(token , channelid)));
                    }
                }
            });
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
