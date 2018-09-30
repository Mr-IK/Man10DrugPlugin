package red.man10.man10drugplugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


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
                String[] buffLevel = str[1].split("/",0);//levelごとにわける
                for (int i = 0;i!=buffLevel.length;i++){
                    String[] buff = buffLevel[i].split(",",0);//バフ名ごとに分ける
                    data.buffs.put(i,buff);//level分けようにナンバーをふる
                }
                break;
            }
            case "DEBUFF":{
                String[] buffLevel = str[1].split("/",0);
                for (int i = 0;i!=buffLevel.length;i++){
                    String[] buff = buffLevel[i].split(",",0);
                    data.deBuffs.put(i,buff);//level分けようにナンバーをふる
                }
                break;
            }
            case "SYMPBUFF":{
                String[] buffLevel = str[1].split("/",0);
                for (int i = 0;i!=buffLevel.length;i++){
                    String[] buff = buffLevel[i].split(",",0);
                    data.symptomsBuffs.put(i,buff);//level分けようにナンバーをふる
                }
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
        int level = 0;//何段階にするか
        int time = 300;//何分立ったら禁断症状が出るか
        int power = 0;//指定回数使用でレベルアップ
        int type = 0; //0...薬物,1...依存を治す,2...治癒
        HashMap<Integer,String[]> buffs = new HashMap<Integer, String[]>();//ポーション名、時間、レベル
        HashMap<Integer,String[]> deBuffs = new HashMap<Integer, String[]>();
        HashMap<Integer,String[]> symptomsBuffs = new HashMap<Integer, String[]>();//禁断症状が出たときにつくバフ
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
