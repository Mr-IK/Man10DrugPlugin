package red.man10.man10drugplugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


import java.util.HashMap;

/*
drugName...拡張子を含まないファイル名
 */

public class LoadConfigData {
    public static void LoadConfig(String drugName,String brLine){//str...行
        if (brLine ==null){
            Bukkit.getLogger().info("空白の行があります");
            return;
        }
        DrugData data = loadData(drugName);
        String[] str = brLine.split(":");
        switch (str[0]){
            case "NAME":data.name = str[1];break;
            case "MATERIAL":data.material = str[1];break;
            case "DAMAGE":data.damage = Short.parseShort(str[1]);break;
            case "LEVEL":data.level = Integer.parseInt(str[1]);break;
            case "POWER":data.power = Integer.parseInt(str[1]);break;
            case "BUFF":{
                String[] buffLevel = str[1].split("/");
                for (int i = 0;i!=buffLevel.length;i++){
                    String[] buff = buffLevel[i].split(",");
                    data.buffs.put(drugName+i+1,buff);//level分けようにナンバーをふる
                }
                break;
            }
            case "DEBUFF":{
                String[] buffLevel = str[1].split("/");
                for (int i = 0;i!=buffLevel.length;i++){
                    String[] buff = buffLevel[i].split(",");
                    data.deBuffs.put(drugName+i+1,buff);//level分けようにナンバーをふる
                }
                break;
            }
        }
        saveData(drugName,data);

    }


    static class DrugData{
        String name;//表示名
        String material;//薬にするアイテム
        short damage;//アイテムのダメージ値
        int level;//何段階にするか
        int power;//強さ
        HashMap<String,String[]> buffs = new HashMap<String, String[]>();
        HashMap<String,String[]> deBuffs = new HashMap<String, String[]>();
    }

    public static HashMap<String,DrugData> drugMap = new HashMap<String, DrugData>();//key...drugName

    public static DrugData loadData(String drugName){
        DrugData data = drugMap.get(drugName);
        if (data == null){
            data = new DrugData();
        }
        return data;
    }
    public static DrugData saveData(String drugName,DrugData key){
        return drugMap.put(drugName,key);
    }
}
