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
            sender.sendMessage(ChatColor.BLUE + "/" + getName() + " group :グループの確認");
        }
        if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("r")) {
            RyuZUBungeeChat.RBC.reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "リロード完了");
        }
        if (args[0].equalsIgnoreCase("group") || args[0].equalsIgnoreCase("g")) {
            if (args.length <= 1) {
                sender.sendMessage(ChatColor.RED + "/" + getName() + " group [GroupName/List]");
                return;
            }
            if (args[1].equalsIgnoreCase("list")) {
                sender.sendMessage(ChatColor.GREEN + "サーバーグループ一覧");
                for(String name : RyuZUBungeeChat.ServerGroups.keySet()) {
                    sender.sendMessage(name);
                }
                return;
            }
            if(RyuZUBungeeChat.ServerGroups.containsKey(args[1])) {
                sender.sendMessage(ChatColor.GREEN + args[1] + "に登録されてるサーバー一覧");
                for(String name : RyuZUBungeeChat.ServerGroups.get(args[1]).servers) {
                    sender.sendMessage(name);
                }
            } else {
                sender.sendMessage(ChatColor.RED + "グループが存在しません");
            }
        }
    }
}
