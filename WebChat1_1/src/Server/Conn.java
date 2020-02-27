package Server;

import java.sql.*;

public class Conn {
    public static Connection conn;
    public static Statement statmt;
    public static ResultSet resSet;

    public Conn()  throws ClassNotFoundException, SQLException {
        conn = null;


            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:users.db");
            //conn.close();


    }

    // --------Создание таблицы--------
    public static void CreateDB() throws ClassNotFoundException, SQLException
    {
        statmt = conn.createStatement();
        statmt.execute("CREATE TABLE if not exists 'users' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'login' text,'password' text, 'nikname' text, 'TOKEN' INT);");

        System.out.println("Таблица создана или уже существует.");
    }


    // --------Заполнение таблицы--------
    public static void WriteDB() throws SQLException
    {
        statmt.execute("INSERT INTO 'users' ('login', 'password','nikname') VALUES ('test', '123','Тест'); ");
        statmt.execute("INSERT INTO 'users' ('login', 'password','nikname') VALUES ('oleg', '456','Олег'); ");
        statmt.execute("INSERT INTO 'users' ('login', 'password','nikname') VALUES ('tatiana', '789','Татьяна'); ");

        System.out.println("Таблица заполнена");
    }


    public static String GetPasswordUser(String Login) throws ClassNotFoundException, SQLException
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
    public static void ReadDB() throws ClassNotFoundException, SQLException
    {
        resSet = statmt.executeQuery("SELECT * FROM users");

        while(resSet.next())
        {
            int id = resSet.getInt("id");
            String name = resSet.getString("login");
            String phone = resSet.getString("password");
            String nikname = resSet.getString("nikname");
            System.out.println( "ID = " + id );
            System.out.println( "name = " + name );
            System.out.println( "password = " + phone );
            System.out.println( "nikname = " + nikname );
            System.out.println();
        }

        System.out.println("Таблица выведена");
    }

    // --------Закрытие--------
    public static void CloseDB() throws ClassNotFoundException, SQLException
    {
        conn.close();
        statmt.close();
        resSet.close();

        System.out.println("Соединения закрыты");
    }


}
