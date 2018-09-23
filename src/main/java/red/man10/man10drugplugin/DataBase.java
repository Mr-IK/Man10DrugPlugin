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
    public static void createDataBase(MySQLManager mysql){
        String sql = "CREATE TABLE man10drugPlugin (uuid text,player text,drug_name text," +
                "count int, level int,time int);";
        mysql.execute(sql);

    }
    public static void loadDataBase(MySQLManager mysql, Player player){
        ResultSet rs = null;
        String[] key = new String[2];
        key[0] = player.getName();
        for (int i = 0;i!=drugName.size();i++){
            key[1] = drugName.get(i);
            PlayerDrugData data = loadData(key);
            String sql = "SELECT count,level,time FROM man10drugPlugin WHERE uuid='"+player.getUniqueId()+
                    "',drug_name='"+ drugName.get(i)+"';";
            rs = mysql.query(sql);
            try {
                data.count = rs.getInt("count");
                data.level = rs.getInt("level");
                data.time = rs.getInt("time");
                rs.close();
                saveData(key,data);
                Bukkit.getLogger().info(player.getName()+" load DB");
            } catch (SQLException e) {
                sql = "INSERT INTO man10drugPlugin VALUES('"+
                player.getUniqueId()+"','"+player.getName()+"','"+drugName.get(i)+"'0,0,0;";
                mysql.execute(sql);
                Bukkit.getLogger().info(player.getName()+" insert DB");
                Bukkit.getLogger().info("SQLException");
            }catch (Exception e){
                sql = "INSERT INTO man10drugPlugin VALUES('"+
                        player.getUniqueId()+"','"+player.getName()+"','"+drugName.get(i)+"'0,0,0;";
                mysql.execute(sql);
                Bukkit.getLogger().info(player.getName()+" insert DB");
                Bukkit.getLogger().info("exception");
            }

        }
    }
    public static void saveDataBase(MySQLManager mysql ,Player player){
        String[] key = new String[2];
        key[0] = player.getName();
        for(int i = 0;i!=drugName.size();i++){
            key[1] = drugName.get(i);
            PlayerDrugData data = loadData(key);
            String sql = "UPDATE man10drugPlugin SET count="+data.count+",level="+data.level+
                    ",time="+data.time+" WHERE uuid='"+player.getUniqueId()+"',drug_name='"
                    +drugName.get(i)+"';";
            mysql.execute(sql);
        }
        Bukkit.getLogger().info(player.getName()+ " save DB");
    }
}
