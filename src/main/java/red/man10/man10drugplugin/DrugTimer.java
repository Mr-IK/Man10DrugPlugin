package red.man10.man10drugplugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;


import static red.man10.man10drugplugin.DataBase.*;
import static red.man10.man10drugplugin.LoadConfigData.*;

public class DrugTimer  extends BukkitRunnable {
    MySQLManager mysql;
    Player player;
    String drug;
    String key;

    public DrugTimer(MySQLManager mysql, Player player, String drug, String key) {
        this.mysql = mysql;
        this.player = player;
        this.drug = drug;
        this.key = key;
    }


    @Override
    public void run() {
        PlayerDrugData data = playerHash.get(key);
        DrugData drugData = drugMap.get(drug);
        player.sendMessage(drugData.sympMessage.get(data.level));
        try {
            for (int i = 0; i != drugData.symptomsBuff.get(data.level).length; i++) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(drugData.symptomsBuff.get(data.level)[i])
                        , Integer.parseInt(drugData.symptomsBuff.get(data.level)[i + 1])
                        , Integer.parseInt(drugData.symptomsBuff.get(data.level)[i + 2])));
                i += 2;

            }
        }catch (Exception e){
            Bukkit.getLogger().info(e.getMessage());
        }
        data.time += drugData.sympTime;
        if (drugData.stopTime !=0&&data.time>=drugData.stopTime||data.count ==0){
            Bukkit.getScheduler().cancelTask(data.id);
            data.isDependence = false;
            data.time = 0;
        }

    }
}
