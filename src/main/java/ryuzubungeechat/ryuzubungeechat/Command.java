package ryuzubungeechat.ryuzubungeechat;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

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
        // 引数が指定されていない場合 (引数の個数が0以下の場合)
        if (args.length <= 0) {
            sender.sendMessage(ChatColor.GOLD + "------------------------使い方------------------------");
            sender.sendMessage(ChatColor.BLUE + "/" + getName() + " reload :リロード");
            sender.sendMessage(ChatColor.BLUE + "/" + getName() + " config :コンフィグを編集します");
            return;
        }
        if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("r")) {
            RyuZUBungeeChat.RBC.reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "リロード完了");
            return;
        }

        if (args[0].equalsIgnoreCase("config") || args[0].equalsIgnoreCase("c")) {
            if (args.length <= 1) {
                sender.sendMessage(ChatColor.BLUE + "/" + getName() + " config [Fromat/List]:コンフィグを編集します");
                return;
            }
            if (args[1].equalsIgnoreCase("Fromat")) {
                if (args.length <= 4) {
                    sender.sendMessage(ChatColor.BLUE + "/" + getName() + " config Fromat [set] [GroupName] [Fromat]:Formatを編集します");
                    return;
                }
                if(args[2].equalsIgnoreCase("set")) {
                    RyuZUBungeeChat.RBC.setFormat(args[3] , args[4]);
                    sender.sendMessage(ChatColor.GREEN + "Formatを編集しました");
                    return;
                }
            }
            if (args[1].equalsIgnoreCase("List")) {
                if (args.length <= 4) {
                    sender.sendMessage(ChatColor.BLUE + "/" + getName() + " config Fromat [add/remove] [GroupName] [ServerName]:共有するServerListを編集します");
                    return;
                }
                if(args[2].equalsIgnoreCase("add")) {
                    RyuZUBungeeChat.RBC.addServer(args[3] , args[4]);
                    sender.sendMessage(ChatColor.GREEN + "Listに追加しました");
                    return;
                }
                if(args[2].equalsIgnoreCase("remove")) {
                    RyuZUBungeeChat.RBC.removeServer(args[3] , args[4]);
                    sender.sendMessage(ChatColor.GREEN + "Listから削除しました");
                    return;
                }
            }
        }
        return;
    }
}
