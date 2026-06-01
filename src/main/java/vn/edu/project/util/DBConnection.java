/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.project.util;

/**
 *
 * @author VICTUS
 */


import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConnection {
    private static DBConnection instance;
    private Connection connection;

    private DBConnection() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                return;
            }
            prop.load(input);
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            this.connection = DriverManager.getConnection(
                prop.getProperty("db.url"),
                prop.getProperty("db.user"),
                prop.getProperty("db.password")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized DBConnection getInstance() {
        try {
            if (instance == null || instance.getConnection().isClosed()) {
                instance = new DBConnection();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}