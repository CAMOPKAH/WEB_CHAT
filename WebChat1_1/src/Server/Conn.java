package Server;

import ChatForm.Logger.Log;

import java.sql.*;

public class Conn {
    public  Connection conn;
    public  Statement statmt;
    public  ResultSet resSet;
    private Log log;
    
    public Conn(Log log)  throws ClassNotFoundException, SQLException {
        conn = null;
        this.log = log;
       

            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:users.db");
            //conn.close();


    }

    // --------Создание таблицы--------
    public  void CreateDB() throws ClassNotFoundException, SQLException
    {
        statmt = conn.createStatement();
        statmt.execute("CREATE TABLE if not exists 'users' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'login' text,'password' text, 'nikname' text, 'TOKEN' INT);");

        log.WriteSys("Таблица создана или уже существует.");
    }
/*

    // --------Заполнение таблицы--------
    public static void WriteDB() throws SQLException
    {
        statmt.execute("INSERT INTO 'users' ('login', 'password','nikname') VALUES ('test', '123','Тест'); ");
        statmt.execute("INSERT INTO 'users' ('login', 'password','nikname') VALUES ('oleg', '456','Олег'); ");
        statmt.execute("INSERT INTO 'users' ('login', 'password','nikname') VALUES ('tatiana', '789','Татьяна'); ");

        log.WriteSys("Таблица заполнена");
    }
*/

    public String GetPasswordUser(String Login) throws ClassNotFoundException, SQLException
    {
        resSet = statmt.executeQuery("SELECT * FROM users u WHERE u.login='"+ Login+"'");
        String password = null;
        while(resSet.next())
        {

            password = resSet.getString("password");

        }
        return password;

    }

    // -------- Вывод таблицы--------
    public  void ReadDB() throws ClassNotFoundException, SQLException
    {
        resSet = statmt.executeQuery("SELECT * FROM users");
;
        while(resSet.next())
        {
            int id = resSet.getInt("id");
            String name = resSet.getString("login");
            String phone = resSet.getString("password");
            String nikname = resSet.getString("nikname");
            log.WriteSys( "ID = " + id );
            log.WriteSys( "name = " + name );
            log.WriteSys( "password = " + phone );
            log.WriteSys( "nikname = " + nikname );
            
            
        }

        log.WriteSys("Таблица выведена");
    }

    // --------Закрытие--------
    public  void CloseDB() throws ClassNotFoundException, SQLException
    {
        conn.close();
        statmt.close();
        resSet.close();

        log.WriteSys("Соединения закрыты");
    }


}
