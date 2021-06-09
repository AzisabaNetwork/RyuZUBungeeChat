package ryuzubungeechat.ryuzubungeechat;

import java.util.ArrayList;
import java.util.List;

public class ChatGroups {
    public List<String> servers;
    public String format;
    public String channelformat;

    public ChatGroups(List<String> servers, String format , String channelformat) {
        this.servers = servers;
        this.format = format;
        this.channelformat = channelformat;
    }
}
