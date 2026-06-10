package vn.edu.nhom8.ui;

import vn.edu.nhom8.dao.NhanVienDAO;
import vn.edu.nhom8.model.NhanVien;
import vn.edu.nhom8.util.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Màn hình Đăng nhập.
 * Layout 2 cột: trái = banner thương hiệu, phải = form.
 * Sau khi đăng nhập thành công → điều hướng đúng Frame theo vai trò.
 */
public class LoginFrame extends JFrame {

    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    // ── Form fields ─────────────────────────────────────────────────────────
    private JTextField txtTaiKhoan;
    private JPasswordField txtMatKhau;
    private JCheckBox chkGhiNho;
    private JLabel lblError;
    private int loginAttempts = 0;
    private static final int MAX_ATTEMPTS = 5;

    public LoginFrame() {
    setTitle("WorkShift Pro — Đăng nhập");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1200, 700);
    setLocationRelativeTo(null);
    initUI();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  GIAO DIỆN
    // ═════════════════════════════════════════════════════════════════════════

    private void initUI() {
        JPanel root = new JPanel(new GridLayout(1, 2));
        root.setBackground(UITheme.BG_PAGE);

        root.add(buildLeftPanel());
        root.add(buildRightPanel());

        setContentPane(root);
    }

    // ── Cột trái: banner ────────────────────────────────────────────────────

    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                // Gradient navy
                GradientPaint gp = new GradientPaint(
                        0, 0, UITheme.NAVY,
                        getWidth(), getHeight(), UITheme.NAVY3);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Decorative circles
                g2.setColor(new Color(255, 255, 255, 12));
                g2.fillOval(-60, -60, 280, 280);
                g2.fillOval(getWidth() - 120, getHeight() - 120, 240, 240);
                g2.setColor(new Color(255, 255, 255, 6));
                g2.fillOval(40, getHeight() - 180, 200, 200);
            }
        };
        panel.setLayout(new GridBagLayout());

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        // Logo icon
        JLabel lblIcon = new JLabel("⚡", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        lblIcon.setForeground(new Color(255, 215, 0));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tên hệ thống
        JLabel lblTitle = new JLabel("WorkShift Pro");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Hệ thống Quản lý Ca Làm Việc");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(new Color(255, 255, 255, 120));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Divider
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 40));
        sep.setMaximumSize(new Dimension(220, 1));

        // Mô tả ngắn
        String[] features = {
            "Check-in / Check-out nhanh chóng",
            "Theo dõi lịch làm việc cá nhân",
            "Gửi yêu cầu đổi ca linh hoạt",
            "Quản lý nhân sự tập trung"
        };

        inner.add(Box.createVerticalStrut(0));
        inner.add(lblIcon);
        inner.add(Box.createVerticalStrut(14));
        inner.add(lblTitle);
        inner.add(Box.createVerticalStrut(6));
        inner.add(lblSub);
        inner.add(Box.createVerticalStrut(24));
        inner.add(sep);
        inner.add(Box.createVerticalStrut(18));

        // Re-add features in correct order
        JPanel featurePanel = new JPanel();
        featurePanel.setOpaque(false);
        featurePanel.setLayout(new BoxLayout(featurePanel, BoxLayout.Y_AXIS));
        for (String f : features) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            row.setOpaque(false);
            row.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Blue circle with checkmark
            JLabel iconLbl = new JLabel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(UITheme.BLUE);
                    g2.fillOval(0, 0, 18, 18);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    FontMetrics fm = g2.getFontMetrics();
                    String mark = "✓";
                    g2.drawString(mark, (18 - fm.stringWidth(mark)) / 2, 13);
                }
                @Override public Dimension getPreferredSize() { return new Dimension(18, 18); }
            };

            JLabel lbl = new JLabel(f);
            lbl.setFont(UITheme.FONT_SMALL);
            lbl.setForeground(new Color(255, 255, 255, 200));
            row.add(iconLbl);
            row.add(lbl);
            featurePanel.add(row);
            featurePanel.add(Box.createVerticalStrut(8));
        }

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(lblIcon);
        content.add(Box.createVerticalStrut(14));
        content.add(lblTitle);
        content.add(Box.createVerticalStrut(6));
        content.add(lblSub);
        content.add(Box.createVerticalStrut(28));
        content.add(featurePanel);

        panel.add(content);
        return panel;
    }

    // ── Cột phải: form ─────────────────────────────────────────────────────

    private JPanel buildRightPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(0, 40, 0, 40));
        form.setPreferredSize(new Dimension(420, 500));
        form.setMaximumSize(new Dimension(420, 500));

        // Tiêu đề
        JLabel lblTitle = new JLabel("Đăng nhập");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(UITheme.TEXT);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Vui lòng nhập thông tin tài khoản của bạn");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.MUTED);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Field: Tên đăng nhập
        JLabel lblUser = formLabel("Tên đăng nhập");
        txtTaiKhoan = createTextField("👤  Nhập tên đăng nhập");

        // ── Field: Mật khẩu
        JLabel lblPwd = formLabel("Mật khẩu");
        txtMatKhau = new JPasswordField();
        styleInput(txtMatKhau);
        txtMatKhau.setFont(UITheme.FONT_BODY);

        // ── Ghi nhớ + quên mật khẩu
        JPanel optRow = new JPanel(new BorderLayout());
        optRow.setOpaque(false);
        optRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        optRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        chkGhiNho = new JCheckBox("Ghi nhớ mật khẩu");
        chkGhiNho.setOpaque(false);
        chkGhiNho.setFont(UITheme.FONT_SMALL);
        chkGhiNho.setForeground(UITheme.TEXT2);
        chkGhiNho.setFocusPainted(false);

        JLabel lblForgot = new JLabel("Bạn quên mật khẩu?");
        lblForgot.setFont(UITheme.FONT_SMALL);
        lblForgot.setForeground(UITheme.BLUE);
        lblForgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        optRow.add(chkGhiNho, BorderLayout.WEST);
        optRow.add(lblForgot, BorderLayout.EAST);

        // ── Thông báo lỗi
        lblError = new JLabel(" ");
        lblError.setFont(UITheme.FONT_SMALL);
        lblError.setForeground(UITheme.RED);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Nút Đăng nhập
        JButton btnLogin = createPrimaryButton("Đăng nhập", UITheme.BLUE);
        btnLogin.addActionListener(e -> doLogin());

        // Nhấn Enter từ bất kỳ field nào
        KeyAdapter enterKey = new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin();
            }
        };
        txtTaiKhoan.addKeyListener(enterKey);
        txtMatKhau.addKeyListener(enterKey);

        // ── Divider
        JPanel divider = new JPanel(new BorderLayout(8, 0));
        divider.setOpaque(false);
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        JSeparator s1 = new JSeparator(); s1.setForeground(UITheme.BORDER);
        JSeparator s2 = new JSeparator(); s2.setForeground(UITheme.BORDER);
        JLabel orLbl = new JLabel("hoặc");
        orLbl.setFont(UITheme.FONT_SMALL);
        orLbl.setForeground(UITheme.MUTED);
        divider.add(s1, BorderLayout.WEST);
        divider.add(orLbl, BorderLayout.CENTER);
        divider.add(s2, BorderLayout.EAST);

        // ── Nút Đăng ký
        JButton btnRegister = createOutlineButton("🧑  Bạn chưa có tài khoản? Đăng ký ngay");
        btnRegister.addActionListener(e ->
            JOptionPane.showMessageDialog(this,
                "Vui lòng liên hệ Admin để được tạo tài khoản.",
                "Đăng ký tài khoản", JOptionPane.INFORMATION_MESSAGE));

        // ── Lắp vào form
        form.add(lblTitle);
        form.add(Box.createVerticalStrut(4));
        form.add(lblSub);
        form.add(Box.createVerticalStrut(28));
        form.add(lblUser);
        form.add(Box.createVerticalStrut(5));
        form.add(txtTaiKhoan);
        form.add(Box.createVerticalStrut(14));
        form.add(lblPwd);
        form.add(Box.createVerticalStrut(5));
        form.add(txtMatKhau);
        form.add(Box.createVerticalStrut(10));
        form.add(optRow);
        form.add(Box.createVerticalStrut(6));
        form.add(lblError);
        form.add(Box.createVerticalStrut(14));
        form.add(btnLogin);
        form.add(Box.createVerticalStrut(14));
        form.add(divider);
        form.add(Box.createVerticalStrut(10));
        form.add(btnRegister);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(form, gbc);
        return panel;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  LOGIC ĐĂNG NHẬP
    // ═════════════════════════════════════════════════════════════════════════

    private void doLogin() {
        if (loginAttempts >= MAX_ATTEMPTS) {
            lblError.setText("Tài khoản bị khóa tạm thời do nhập sai quá " + MAX_ATTEMPTS + " lần.");
            return;
        }

        String taiKhoan = txtTaiKhoan.getText().trim();
        String matKhau  = new String(txtMatKhau.getPassword()).trim();

        if (taiKhoan.isEmpty() || matKhau.isEmpty()) {
            lblError.setText("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
            return;
        }

        NhanVien nv = nhanVienDAO.login(taiKhoan, matKhau);

        if (nv == null) {
            loginAttempts++;
            int con = MAX_ATTEMPTS - loginAttempts;
            lblError.setText("Sai tài khoản hoặc mật khẩu! Còn " + con + " lần thử.");
            txtMatKhau.setText("");
            txtMatKhau.requestFocus();
            return;
        }

        if (!"HoatDong".equalsIgnoreCase(nv.getTrangThai())) {
            lblError.setText("Tài khoản này đã bị vô hiệu hóa. Liên hệ Admin.");
            return;
        }

        // Đăng nhập thành công
        SessionManager.getInstance().setCurrentUser(nv);
        lblError.setText(" ");
        openMainFrame(nv);
    }

    /**
     * Mở đúng Frame theo vai trò, đóng LoginFrame.
     */
    private void openMainFrame(NhanVien nv) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame;
            switch (nv.getVaiTro().toLowerCase()) {
                case "admin":
                    frame = new AdminFrame();
                    break;
                case "manager":
                    frame = new ManagerFrame();
                    break;
                default: // staff
                    frame = new StaffFrame();
                    break;
            }
            frame.setVisible(true);
            this.dispose();
        });
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  HELPERS TẠO COMPONENT
    // ═════════════════════════════════════════════════════════════════════════

    private JLabel formLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(UITheme.TEXT2);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField createTextField(String placeholder) {
        JTextField tf = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(UITheme.MUTED);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    Insets ins = getInsets();
                    g2.drawString(placeholder, ins.left + 2, getHeight() / 2 + 5);
                }
            }
        };
        styleInput(tf);
        return tf;
    }

    private void styleInput(JComponent tf) {
        tf.setFont(UITheme.FONT_BODY);
        tf.setBackground(UITheme.BG_INPUT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                new EmptyBorder(8, 12, 8, 12)
        ));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UITheme.BLUE, 2),
                        new EmptyBorder(7, 11, 7, 11)));
            }
            @Override public void focusLost(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UITheme.BORDER),
                        new EmptyBorder(8, 12, 8, 12)));
            }
        });
    }

    private JButton createPrimaryButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker() :
                            getModel().isRollover() ? bg.brighter() : bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createOutlineButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_BODY);
        btn.setForeground(UITheme.TEXT2);
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                new EmptyBorder(8, 12, 8, 12)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UITheme.BLUE),
                        new EmptyBorder(8, 12, 8, 12)));
                btn.setForeground(UITheme.BLUE);
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UITheme.BORDER),
                        new EmptyBorder(8, 12, 8, 12)));
                btn.setForeground(UITheme.TEXT2);
            }
        });
        return btn;
    }
}