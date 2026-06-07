package vn.edu.nhom8.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RegisterFrame extends JFrame {

    private JTextField txtFullName;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JButton btnRegister;
    
    private JLabel lblNotification; 
    private Timer hideTimer;

    private final Color primaryColor = new Color(26, 54, 93); // Màu xanh đậm đồng bộ

    public RegisterFrame() {
        setTitle("Đăng ký tài khoản - Hệ thống Quản lý Chấm công & Phân ca");
        setSize(850, 680); // Tăng chiều cao để chứa đủ form mới một cách thoáng đãng
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setLayout(new GridLayout(1, 2)); 

        initUI();
        setupListeners();
    }

    private void initUI() {
        // ==========================================
        // CỘT TRÁI: Thương hiệu WORKFLOW
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
        // CỘT PHẢI: Form Đăng ký cập nhật (Thêm Email, SĐT)
        // ==========================================
        JPanel panelRight = new JPanel();
        panelRight.setBackground(Color.WHITE);
        panelRight.setLayout(new BoxLayout(panelRight, BoxLayout.Y_AXIS));
        panelRight.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // Tiêu đề
        JPanel panelHeaderWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelHeaderWrapper.setBackground(Color.WHITE);
        panelHeaderWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panelHeaderWrapper.setAlignmentX(Component.LEFT_ALIGNMENT); 

        JLabel lblHeader = new JLabel("Đăng Ký");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblHeader.setForeground(primaryColor);
        panelHeaderWrapper.add(lblHeader);

        // 1. Trường: Họ và tên
        JLabel lblFullName = new JLabel("Họ và tên:");
        lblFullName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblFullName.setForeground(new Color(74, 85, 104));
        lblFullName.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtFullName = new JTextField();
        txtFullName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtFullName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txtFullName.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 2. Trường: Email (Mới)
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblEmail.setForeground(new Color(74, 85, 104));
        lblEmail.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtEmail = new JTextField();
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txtEmail.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 3. Trường: Số điện thoại (Mới)
        JLabel lblPhone = new JLabel("Số điện thoại:");
        lblPhone.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPhone.setForeground(new Color(74, 85, 104));
        lblPhone.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPhone = new JTextField();
        txtPhone.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPhone.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txtPhone.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 4. Trường: Tên đăng nhập
        JLabel lblUser = new JLabel("Tên đăng nhập:");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setForeground(new Color(74, 85, 104));
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT); 

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 5. Trường: Mật khẩu
        JLabel lblPass = new JLabel("Mật khẩu:");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPass.setForeground(new Color(74, 85, 104));
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT); 

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 6. Trường: Xác nhận mật khẩu
        JLabel lblConfirmPass = new JLabel("Xác nhận mật khẩu:");
        lblConfirmPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblConfirmPass.setForeground(new Color(74, 85, 104));
        lblConfirmPass.setAlignmentX(Component.LEFT_ALIGNMENT); 

        txtConfirmPassword = new JPasswordField();
        txtConfirmPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtConfirmPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txtConfirmPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        // KHU VỰC THÔNG BÁO POPUP
        JPanel panelNoti = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelNoti.setBackground(Color.WHITE);
        panelNoti.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        panelNoti.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        lblNotification = new JLabel(" "); 
        lblNotification.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelNoti.add(lblNotification);

        // NÚT ĐĂNG KÝ
        JPanel panelButtonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelButtonWrapper.setBackground(Color.WHITE);
        panelButtonWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        panelButtonWrapper.setAlignmentX(Component.LEFT_ALIGNMENT); 

        btnRegister = new JButton("Đăng ký tài khoản");
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnRegister.setBackground(new Color(37, 99, 235)); 
        btnRegister.setForeground(Color.WHITE); 
        btnRegister.setPreferredSize(new Dimension(220, 42)); 
        btnRegister.setFocusPainted(false);
        btnRegister.setOpaque(true);
        btnRegister.setBorderPainted(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelButtonWrapper.add(btnRegister);

        // Link chuyển hướng
        JPanel panelLoginRedirect = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelLoginRedirect.setBackground(Color.WHITE);
        panelLoginRedirect.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        panelLoginRedirect.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblLogPrompt = new JLabel("Đã có tài khoản? ");
        lblLogPrompt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblLogPrompt.setForeground(new Color(100, 116, 139));
        
        JLabel lblLoginLink = new JLabel("<html><u>Đăng nhập ngay</u></html>");
        lblLoginLink.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblLoginLink.setForeground(new Color(37, 99, 235));
        lblLoginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelLoginRedirect.add(lblLogPrompt);
        panelLoginRedirect.add(lblLoginLink);

        lblLoginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new LoginFrame().setVisible(true);
                dispose(); 
            }
        });

        // Xếp các component với khoảng cách (RigidArea) được tinh chỉnh nhẹ lại
        panelRight.add(panelHeaderWrapper); 
        panelRight.add(Box.createRigidArea(new Dimension(0, 10)));
        
        panelRight.add(lblFullName);
        panelRight.add(Box.createRigidArea(new Dimension(0, 4)));
        panelRight.add(txtFullName);
        panelRight.add(Box.createRigidArea(new Dimension(0, 10)));
        
        panelRight.add(lblEmail);
        panelRight.add(Box.createRigidArea(new Dimension(0, 4)));
        panelRight.add(txtEmail);
        panelRight.add(Box.createRigidArea(new Dimension(0, 10)));
        
        panelRight.add(lblPhone);
        panelRight.add(Box.createRigidArea(new Dimension(0, 4)));
        panelRight.add(txtPhone);
        panelRight.add(Box.createRigidArea(new Dimension(0, 10)));
        
        panelRight.add(lblUser);
        panelRight.add(Box.createRigidArea(new Dimension(0, 4)));
        panelRight.add(txtUsername);
        panelRight.add(Box.createRigidArea(new Dimension(0, 10)));
        
        panelRight.add(lblPass);
        panelRight.add(Box.createRigidArea(new Dimension(0, 4)));
        panelRight.add(txtPassword);
        panelRight.add(Box.createRigidArea(new Dimension(0, 10)));
        
        panelRight.add(lblConfirmPass);
        panelRight.add(Box.createRigidArea(new Dimension(0, 4)));
        panelRight.add(txtConfirmPassword);
        
        panelRight.add(panelNoti); 
        panelRight.add(panelButtonWrapper);
        panelRight.add(Box.createRigidArea(new Dimension(0, 5)));
        panelRight.add(panelLoginRedirect);

        add(panelLeft);
        add(panelRight);
    }

    private void setupListeners() {
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processRegister();
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

    private void processRegister() {
        String fullName = txtFullName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());

        // 1. Kiểm tra không để trống bất kỳ ô nào
        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || 
            username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showNotification("Vui lòng nhập đầy đủ thông tin!", true);
            return;
        }

        // 2. Kiểm tra định dạng Email hợp lệ (Regex cơ bản)
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showNotification("Email không đúng định dạng!", true);
            return;
        }

        // 3. Kiểm tra định dạng Số điện thoại (Chỉ chứa số, độ dài 10-11)
        if (!phone.matches("^\\d{10,11}$")) {
            showNotification("Số điện thoại phải từ 10-11 chữ số!", true);
            return;
        }

        // 4. Kiểm tra độ dài Tài khoản & Mật khẩu
        if (username.length() < 4) {
            showNotification("Tài khoản phải có ít nhất 4 ký tự!", true);
            return;
        }
        if (password.length() < 6) {
            showNotification("Mật khẩu phải có ít nhất 6 ký tự!", true);
            return;
        }

        // 5. Kiểm tra mật khẩu nhập lại trùng khớp
        if (!password.equals(confirmPassword)) {
            showNotification("Mật khẩu xác nhận không khớp!", true);
            return;
        }

        // ĐĂNG KÝ THÀNH CÔNG (Mock Data)
        showNotification("Đăng ký thành công!", false);
        
        // Khóa form lại để tránh thao tác spam click
        btnRegister.setEnabled(false);
        txtFullName.setEnabled(false);
        txtEmail.setEnabled(false);
        txtPhone.setEnabled(false);
        txtUsername.setEnabled(false);
        txtPassword.setEnabled(false);
        txtConfirmPassword.setEnabled(false);
        
    }
}