package red.man10.man10drugplugin;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
drugName...拡張子を含まないファイル名
 */

public class LoadConfigData {
    ////////////////
    //YMLを読み込む
    public static void loadConfig(File file,String drugName){
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        DrugData drug = loadData(drugName);
        drug.name = data.getString("name","drug");
        drug.material = data.getString("material","DIAMOND_HOE");
        drug.type = (byte) data.getInt("type",0);
        drug.damage = (short) data.getInt("damage",0);
        drug.useMessage = data.getStringList("usemessage");
        if (drug.type == 0){//依存

            for (int i = 0;i!=data.getStringList("buff").size();i++){
                drug.buff.add(data.getStringList("buff").get(i).split(",",0));
            }

            for (int i = 0;i!=data.getStringList("sympbuff").size();i++){
                drug.symptomsBuff.add(data.getStringList("sympbuff").get(i).split(",",0));
            }

            for (int i =0;i!=data.getStringList("particle").size();i++){
                drug.particle.add(data.getStringList("particle").get(i).split(",",0));
            }
            drug.level = data.getInt("level",0);//段階
            drug.power = data.getInt("power",50);
            drug.sympMessage = data.getStringList("sympmessage");//禁断メッセージ
            drug.symptoms = (byte) data.getInt("symptoms");
            if (drug.symptoms ==1){
                drug.time = data.getInt("time",2400);//2分
                drug.sympTime = data.getInt("symptime",1200);//繰り返し
                drug.stopTime = data.getInt("stoptime",0);//依存が自然に止まる時間
            }
        }
        if (drug.type == 1){//禁欲弱
            drug.weakDrug = data.getString("weakdrug");
            drug.time = data.getInt("time",2400);//2分
            drug.sympTime = data.getInt("symptime",1200);//繰り返し
            drug.level = data.getInt("level",10);//段階
            drug.power = data.getInt("power",10);

        }
        if (drug.type == 2){//禁欲強
            drug.weakDrug = data.getString("weakdrug");

        }
        saveData(drugName,drug);
    }


    static class DrugData{
        String name;//表示名
        String material;//薬にするアイテム
        String weakDrug;//弱める薬、ファイル名
        short damage = 0;//アイテムのダメージ値
        int level = 0;//何段階にするか、依存を治す場合 カウントを下げる強さ
        long time = 2400;//禁断の出る時間(tick) def 2min
        long sympTime = 1200;//繰り返し
        long stopTime = 0;//依存が止まる時間（任意）
        int power = 0;//指定回数使用でレベルアップ、依存を治す薬の場合、指定回数で依存している薬のレベルを下げる
        byte type = 0; //0...薬物,1...依存を治す,2...治癒
        byte symptoms = 0;//0...無効1...有効
        List<String[]> buff = new ArrayList<String[]>();//index-1 = level
        List<String[]> symptomsBuff = new ArrayList<String[]>();
        List<String> lore = new ArrayList<String>();//説明部分
        List<String> useMessage = new ArrayList<String>();
        List<String> sympMessage = new ArrayList<String>();
        List<String[]> particle = new ArrayList<String[]>();

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
