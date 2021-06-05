package ryuzubungeechat.ryuzubungeechat;

import java.util.ArrayList;
import java.util.List;

public class ChatGroups {
    public List<String> servers = new ArrayList<>();
    public String format;

    public ChatGroups(List<String> servers, String format) {
        this.servers = servers;
        this.format = format;
    }
}
