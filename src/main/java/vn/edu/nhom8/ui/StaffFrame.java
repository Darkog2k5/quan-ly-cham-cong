package vn.edu.nhom8.ui;

import vn.edu.nhom8.model.NhanVien;
import vn.edu.nhom8.util.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Màn hình Nhân viên (Staff).
 * Toolbar: Check-in | Check-out | Xem ca làm | Đổi ca
 * Content: Thông tin NV + bảng lịch sử chấm công
 */
public class StaffFrame extends BaseFrame {

    private JLabel lblStatus;
    private JButton btnCheckIn;
    private JButton btnCheckOut;
    private DefaultTableModel historyModel;
    private JLabel lblCaTen;
    private JLabel lblCaGio;
    private boolean isCheckedIn = false;
    private JTabbedPane tabs;

    // Thẻ tab index
    private static final int TAB_CHAMCONG = 0;
    private static final int TAB_LICHCA   = 1;
    private static final int TAB_DOICA    = 2;

    public StaffFrame() {
        super("Nhân viên");
        buildContent();
        setVisible(true);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  TOOLBAR
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    protected JPanel buildToolbar() {
        JPanel tb = createToolbarPanel();

        JButton btnCi  = toolbarBtn("", "Check-in");
        JButton btnCo  = toolbarBtn("", "Check-out");
        JButton btnXem = toolbarBtn("", "Xem ca làm");
        JButton btnDoi = toolbarBtn("", "Đổi ca");

        btnCi.addActionListener(e  -> { tabs.setSelectedIndex(TAB_CHAMCONG); doCheckIn(); });
        btnCo.addActionListener(e  -> { tabs.setSelectedIndex(TAB_CHAMCONG); doCheckOut(); });
        btnXem.addActionListener(e -> tabs.setSelectedIndex(TAB_LICHCA));
        btnDoi.addActionListener(e -> tabs.setSelectedIndex(TAB_DOICA));

        tb.add(btnCi);
        tb.add(btnCo);
        tb.add(toolbarSep());
        tb.add(btnXem);
        tb.add(btnDoi);
        return tb;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  CONTENT
    // ═════════════════════════════════════════════════════════════════════════

    private void buildContent() {
        NhanVien nv = SessionManager.getInstance().getCurrentUser();

        // ── Thanh thông tin NV trên cùng ─────────────────────────────────
        contentPanel.add(buildNVInfoBar(nv), BorderLayout.NORTH);

        // ── Tabs ──────────────────────────────────────────────────────────
        tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(UITheme.FONT_BODY);
        tabs.setBackground(UITheme.BG_PAGE);

        tabs.addTab("Chấm công", buildChamCongTab());
        tabs.addTab("Lịch ca làm", buildLichCaTab());
        tabs.addTab("Đổi ca", buildDoiCaTab());

        contentPanel.add(tabs, BorderLayout.CENTER);
    }

    // ── Thanh thông tin NV ────────────────────────────────────────────────

    private JPanel buildNVInfoBar(NhanVien nv) {
        JPanel bar = new JPanel(new GridLayout(1, 3, 12, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(0, 0, 14, 0));

        // Card 1: Avatar + tên
        JPanel card1 = createCard(null);
        card1.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 8));
        JPanel avatar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0,0,UITheme.NAVY,getWidth(),getHeight(),UITheme.BLUE);
                g2.setPaint(gp);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI",Font.BOLD,22));
                String ini = nv != null && nv.getHoTen().length()>0 ? nv.getHoTen().substring(0,1).toUpperCase() : "?";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(ini,(getWidth()-fm.stringWidth(ini))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        avatar.setPreferredSize(new Dimension(54, 54));
        avatar.setOpaque(false);

        JPanel nvMeta = new JPanel(new GridLayout(3, 1, 0, 2));
        nvMeta.setOpaque(false);
        JLabel lbName = new JLabel(nv != null ? nv.getHoTen() : "");
        lbName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        JLabel lbMaNV = new JLabel("Mã NV: " + (nv != null ? nv.getMaNV() : ""));
        lbMaNV.setFont(UITheme.FONT_SMALL); lbMaNV.setForeground(UITheme.MUTED);
        JLabel lbRole = new JLabel("Vai trò: " + (nv != null ? nv.getVaiTro() : ""));
        lbRole.setFont(UITheme.FONT_SMALL); lbRole.setForeground(UITheme.MUTED);
        nvMeta.add(lbName); nvMeta.add(lbMaNV); nvMeta.add(lbRole);
        card1.add(avatar); card1.add(nvMeta);

        // Card 2: Ca hôm nay
        JPanel card2 = createCard("Ca làm hôm nay");
        JPanel card2Body = new JPanel(new GridLayout(3,1,0,4));
        card2Body.setOpaque(false);
        lblCaTen = new JLabel("Đang tải...");
        lblCaTen.setFont(new Font("Segoe UI",Font.BOLD,16));
        lblCaTen.setForeground(UITheme.BLUE);
        lblCaGio = new JLabel("--:-- → --:--");
        lblCaGio.setFont(UITheme.FONT_BODY); lblCaGio.setForeground(UITheme.TEXT2);
        lblStatus = new JLabel("Chưa check-in");
        lblStatus.setFont(UITheme.FONT_BOLD);
        lblStatus.setForeground(UITheme.AMBER);
        card2Body.add(lblCaTen); card2Body.add(lblCaGio); card2Body.add(lblStatus);
        card2.add(card2Body, BorderLayout.CENTER);
        // Tải ca hôm nay (giả lập)
        lblCaTen.setText("Ca sáng");
        lblCaGio.setText("07:00  →  12:00");

        // Card 3: Nút check-in / check-out
        JPanel card3 = createCard("Chấm công nhanh");
        JPanel btnRow = new JPanel(new GridLayout(1,2,10,0));
        btnRow.setOpaque(false);
        btnCheckIn  = actionBtn("CHECK-IN",  UITheme.GREEN);
        btnCheckOut = actionBtn("CHECK-OUT", UITheme.AMBER);
        btnCheckOut.setEnabled(false);
        btnCheckIn.addActionListener(e  -> doCheckIn());
        btnCheckOut.addActionListener(e -> doCheckOut());
        btnRow.add(btnCheckIn); btnRow.add(btnCheckOut);
        card3.add(btnRow, BorderLayout.CENTER);

        bar.add(card1); bar.add(card2); bar.add(card3);
        return bar;
    }

    // ── Tab chấm công ─────────────────────────────────────────────────────

    private JPanel buildChamCongTab() {
        JPanel panel = new JPanel(new BorderLayout(0,12));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12,0,0,0));

        JLabel title = new JLabel("Lịch sử chấm công hôm nay");
        title.setFont(UITheme.FONT_HEAD);
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"Thời gian", "Sự kiện", "Phần mềm", "Thông tin chi tiết"};
        historyModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = createTable(cols);
        table.setModel(historyModel);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // ── Tab lịch ca ──────────────────────────────────────────────────────

    private JPanel buildLichCaTab() {
        JPanel panel = new JPanel(new BorderLayout(0,12));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12,0,0,0));

        JLabel title = new JLabel("Lịch làm việc cá nhân");
        title.setFont(UITheme.FONT_HEAD);
        panel.add(title, BorderLayout.NORTH);

        // Lưới lịch tháng (7 cột)
        JPanel calWrap = new JPanel(new BorderLayout(0,8));
        calWrap.setOpaque(false);

        // Điều hướng tháng
        JPanel navRow = new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));
        navRow.setOpaque(false);
        JButton btnPrev  = actionBtn("", UITheme.BLUE);
        JButton btnNext  = actionBtn("", UITheme.BLUE);
        JButton btnToday = actionBtn("Hôm nay", new Color(71,85,105));
        JLabel  lblMonth = new JLabel("Tháng 6 / 2025");
        lblMonth.setFont(new Font("Segoe UI",Font.BOLD,15));
        navRow.add(btnPrev); navRow.add(btnNext); navRow.add(btnToday);
        navRow.add(Box.createHorizontalStrut(12)); navRow.add(lblMonth);
        calWrap.add(navRow, BorderLayout.NORTH);

        // Grid tháng đơn giản (30 ngày)
        JPanel grid = new JPanel(new GridLayout(0,7,3,3));
        grid.setBackground(UITheme.BG_CARD);
        grid.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                new EmptyBorder(8,8,8,8)));

        String[] dows = {"CN","T2","T3","T4","T5","T6","T7"};
        for (String d : dows) {
            JLabel h = new JLabel(d, SwingConstants.CENTER);
            h.setFont(UITheme.FONT_BOLD);
            h.setForeground(UITheme.MUTED);
            grid.add(h);
        }
        // Tháng 6/2025 bắt đầu Chủ nhật → 0 ô trống ở đầu... thực ra bắt đầu CN (=0)
        // Thêm ô trống
        for (int i = 0; i < 0; i++) grid.add(new JLabel());

        java.util.Calendar cal = java.util.Calendar.getInstance();
        int today = cal.get(java.util.Calendar.DAY_OF_MONTH);
        // Giả lập ca: ngày 2,4,5,6,9,11
        java.util.Set<Integer> caSet = new java.util.HashSet<>(java.util.Arrays.asList(2,4,5,6,9,11,13,16,18));

        for (int d = 1; d <= 30; d++) {
            final int day = d;
            boolean hasCa = caSet.contains(d);
            JPanel cell = new JPanel(new BorderLayout());
            cell.setBorder(new EmptyBorder(3,3,3,3));
            cell.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            if (d == today) {
                cell.setBackground(UITheme.BLUE);
            } else if (hasCa) {
                cell.setBackground(UITheme.BLUE_PALE);
            } else {
                cell.setBackground(new Color(248,250,252));
            }
            JLabel lDay = new JLabel(String.valueOf(d), SwingConstants.CENTER);
            lDay.setFont(d == today
                    ? new Font("Segoe UI",Font.BOLD,13)
                    : UITheme.FONT_SMALL);
            lDay.setForeground(d == today ? Color.WHITE
                    : hasCa ? UITheme.NAVY : UITheme.MUTED);
            cell.add(lDay, BorderLayout.CENTER);
            if (hasCa && d != today) {
                JLabel dot = new JLabel("", SwingConstants.CENTER);
                dot.setFont(new Font("Segoe UI",Font.PLAIN,8));
                dot.setForeground(UITheme.BLUE);
                cell.add(dot, BorderLayout.SOUTH);
            }
            cell.addMouseListener(new java.awt.event.MouseAdapter(){
                @Override public void mouseClicked(java.awt.event.MouseEvent e){
                    if (caSet.contains(day)) {
                        JOptionPane.showMessageDialog(StaffFrame.this,
                            "Ngày " + day + "/06/2025\n\nCa làm: Ca sáng\nGiờ: 07:00 – 12:00\nTrạng thái: Đã phân",
                            "Chi tiết ca", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(StaffFrame.this,
                            "Ngày " + day + "/06/2025\n\nKhông có ca làm việc.",
                            "Chi tiết", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            });
            grid.add(cell);
        }
        calWrap.add(grid, BorderLayout.CENTER);

        // Chú thích
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT,12,4));
        legend.setOpaque(false);
        legend.add(legendItem(UITheme.BLUE,     "Hôm nay"));
        legend.add(legendItem(UITheme.BLUE_PALE,"Có ca làm"));
        legend.add(legendItem(new Color(248,250,252),"Không có ca"));
        calWrap.add(legend, BorderLayout.SOUTH);

        panel.add(calWrap, BorderLayout.CENTER);
        return panel;
    }

    // ── Tab đổi ca ────────────────────────────────────────────────────────

    private JPanel buildDoiCaTab() {
        JPanel panel = new JPanel(new GridLayout(1,2,14,0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12,0,0,0));

        // Form gửi yêu cầu
        JPanel formCard = createCard("Gửi yêu cầu đổi ca");
        JPanel formBody = new JPanel();
        formBody.setOpaque(false);
        formBody.setLayout(new BoxLayout(formBody, BoxLayout.Y_AXIS));

        JLabel hint = new JLabel("Chỉ được gửi cho ca trong tương lai.");
        hint.setFont(UITheme.FONT_SMALL);
        hint.setForeground(UITheme.AMBER);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] caItems = {"Ca sáng 07/06 (07:00–12:00)", "Ca chiều 08/06 (13:00–18:00)", "Ca tối 09/06 (18:00–22:00)"};
        JComboBox<String> cboCa = new JComboBox<>(caItems);
        cboCa.setFont(UITheme.FONT_BODY);
        cboCa.setMaximumSize(new Dimension(Integer.MAX_VALUE,32));
        cboCa.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtTarget = new JTextField();
        txtTarget.setFont(UITheme.FONT_BODY);
        txtTarget.setMaximumSize(new Dimension(Integer.MAX_VALUE,32));
        txtTarget.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtTarget.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),new EmptyBorder(4,8,4,8)));

        JTextArea txtLyDo = new JTextArea(3,20);
        txtLyDo.setFont(UITheme.FONT_BODY);
        txtLyDo.setLineWrap(true);
        txtLyDo.setWrapStyleWord(true);
        txtLyDo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),new EmptyBorder(6,8,6,8)));
        JScrollPane scrollLyDo = new JScrollPane(txtLyDo);
        scrollLyDo.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollLyDo.setMaximumSize(new Dimension(Integer.MAX_VALUE,90));

        JButton btnGui = actionBtn("Gửi yêu cầu", UITheme.BLUE);
        btnGui.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnGui.addActionListener(e -> {
            if (txtLyDo.getText().trim().isEmpty()) {
                showError("Vui lòng nhập lý do đổi ca."); return;
            }
            showSuccess("Gửi yêu cầu đổi ca thành công!\nVui lòng chờ Quản lý duyệt.");
            txtLyDo.setText(""); txtTarget.setText("");
        });

        formBody.add(hint);
        formBody.add(Box.createVerticalStrut(10));
        formBody.add(fieldLabel("Ca cần đổi *"));
        formBody.add(Box.createVerticalStrut(4));
        formBody.add(cboCa);
        formBody.add(Box.createVerticalStrut(10));
        formBody.add(fieldLabel("Mã NV muốn đổi cùng (không bắt buộc)"));
        formBody.add(Box.createVerticalStrut(4));
        formBody.add(txtTarget);
        formBody.add(Box.createVerticalStrut(10));
        formBody.add(fieldLabel("Lý do *"));
        formBody.add(Box.createVerticalStrut(4));
        formBody.add(scrollLyDo);
        formBody.add(Box.createVerticalStrut(14));
        formBody.add(btnGui);

        formCard.add(formBody, BorderLayout.CENTER);

        // Bảng yêu cầu đã gửi
        JPanel histCard = createCard("Lịch sử yêu cầu đổi ca");
        String[] cols = {"Mã YC","Ca gốc","Ngày","NV đổi cùng","Trạng thái","Ngày tạo"};
        JTable tbl = createTable(cols);
        DefaultTableModel mdl = (DefaultTableModel) tbl.getModel();
        mdl.addRow(new Object[]{"YC001","Ca sáng","03/06","NV002","Đã duyệt","02/06 08:00"});
        mdl.addRow(new Object[]{"YC002","Ca chiều","05/06","—","Từ chối","04/06 14:30"});
        JScrollPane scroll = new JScrollPane(tbl);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        histCard.add(scroll, BorderLayout.CENTER);

        panel.add(formCard);
        panel.add(histCard);
        return panel;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  LOGIC
    // ═════════════════════════════════════════════════════════════════════════

    private void doCheckIn() {
        if (isCheckedIn) { showError("Bạn đã check-in rồi!"); return; }
        String now = new SimpleDateFormat("HH:mm:ss").format(new Date());
        isCheckedIn = true;
        btnCheckIn.setEnabled(false);
        btnCheckOut.setEnabled(true);
        lblStatus.setText("Đang trong ca  (" + now + ")");
        lblStatus.setForeground(UITheme.GREEN);
        historyModel.addRow(new Object[]{now, "Check-in", "WorkShift Pro v1.0", "Ca sáng – Đúng giờ"});
        showSuccess("Check-in thành công lúc " + now + "!\nChúc bạn làm việc hiệu quả. ☕");
    }

    private void doCheckOut() {
        if (!isCheckedIn) { showError("Bạn chưa check-in!"); return; }
        int r = JOptionPane.showConfirmDialog(this, "Xác nhận check-out?", "Check-out",
                JOptionPane.YES_NO_OPTION);
        if (r != JOptionPane.YES_OPTION) return;
        String now = new SimpleDateFormat("HH:mm:ss").format(new Date());
        btnCheckOut.setEnabled(false);
        lblStatus.setText("Đã hoàn thành ca  (" + now + ")");
        lblStatus.setForeground(UITheme.MUTED);
        historyModel.addRow(new Object[]{now, "Check-out", "WorkShift Pro v1.0", "Kết thúc ca"});
        showSuccess("Check-out thành công lúc " + now + "!\nHẹn gặp lại.");
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI",Font.BOLD,11));
        lbl.setForeground(UITheme.TEXT2);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JPanel legendItem(Color color, String label) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));
        p.setOpaque(false);
        JLabel box = new JLabel("   ");
        box.setBackground(color);
        box.setOpaque(true);
        box.setBorder(BorderFactory.createLineBorder(color.darker()));
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_SMALL);
        p.add(box); p.add(lbl);
        return p;
    }
}
