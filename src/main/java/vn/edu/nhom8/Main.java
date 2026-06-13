package vn.edu.nhom8;

import vn.edu.nhom8.ui.LoginFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Điểm khởi chạy ứng dụng WorkShift Pro.
 * Luồng: Main → LoginFrame → (StaffFrame | ManagerFrame | AdminFrame) theo vai trò.
 */
public class Main {

    public static void main(String[] args) {
        // Dùng System LookAndFeel để gần với OS, nhưng override font
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Font mặc định toàn app
        Font defaultFont = new Font("Segoe UI", Font.PLAIN, 13);
        for (Object key : UIManager.getDefaults().keySet()) {
            if (key.toString().endsWith(".font")) {
                UIManager.put(key, defaultFont);
            }
        }

        // Smooth rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}
