package red.man10.man10drugplugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.List;

import static red.man10.man10drugplugin.LoadConfigData.LoadConfig;

public final class Man10DrugPlugin extends JavaPlugin {

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
                while ((str = br.readLine()) !=null){
                    LoadConfig(drugData[i].getName().replace(".txt",""),str);
                }

            }
        } catch (FileNotFoundException e) {
            Bukkit.getLogger().info("catch,br");
        } catch (IOException e) {
            Bukkit.getLogger().info("catch,br line");
        }
    }
}
