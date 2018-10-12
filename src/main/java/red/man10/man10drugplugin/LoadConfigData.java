package red.man10.man10drugplugin;

import org.bukkit.Bukkit;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
            case "NAME":data.name = str[1];break;//必須
            case "MATERIAL":data.material = str[1];break;//必須
            case "USEME" :data.useMessage = str[1];break;//任意
            case "SYMPME" :data.symptomsMessage = str[1];break;//任意
            case "WEAKDRUG" :data.weakDrug = str[1];break;//任意
            case "DAMAGE":data.damage = Short.parseShort(str[1]);break;//def 0
            case "LEVEL":data.level = Integer.parseInt(str[1]);break;//def 0
            case "POWER":data.power = Integer.parseInt(str[1]);break;//def 0
            case "TYPE" :data.type = Integer.parseInt(str[1]);break;//def 0
            case "TIME" :data.time = Integer.parseInt(str[1]);break;//def 300(5分)
            case "BUFF":{
                String[] buff = str[1].split(",",0);
                data.buff.add(buff);
                break;
            }
            case "DEBUFF":{
                String[] buff = str[1].split(",",0);
                data.deBuff.add(buff);
                break;
            }
            case "SYMPBUFF":{
                String[] buff = str[1].split(",",0);
                data.symptomsBuff.add(buff);
                break;
            }
            case "LORE":data.lore.add(str[1]);break;
        }
        saveData(drugName,data);

    }


    static class DrugData{
        String name;//表示名
        String material;//薬にするアイテム
        String weakDrug;//弱める薬、ファイル名
        String useMessage;//使ったときのチャットメッセージ
        String symptomsMessage;//禁断症状が出たときのメッセージ
        short damage = 0;//アイテムのダメージ値
        int level = 0;//何段階にするか、依存を治す場合 カウントを下げる強さ
        int time = 300;//何分立ったら禁断症状が出るか
        int power = 0;//指定回数使用でレベルアップ、依存を治す薬の場合、指定回数で依存している薬のレベルを下げる
        int type = 0; //0...薬物,1...依存を治す,2...治癒
        List<String[]> buff = new ArrayList<String[]>();//index-1 = level
        List<String[]> deBuff = new ArrayList<String[]>();
        List<String[]> symptomsBuff = new ArrayList<String[]>();
        List<String> lore = new ArrayList<String>();//説明部分
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
