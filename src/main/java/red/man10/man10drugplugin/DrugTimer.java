package red.man10.man10drugplugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Timer;
import java.util.TimerTask;
import static red.man10.man10drugplugin.DataBase.*;
import static red.man10.man10drugplugin.LoadConfigData.*;

public class DrugTimer  extends Thread{
    int time;
    MySQLManager mysql;
    Man10DrugPlugin plugin;
    Player player;
    String drug;
    Thread th;
    public DrugTimer(int time,MySQLManager mysql,Player player,String drug){
        this.time = time;
        this.mysql = mysql;
        this.player = player;
        this.drug = drug;
        th = new Thread();
    }

    @Override
    public void run() {
        Bukkit.getLogger().info("thread run");
        String[] key = {player.getName(),drug};
        PlayerDrugData data = playerHash.get(key);
        DrugData drugData = drugMap.get(drug);
        player.sendMessage(drugData.symptomsMessage);
        for (int i = 0;i!=drugData.symptomsBuffs.size();i+=3) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(drugData.symptomsBuffs.get(data.level-1)[i]),
                    Integer.parseInt(drugData.symptomsBuffs.get(data.level-1)[i + 1]),
                    Integer.parseInt(drugData.symptomsBuffs.get(data.level-1)[i + 2])));
            Bukkit.getLogger().info("add potion");
        }

        data.time = 0;
        try {
            th.sleep(drugData.time*60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    public void runTimer(){
        th.start();
    }
}
