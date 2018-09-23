package red.man10.man10drugplugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.*;
import java.util.*;

import static red.man10.man10drugplugin.DataBase.*;
import static red.man10.man10drugplugin.LoadConfigData.*;
import static red.man10.man10drugplugin.LoadConfigData.LoadConfig;
import static red.man10.man10drugplugin.LoadConfigData.drugMap;

public final class Man10DrugPlugin extends JavaPlugin implements Listener {

    static List<String> drugName = new ArrayList<String>();//薬の名前
    static HashMap<String,ItemStack> drugStack = new HashMap<String, ItemStack>();//key,drugMap.name
    FileConfiguration config;
    MySQLManager mysql;

    @Override
    public void onEnable() {
        mysql = new MySQLManager(this,"man10drugPlugin");
        getCommand("mdp").setExecutor(new MDPCommand(this,mysql));
        saveDefaultConfig();
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        config = getConfig();
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
//        File[] drugData = drugFolder.listFiles();
        List<File> drugData = new ArrayList<File>(Arrays.asList(drugFolder.listFiles()));
        if (drugData == null){
            Bukkit.getLogger().info("ドラックデータが見つかりません");
            Bukkit.getLogger().info("読み込みを終了します");
            return;
        }
        try {
            for (int i = 0; i!=drugData.size();i++){
                if (drugData.get(i).getName().equalsIgnoreCase("config.yml")||
                !drugData.get(i).isFile()){
                    drugData.remove(drugData.get(i));
                    i--;
                    continue;
                }
                Bukkit.getLogger().info("loading..."+drugData.get(i).getName());
                BufferedReader br = new BufferedReader(new FileReader(drugData.get(i)));
                String str;
                drugName.add(drugData.get(i).getName().replace(".txt",""));
                while ((str = br.readLine()) !=null){
                    LoadConfig(drugName.get(i),str);
                }
                drugStack.put(drugName.get(i),drugItem(drugName.get(i)));


            }
        } catch (FileNotFoundException e) {
            Bukkit.getLogger().info("catch,br");
        } catch (IOException e) {
            Bukkit.getLogger().info("catch,br line");
        }
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
        ItemStack drug = new ItemStack(Material.valueOf(data.material),1,data.damage);
        ItemMeta meta = drug.getItemMeta();
        meta.setDisplayName(data.name);
        if (data.lore!=null){
            meta.setLore(data.lore);
        }
        drug.setItemMeta(meta);
        return drug;
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event){
        loadDataBase(mysql,event.getPlayer());
    }

    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event){
        saveDataBase(mysql,event.getPlayer());
    }

    @EventHandler
    public void useDrugEvent(PlayerInteractEvent event){
        if (event.getAction() == Action.RIGHT_CLICK_AIR
                || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() == Material.AIR)return;
            Bukkit.getLogger().info("click event");
            for (Map.Entry<String, ItemStack> map : drugStack.entrySet()) {
                if (item == map.getValue()) {
                    Bukkit.getLogger().info(player.getName()+" used "+map.getKey()  );
                    useDrug(map.getKey(), map.getValue(), player);
                    return;
                }
            }
        }
    }

    public static void useDrug(String key, ItemStack stack, Player player){
        String[] playerKey = {player.getName(),key};
        DrugData data = loadData(key);
        PlayerDrugData playerData = loadData(playerKey);
        player.getInventory().remove(stack);
        for (int i = 0;i!=data.level;i++){
            if (playerData.level == i){
                for (int i1 = 0;i1!=data.buffs.size();i1+=3) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(data.buffs.get(i)[i1]),
                            Integer.parseInt(data.buffs.get(i)[i1 + 1]),
                            Integer.parseInt(data.buffs.get(i)[i1 + 2])));
                }
                for (int i1 = 0;i1!=data.deBuffs.size();i1+=3) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(data.deBuffs.get(i)[i1]),
                            Integer.parseInt(data.deBuffs.get(i)[i1 + 1]),
                            Integer.parseInt(data.deBuffs.get(i)[i1 + 2])));
                }
            }
        }
        playerData.count ++;
        for (int i = 0;i!=data.level;i++){
            if (playerData.count == data.power*i){
                playerData.level ++;
                break;
            }
        }
        saveData(key,data);
        saveData(playerKey,playerData);
    }

}
