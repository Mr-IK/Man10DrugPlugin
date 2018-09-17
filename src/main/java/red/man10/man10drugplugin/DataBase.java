package red.man10.man10drugplugin;

public class DataBase {
    public static void createDataBase(MySQLManager mysql){
        String sql = "CREATE TABLE man10drugPlugin (uuid text,player text,drug_name text," +
                "count int, level int)";
        mysql.execute(sql);

    }
}
