package ryuzubungeechat.ryuzubungeechat;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Command extends net.md_5.bungee.api.plugin.Command {
    public Command(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("rbc.op")) {
            sender.sendMessage(ChatColor.RED + "ぽまえけんげんないやろ");
            return;
        }
        if (args.length <= 0) {
            sender.sendMessage(ChatColor.GOLD + "------------------------使い方------------------------");
            sender.sendMessage(ChatColor.BLUE + "/" + getName() + " reload :リロード");
            sender.sendMessage(ChatColor.BLUE + "/" + getName() + " config :コンフィグを編集します");
        }
        if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("r")) {
            RyuZUBungeeChat.RBC.reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "リロード完了");
        }
    }
}
