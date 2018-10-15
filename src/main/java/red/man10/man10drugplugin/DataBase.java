package red.man10.man10drugplugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.util.HashMap;

import static red.man10.man10drugplugin.LoadConfigData.drugMap;
import static red.man10.man10drugplugin.Man10DrugPlugin.drugName;

public class DataBase{

    static HashMap<String,PlayerDrugData> playerHash = new HashMap<String, PlayerDrugData>();

//key...playerName+drugName
    static PlayerDrugData loadData(String key){
        PlayerDrugData data = playerHash.get(key);
        if (data == null){
            data = new PlayerDrugData();
        }
        return data;
    }

    static PlayerDrugData saveData(String key,PlayerDrugData data){
        return playerHash.put(key,data);
    }

    public static void loadDataBase(MySQLManager mysql, Player player){
        ResultSet rs;
        for (int i = 0;i!=drugName.size();i++){
            String key = player.getName()+drugName.get(i);
            PlayerDrugData data = loadData(key);
            String sql = "SELECT count,level FROM man10drugPlugin.drug WHERE uuid='"+player.getUniqueId()+
                    "' and drug_name='"+ drugName.get(i)+"';";
            rs = mysql.query(sql);
            try {
                if (rs == null||!rs.next()){
                    sql = "INSERT INTO man10drugPlugin.drug VALUES('"+
                            player.getUniqueId()+"','"+player.getName()+"','"+drugName.get(i)+"',0,0,0);";
                    mysql.execute(sql);
                    Bukkit.getLogger().info(player.getName()+" insert DB");
                    sql = "SELECT count,level FROM man10drugPlugin.drug WHERE uuid='"+player.getUniqueId()+
                            "' and drug_name='"+ drugName.get(i)+"';";
                    rs = mysql.query(sql);
                    rs.next();
                }
                data.count = rs.getInt("count");
                data.level = rs.getInt("level");

                rs.close();
                if (drugMap.get(drugName.get(i)).symptoms==1){
                    data.drugTimer = new DrugTimer(mysql,player,drugName.get(i),key);
                }
                saveData(key,data);
            } catch (Exception e) {
                Bukkit.getLogger().info(e.toString());
            }
        }
        Bukkit.getLogger().info(player.getName()+" load DB");
    }

    public static void saveDataBase(MySQLManager mysql ,Player player){
        for(int i = 0;i!=drugName.size();i++){
            String key = player.getName()+drugName.get(i);
            PlayerDrugData data = loadData(key);
            if (drugMap.get(drugName.get(i)).symptoms==1){
                Bukkit.getScheduler().cancelTask(data.id);
            }

            String sql = "UPDATE man10drugPlugin.drug SET count="+data.count+",level="+data.level+
                    " WHERE uuid='"+player.getUniqueId()+"'and drug_name='"
                    +drugName.get(i)+"';";
            mysql.execute(sql);
        }
        Bukkit.getLogger().info(player.getName()+ " save DB");
    }

    static class PlayerDrugData{
        int count;
        int level;
        int id;
        boolean isDependence;
        DrugTimer drugTimer;
    }
}
