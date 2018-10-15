package red.man10.man10drugplugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
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

    public void useDrug(String key, ItemStack stack, Player player){
        String playerKey = player.getName()+key;
        LoadConfigData.DrugData data = drugMap.get(key);
        PlayerDrugData playerData = playerHash.get(playerKey);
        if (data==null||playerData==null){
            player.sendMessage(chatMessage+"§2今は薬を吸う気分ではないようだ");
            return;
        }

        ////////////////////
        //依存薬
        ////////////////////
        if (data.type == 0){
            if (data.buff.size()==0){
                player.sendMessage("§4§lスーハー.....§2あれ？");
            }else {
                player.sendMessage(data.useMessage);
                for (int i = 0;i!=data.buff.get(playerData.level).length;i++) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(data.buff.get(playerData.level)[i]),
                            Integer.parseInt(data.buff.get(playerData.level)[i + 1]),
                            Integer.parseInt(data.buff.get(playerData.level)[i + 2])));
                    i+=2;
                }
            }
            if (playerData.drugTimer!=null){//禁断症状開始
                if (playerData.isDependence){
                    playerData.isDependence = false;
                    Bukkit.getScheduler().cancelTask(playerData.id);
                }
                playerData.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,playerData.drugTimer,data.time,data.sympTime);
                playerData.isDependence = true;

            }
            playerData.count ++;
            for (int i = 0;i!=data.level;i++){//レベルアップ処理
                if (playerData.count == data.power*i){
                    playerData.level ++;
                    break;
                }
            }

        //////////////////////
        //治癒 （弱）
        /////////////////////
        }else if (data.type == 1){
            playerData.count ++;
            String pKey =  player.getName()+data.weakDrug;
            PlayerDrugData hash = playerHash.get(pKey);//依存を弱める対象
            player.sendMessage(data.useMessage);
            if (playerData.count==data.power){//指定回数
                playerData.count = 0;


                hash.count -= data.level;//指定の値だけカウントを下げる
                for (int i = 0;i!=hash.level;i++){
                    if (hash.count <= drugMap.get(data.weakDrug).power*i){//カウントがパワー*i未満になったらレベルを下げる
                        hash.level --;
                        break;
                    }
                }
                DataBase.saveData(pKey,hash);
            }
            if (hash.isDependence){
                hash.isDependence= false;
                Bukkit.getScheduler().cancelTask(hash.id);
            }
            hash.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,hash.drugTimer,data.time,data.sympTime);
            hash.isDependence = true;
            DataBase.saveData(pKey,hash);


        ////////////////////
        //治癒（強)
        /////////////////////
        }else if (data.type == 2){
            player.sendMessage(data.useMessage);
            String pKey =  player.getName()+data.weakDrug;
            PlayerDrugData hash = playerHash.get(pKey);//依存を弱める対象
            if (hash.isDependence){
                hash.isDependence = false;
                Bukkit.getScheduler().cancelTask(hash.id);
            }
            hash.level = 0;
            hash.count = 0;
            DataBase.saveData(pKey,hash);

        }
        stack.setAmount(stack.getAmount()-1);
        player.getInventory().setItemInMainHand(stack);
        saveData(key,data);
        DataBase.saveData(playerKey,playerData);
    }

    @EventHandler
    public void useDrugEvent(PlayerInteractEvent event){
        if (event.getAction() == Action.RIGHT_CLICK_AIR
                || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() == Material.AIR||
                    item.getItemMeta().getLore() == null||
                    item.getItemMeta().getLore().isEmpty())return;
            for (int i = 0;i!=loreData.size();i++){
                if (item.getItemMeta().getLore().get(item.getItemMeta().getLore().size()-1)
                        .equalsIgnoreCase(loreData.get(i))){
                    String key = loreData.get(i).replaceAll("§","");
                    event.setCancelled(true);
                    useDrug(key, item, player);
                }
            }
        }
    }

    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event){
        saveDataBase(mysql,event.getPlayer());

    }
    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event){
        loadDataBase(mysql,event.getPlayer());

    }
    @EventHandler
    public void playerDrinkMilkEvent(PlayerItemConsumeEvent event){
        if (event.getItem().getType()==Material.MILK_BUCKET){
            event.setCancelled(true);
        }
    }


}
