package red.man10.man10drugplugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


import static red.man10.man10drugplugin.DataBase.*;
import static red.man10.man10drugplugin.DataBase.loadDataBase;
import static red.man10.man10drugplugin.DataBase.playerHash;
import static red.man10.man10drugplugin.DataBase.saveDataBase;
import static red.man10.man10drugplugin.LoadConfigData.drugMap;
import static red.man10.man10drugplugin.LoadConfigData.saveData;
import static red.man10.man10drugplugin.MDPCommand.chatMessage;
import static red.man10.man10drugplugin.Man10DrugPlugin.loreData;

public class MDPEvents implements Listener {
    MySQLManager mysql;
    Man10DrugPlugin plugin;
    public MDPEvents(Man10DrugPlugin plugin,MySQLManager mysql){
        this.plugin = plugin;
        this.mysql = mysql;
    }
    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event){
        loadDataBase(mysql,event.getPlayer());
    }

    public static void useDrug(String key, ItemStack stack, Player player){
        String playerKey = player.getName()+key;
        LoadConfigData.DrugData data = drugMap.get(key);
        PlayerDrugData playerData = playerHash.get(playerKey);
        if (data==null||playerData==null){
            player.sendMessage(chatMessage+"§2今は薬を吸う気分ではないようだ");
            return;
        }
        if (data.type == 0){
            if (data.buff.size()==0){
                player.sendMessage("§4§lスーハー.....§2あれ？");
            }else {
                player.sendMessage(data.useMessage);
                for (int i = 0;i!=data.buff.size();i++) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(data.buff.get(playerData.level)[i]),
                            Integer.parseInt(data.buff.get(playerData.level)[i += 1]),
                            Integer.parseInt(data.buff.get(playerData.level)[i += 2])));
                    Bukkit.getLogger().info("add potion");
                }
            }
            if (data.deBuff.size()!=0){
                for (int i = 0;i!=data.deBuff.size();i++) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(data.deBuff.get(playerData.level)[i]),
                            Integer.parseInt(data.deBuff.get(playerData.level)[i += 1]),
                            Integer.parseInt(data.deBuff.get(playerData.level)[i += 2])));
                }
            }
            playerData.count ++;
            for (int i = 0;i!=data.level;i++){
                if (playerData.count == data.power*i){
                    playerData.level ++;
                    break;
                }
            }
        }else if (data.type == 1){
            playerData.count ++;
            if (playerData.count==data.power){
                String pKey =  player.getName()+data.weakDrug;
                PlayerDrugData hash = playerHash.get(pKey);
                hash.count -= data.level;
                for (int i = 0;i!=hash.level;i++){
                    if (hash.count <= drugMap.get(data.weakDrug).power*i){
                        playerData.level --;
                        break;
                    }
                }
                DataBase.saveData(pKey,hash);
            }
        }
        saveData(key,data);
        DataBase.saveData(playerKey,playerData);
    }

    @EventHandler
    public void useDrugEvent(PlayerInteractEvent event){
        if (event.getAction() == Action.RIGHT_CLICK_AIR
                || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() == Material.AIR)return;
            if (item.getItemMeta().getLore() == null||
                    item.getItemMeta().getLore().isEmpty())return;
            for (int i = 0;i!=loreData.size();i++){
                if (item.getItemMeta().getLore().get(0).startsWith(loreData.get(i))){
                    Bukkit.getLogger().info("use event,"+loreData.get(i));
                    String key = loreData.get(i).replaceAll("§","");
                    useDrug(key, item, player);
                    Bukkit.getLogger().info(player.getName()+" used "+key );
                    return;
                }
            }
        }
    }

    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event){
        saveDataBase(mysql,event.getPlayer());

    }

}
