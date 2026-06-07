package vn.edu.nhom8.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ForgotPasswordFrame extends JFrame {

    private JTextField txtUsername;
    private JButton btnReset;
    private JLabel lblNotification; 
    private Timer hideTimer;

    private final Color primaryColor = new Color(26, 54, 93);

    public ForgotPasswordFrame() {
        setTitle("Khôi phục mật khẩu - Hệ thống Quản lý Chấm công");
        setSize(850, 550); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setLayout(new GridLayout(1, 2)); 

        initUI();
        setupListeners();
    }

    private void initUI() {
        // ==========================================
        // CỘT TRÁI: Dùng chữ "WORKFLOW" làm điểm nhấn
        // ==========================================
        JPanel panelLeft = new JPanel();
        panelLeft.setBackground(primaryColor); 
        panelLeft.setLayout(new BoxLayout(panelLeft, BoxLayout.Y_AXIS));
        panelLeft.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        panelLeft.add(Box.createVerticalGlue());

        JLabel lblLogo = new JLabel("WORKFLOW", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 40));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelLeft.add(lblLogo);

        panelLeft.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel lblTitle = new JLabel("<html><center>HỆ THỐNG QUẢN LÝ<br>CHẤM CÔNG & PHÂN CA</center></html>", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(191, 219, 254)); 
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelLeft.add(lblTitle);

        panelLeft.add(Box.createVerticalGlue());

        // ==========================================
        // CỘT PHẢI: Form Khôi phục mật khẩu
        // ==========================================
        JPanel panelRight = new JPanel();
        panelRight.setBackground(Color.WHITE);
        panelRight.setLayout(new BoxLayout(panelRight, BoxLayout.Y_AXIS));
        panelRight.setBorder(BorderFactory.createEmptyBorder(60, 50, 60, 50));

        // Tiêu đề
        JPanel panelHeaderWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelHeaderWrapper.setBackground(Color.WHITE);
        panelHeaderWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panelHeaderWrapper.setAlignmentX(Component.LEFT_ALIGNMENT); 

        JLabel lblHeader = new JLabel("Khôi Phục Mật Khẩu");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblHeader.setForeground(primaryColor);
        panelHeaderWrapper.add(lblHeader);

        // Mô tả hướng dẫn
        JLabel lblInstruct = new JLabel("<html><center>Vui lòng nhập Tên đăng nhập của bạn.<br>Hệ thống sẽ cấp lại mật khẩu mặc định.</center></html>", SwingConstants.CENTER);
        lblInstruct.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblInstruct.setForeground(new Color(100, 116, 139));
        lblInstruct.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tên đăng nhập
        JLabel lblUser = new JLabel("Tên đăng nhập:");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setForeground(new Color(74, 85, 104));
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT); 

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Khung thông báo nội tuyến
        JPanel panelNoti = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelNoti.setBackground(Color.WHITE);
        panelNoti.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        panelNoti.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        lblNotification = new JLabel(" "); 
        lblNotification.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelNoti.add(lblNotification);

        // Nút Khôi phục
        JPanel panelButtonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelButtonWrapper.setBackground(Color.WHITE);
        panelButtonWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        panelButtonWrapper.setAlignmentX(Component.LEFT_ALIGNMENT); 

        btnReset = new JButton("Khôi phục mật khẩu");
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnReset.setBackground(new Color(37, 99, 235)); 
        btnReset.setForeground(Color.WHITE); 
        btnReset.setPreferredSize(new Dimension(220, 42)); 
        btnReset.setFocusPainted(false);
        btnReset.setOpaque(true);
        btnReset.setBorderPainted(false);
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelButtonWrapper.add(btnReset);

        // Nút quay lại đăng nhập
        JPanel panelBack = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBack.setBackground(Color.WHITE);
        panelBack.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panelBack.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblBack = new JLabel("<html><u>&larr; Quay lại đăng nhập</u></html>");
        lblBack.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblBack.setForeground(new Color(100, 116, 139));
        lblBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelBack.add(lblBack);

        // Sự kiện click quay lại trang Login
        lblBack.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new LoginFrame().setVisible(true);
                dispose(); // Đóng trang hiện tại
            }
        });

        // Lắp ráp Cột Phải
        panelRight.add(panelHeaderWrapper); 
        panelRight.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JPanel instructWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        instructWrapper.setBackground(Color.WHITE);
        instructWrapper.add(lblInstruct);
        instructWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelRight.add(instructWrapper);

        panelRight.add(Box.createRigidArea(new Dimension(0, 20)));
        panelRight.add(lblUser);
        panelRight.add(Box.createRigidArea(new Dimension(0, 6)));
        panelRight.add(txtUsername);
        panelRight.add(Box.createRigidArea(new Dimension(0, 15)));
        panelRight.add(panelNoti); 
        panelRight.add(panelButtonWrapper);
        panelRight.add(Box.createRigidArea(new Dimension(0, 15)));
        panelRight.add(panelBack);

        add(panelLeft);
        add(panelRight);
    }

    private void setupListeners() {
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processResetPassword();
            }
        });
    }

    private void showNotification(String message, boolean isError) {
        lblNotification.setText(message);
        lblNotification.setForeground(isError ? new Color(220, 38, 38) : new Color(22, 163, 74));

        if (hideTimer != null && hideTimer.isRunning()) {
            hideTimer.stop();
        }

        hideTimer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lblNotification.setText(" "); 
            }
        });
        hideTimer.setRepeats(false); 
        hideTimer.start();
    }

    private void processResetPassword() {
        String username = txtUsername.getText().trim();

        if (username.isEmpty()) {
            showNotification("Vui lòng nhập Tên đăng nhập!", true);
            return;
        }

        if (username.length() < 4) {
            showNotification("Tài khoản không hợp lệ!", true);
            return;
        }

        // Mock data logic (Sau này ghép DAO để update mật khẩu về '123456' trên DB)
        if (username.equals("admin") || username.equals("manager") || username.equals("staff")) {
            showNotification("Mật khẩu đã được đặt lại thành '123456'", false);
            btnReset.setEnabled(false);
            txtUsername.setEnabled(false);
        } else {
            showNotification("Không tìm thấy tài khoản trong hệ thống!", true);
        }
    }
}