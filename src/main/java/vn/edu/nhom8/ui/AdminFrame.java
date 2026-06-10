package vn.edu.nhom8.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Màn hình Quản trị hệ thống (Admin).
 * Toolbar nhóm: [Thêm ca | Sửa ca | Xóa ca | Xem ds ca] | [Thêm TK | Sửa TK | Khóa TK | Ds TK]
 */
public class AdminFrame extends BaseFrame {

    private JTabbedPane tabs;

    // Models
    private DefaultTableModel caModel;
    private DefaultTableModel nvModel;

    public AdminFrame() {
        super("Quản trị hệ thống");
        buildContent();
        setVisible(true);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  TOOLBAR
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    protected JPanel buildToolbar() {
        JPanel tb = createToolbarPanel();

        // Nhóm ca làm
        JLabel lblCa = new JLabel("  Cấu hình ca làm  ");
        lblCa.setFont(new Font("Segoe UI",Font.BOLD,9));
        lblCa.setForeground(new Color(255,255,255,100));

        JButton btnThemCa  = toolbarBtn("➕", "Thêm ca");
        JButton btnSuaCa   = toolbarBtn("✏️", "Sửa ca");
        JButton btnXoaCa   = toolbarBtn("🗑", "Xóa ca");
        JButton btnDsCa    = toolbarBtn("📋", "Xem ds ca");

        // Nhóm tài khoản
        JLabel lblTK = new JLabel("  Quản lý tài khoản  ");
        lblTK.setFont(new Font("Segoe UI",Font.BOLD,9));
        lblTK.setForeground(new Color(255,255,255,100));

        JButton btnThemTK  = toolbarBtn("👤+", "Thêm TK");
        JButton btnSuaTK   = toolbarBtn("✏️", "Sửa TK");
        JButton btnKhoaTK  = toolbarBtn("🔒", "Khóa TK");
        JButton btnDsTK    = toolbarBtn("👥", "Ds tài khoản");

        btnThemCa.addActionListener(e  -> { tabs.setSelectedIndex(0); showFormThemCa(false, -1); });
        btnSuaCa.addActionListener(e   -> { tabs.setSelectedIndex(0); suaCaSelected(); });
        btnXoaCa.addActionListener(e   -> { tabs.setSelectedIndex(0); xoaCaSelected(); });
        btnDsCa.addActionListener(e    -> tabs.setSelectedIndex(0));
        btnThemTK.addActionListener(e  -> { tabs.setSelectedIndex(1); showFormThemTK(false, -1); });
        btnSuaTK.addActionListener(e   -> { tabs.setSelectedIndex(1); suaTKSelected(); });
        btnKhoaTK.addActionListener(e  -> { tabs.setSelectedIndex(1); khoaTKSelected(); });
        btnDsTK.addActionListener(e    -> tabs.setSelectedIndex(1));

        tb.add(lblCa);
        tb.add(btnThemCa); tb.add(btnSuaCa); tb.add(btnXoaCa); tb.add(btnDsCa);
        tb.add(toolbarSep());
        tb.add(lblTK);
        tb.add(btnThemTK); tb.add(btnSuaTK); tb.add(btnKhoaTK); tb.add(btnDsTK);
        return tb;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  CONTENT
    // ═════════════════════════════════════════════════════════════════════════

    private void buildContent() {
        tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(UITheme.FONT_BODY);
        tabs.addTab("⏰  Cấu hình ca làm",  buildCaLamViecTab());
        tabs.addTab("👥  Quản lý tài khoản", buildTaiKhoanTab());
        contentPanel.add(tabs, BorderLayout.CENTER);
    }

    // ── Tab cấu hình ca ──────────────────────────────────────────────────────

    private JPanel buildCaLamViecTab() {
        JPanel panel = new JPanel(new GridLayout(1,2,14,0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12,0,0,0));

        // ── Form thêm/sửa ca
        JPanel formCard = createCard("⏰  Thêm / Sửa ca làm việc");
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body,BoxLayout.Y_AXIS));

        JTextField txtTenCa = inputField();
        JSpinner spBD = timeSpinner(7,0);
        JSpinner spKT = timeSpinner(12,0);

        JButton btnThem  = actionBtn("➕  Thêm ca",    UITheme.BLUE);
        JButton btnReset = actionBtn("↺  Làm mới",     new Color(71,85,105));
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));
        btnRow.setOpaque(false); btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE,36));

        btnThem.addActionListener(e -> {
            String ten = txtTenCa.getText().trim();
            if (ten.isEmpty()) { showError("Vui lòng nhập tên ca."); return; }
            java.util.Date bd = (java.util.Date)spBD.getValue();
            java.util.Date kt = (java.util.Date)spKT.getValue();
            String sBD = new java.text.SimpleDateFormat("HH:mm").format(bd);
            String sKT = new java.text.SimpleDateFormat("HH:mm").format(kt);
            String maCa = "CA0" + (caModel.getRowCount()+1);
            caModel.addRow(new Object[]{maCa, ten, sBD, sKT, "Hoạt động"});
            txtTenCa.setText("");
            showSuccess("Thêm ca \"" + ten + "\" thành công!");
        });
        btnReset.addActionListener(e -> txtTenCa.setText(""));

        btnRow.add(btnThem); btnRow.add(btnReset);

        body.add(flabel("Tên ca *")); body.add(Box.createVerticalStrut(4));
        body.add(txtTenCa); body.add(Box.createVerticalStrut(10));
        body.add(flabel("Giờ bắt đầu *")); body.add(Box.createVerticalStrut(4));
        body.add(spBD); body.add(Box.createVerticalStrut(10));
        body.add(flabel("Giờ kết thúc *")); body.add(Box.createVerticalStrut(4));
        body.add(spKT); body.add(Box.createVerticalStrut(14));
        body.add(btnRow);
        formCard.add(body, BorderLayout.CENTER);

        // ── Danh sách ca
        JPanel listCard = createCard("📋  Danh sách ca làm việc");
        String[] cols = {"Mã ca","Tên ca","Giờ bắt đầu","Giờ kết thúc","Trạng thái"};
        JTable tbl = createTable(cols);
        caModel = (DefaultTableModel)tbl.getModel();
        caModel.addRow(new Object[]{"CA01","Ca sáng","07:00","12:00","Hoạt động"});
        caModel.addRow(new Object[]{"CA02","Ca chiều","13:00","18:00","Hoạt động"});
        caModel.addRow(new Object[]{"CA03","Ca tối","18:00","22:00","Hoạt động"});

        // Double-click để sửa
        tbl.addMouseListener(new java.awt.event.MouseAdapter(){
            @Override public void mouseClicked(java.awt.event.MouseEvent e){
                if (e.getClickCount()==2) showFormThemCa(true, tbl.getSelectedRow());
            }
        });

        JPanel tblBtnRow = new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));
        tblBtnRow.setOpaque(false);
        JButton btnSua = actionBtn("✏️  Sửa",  UITheme.BLUE);
        JButton btnXoa = actionBtn("🗑  Xóa",  UITheme.RED);
        btnSua.addActionListener(e -> showFormThemCa(true, tbl.getSelectedRow()));
        btnXoa.addActionListener(e -> {
            int row = tbl.getSelectedRow();
            if (row<0){ showError("Chọn ca cần xóa."); return; }
            int r = JOptionPane.showConfirmDialog(this,
                    "Xóa ca \"" + caModel.getValueAt(row,1) + "\"?","Xác nhận",JOptionPane.YES_NO_OPTION);
            if (r==JOptionPane.YES_OPTION) caModel.removeRow(row);
        });
        tblBtnRow.add(btnSua); tblBtnRow.add(btnXoa);

        JScrollPane scroll = new JScrollPane(tbl);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        JPanel listBody = new JPanel(new BorderLayout(0,8));
        listBody.setOpaque(false);
        listBody.add(scroll, BorderLayout.CENTER);
        listBody.add(tblBtnRow, BorderLayout.SOUTH);
        listCard.add(listBody, BorderLayout.CENTER);

        panel.add(formCard); panel.add(listCard);
        return panel;
    }

    // ── Tab quản lý tài khoản ────────────────────────────────────────────────

    private JPanel buildTaiKhoanTab() {
        JPanel panel = new JPanel(new BorderLayout(0,12));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12,0,0,0));

        // Thanh tìm kiếm + nút
        JPanel toolbar2 = new JPanel(new BorderLayout(8,0));
        toolbar2.setOpaque(false);
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));
        searchRow.setOpaque(false);
        JTextField txtSearch = inputField();
        txtSearch.setMaximumSize(new Dimension(220,32));
        txtSearch.setPreferredSize(new Dimension(220,32));
        JButton btnSearch = actionBtn("🔍  Tìm", UITheme.BLUE);
        searchRow.add(new JLabel("Tìm kiếm:  "));
        searchRow.add(txtSearch);
        searchRow.add(btnSearch);

        JPanel btnRow2 = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));
        btnRow2.setOpaque(false);
        JButton btnThem  = actionBtn("👤+  Thêm tài khoản", UITheme.BLUE);
        btnThem.addActionListener(e -> showFormThemTK(false,-1));
        btnRow2.add(btnThem);

        toolbar2.add(searchRow, BorderLayout.WEST);
        toolbar2.add(btnRow2, BorderLayout.EAST);
        panel.add(toolbar2, BorderLayout.NORTH);

        // Bảng NV
        String[] cols = {"Mã NV","Họ tên","Vai trò","Tài khoản","Trạng thái"};
        JTable tbl = createTable(cols);
        nvModel = (DefaultTableModel)tbl.getModel();
        nvModel.addRow(new Object[]{"NV001","Nguyễn Huy","Staff","huy.nguyen","Hoạt động"});
        nvModel.addRow(new Object[]{"NV002","Trần Kiệt","Manager","kiet.tran","Hoạt động"});
        nvModel.addRow(new Object[]{"NV003","Lê Khởi","Manager","khoi.le","Hoạt động"});
        nvModel.addRow(new Object[]{"NV004","Phạm Minh","Staff","minh.pham","Bị khóa"});
        nvModel.addRow(new Object[]{"NV005","Hoàng An","Admin","an.hoang","Hoạt động"});

        tbl.addMouseListener(new java.awt.event.MouseAdapter(){
            @Override public void mouseClicked(java.awt.event.MouseEvent e){
                if (e.getClickCount()==2) showFormThemTK(true, tbl.getSelectedRow());
            }
        });

        JScrollPane scroll = new JScrollPane(tbl);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        panel.add(scroll, BorderLayout.CENTER);

        // Action bar
        JPanel actRow = new JPanel(new FlowLayout(FlowLayout.LEFT,10,5));
        actRow.setOpaque(false);
        JButton btnSua  = actionBtn("✏️  Sửa TK",   UITheme.BLUE);
        JButton btnKhoa = actionBtn("🔒  Khóa TK",  UITheme.AMBER);
        JButton btnMo   = actionBtn("🔓  Mở khóa",  UITheme.GREEN);
        JLabel  hint    = new JLabel("← Chọn một hàng rồi thao tác");
        hint.setFont(UITheme.FONT_SMALL); hint.setForeground(UITheme.MUTED);

        btnSua.addActionListener(e  -> showFormThemTK(true, tbl.getSelectedRow()));
        btnKhoa.addActionListener(e -> toggleKhoa(tbl, true));
        btnMo.addActionListener(e   -> toggleKhoa(tbl, false));

        actRow.add(btnSua); actRow.add(btnKhoa); actRow.add(btnMo); actRow.add(hint);
        panel.add(actRow, BorderLayout.SOUTH);

        return panel;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  DIALOGS
    // ═════════════════════════════════════════════════════════════════════════

    private void showFormThemCa(boolean isEdit, int row) {
        JDialog dlg = new JDialog(this, isEdit ? "Sửa ca làm việc" : "Thêm ca làm việc", true);
        dlg.setSize(380, 280);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel body = new JPanel(new GridLayout(5,2,10,10));
        body.setBorder(new EmptyBorder(20,24,10,24));

        JTextField txtTen = new JTextField();
        JSpinner spBD = timeSpinner(7,0);
        JSpinner spKT = timeSpinner(12,0);

        if (isEdit && row >= 0 && caModel != null) {
            txtTen.setText((String)caModel.getValueAt(row,1));
            // Giờ để đơn giản không load lại spinner
        }

        body.add(new JLabel("Tên ca:")); body.add(txtTen);
        body.add(new JLabel("Giờ bắt đầu:")); body.add(spBD);
        body.add(new JLabel("Giờ kết thúc:")); body.add(spKT);
        body.add(new JLabel()); body.add(new JLabel());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,10));
        JButton btnOk = actionBtn("Lưu", UITheme.BLUE);
        JButton btnCancel = actionBtn("Hủy", new Color(71,85,105));
        btnOk.addActionListener(e -> {
            String ten = txtTen.getText().trim();
            if (ten.isEmpty()) { showError("Vui lòng nhập tên ca."); return; }
            java.util.Date bd = (java.util.Date)spBD.getValue();
            java.util.Date kt = (java.util.Date)spKT.getValue();
            String sBD = new java.text.SimpleDateFormat("HH:mm").format(bd);
            String sKT = new java.text.SimpleDateFormat("HH:mm").format(kt);
            if (isEdit && row >= 0) {
                caModel.setValueAt(ten,row,1);
                caModel.setValueAt(sBD,row,2);
                caModel.setValueAt(sKT,row,3);
            } else {
                caModel.addRow(new Object[]{"CA0"+(caModel.getRowCount()+1),ten,sBD,sKT,"Hoạt động"});
            }
            dlg.dispose();
        });
        btnCancel.addActionListener(e -> dlg.dispose());
        btnRow.add(btnCancel); btnRow.add(btnOk);

        dlg.add(body, BorderLayout.CENTER);
        dlg.add(btnRow, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void showFormThemTK(boolean isEdit, int row) {
        JDialog dlg = new JDialog(this, isEdit ? "Sửa tài khoản" : "Thêm tài khoản mới", true);
        dlg.setSize(420, 340);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel body = new JPanel(new GridLayout(6,2,10,10));
        body.setBorder(new EmptyBorder(20,24,10,24));

        JTextField txtHoTen  = new JTextField();
        JTextField txtTaiKhoan = new JTextField();
        JPasswordField txtPwd = new JPasswordField();
        JComboBox<String> cboVaiTro = new JComboBox<>(new String[]{"Staff","Manager","Admin"});

        if (isEdit && row>=0 && nvModel!=null) {
            txtHoTen.setText((String)nvModel.getValueAt(row,1));
            cboVaiTro.setSelectedItem(nvModel.getValueAt(row,2));
            txtTaiKhoan.setText((String)nvModel.getValueAt(row,3));
        }

        body.add(new JLabel("Họ tên:"));       body.add(txtHoTen);
        body.add(new JLabel("Tài khoản:"));    body.add(txtTaiKhoan);
        body.add(new JLabel("Mật khẩu:"));     body.add(txtPwd);
        body.add(new JLabel("Vai trò:"));       body.add(cboVaiTro);
        body.add(new JLabel());                 body.add(new JLabel());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,10));
        JButton btnOk     = actionBtn("Lưu",  UITheme.BLUE);
        JButton btnCancel = actionBtn("Hủy",  new Color(71,85,105));
        btnOk.addActionListener(e -> {
            String hoTen = txtHoTen.getText().trim();
            String tk    = txtTaiKhoan.getText().trim();
            if (hoTen.isEmpty() || tk.isEmpty()) { showError("Vui lòng nhập đầy đủ thông tin."); return; }
            if (isEdit && row>=0) {
                nvModel.setValueAt(hoTen,row,1);
                nvModel.setValueAt(cboVaiTro.getSelectedItem(),row,2);
                nvModel.setValueAt(tk,row,3);
            } else {
                String maNV = "NV00"+(nvModel.getRowCount()+1);
                nvModel.addRow(new Object[]{maNV,hoTen,cboVaiTro.getSelectedItem(),tk,"Hoạt động"});
            }
            dlg.dispose();
        });
        btnCancel.addActionListener(e -> dlg.dispose());
        btnRow.add(btnCancel); btnRow.add(btnOk);

        dlg.add(body, BorderLayout.CENTER);
        dlg.add(btnRow, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void suaCaSelected() {
        // Gọi từ toolbar — cần tìm bảng ca đang chọn
        JOptionPane.showMessageDialog(this,"Vui lòng vào tab Ca làm và double-click vào hàng cần sửa.",
                "Hướng dẫn", JOptionPane.INFORMATION_MESSAGE);
    }

    private void xoaCaSelected() {
        JOptionPane.showMessageDialog(this,"Vui lòng vào tab Ca làm và chọn hàng cần xóa.",
                "Hướng dẫn", JOptionPane.INFORMATION_MESSAGE);
    }

    private void suaTKSelected() {
        JOptionPane.showMessageDialog(this,"Vui lòng vào tab Tài khoản và double-click vào hàng cần sửa.",
                "Hướng dẫn", JOptionPane.INFORMATION_MESSAGE);
    }

    private void khoaTKSelected() {
        JOptionPane.showMessageDialog(this,"Vui lòng vào tab Tài khoản, chọn hàng rồi nhấn Khóa TK.",
                "Hướng dẫn", JOptionPane.INFORMATION_MESSAGE);
    }

    private void toggleKhoa(JTable tbl, boolean khoa) {
        int row = tbl.getSelectedRow();
        if (row<0) { showError("Vui lòng chọn tài khoản."); return; }
        String ten = (String)nvModel.getValueAt(row,1);
        String action = khoa ? "Khóa" : "Mở khóa";
        int r = JOptionPane.showConfirmDialog(this,
                action + " tài khoản \"" + ten + "\"?",
                "Xác nhận " + action, JOptionPane.YES_NO_OPTION);
        if (r==JOptionPane.YES_OPTION) {
            nvModel.setValueAt(khoa ? "Bị khóa" : "Hoạt động", row, 4);
            showSuccess(action + " tài khoản \"" + ten + "\" thành công.");
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private JTextField inputField() {
        JTextField tf = new JTextField();
        tf.setFont(UITheme.FONT_BODY);
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE,32));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                new EmptyBorder(4,8,4,8)));
        return tf;
    }

    private JSpinner timeSpinner(int h, int m) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.HOUR_OF_DAY, h);
        cal.set(java.util.Calendar.MINUTE, m);
        SpinnerDateModel model = new SpinnerDateModel(cal.getTime(), null, null, java.util.Calendar.MINUTE);
        JSpinner sp = new JSpinner(model);
        sp.setEditor(new JSpinner.DateEditor(sp,"HH:mm"));
        sp.setFont(UITheme.FONT_BODY);
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE,32));
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);
        return sp;
    }

    private JLabel flabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI",Font.BOLD,11));
        lbl.setForeground(UITheme.TEXT2);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }
}
