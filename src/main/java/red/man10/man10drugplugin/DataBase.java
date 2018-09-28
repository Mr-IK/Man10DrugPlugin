package red.man10.man10drugplugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import static red.man10.man10drugplugin.Man10DrugPlugin.drugName;

public class DataBase {

    static class PlayerDrugData{
        int count;
        int level;
        int time;
    }
    static HashMap<String[],PlayerDrugData> playerHash = new HashMap<String[], PlayerDrugData>();
//key...playerName,drugName
    static PlayerDrugData loadData(String[] key){
        PlayerDrugData data = playerHash.get(key);
        if (data == null){
            data = new PlayerDrugData();
        }
        return data;
    }
    static PlayerDrugData saveData(String[] key,PlayerDrugData data){
        return playerHash.put(key,data);
    }
    public static void loadDataBase(MySQLManager mysql, Player player){
        ResultSet rs;
        for (int i = 0;i!=drugName.size();i++){
            String[] key = {player.getName(),drugName.get(i)};
            PlayerDrugData data = loadData(key);
            String sql = "SELECT count,level,time FROM man10drugPlugin.drug WHERE uuid='"+player.getUniqueId()+
                    "' and drug_name='"+ drugName.get(i)+"';";
            rs = mysql.query(sql);
            try {
                if (rs == null||!rs.next()){
                    sql = "INSERT INTO man10drugPlugin.drug VALUES('"+
                            player.getUniqueId()+"','"+player.getName()+"','"+drugName.get(i)+"',0,0,0);";
                    mysql.execute(sql);
                    Bukkit.getLogger().info(player.getName()+" insert DB");
                    sql = "SELECT count,level,time FROM man10drugPlugin.drug WHERE uuid='"+player.getUniqueId()+
                            "' and drug_name='"+ drugName.get(i)+"';";
                    rs = mysql.query(sql);
                }
//                rs.beforeFirst();
                rs.next();
                data.count = rs.getInt("count");
                data.level = rs.getInt("level");
                data.time = rs.getInt("time");
                Bukkit.getLogger().info(player.getName()+" load DB" + drugName.get(i)+","+i+","+data.count);

//                while (rs.next()){
//                    data.count = rs.getInt("count");
//                    data.level = rs.getInt("level");
//                    data.time = rs.getInt("time");
//                    Bukkit.getLogger().info(player.getName()+" load DB" + drugName.get(i)+i);
//                    break;
//                }
                rs.close();
                saveData(key,data);
            } catch (Exception e) {
                Bukkit.getLogger().info(e.toString());
            }
        }
    }
    public static void saveDataBase(MySQLManager mysql ,Player player){
        String[] key = new String[2];
        key[0] = player.getName();
        for(int i = 0;i!=drugName.size();i++){
            key[1] = drugName.get(i);
            PlayerDrugData data = loadData(key);
            String sql = "UPDATE man10drugPlugin.drug SET count="+data.count+",level="+data.level+
                    ",time="+data.time+" WHERE uuid='"+player.getUniqueId()+"'and drug_name='"
                    +drugName.get(i)+"';";
            mysql.execute(sql);
        }
        Bukkit.getLogger().info(player.getName()+ " save DB");
    }
}
