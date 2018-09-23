package red.man10.man10drugplugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static red.man10.man10drugplugin.LoadConfigData.drugMap;
import static red.man10.man10drugplugin.Man10DrugPlugin.*;

public class MDPCommand implements CommandExecutor {
    final String  permissionErrorString = "§4§lYou don't have permission.";
    final String permission = "man10drug.useCmd";
    final String chatMessage = "§5[Man10DrugPlugin]";
    Man10DrugPlugin plugin;
    MySQLManager mysql;
    public MDPCommand(Man10DrugPlugin plugin, MySQLManager mysql) {
        this.plugin = plugin;
        this.mysql = mysql;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        if (!sender.hasPermission(permission)){
            sender.sendMessage(permissionErrorString);
        }
            Player player = (Player)sender;
        if (args.length == 0){
            helpChat(player);
            return true;
        }
        String cmd = args[0];
        if (cmd.equalsIgnoreCase("get")){
            if (args.length == 2){//args[1]...drugName
                if (drugStack.get(args[1])==null){
                    player.sendMessage(chatMessage+"§4"+args[1]+"§aという名前の薬は見つかりませんでした。");
                    player.sendMessage(chatMessage+"§a設定ファイルの”NAME”の項目に入力した薬の名前を入力してください");
                    return false;
                }
                player.getInventory().addItem(drugStack.get(args[1]));
                return true;
            }
            player.sendMessage(chatMessage+"§e/mdp get [drugName]");
            return false;
        }
        if (cmd.equalsIgnoreCase("save")){
            if (args.length == 2){
                if (args[1].equalsIgnoreCase("all")){
                    for (Player p : Bukkit.getServer().getOnlinePlayers()){
                        DataBase.saveDataBase(mysql,p);
                    }
                    player.sendMessage(chatMessage+"§aオンラインプレイヤーのドラッグデータを保存しました");
                    return true;
                }
                DataBase.saveDataBase(mysql,Bukkit.getPlayer(args[1]));
                player.sendMessage(chatMessage+"§a"+args[1]+"§bのドラッグデータを保存しました");
                return true;
            }
        }
        if (cmd.equalsIgnoreCase("load")){
            if (args.length == 2){
                if (args[1].equalsIgnoreCase("all")){
                    Bukkit.broadcastMessage(chatMessage+"§4§lMan10DrugPluginをリロードします");
                    for (Player p : Bukkit.getServer().getOnlinePlayers()){
                        DataBase.loadDataBase(mysql,p);
                    }
                    player.sendMessage(chatMessage+"§aオンラインプレイヤーのドラッグデータを読み込みました");
                    return true;
                }
                DataBase.loadDataBase(mysql,Bukkit.getPlayer(args[1]));
                player.sendMessage(chatMessage+"§a"+args[1]+"§bのドラッグデータを読み込みました");
                return true;
            }
        }
        if (cmd.equalsIgnoreCase("reload")){
            player.sendMessage(chatMessage+"§aプレイヤーデータの保存中");
            for (Player p : Bukkit.getServer().getOnlinePlayers()){
                DataBase.saveDataBase(mysql,p);
            }
            player.sendMessage(chatMessage+"§aコンフィグ再読み込み");
            Man10DrugPlugin.drugDataLoad();
            player.sendMessage(chatMessage+"§aプレイヤーデータ読み込み");
            for (Player p : Bukkit.getServer().getOnlinePlayers()){
                DataBase.loadDataBase(mysql,p);
            }
            return true;
        }
        if (cmd.equalsIgnoreCase("show")){
            if (args.length != 3){
                return true;
            }
            player.sendMessage(chatMessage+"§a"+args[1]+"の§a"+args[2]+"の使用情報");
            try {
                String[] key = {args[1],args[2]};
                player.sendMessage(chatMessage+"§e使用回数§a"+DataBase.playerHash.get(key).count);
                player.sendMessage(chatMessage+"§e現在のレベル§a"+DataBase.playerHash.get(key).level);
                player.sendMessage(chatMessage+"§e最後に使用してからの経過時間§a"+DataBase.playerHash.get(key).time);
            }catch (NullPointerException e ){
                player.sendMessage(chatMessage+"§4使用情報を取得できませんでした");
                Bukkit.getLogger().info(e.toString());
            }
            return true;
        }
        if (cmd.equalsIgnoreCase("drugName")){
            player.sendMessage(chatMessage+"§e読み込まれているドラッグ一覧");
            for (int i = 0;i!=drugName.size();i++){
                player.sendMessage(chatMessage+"§e"+drugName.get(i)+","+drugMap.get(drugName.get(i)).name);
            }
        }
        if (cmd.equalsIgnoreCase("insert")){
            String sql = "INSERT INTO man10drugPlugin.drug VALUES('"+
                    Bukkit.getPlayer(args[1]).getUniqueId()
                    +"','"+Bukkit.getPlayer(args[1])+"','"+args[2]+"',0,0,0);";
            mysql.execute(sql);

        }
        return true;

    }

    void helpChat(Player player){
        player.sendMessage("§e§lMan10DrugPlugin HELP");
        player.sendMessage("§e/mdp get [drugName] 薬を手に入れる drugNameは設定ファイルの”NAME”の項目を入力してください");
        player.sendMessage("§e/mdp load [player名] 薬の使用データを読み込みます player名を”all”にすると" +
                "現在オンラインのすべてプレイヤーのデータを読み込みます");
        player.sendMessage("§e/mdp save [player名] 薬のデータを保存します player名を”all’にすると" +
                "現在オンラインのすべてのプレイヤーのデータを保存します");
        player.sendMessage("§e/mdp reload 薬の設定ファイルを再読込みします");
        player.sendMessage("§e/mdp show [player名] [drugName] 薬の使用情報を見ることができます");
        player.sendMessage("§e/mdp drugName 読み込まれている薬の名前を表示します");
    }

}
