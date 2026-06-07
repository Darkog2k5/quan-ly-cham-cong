package vn.edu.nhom8.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    
    private JLabel lblNotification; 
    private Timer hideTimer;
    private int failedAttempts = 0; 

    private final Color primaryColor = new Color(26, 54, 93);

    public LoginFrame() {
        setTitle("Đăng nhập - Hệ thống Quản lý Chấm công & Phân ca");
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

        panelLeft.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel lblDesc = new JLabel("<html><p style='text-align: center;'>Giải pháp tối ưu giúp nhân viên dễ dàng theo dõi lịch làm việc, check-in nhanh chóng và quản lý yêu cầu đổi ca linh hoạt.</p></html>", SwingConstants.CENTER);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDesc.setForeground(new Color(226, 232, 240));
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelLeft.add(lblDesc);

        panelLeft.add(Box.createVerticalGlue());


        // ==========================================
        // CỘT PHẢI: Form Đăng nhập
        // ==========================================
        JPanel panelRight = new JPanel();
        panelRight.setBackground(Color.WHITE);
        panelRight.setLayout(new BoxLayout(panelRight, BoxLayout.Y_AXIS));
        panelRight.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JPanel panelHeaderWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelHeaderWrapper.setBackground(Color.WHITE);
        panelHeaderWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panelHeaderWrapper.setAlignmentX(Component.LEFT_ALIGNMENT); 

        JLabel lblHeader = new JLabel("Đăng Nhập");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblHeader.setForeground(primaryColor);
        panelHeaderWrapper.add(lblHeader);

        JLabel lblUser = new JLabel("Tên đăng nhập:");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setForeground(new Color(74, 85, 104));
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT); 

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblPass = new JLabel("Mật khẩu:");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPass.setForeground(new Color(74, 85, 104));
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT); 

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel panelOptions = new JPanel(new BorderLayout());
        panelOptions.setBackground(Color.WHITE);
        panelOptions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panelOptions.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JCheckBox chkRemember = new JCheckBox("Ghi nhớ mật khẩu");
        chkRemember.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkRemember.setBackground(Color.WHITE);
        chkRemember.setForeground(new Color(74, 85, 104));
        
        JLabel lblForgot = new JLabel("<html><u>Quên mật khẩu?</u></html>");
        lblForgot.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblForgot.setForeground(new Color(37, 99, 235));
        lblForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblForgot.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new ForgotPasswordFrame().setVisible(true); // Mở form Quên MK
                dispose(); // Đóng form Đăng nhập hiện tại
            }
        });
        
        panelOptions.add(chkRemember, BorderLayout.WEST);
        panelOptions.add(lblForgot, BorderLayout.EAST);

        JPanel panelNoti = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelNoti.setBackground(Color.WHITE);
        panelNoti.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        panelNoti.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        lblNotification = new JLabel(" "); 
        lblNotification.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelNoti.add(lblNotification);

        JPanel panelButtonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelButtonWrapper.setBackground(Color.WHITE);
        panelButtonWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        panelButtonWrapper.setAlignmentX(Component.LEFT_ALIGNMENT); 

        btnLogin = new JButton("Đăng nhập");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogin.setBackground(new Color(37, 99, 235)); 
        btnLogin.setForeground(Color.WHITE); 
        btnLogin.setPreferredSize(new Dimension(220, 42)); 
        btnLogin.setFocusPainted(false);
        btnLogin.setOpaque(true);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelButtonWrapper.add(btnLogin);

        JPanel panelRegister = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelRegister.setBackground(Color.WHITE);
        panelRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panelRegister.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblRegPrompt = new JLabel("Bạn chưa có tài khoản? ");
        lblRegPrompt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblRegPrompt.setForeground(new Color(100, 116, 139));
        
        JLabel lblRegister = new JLabel("<html><u>Đăng ký ngay</u></html>");
        lblRegister.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblRegister.setForeground(new Color(37, 99, 235));
        lblRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblRegister.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new RegisterFrame().setVisible(true); // Mở form Đăng ký
                dispose(); // Tắt form Đăng nhập hiện tại
            }
        });
        panelRegister.add(lblRegPrompt);
        panelRegister.add(lblRegister);

        panelRight.add(panelHeaderWrapper); 
        panelRight.add(Box.createRigidArea(new Dimension(0, 20)));
        panelRight.add(lblUser);
        panelRight.add(Box.createRigidArea(new Dimension(0, 6)));
        panelRight.add(txtUsername);
        panelRight.add(Box.createRigidArea(new Dimension(0, 15)));
        panelRight.add(lblPass);
        panelRight.add(Box.createRigidArea(new Dimension(0, 6)));
        panelRight.add(txtPassword);
        panelRight.add(Box.createRigidArea(new Dimension(0, 12)));
        panelRight.add(panelOptions);
        panelRight.add(panelNoti); 
        panelRight.add(panelButtonWrapper);
        panelRight.add(Box.createRigidArea(new Dimension(0, 15)));
        panelRight.add(panelRegister);

        add(panelLeft);
        add(panelRight);
    }

    private void setupListeners() {
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processLogin();
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

    private void processLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showNotification("Vui lòng nhập đầy đủ thông tin!", true);
            return;
        }

        if (username.length() < 4) {
            showNotification("Tài khoản phải có ít nhất 4 ký tự!", true);
            return;
        }
        if (password.length() < 6) {
            showNotification("Mật khẩu phải có ít nhất 6 ký tự!", true);
            return;
        }

        if (username.equals("admin") && password.equals("123456")) {
            showNotification("Đăng nhập Admin thành công!", false);
            failedAttempts = 0;
        } else if (username.equals("manager") && password.equals("123456")) {
            showNotification("Đăng nhập Quản lý thành công!", false);
            failedAttempts = 0;
        } else if (username.equals("staff") && password.equals("123456")) {
            showNotification("Đăng nhập Nhân viên thành công!", false);
            failedAttempts = 0;
        } else {
            failedAttempts++;
            if (failedAttempts >= 5) {
                showNotification("Tài khoản bị khóa do nhập sai quá 5 lần!", true);
                btnLogin.setEnabled(false);
                txtUsername.setEnabled(false);
                txtPassword.setEnabled(false);
                txtPassword.setText("");
            } else {
                showNotification("Sai tài khoản hoặc mật khẩu! (" + failedAttempts + "/5)", true);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LoginFrame().setVisible(true);
        });
    }
}