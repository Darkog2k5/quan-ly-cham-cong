package vn.edu.nhom8.ui;

import vn.edu.nhom8.model.NhanVien;
import vn.edu.nhom8.util.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Màn hình Quản lý (Manager).
 * Toolbar: Xếp lịch NV | Duyệt đổi ca | Xem lịch tổng | Xuất báo cáo chấm công
 */
public class ManagerFrame extends BaseFrame {

    private JTabbedPane tabs;

    public ManagerFrame() {
        super("Quản lý");
        buildContent();
        setVisible(true);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  TOOLBAR
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    protected JPanel buildToolbar() {
        JPanel tb = createToolbarPanel();

        JButton btnXep  = toolbarBtn("📅", "Xếp lịch nhân viên");
        JButton btnDuyet= toolbarBtn("✅", "Duyệt đổi ca");
        JButton btnLich = toolbarBtn("📊", "Xem lịch tổng");
        JButton btnBc   = toolbarBtn("📄", "Xuất báo cáo");

        btnXep.addActionListener(e   -> tabs.setSelectedIndex(0));
        btnDuyet.addActionListener(e -> tabs.setSelectedIndex(1));
        btnLich.addActionListener(e  -> tabs.setSelectedIndex(2));
        btnBc.addActionListener(e    -> tabs.setSelectedIndex(3));

        tb.add(btnXep);
        tb.add(btnDuyet);
        tb.add(toolbarSep());
        tb.add(btnLich);
        tb.add(btnBc);
        return tb;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  CONTENT
    // ═════════════════════════════════════════════════════════════════════════

    private void buildContent() {
        // Stat bar
        contentPanel.add(buildStatBar(), BorderLayout.NORTH);

        // Tabs
        tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(UITheme.FONT_BODY);
        tabs.addTab("📅  Xếp lịch",        buildXepLichTab());
        tabs.addTab("✅  Duyệt đổi ca",     buildDuyetDoiCaTab());
        tabs.addTab("📊  Lịch tổng",        buildLichTongTab());
        tabs.addTab("📄  Xuất báo cáo",     buildBaoCaoTab());
        contentPanel.add(tabs, BorderLayout.CENTER);
    }

    // ── Stat bar ─────────────────────────────────────────────────────────────

    private JPanel buildStatBar() {
        JPanel bar = new JPanel(new GridLayout(1,4,12,0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(0,0,14,0));
        bar.add(statCard("👥  Nhân viên hoạt động", "24", UITheme.BLUE));
        bar.add(statCard("✅  Đã chấm công hôm nay", "18", UITheme.GREEN));
        bar.add(statCard("🔄  Yêu cầu chờ duyệt",   "3",  UITheme.AMBER));
        bar.add(statCard("❌  Chưa chấm công",       "6",  UITheme.RED));
        return bar;
    }

    private JPanel statCard(String label, String value, Color color) {
        JPanel card = createCard(null);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,4,0,0,color),
                new EmptyBorder(12,14,12,14)));
        JLabel lVal = new JLabel(value);
        lVal.setFont(new Font("Consolas",Font.BOLD,28));
        lVal.setForeground(color);
        JLabel lLbl = new JLabel("<html>" + label + "</html>");
        lLbl.setFont(UITheme.FONT_SMALL);
        lLbl.setForeground(UITheme.MUTED);
        card.add(lVal, BorderLayout.CENTER);
        card.add(lLbl, BorderLayout.SOUTH);
        return card;
    }

    // ── Tab Xếp lịch ────────────────────────────────────────────────────────

    private JPanel buildXepLichTab() {
        JPanel panel = new JPanel(new GridLayout(1,2,14,0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12,0,0,0));

        // Form xếp lịch
        JPanel formCard = createCard("📅  Xếp lịch cho nhân viên");
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner,"dd/MM/yyyy"));
        dateSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE,32));
        dateSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] caItems = {"Ca sáng (07:00–12:00)","Ca chiều (13:00–18:00)","Ca tối (18:00–22:00)"};
        JComboBox<String> cboCa = new JComboBox<>(caItems);
        cboCa.setFont(UITheme.FONT_BODY);
        cboCa.setMaximumSize(new Dimension(Integer.MAX_VALUE,32));
        cboCa.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Danh sách NV checkbox
        JPanel nvPanel = new JPanel();
        nvPanel.setLayout(new BoxLayout(nvPanel,BoxLayout.Y_AXIS));
        nvPanel.setBackground(new Color(248,250,252));
        nvPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                new EmptyBorder(8,10,8,10)));

        String[][] nvList = {{"NV001","Nguyễn Huy"},{"NV002","Trần Kiệt"},
                             {"NV003","Lê Khởi"},{"NV004","Phạm Minh"},{"NV005","Hoàng An"}};
        java.util.List<JCheckBox> checkBoxes = new java.util.ArrayList<>();
        for (String[] nv : nvList) {
            JCheckBox cb = new JCheckBox(nv[0] + "  –  " + nv[1]);
            cb.setFont(UITheme.FONT_BODY);
            cb.setOpaque(false);
            cb.setAlignmentX(Component.LEFT_ALIGNMENT);
            checkBoxes.add(cb);
            nvPanel.add(cb);
            nvPanel.add(Box.createVerticalStrut(4));
        }

        JScrollPane scrollNV = new JScrollPane(nvPanel);
        scrollNV.setMaximumSize(new Dimension(Integer.MAX_VALUE,130));
        scrollNV.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollNV.setBorder(null);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));
        btnRow.setOpaque(false);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE,36));
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton btnAll  = actionBtn("Chọn tất cả", new Color(71,85,105));
        JButton btnNone = actionBtn("Bỏ chọn",     new Color(100,116,135));
        btnAll.addActionListener(e  -> checkBoxes.forEach(c -> c.setSelected(true)));
        btnNone.addActionListener(e -> checkBoxes.forEach(c -> c.setSelected(false)));
        btnRow.add(btnAll); btnRow.add(btnNone);

        JButton btnLuu = actionBtn("💾  Lưu lịch", UITheme.GREEN);
        btnLuu.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLuu.addActionListener(e -> {
            long count = checkBoxes.stream().filter(JCheckBox::isSelected).count();
            if (count == 0) { showError("Vui lòng chọn ít nhất 1 nhân viên."); return; }
            showSuccess("Đã xếp lịch thành công cho " + count + " nhân viên!");
            checkBoxes.forEach(c -> c.setSelected(false));
        });

        body.add(flabel("Ngày làm việc *")); body.add(Box.createVerticalStrut(4));
        body.add(dateSpinner); body.add(Box.createVerticalStrut(10));
        body.add(flabel("Ca làm việc *")); body.add(Box.createVerticalStrut(4));
        body.add(cboCa); body.add(Box.createVerticalStrut(10));
        body.add(flabel("Nhân viên *")); body.add(Box.createVerticalStrut(4));
        body.add(btnRow); body.add(Box.createVerticalStrut(4));
        body.add(scrollNV); body.add(Box.createVerticalStrut(12));
        body.add(btnLuu);
        formCard.add(body, BorderLayout.CENTER);

        // Lịch đã xếp hôm nay
        JPanel rightCard = createCard("📋  Lịch đã xếp hôm nay");
        String[] cols = {"Mã lịch","Nhân viên","Ca","Ngày","Trạng thái"};
        JTable tbl = createTable(cols);
        DefaultTableModel mdl = (DefaultTableModel)tbl.getModel();
        mdl.addRow(new Object[]{"L001","Nguyễn Huy","Ca sáng","06/06","Đã phân"});
        mdl.addRow(new Object[]{"L002","Trần Kiệt","Ca chiều","06/06","Đã phân"});
        mdl.addRow(new Object[]{"L003","Lê Khởi","Ca tối","06/06","Đã phân"});
        JScrollPane scroll = new JScrollPane(tbl);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        rightCard.add(scroll, BorderLayout.CENTER);

        panel.add(formCard); panel.add(rightCard);
        return panel;
    }

    // ── Tab Duyệt đổi ca ─────────────────────────────────────────────────────

    private JPanel buildDuyetDoiCaTab() {
        JPanel panel = new JPanel(new BorderLayout(0,12));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12,0,0,0));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        JLabel title = new JLabel("✅  Yêu cầu đổi ca đang chờ duyệt");
        title.setFont(UITheme.FONT_HEAD);
        JButton btnRefresh = actionBtn("🔄  Làm mới", new Color(71,85,105));
        topRow.add(title, BorderLayout.WEST);
        topRow.add(btnRefresh, BorderLayout.EAST);
        panel.add(topRow, BorderLayout.NORTH);

        String[] cols = {"Mã YC","Người gửi","Ca gốc","Ngày ca","Đổi cùng","Lý do","Ngày tạo"};
        JTable tbl = createTable(cols);
        DefaultTableModel mdl = (DefaultTableModel)tbl.getModel();
        mdl.addRow(new Object[]{"YC003","Nguyễn Huy","Ca chiều","07/06","Trần Kiệt","Việc gia đình","05/06 10:00"});
        mdl.addRow(new Object[]{"YC004","Lê Khởi","Ca sáng","08/06","Phạm Minh","Khám sức khỏe","05/06 14:20"});
        mdl.addRow(new Object[]{"YC005","Phạm Minh","Ca tối","09/06","—","Bận đột xuất","05/06 20:15"});

        JScrollPane scroll = new JScrollPane(tbl);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        panel.add(scroll, BorderLayout.CENTER);

        // Action bar
        JPanel actRow = new JPanel(new FlowLayout(FlowLayout.LEFT,10,5));
        actRow.setOpaque(false);
        JButton btnDuyet  = actionBtn("✅  Duyệt",    UITheme.GREEN);
        JButton btnTuChoi = actionBtn("❌  Từ chối",  UITheme.RED);
        JLabel  hint      = new JLabel("← Chọn một hàng rồi nhấn nút");
        hint.setFont(UITheme.FONT_SMALL); hint.setForeground(UITheme.MUTED);

        btnDuyet.addActionListener(e -> {
            int row = tbl.getSelectedRow();
            if (row < 0) { showError("Vui lòng chọn một yêu cầu trong bảng."); return; }
            int r = JOptionPane.showConfirmDialog(this,
                    "Duyệt yêu cầu: " + mdl.getValueAt(row,0) + " — " + mdl.getValueAt(row,1) + "?",
                    "Xác nhận duyệt", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) { mdl.removeRow(row); showSuccess("Đã duyệt yêu cầu. Lịch phân ca đã cập nhật."); }
        });
        btnTuChoi.addActionListener(e -> {
            int row = tbl.getSelectedRow();
            if (row < 0) { showError("Vui lòng chọn một yêu cầu trong bảng."); return; }
            int r = JOptionPane.showConfirmDialog(this,
                    "Từ chối yêu cầu: " + mdl.getValueAt(row,0) + "?",
                    "Xác nhận từ chối", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (r == JOptionPane.YES_OPTION) { mdl.removeRow(row); showSuccess("Đã từ chối yêu cầu."); }
        });
        actRow.add(btnDuyet); actRow.add(btnTuChoi); actRow.add(hint);
        panel.add(actRow, BorderLayout.SOUTH);

        return panel;
    }

    // ── Tab Lịch tổng ────────────────────────────────────────────────────────

    private JPanel buildLichTongTab() {
        JPanel panel = new JPanel(new BorderLayout(0,12));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12,0,0,0));

        // Nav tuần
        JPanel navRow = new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));
        navRow.setOpaque(false);
        JButton btnPrev  = actionBtn("◀  Tuần trước", UITheme.BLUE);
        JButton btnNext  = actionBtn("Tuần sau  ▶",   UITheme.BLUE);
        JButton btnToday = actionBtn("Tuần này",       new Color(71,85,105));
        JLabel lblWeek   = new JLabel("Tuần 23 — 02/06 đến 08/06/2025");
        lblWeek.setFont(new Font("Segoe UI",Font.BOLD,14));
        navRow.add(btnPrev); navRow.add(btnToday); navRow.add(btnNext);
        navRow.add(Box.createHorizontalStrut(16)); navRow.add(lblWeek);
        panel.add(navRow, BorderLayout.NORTH);

        // Lưới tuần
        String[] nvNames = {"Nguyễn Huy","Trần Kiệt","Lê Khởi","Phạm Minh","Hoàng An"};
        String[] days = {"T2\n02/06","T3\n03/06","T4\n04/06","T5\n05/06","T6\n06/06","T7\n07/06","CN\n08/06"};
        // Data giả lập
        String[][] data = {
            {"CA_SANG","","CA_CHIEU","CA_SANG","CA_SANG","",""},
            {"CA_CHIEU","CA_SANG","","CA_CHIEU","CA_CHIEU","CA_SANG",""},
            {"","CA_TOI","CA_SANG","","CA_TOI","","CA_SANG"},
            {"CA_SANG","CA_CHIEU","CA_SANG","CA_SANG","","CA_CHIEU",""},
            {"CA_TOI","","","CA_TOI","CA_SANG","","CA_CHIEU"}
        };

        JPanel gridCard = new JPanel(new BorderLayout());
        gridCard.setBackground(UITheme.BG_CARD);
        gridCard.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));

        // Bảng
        int rows = nvNames.length;
        int cols = days.length + 1; // +1 cột tên NV
        JPanel grid = new JPanel(new GridLayout(rows+1, cols, 1, 1));
        grid.setBackground(UITheme.BORDER);

        // Header
        JLabel hdrNV = headerCell("Nhân viên", UITheme.NAVY2, Color.WHITE);
        grid.add(hdrNV);
        for (String d : days) {
            boolean isToday = d.contains("06/06");
            Color bg = isToday ? UITheme.NAVY : UITheme.NAVY2;
            grid.add(headerCell("<html><center>" + d.replace("\n","<br>") + "</center></html>", bg, Color.WHITE));
        }

        // Hàng NV
        for (int i = 0; i < nvNames.length; i++) {
            JLabel nvLbl = new JLabel("  " + nvNames[i]);
            nvLbl.setFont(UITheme.FONT_SMALL);
            nvLbl.setForeground(UITheme.TEXT2);
            nvLbl.setBackground(i%2==0 ? Color.WHITE : new Color(248,250,252));
            nvLbl.setOpaque(true);
            nvLbl.setPreferredSize(new Dimension(120,44));
            grid.add(nvLbl);

            for (int d = 0; d < days.length; d++) {
                String shift = data[i][d];
                JPanel cell = new JPanel(new GridBagLayout());
                boolean isToday = days[d].contains("06/06");
                cell.setBackground(isToday
                        ? new Color(239,246,255)
                        : (i%2==0 ? Color.WHITE : new Color(248,250,252)));
                cell.setPreferredSize(new Dimension(80,44));
                if (!shift.isEmpty()) {
                    JLabel lbl = shiftLabel(shift);
                    cell.add(lbl);
                }
                grid.add(cell);
            }
        }
        gridCard.add(grid, BorderLayout.CENTER);

        // Chú thích
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT,14,4));
        legend.setOpaque(false);
        legend.add(legendItem(UITheme.BLUE_PALE,  new Color(30,64,175),  "Ca sáng (07:00–12:00)"));
        legend.add(legendItem(UITheme.AMBER_PALE, new Color(120,53,15),  "Ca chiều (13:00–18:00)"));
        legend.add(legendItem(new Color(237,233,254), new Color(76,29,149), "Ca tối (18:00–22:00)"));
        gridCard.add(legend, BorderLayout.SOUTH);

        JScrollPane scroll = new JScrollPane(gridCard);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ── Tab Xuất báo cáo ─────────────────────────────────────────────────────

    private JPanel buildBaoCaoTab() {
        JPanel panel = new JPanel(new GridLayout(1,2,14,0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12,0,0,0));

        // Form
        JPanel formCard = createCard("📄  Xuất báo cáo chấm công");
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body,BoxLayout.Y_AXIS));

        String[] months = {"Tháng 1","Tháng 2","Tháng 3","Tháng 4","Tháng 5","Tháng 6",
                           "Tháng 7","Tháng 8","Tháng 9","Tháng 10","Tháng 11","Tháng 12"};
        JComboBox<String> cboThang = new JComboBox<>(months);
        cboThang.setSelectedIndex(5);
        cboThang.setFont(UITheme.FONT_BODY);
        cboThang.setMaximumSize(new Dimension(Integer.MAX_VALUE,32));
        cboThang.setAlignmentX(Component.LEFT_ALIGNMENT);

        SpinnerNumberModel yearModel = new SpinnerNumberModel(2025,2020,2030,1);
        JSpinner spNam = new JSpinner(yearModel);
        spNam.setMaximumSize(new Dimension(Integer.MAX_VALUE,32));
        spNam.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnXuat = actionBtn("📊  Xuất file Excel (.xlsx)", UITheme.GREEN);
        btnXuat.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnXuat.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new java.io.File("BaoCaoChamCong_T6_2025.xlsx"));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                showSuccess("Xuất báo cáo thành công!\nFile: " + fc.getSelectedFile().getAbsolutePath());
            }
        });

        body.add(flabel("Chọn tháng *")); body.add(Box.createVerticalStrut(4));
        body.add(cboThang); body.add(Box.createVerticalStrut(10));
        body.add(flabel("Năm *")); body.add(Box.createVerticalStrut(4));
        body.add(spNam); body.add(Box.createVerticalStrut(14));
        body.add(btnXuat);
        formCard.add(body, BorderLayout.CENTER);

        // Preview bảng tổng hợp
        JPanel previewCard = createCard("📋  Tổng hợp tháng 6/2025");
        String[] cols = {"Nhân viên","Tổng ca","Tổng giờ","Đúng giờ","Đi trễ","Về sớm"};
        JTable tbl = createTable(cols);
        DefaultTableModel mdl = (DefaultTableModel)tbl.getModel();
        mdl.addRow(new Object[]{"Nguyễn Huy","18","90h","16","2","0"});
        mdl.addRow(new Object[]{"Trần Kiệt","20","100h","20","0","0"});
        mdl.addRow(new Object[]{"Lê Khởi","15","75h","14","1","0"});
        mdl.addRow(new Object[]{"Phạm Minh","12","60h","11","1","0"});
        JScrollPane scroll = new JScrollPane(tbl);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        previewCard.add(scroll, BorderLayout.CENTER);

        panel.add(formCard); panel.add(previewCard);
        return panel;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private JLabel flabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI",Font.BOLD,11));
        lbl.setForeground(UITheme.TEXT2);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JLabel headerCell(String text, Color bg, Color fg) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(UITheme.FONT_BOLD);
        lbl.setBackground(bg);
        lbl.setForeground(fg);
        lbl.setOpaque(true);
        lbl.setPreferredSize(new Dimension(80,40));
        return lbl;
    }

    private JLabel shiftLabel(String type) {
        String text; Color bg; Color fg;
        switch (type) {
            case "CA_SANG":  text="Sáng";  bg=UITheme.BLUE_PALE; fg=new Color(30,64,175); break;
            case "CA_CHIEU": text="Chiều"; bg=UITheme.AMBER_PALE;fg=new Color(120,53,15); break;
            case "CA_TOI":   text="Tối";   bg=new Color(237,233,254);fg=new Color(76,29,149); break;
            default:         return new JLabel();
        }
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI",Font.BOLD,10));
        lbl.setBackground(bg); lbl.setForeground(fg); lbl.setOpaque(true);
        lbl.setBorder(new EmptyBorder(3,8,3,8));
        return lbl;
    }

    private JPanel legendItem(Color bg, Color fg, String label) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));
        p.setOpaque(false);
        JLabel box = new JLabel("   ");
        box.setBackground(bg); box.setOpaque(true);
        box.setBorder(BorderFactory.createLineBorder(bg.darker()));
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_SMALL);
        lbl.setForeground(fg);
        p.add(box); p.add(lbl);
        return p;
    }
}
