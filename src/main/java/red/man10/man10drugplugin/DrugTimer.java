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
    MySQLManager mysql;
    TimerTask task;
    Player player;
    String drug;
    boolean isDependence;
    public DrugTimer(MySQLManager mysql,Player player,String drug){
        this.mysql = mysql;
        this.player = player;
        this.drug = drug;
    }

    public void startTask(){
        String key = player.getName()+drug;
        PlayerDrugData data = playerHash.get(key);
        DrugData drugData = drugMap.get(drug);
        task = new TimerTask() {//禁断症状
            @Override
            public void run() {
            Bukkit.getLogger().info("task run");
            player.sendMessage(drugData.symptomsMessage);
            for (int i = 0;i!=drugData.symptomsBuff.size();i+=3) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(drugData.symptomsBuff.get(data.level)[i]),
                        Integer.parseInt(drugData.symptomsBuff.get(data.level)[i + 1]),
                        Integer.parseInt(drugData.symptomsBuff.get(data.level)[i + 2])));
                Bukkit.getLogger().info("add potion");
            }
            isDependence = true;
            }
        };
        if (data.count>=1){
            Timer time = new Timer();
            if (isDependence){
                time.schedule(task,0,drugData.time*60000);
            }else {
                time.schedule(task,drugData.time*60000,drugData.time*60000);
            }
        }
        if (data.count == 0){
            task.cancel();
            isDependence = false;
        }
    }
    public void stopTask(){
        task.cancel();
        Bukkit.getLogger().info("stop task");
    }

}
