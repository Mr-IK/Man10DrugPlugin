package red.man10.man10drugplugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static red.man10.man10drugplugin.LoadConfigData.LoadConfig;
import static red.man10.man10drugplugin.LoadConfigData.drugMap;

public final class Man10DrugPlugin extends JavaPlugin implements Listener {

    static List<String> drugName = new ArrayList<String>();
    static List<ItemStack> drugItemStack = new ArrayList<ItemStack>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return true;
    }

    @Override
    public void onEnable() {
        drugDataLoad();//load config

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static void drugDataLoad(){
        File drugFolder = new File(Bukkit.getServer()
                .getPluginManager().getPlugin("Man10DrugPlugin").getDataFolder(),File.separator);
        if (!drugFolder.exists()){
            Bukkit.getLogger().info("ドラッグデータを保存するフォルダが見つかりません");
            Bukkit.getLogger().info("読み込み処理を終了します");
            return;
        }
        File[] drugData = drugFolder.listFiles();
        if (drugData == null){
            Bukkit.getLogger().info("ドラックデータが見つかりません");
            Bukkit.getLogger().info("読み込みを終了します");
            return;
        }
        try {
            for (int i = 0; i!=drugData.length;i++){
                Bukkit.getLogger().info("loading..."+drugData[i].getName());
                BufferedReader br = new BufferedReader(new FileReader(drugData[i]));
                String str;
                drugName.add(drugData[i].getName().replace(".txt",""));
                while ((str = br.readLine()) !=null){
                    LoadConfig(drugName.get(i),str);
                }
                drugItemStack.add(new ItemStack(Material.DIAMOND_HOE));
            }
        } catch (FileNotFoundException e) {
            Bukkit.getLogger().info("catch,br");
        } catch (IOException e) {
            Bukkit.getLogger().info("catch,br line");
        }
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event){

    }

    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event){

    }

    @EventHandler
    public void useDrugEvent(PlayerInteractEvent event){
        String drug;
        for (int i = 0;i!=drugMap.size();i++){//使用ドラッグ特定
            if (!(event.getItem() ==drugItemStack.get(i))){
                drug = drugName.get(i);
                Bukkit.getLogger().info(event.getPlayer().getName()+"が"+drugName.get(i)+"を使いました");
                break;
            }
            if (i==drugMap.size()-1)return;

        }
        Player player = event.getPlayer();
        player.getInventory().remove(event.getItem());
        player.

    }
}
