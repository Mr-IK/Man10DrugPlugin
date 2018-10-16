package red.man10.man10drugplugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;

import static red.man10.man10drugplugin.DataBase.playerHash;
import static red.man10.man10drugplugin.LoadConfigData.*;

/*
    Create by forest611
 */

public final class Man10DrugPlugin extends JavaPlugin {

    static List<String> drugName = new ArrayList<String>();//薬の名前
    static HashMap<String,ItemStack> drugStack = new HashMap<String, ItemStack>();//key,drugMap.name
    static List<String> loreData = new ArrayList<String>();//lore
    FileConfiguration config;
    private MySQLManager mysql;

    public static void drugDataLoad(){
        drugName.clear();
        drugStack.clear();
        drugMap.clear();
        playerHash.clear();
        File drugFolder = new File(Bukkit.getServer()
                .getPluginManager().getPlugin("Man10DrugPlugin").getDataFolder(),File.separator);
        if (!drugFolder.exists()){
            Bukkit.getLogger().info("ドラッグデータを保存するフォルダが見つかりません");
            Bukkit.getLogger().info("読み込み処理を終了します");
            return;
        }
        List<File> drugData = new ArrayList<File>(Arrays.asList(drugFolder.listFiles()));
        try {
            for (int i = 0; i!=drugData.size();i++){
                if (drugData.get(i).getName().equalsIgnoreCase("config.yml")||
                !drugData.get(i).isFile()){
                    drugData.remove(drugData.get(i));
                    i--;
                    continue;
                }
                drugName.add(drugData.get(i).getName().replace(".txt",""));
                Bukkit.getLogger().info("loading..."+drugName.get(i));
                BufferedReader br = new BufferedReader(new FileReader(drugData.get(i)));
                String str;
                while ((str = br.readLine()) !=null){
                    LoadConfig(drugName.get(i),str);
                }
                br.close();
                drugStack.put(drugName.get(i),drugItem(drugName.get(i)));


            }
        } catch (FileNotFoundException e) {
            Bukkit.getLogger().info("catch,br");
        } catch (IOException e) {
            Bukkit.getLogger().info("catch,br line");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for (Player p : Bukkit.getServer().getOnlinePlayers()){
            DataBase.saveDataBase(mysql,p);
        }
        Bukkit.getLogger().info("オンラインプレイヤーのドラッグデータを保存しました");

    }

    private static ItemStack drugItem(String drugName){
        DrugData data = loadData(drugName);
        if (data.name == null){
            Bukkit.getLogger().info("表示名が入力されていません");
            return null;
        }
        if (data.material == null){
            Bukkit.getLogger().info("マテリアルが入力されていません");
            return null;
        }
        ItemStack drug = new ItemStack(Material.valueOf(data.material),1);
        ItemMeta meta = drug.getItemMeta();
        meta.setDisplayName(data.name);


        StringBuffer l = new StringBuffer();
        char[] nameData = drugName.toCharArray();
        for (int i = 0;i!=nameData.length;i++){//loreで認識するために
            l.append("§").append(nameData[i]);
        }
        loreData.add(String.valueOf(l));
        if (data.lore.isEmpty()){
            data.lore.add("§e賢いman10民は薬を吸ってはいけません！");
            data.lore.add(String.valueOf(l));
        }else {
            data.lore.add(String.valueOf(l));
        }
        meta.setLore(data.lore);
        if (data.damage != 0){
            drug.setDurability(data.damage);
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }
        drug.setItemMeta(meta);
        return drug;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();
        mysql = new MySQLManager(this,"man10drugPlugin");
        getCommand("mdp").setExecutor(new MDPCommand(this,mysql));
        Bukkit.getServer().getPluginManager().registerEvents(new MDPEvents(this,mysql), this);
        drugDataLoad();//load config
        for (Player p : Bukkit.getServer().getOnlinePlayers()){//reload時にプレイヤーがいた場合
            DataBase.loadDataBase(this,mysql,p);
        }
        Bukkit.getLogger().info("オンラインプレイヤーのドラッグデータを読み込みました");
    }
}
