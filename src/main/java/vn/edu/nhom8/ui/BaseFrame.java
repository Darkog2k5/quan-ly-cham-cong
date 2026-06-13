package vn.edu.nhom8.ui;

import vn.edu.nhom8.model.NhanVien;
import vn.edu.nhom8.util.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Frame gốc dùng chung cho StaffFrame, ManagerFrame, AdminFrame.
 *
 * Layout:
 *   ┌─────────────────────────────────────────────────────────┐
 *   │  Topbar: Logo | Clock | NV info + Bell + Avatar         │
 *   ├─────────────────────────────────────────────────────────┤
 *   │  RoleTabs: [ Nhân viên ] [ Quản lý ] [ Quản trị HT ]  │  ← tab vai trò
 *   ├─────────────────────────────────────────────────────────┤
 *   │  Toolbar (thay đổi theo tab đang chọn)                  │
 *   ├─────────────────────────────────────────────────────────┤
 *   │  ContentPanel (nội dung tab đang chọn)                  │
 *   └─────────────────────────────────────────────────────────┘
 *
 * Quyền truy cập:
 *   Staff   → chỉ tab 0 (Nhân viên)
 *   Manager → tab 0 + 1 (Nhân viên + Quản lý)
 *   Admin   → tab 0 + 1 + 2
 */
public abstract class BaseFrame extends JFrame {

    // ── Index các tab vai trò ─────────────────────────────────────────────────
    public static final int ROLE_TAB_STAFF   = 0;
    public static final int ROLE_TAB_MANAGER = 1;
    public static final int ROLE_TAB_ADMIN   = 2;

    /** Tab vai trò cấp cao – subclass KHÔNG được thêm tab vào đây trực tiếp */
    protected JTabbedPane roleTabs;

    /**
     * Panel nội dung cho từng tab vai trò.
     * Index = ROLE_TAB_STAFF / ROLE_TAB_MANAGER / ROLE_TAB_ADMIN
     */
    protected JPanel[] roleContentPanels = new JPanel[3];

    /**
     * Toolbar cho từng tab vai trò – subclass ghi đè buildToolbarForRole(int).
     * Index = ROLE_TAB_STAFF / ROLE_TAB_MANAGER / ROLE_TAB_ADMIN
     */
    private JPanel[] roleToolbars = new JPanel[3];

    /** Panel wrapper chứa toolbar hiện hành (thay đổi khi đổi tab) */
    private JPanel toolbarWrapper;

    /** Badge chuông thông báo */
    protected JLabel lblBadge;

    private JLabel lblClock;
    private Timer  clockTimer;

    // ─────────────────────────────────────────────────────────────────────────

    public BaseFrame(String title) {
        setTitle("WorkShift Pro — " + title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 760);
        setMinimumSize(new Dimension(960, 640));
        setLocationRelativeTo(null);
        initLayout();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  LAYOUT KHUNG
    // ═════════════════════════════════════════════════════════════════════════

    private void initLayout() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_PAGE);

        // ── Topbar ──────────────────────────────────────────────────────────
        root.add(buildTopBar(), BorderLayout.NORTH);

        // ── Center: roleTabs + toolbar + content ─────────────────────────────
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(UITheme.BG_PAGE);

        // RoleTabs
        roleTabs = buildRoleTabs();
        center.add(roleTabs, BorderLayout.NORTH);

        // Toolbar wrapper – sẽ swap khi đổi tab
        toolbarWrapper = new JPanel(new BorderLayout());
        toolbarWrapper.setOpaque(false);

        // Content scrollable wrapper
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(UITheme.BG_PAGE);
        contentWrapper.add(toolbarWrapper, BorderLayout.NORTH);

        // Tạo sẵn 3 roleContentPanels
        for (int i = 0; i < 3; i++) {
            roleContentPanels[i] = new JPanel(new BorderLayout());
            roleContentPanels[i].setBackground(UITheme.BG_PAGE);
            roleContentPanels[i].setBorder(new EmptyBorder(16, 16, 16, 16));
        }

        // CardLayout để swap content theo tab
        CardLayout cardLayout = new CardLayout();
        JPanel cardHost = new JPanel(cardLayout);
        cardHost.setBackground(UITheme.BG_PAGE);
        for (int i = 0; i < 3; i++) {
            JScrollPane scroll = new JScrollPane(roleContentPanels[i]);
            scroll.setBorder(null);
            scroll.getVerticalScrollBar().setUnitIncrement(16);
            scroll.setBackground(UITheme.BG_PAGE);
            cardHost.add(scroll, "role" + i);
        }

        contentWrapper.add(cardHost, BorderLayout.CENTER);
        center.add(contentWrapper, BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);
        setContentPane(root);

        // Lắng nghe khi đổi tab vai trò
        roleTabs.addChangeListener(e -> {
            int idx = roleTabs.getSelectedIndex();
            // Cập nhật toolbar
            toolbarWrapper.removeAll();
            if (roleToolbars[idx] != null) {
                toolbarWrapper.add(roleToolbars[idx], BorderLayout.CENTER);
            }
            toolbarWrapper.revalidate();
            toolbarWrapper.repaint();
            // Cập nhật content
            cardLayout.show(cardHost, "role" + idx);
            // Hook cho subclass
            onRoleTabChanged(idx);
        });
    }

    // ── RoleTabs ──────────────────────────────────────────────────────────────

    private JTabbedPane buildRoleTabs() {
        JTabbedPane tp = new JTabbedPane(JTabbedPane.TOP) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(UITheme.NAVY2);
                g2.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        tp.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tp.setOpaque(true);
        tp.setBackground(UITheme.NAVY2);
        tp.setForeground(Color.WHITE);

        // Thêm 3 tab (nội dung placeholder – subclass sẽ populate roleContentPanels)
        tp.addTab("👤  Nhân viên",       new JPanel());
        tp.addTab("📊  Quản lý",          new JPanel());
        tp.addTab("🛠  Quản trị hệ thống", new JPanel());

        // Custom tab UI
        tp.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement,
                    int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2 = (Graphics2D) g;
                if (isSelected) {
                    g2.setColor(UITheme.BG_PAGE);
                    g2.fillRect(x, y, w, h);
                } else if (!tp.isEnabledAt(tabIndex)) {
                    g2.setColor(new Color(30, 41, 59)); // disabled
                    g2.fillRect(x, y, w, h);
                } else {
                    g2.setColor(UITheme.NAVY2);
                    g2.fillRect(x, y, w, h);
                }
            }
            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement,
                    int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                if (isSelected) {
                    g.setColor(UITheme.BLUE);
                    g.fillRect(x + 2, y + h - 3, w - 4, 3);
                }
            }
            @Override
            protected void paintFocusIndicator(Graphics g, int tabPlacement,
                    Rectangle[] rects, int tabIndex, Rectangle iconRect,
                    Rectangle textRect, boolean isSelected) { /* no focus ring */ }
            @Override protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) { return 38; }
        });

        // Màu chữ theo trạng thái (sẽ được setEnabledAt từ subclass)
        tp.addChangeListener(e -> updateTabColors(tp));
        updateTabColors(tp);

        return tp;
    }

    private void updateTabColors(JTabbedPane tp) {
        for (int i = 0; i < tp.getTabCount(); i++) {
            if (!tp.isEnabledAt(i)) {
                tp.setForegroundAt(i, new Color(71, 85, 105)); // disabled text
            } else if (tp.getSelectedIndex() == i) {
                tp.setForegroundAt(i, UITheme.TEXT);
            } else {
                tp.setForegroundAt(i, new Color(203, 213, 225));
            }
        }
    }

    // ── Topbar ────────────────────────────────────────────────────────────────

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UITheme.NAVY);
        bar.setPreferredSize(new Dimension(0, 56));
        bar.setBorder(new EmptyBorder(0, 16, 0, 16));

        // Trái: logo + tên hệ thống
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        JLabel lblLogo = new JLabel("⚡");
        lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        lblLogo.setForeground(new Color(251, 191, 36));

        JLabel lblApp = new JLabel("WorkShift Pro");
        lblApp.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblApp.setForeground(Color.WHITE);

        // Clock ở giữa
        lblClock = new JLabel();
        lblClock.setFont(UITheme.FONT_CLOCK);
        lblClock.setForeground(new Color(251, 191, 36));
        lblClock.setHorizontalAlignment(SwingConstants.CENTER);

        // Phải: thông tin NV + chuông + avatar
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        NhanVien nv = SessionManager.getInstance().getCurrentUser();
        String ten    = nv != null ? nv.getHoTen() : "Người dùng";
        String vaiTro = nv != null ? nv.getVaiTro() : "";

        JPanel nvInfo = new JPanel(new GridLayout(2, 1, 0, 0));
        nvInfo.setOpaque(false);
        JLabel lblTen = new JLabel(ten);
        lblTen.setFont(UITheme.FONT_BOLD);
        lblTen.setForeground(Color.WHITE);
        JLabel lblRole = new JLabel(vaiTro);
        lblRole.setFont(UITheme.FONT_SMALL);
        lblRole.setForeground(new Color(148, 163, 184));
        nvInfo.add(lblTen);
        nvInfo.add(lblRole);

        // Chuông thông báo
        JButton btnBell = createIconButton("🔔");
        lblBadge = new JLabel("●");
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblBadge.setForeground(Color.RED);
        lblBadge.setVisible(false);
        JPanel bellWrap = new JPanel(null);
        bellWrap.setOpaque(false);
        bellWrap.setPreferredSize(new Dimension(38, 38));
        btnBell.setBounds(0, 4, 32, 30);
        lblBadge.setBounds(20, 0, 14, 14);
        bellWrap.add(btnBell);
        bellWrap.add(lblBadge);
        btnBell.addActionListener(e -> onBellClick());

        // Avatar
        String initials = ten.length() >= 1
                ? String.valueOf(ten.charAt(0)).toUpperCase()
                : "?";
        JButton btnAvatar = new JButton(initials) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, UITheme.BLUE, getWidth(), getHeight(), UITheme.TEAL);
                g2.setPaint(gp);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        btnAvatar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAvatar.setPreferredSize(new Dimension(36, 36));
        btnAvatar.setContentAreaFilled(false);
        btnAvatar.setBorderPainted(false);
        btnAvatar.setFocusPainted(false);
        btnAvatar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAvatar.addActionListener(e -> showAvatarMenu(btnAvatar));

        left.add(lblLogo);
        left.add(lblApp);
        right.add(nvInfo);
        right.add(bellWrap);
        right.add(btnAvatar);

        JPanel centerWrap = new JPanel(new GridBagLayout());
        centerWrap.setOpaque(false);
        centerWrap.add(lblClock);

        bar.add(left,       BorderLayout.WEST);
        bar.add(centerWrap, BorderLayout.CENTER);
        bar.add(right,      BorderLayout.EAST);

        clockTimer = new Timer(1000, e -> updateClock());
        clockTimer.start();
        updateClock();

        return bar;
    }

    private void updateClock() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss   dd/MM/yyyy");
        lblClock.setText(sdf.format(new Date()));
    }

    // ── Menu avatar popup ────────────────────────────────────────────────────

    private void showAvatarMenu(Component anchor) {
        JPopupMenu popup = new JPopupMenu();
        popup.setBackground(Color.WHITE);
        popup.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));

        NhanVien nv = SessionManager.getInstance().getCurrentUser();
        JMenuItem miName = new JMenuItem("👤  " + (nv != null ? nv.getHoTen() : "") +
                "  (" + (nv != null ? nv.getVaiTro() : "") + ")");
        miName.setFont(UITheme.FONT_BOLD);
        miName.setEnabled(false);

        JMenuItem miInfo   = menuItem("Đổi thông tin cá nhân", "✏️");
        JMenuItem miPwd    = menuItem("Đổi mật khẩu", "🔐");
        JMenuItem miLogout = menuItem("Đăng xuất", "🚪");
        miLogout.setForeground(UITheme.RED);

        miInfo.addActionListener(e   -> showDoiThongTin());
        miPwd.addActionListener(e    -> showDoiMatKhau());
        miLogout.addActionListener(e -> doLogout());

        popup.add(miName);
        popup.addSeparator();
        popup.add(miInfo);
        popup.add(miPwd);
        popup.addSeparator();
        popup.add(miLogout);

        popup.show(anchor, 0, anchor.getHeight() + 4);
    }

    // ── Popup đổi mật khẩu ───────────────────────────────────────────────────

    private void showDoiMatKhau() {
        JPanel p = new JPanel(new GridLayout(3, 2, 8, 10));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        JPasswordField fOld  = new JPasswordField();
        JPasswordField fNew  = new JPasswordField();
        JPasswordField fConf = new JPasswordField();
        p.add(new JLabel("Mật khẩu cũ:"));   p.add(fOld);
        p.add(new JLabel("Mật khẩu mới:"));   p.add(fNew);
        p.add(new JLabel("Xác nhận:"));        p.add(fConf);

        int r = JOptionPane.showConfirmDialog(this, p, "Đổi mật khẩu",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r == JOptionPane.OK_OPTION) {
            String n = new String(fNew.getPassword());
            String c = new String(fConf.getPassword());
            if (n.isEmpty()) { showError("Mật khẩu mới không được để trống."); return; }
            if (!n.equals(c)) { showError("Mật khẩu xác nhận không khớp."); return; }
            if (n.length() < 6) { showError("Mật khẩu phải có ít nhất 6 ký tự."); return; }
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!", "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showDoiThongTin() {
        JOptionPane.showMessageDialog(this,
                "Tính năng đổi thông tin cá nhân sẽ được cập nhật sau.",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void doLogout() {
        int r = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn đăng xuất không?",
                "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            clockTimer.stop();
            SessionManager.getInstance().logout();
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  PROTECTED API cho subclass
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Subclass gọi trong constructor TRƯỚC khi setVisible:
     *   initRoleTabs(enabledUntilIndex, defaultTabIndex);
     *
     * Ví dụ:
     *   Staff   → initRoleTabs(ROLE_TAB_STAFF,   ROLE_TAB_STAFF)
     *   Manager → initRoleTabs(ROLE_TAB_MANAGER, ROLE_TAB_MANAGER)
     *   Admin   → initRoleTabs(ROLE_TAB_ADMIN,   ROLE_TAB_ADMIN)
     */
    protected void initRoleTabs(int enabledUntilIndex, int defaultTabIndex) {
        // Disable các tab vượt quyền
        for (int i = 0; i < roleTabs.getTabCount(); i++) {
            roleTabs.setEnabledAt(i, i <= enabledUntilIndex);
        }
        updateTabColors(roleTabs);

        // Build toolbar cho mỗi tab đã enable
        for (int i = 0; i <= enabledUntilIndex; i++) {
            roleToolbars[i] = buildToolbarForRole(i);
        }

        // Hiện tab mặc định
        roleTabs.setSelectedIndex(defaultTabIndex);
        toolbarWrapper.removeAll();
        if (roleToolbars[defaultTabIndex] != null) {
            toolbarWrapper.add(roleToolbars[defaultTabIndex], BorderLayout.CENTER);
        }
        toolbarWrapper.revalidate();
    }

    /**
     * Subclass override để build toolbar riêng cho mỗi tab vai trò.
     * @param roleTabIndex ROLE_TAB_STAFF | ROLE_TAB_MANAGER | ROLE_TAB_ADMIN
     * @return JPanel toolbar, hoặc null nếu không cần toolbar
     */
    protected JPanel buildToolbarForRole(int roleTabIndex) {
        return null; // mặc định: không có toolbar
    }

    /**
     * Cho phép subclass khóa (hoặc mở) một tab vai trò cụ thể sau khi initRoleTabs.
     * Dùng khi 1 vai trò không được phép truy cập tab thấp hơn
     * (ví dụ: Quản lý không được vào tab "Nhân viên").
     */
    protected void setRoleTabEnabled(int roleTabIndex, boolean enabled) {
        roleTabs.setEnabledAt(roleTabIndex, enabled);
        updateTabColors(roleTabs);
    }

    /** Hook cho subclass. */
    protected void onRoleTabChanged(int newRoleTabIndex) { }

    /**
     * Trả về panel nội dung của tab vai trò tương ứng.
     * Subclass dùng để đặt nội dung: getRoleContentPanel(ROLE_TAB_STAFF).add(...)
     */
    protected JPanel getRoleContentPanel(int roleTabIndex) {
        return roleContentPanels[roleTabIndex];
    }

    // Backward-compat: subclass cũ dùng contentPanel → trỏ vào tab Staff
    @Deprecated
    protected JPanel contentPanel;

    /**
     * Gọi sau khi subclass đặt nội dung vào roleContentPanels,
     * để ghi đè contentPanel trỏ về đúng tab.
     * @deprecated Dùng getRoleContentPanel(index) thay thế.
     */
    @Deprecated
    protected void setDefaultContentPanel(int roleTabIndex) {
        this.contentPanel = roleContentPanels[roleTabIndex];
    }

    /** Chuông – hook. */
    protected void onBellClick() {
        JOptionPane.showMessageDialog(this,
                "Chưa có thông báo mới.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  HELPERS
    // ═════════════════════════════════════════════════════════════════════════

    protected JButton createIconButton(String icon) {
        JButton btn = new JButton(icon) {
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                if (hovered) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(255, 255, 255, 30));
                    g2.fillOval(1, 1, getWidth() - 2, getHeight() - 2);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setForeground(Color.WHITE);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    protected JMenuItem menuItem(String text, String icon) {
        JMenuItem mi = new JMenuItem(icon + "  " + text);
        mi.setFont(UITheme.FONT_BODY);
        mi.setBorder(new EmptyBorder(6, 12, 6, 20));
        return mi;
    }

    /** Tạo nút Toolbar chuẩn (icon + nhãn) */
    protected JButton toolbarBtn(String icon, String label) {
        String display = (icon != null && !icon.isEmpty()) ? icon + "  " + label : label;
        JButton btn = new JButton(display) {
            private boolean hovered = false;
            private boolean pressed = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e)  { hovered = true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)   { hovered = false; pressed = false; repaint(); }
                    @Override public void mousePressed(MouseEvent e)  { pressed = true;  repaint(); }
                    @Override public void mouseReleased(MouseEvent e) { pressed = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (pressed) {
                    g2.setColor(new Color(255, 255, 255, 55));
                    g2.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 8, 8, 8);
                } else if (hovered) {
                    g2.setColor(new Color(255, 255, 255, 35));
                    g2.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 8, 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(UITheme.FONT_BODY);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setBorder(new EmptyBorder(6, 10, 6, 10));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /** Tạo JPanel toolbar nền NAVY2 */
    protected JPanel createToolbarPanel() {
        JPanel tb = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 4));
        tb.setBackground(UITheme.NAVY2);
        tb.setPreferredSize(new Dimension(0, 56));
        tb.setBorder(new EmptyBorder(0, 8, 0, 8));
        return tb;
    }

    /** Tạo separator mỏng trong toolbar */
    protected JComponent toolbarSep() {
        JPanel sep = new JPanel();
        sep.setBackground(new Color(255, 255, 255, 40));
        sep.setPreferredSize(new Dimension(1, 36));
        return sep;
    }

    /** Hiển thị thông báo lỗi */
    protected void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    /** Hiển thị thông báo thành công */
    protected void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Tạo card trắng bo góc */
    protected JPanel createCard(String titleText) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                new EmptyBorder(12, 14, 14, 14)
        ));
        if (titleText != null && !titleText.isEmpty()) {
            JLabel title = new JLabel(titleText);
            title.setFont(UITheme.FONT_HEAD);
            title.setForeground(UITheme.TEXT);
            title.setBorder(new EmptyBorder(0, 0, 10, 0));
            card.add(title, BorderLayout.NORTH);
        }
        return card;
    }

    /** Tạo JTable đẹp chuẩn */
    protected JTable createTable(String[] cols) {
        JTable table = new JTable(new javax.swing.table.DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        table.setFont(UITheme.FONT_BODY);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(UITheme.BLUE_PALE);
        table.setSelectionForeground(UITheme.TEXT);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                lbl.setBackground(UITheme.NAVY);
                lbl.setForeground(Color.WHITE);
                lbl.setFont(UITheme.FONT_BOLD);
                lbl.setOpaque(true);
                lbl.setBorder(new EmptyBorder(0, 10, 0, 10));
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                return lbl;
            }
        });
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) comp.setBackground(r % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return comp;
            }
        });
        return table;
    }

    /** Nút hành động trong card */
    protected JButton actionBtn(String text, Color bg) {
        Color bgHover = bg.darker();
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_BOLD);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(bgHover); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }

    // ── Backward compat: subclass cũ có buildToolbar() abstract ──────────────
    // Giữ lại để không break compile (để trống)
    protected JPanel buildToolbar() { return null; }
}