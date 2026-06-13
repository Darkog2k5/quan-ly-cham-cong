package vn.edu.nhom8.ui;

import vn.edu.nhom8.dao.CaLamViecDAO;
import vn.edu.nhom8.dao.NhanVienDAO;
import vn.edu.nhom8.model.CaLamViec;
import vn.edu.nhom8.model.NhanVien;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Màn hình Quản trị hệ thống (Admin).
 * Toolbar nhóm: [Thêm ca | Sửa ca | Xóa ca | Xem ds ca] | [Thêm TK | Sửa TK | Khóa TK | Ds TK]
 */
public class AdminFrame extends BaseFrame {

    private JTabbedPane tabs;
    private JTable tblCa;
    private JTable tblNV;
    private DefaultTableModel caModel;
    private DefaultTableModel nvModel;

    private final CaLamViecDAO caDAO  = new CaLamViecDAO();
    private final NhanVienDAO  nvDAO  = new NhanVienDAO();

    public AdminFrame() {
        super("Quản trị hệ thống");
        buildContent();
        initRoleTabs(ROLE_TAB_ADMIN, ROLE_TAB_ADMIN);
        setVisible(true);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  TOOLBAR
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    protected JPanel buildToolbarForRole(int roleTabIndex) {
        if (roleTabIndex != ROLE_TAB_ADMIN) return null;
        JPanel tb = createToolbarPanel();

        JLabel lblCa = new JLabel("  Cấu hình ca làm  ");
        lblCa.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblCa.setForeground(new Color(255, 255, 255, 100));

        JButton btnThemCa = toolbarBtn(FontAwesomeSolid.PLUS, "Thêm ca");
        JButton btnSuaCa  = toolbarBtn(FontAwesomeSolid.EDIT, "Sửa ca");
        JButton btnXoaCa  = toolbarBtn(FontAwesomeSolid.TRASH, "Xóa ca");
        JButton btnDsCa   = toolbarBtn(FontAwesomeSolid.LIST, "Xem ds ca");

        JLabel lblTK = new JLabel("  Quản lý tài khoản  ");
        lblTK.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblTK.setForeground(new Color(255, 255, 255, 100));

        JButton btnThemTK = toolbarBtn(FontAwesomeSolid.USER_PLUS, "Thêm TK");
        JButton btnSuaTK  = toolbarBtn(FontAwesomeSolid.USER_EDIT, "Sửa TK");
        JButton btnKhoaTK = toolbarBtn(FontAwesomeSolid.LOCK, "Khóa TK");
        JButton btnDsTK   = toolbarBtn(FontAwesomeSolid.USERS, "Ds tài khoản");

        btnThemCa.addActionListener(e -> { tabs.setSelectedIndex(0); showFormThemCa(false, -1); });
        btnSuaCa .addActionListener(e -> { tabs.setSelectedIndex(0); suaCaSelected(); });
        btnXoaCa .addActionListener(e -> { tabs.setSelectedIndex(0); xoaCaSelected(); });
        btnDsCa  .addActionListener(e -> tabs.setSelectedIndex(0));
        btnThemTK.addActionListener(e -> { tabs.setSelectedIndex(1); showFormThemTK(false, -1); });
        btnSuaTK .addActionListener(e -> { tabs.setSelectedIndex(1); suaTKSelected(); });
        btnKhoaTK.addActionListener(e -> { tabs.setSelectedIndex(1); khoaTKSelected(); });
        btnDsTK  .addActionListener(e -> tabs.setSelectedIndex(1));

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
        tabs.addTab("Cấu hình ca làm",  buildCaLamViecTab());
        tabs.addTab("Quản lý tài khoản", buildTaiKhoanTab());
        getRoleContentPanel(ROLE_TAB_ADMIN).add(tabs, BorderLayout.CENTER);
    }

    // ── Tab cấu hình ca ──────────────────────────────────────────────────────

    private JPanel buildCaLamViecTab() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 14, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 0, 0, 0));

        // ── Form thêm ca
        JPanel formCard = createCard("Thêm ca làm việc");
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        JTextField txtTenCa = inputField();
        JSpinner spBD = timeSpinner(7, 0);
        JSpinner spKT = timeSpinner(12, 0);

        JButton btnThem  = actionBtn("Thêm ca",  UITheme.BLUE);
        JButton btnReset = actionBtn("Làm mới",   new Color(71, 85, 105));
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        btnThem.addActionListener(e -> {
            String ten = txtTenCa.getText().trim();
            if (ten.isEmpty()) { showError("Vui lòng nhập tên ca."); return; }
            java.util.Date bd = (java.util.Date) spBD.getValue();
            java.util.Date kt = (java.util.Date) spKT.getValue();
            String sBD = new SimpleDateFormat("HH:mm").format(bd);
            String sKT = new SimpleDateFormat("HH:mm").format(kt);

            // Tạo mã ca tự động: CA + số thứ tự
            String maCa = "CA" + String.format("%02d", caModel.getRowCount() + 1);

            CaLamViec clv = new CaLamViec();
            clv.setMaCa(maCa);
            clv.setTenCa(ten);
            clv.setGioBatDau(Time.valueOf(sBD + ":00"));
            clv.setGioKetThuc(Time.valueOf(sKT + ":00"));

            boolean ok = caDAO.insert(clv);
            if (ok) {
                caModel.addRow(new Object[]{maCa, ten, sBD, sKT});
                txtTenCa.setText("");
                showSuccess("Thêm ca \"" + ten + "\" thành công!");
            } else {
                showError("Thêm ca thất bại. Vui lòng kiểm tra kết nối DB.");
            }
        });
        btnReset.addActionListener(e -> txtTenCa.setText(""));

        btnRow.add(btnThem); btnRow.add(btnReset);

        body.add(flabel("Tên ca *"));        body.add(Box.createVerticalStrut(4));
        body.add(txtTenCa);                  body.add(Box.createVerticalStrut(10));
        body.add(flabel("Giờ bắt đầu *"));   body.add(Box.createVerticalStrut(4));
        body.add(spBD);                      body.add(Box.createVerticalStrut(10));
        body.add(flabel("Giờ kết thúc *"));  body.add(Box.createVerticalStrut(4));
        body.add(spKT);                      body.add(Box.createVerticalStrut(14));
        body.add(btnRow);
        formCard.add(body, BorderLayout.CENTER);

        // ── Danh sách ca (load từ DB)
        JPanel listCard = createCard("Danh sách ca làm việc");
        String[] cols = {"Mã ca", "Tên ca", "Giờ bắt đầu", "Giờ kết thúc"};
        tblCa = createTable(cols);
        caModel = (DefaultTableModel) tblCa.getModel();

        tblCa.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) showFormThemCa(true, tblCa.getSelectedRow());
            }
        });

        JPanel tblBtnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        tblBtnRow.setOpaque(false);
        JButton btnSua = actionBtn("Sửa",  UITheme.BLUE);
        JButton btnXoa = actionBtn("Xóa",   UITheme.RED);
        btnSua.addActionListener(e -> showFormThemCa(true, tblCa.getSelectedRow()));
        btnXoa.addActionListener(e -> {
            int row = tblCa.getSelectedRow();
            if (row < 0) { showError("Chọn ca cần xóa."); return; }
            String maCa  = (String) caModel.getValueAt(row, 0);
            String tenCa = (String) caModel.getValueAt(row, 1);
            int r = JOptionPane.showConfirmDialog(this,
                    "Xóa ca \"" + tenCa + "\"?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) {
                boolean ok = caDAO.delete(maCa);
                if (ok) { caModel.removeRow(row); showSuccess("Đã xóa ca."); }
                else showError("Xóa thất bại (có thể ca đang được dùng trong lịch phân ca).");
            }
        });
        tblBtnRow.add(btnSua); tblBtnRow.add(btnXoa);

        JScrollPane scroll = new JScrollPane(tblCa);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        JPanel listBody = new JPanel(new BorderLayout(0, 8));
        listBody.setOpaque(false);
        listBody.add(scroll, BorderLayout.CENTER);
        listBody.add(tblBtnRow, BorderLayout.SOUTH);
        listCard.add(listBody, BorderLayout.CENTER);

        panel.add(formCard); panel.add(listCard);

        // Load dữ liệu ca từ DB
        loadDanhSachCa();

        return panel;
    }

    /** Load danh sách ca từ DB lên bảng. */
    private void loadDanhSachCa() {
        new SwingWorker<List<CaLamViec>, Void>() {
            @Override protected List<CaLamViec> doInBackground() {
                return caDAO.findAll();
            }
            @Override protected void done() {
                try {
                    caModel.setRowCount(0);
                    SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
                    for (CaLamViec c : get()) {
                        caModel.addRow(new Object[]{
                                c.getMaCa(),
                                c.getTenCa(),
                                c.getGioBatDau()  != null ? c.getGioBatDau().toString().substring(0, 5)  : "—",
                                c.getGioKetThuc() != null ? c.getGioKetThuc().toString().substring(0, 5) : "—"
                        });
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    // ── Tab quản lý tài khoản ────────────────────────────────────────────────

    private JPanel buildTaiKhoanTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 0, 0, 0));

        // Thanh tìm kiếm + nút
        JPanel toolbar2 = new JPanel(new BorderLayout(8, 0));
        toolbar2.setOpaque(false);
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchRow.setOpaque(false);
        JTextField txtSearch = inputField();
        txtSearch.setMaximumSize(new Dimension(220, 32));
        txtSearch.setPreferredSize(new Dimension(220, 32));
        JButton btnSearch = actionBtn("Tìm", UITheme.BLUE);
        searchRow.add(new JLabel("Tìm kiếm:  "));
        searchRow.add(txtSearch);
        searchRow.add(btnSearch);

        JPanel btnRow2 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow2.setOpaque(false);
        JButton btnThem = actionBtn("Thêm tài khoản", UITheme.BLUE);
        JButton btnLamMoi = actionBtn("Làm mới", new Color(71, 85, 105));
        btnThem.addActionListener(e -> showFormThemTK(false, -1));
        btnLamMoi.addActionListener(e -> loadDanhSachNV());
        btnRow2.add(btnLamMoi); btnRow2.add(btnThem);

        toolbar2.add(searchRow, BorderLayout.WEST);
        toolbar2.add(btnRow2, BorderLayout.EAST);
        panel.add(toolbar2, BorderLayout.NORTH);

        // Bảng NV (load từ DB)
        String[] cols = {"Mã NV", "Họ tên", "Vai trò", "Tài khoản", "Trạng thái"};
        tblNV = createTable(cols);
        nvModel = (DefaultTableModel) tblNV.getModel();

        tblNV.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) showFormThemTK(true, tblNV.getSelectedRow());
            }
        });

        // Tìm kiếm
        btnSearch.addActionListener(e -> {
            String kw = txtSearch.getText().trim().toLowerCase();
            if (kw.isEmpty()) { loadDanhSachNV(); return; }
            new SwingWorker<List<NhanVien>, Void>() {
                @Override protected List<NhanVien> doInBackground() { return nvDAO.findAll(); }
                @Override protected void done() {
                    try {
                        nvModel.setRowCount(0);
                        for (NhanVien nv : get()) {
                            if (nv.getHoTen().toLowerCase().contains(kw)
                             || nv.getMaNV().toLowerCase().contains(kw)
                             || nv.getTaiKhoan().toLowerCase().contains(kw)) {
                                nvModel.addRow(new Object[]{nv.getMaNV(), nv.getHoTen(),
                                        nv.getVaiTro(), nv.getTaiKhoan(), nv.getTrangThai()});
                            }
                        }
                    } catch (Exception ex) { ex.printStackTrace(); }
                }
            }.execute();
        });

        JScrollPane scroll = new JScrollPane(tblNV);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        panel.add(scroll, BorderLayout.CENTER);

        // Action bar
        JPanel actRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        actRow.setOpaque(false);
        JButton btnSua  = actionBtn("Sửa TK",   UITheme.BLUE);
        JButton btnKhoa = actionBtn("Khóa TK",   UITheme.AMBER);
        JButton btnMo   = actionBtn("Mở khóa",   UITheme.GREEN);
        JLabel hint = new JLabel("← Chọn một hàng rồi thao tác");
        hint.setFont(UITheme.FONT_SMALL); hint.setForeground(UITheme.MUTED);

        btnSua .addActionListener(e -> showFormThemTK(true, tblNV.getSelectedRow()));
        btnKhoa.addActionListener(e -> toggleKhoa(tblNV, true));
        btnMo  .addActionListener(e -> toggleKhoa(tblNV, false));

        actRow.add(btnSua); actRow.add(btnKhoa); actRow.add(btnMo); actRow.add(hint);
        panel.add(actRow, BorderLayout.SOUTH);

        // Load dữ liệu từ DB
        loadDanhSachNV();

        return panel;
    }

    /** Load danh sách nhân viên từ DB lên bảng. */
    private void loadDanhSachNV() {
        new SwingWorker<List<NhanVien>, Void>() {
            @Override protected List<NhanVien> doInBackground() {
                return nvDAO.findAll();
            }
            @Override protected void done() {
                try {
                    nvModel.setRowCount(0);
                    for (NhanVien nv : get()) {
                        nvModel.addRow(new Object[]{
                                nv.getMaNV(), nv.getHoTen(),
                                nv.getVaiTro(), nv.getTaiKhoan(), nv.getTrangThai()
                        });
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  DIALOGS
    // ═════════════════════════════════════════════════════════════════════════

    private void showFormThemCa(boolean isEdit, int row) {
        JDialog dlg = new JDialog(this, isEdit ? "Sửa ca làm việc" : "Thêm ca làm việc", true);
        dlg.setSize(380, 280);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel body = new JPanel(new GridLayout(5, 2, 10, 10));
        body.setBorder(new EmptyBorder(20, 24, 10, 24));

        JTextField txtTen = new JTextField();
        JSpinner spBD = timeSpinner(7, 0);
        JSpinner spKT = timeSpinner(12, 0);

        String maCaEdit = null;
        if (isEdit && row >= 0 && caModel != null) {
            maCaEdit = (String) caModel.getValueAt(row, 0);
            txtTen.setText((String) caModel.getValueAt(row, 1));
        }
        final String maCaFinal = maCaEdit;

        body.add(new JLabel("Tên ca:"));      body.add(txtTen);
        body.add(new JLabel("Giờ bắt đầu:")); body.add(spBD);
        body.add(new JLabel("Giờ kết thúc:")); body.add(spKT);
        body.add(new JLabel());               body.add(new JLabel());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnOk     = actionBtn("Lưu", UITheme.BLUE);
        JButton btnCancel = actionBtn("Hủy", new Color(71, 85, 105));
        btnOk.addActionListener(e -> {
            String ten = txtTen.getText().trim();
            if (ten.isEmpty()) { showError("Vui lòng nhập tên ca."); return; }
            String sBD = new SimpleDateFormat("HH:mm").format((java.util.Date) spBD.getValue());
            String sKT = new SimpleDateFormat("HH:mm").format((java.util.Date) spKT.getValue());

            CaLamViec clv = new CaLamViec();
            clv.setTenCa(ten);
            clv.setGioBatDau(Time.valueOf(sBD + ":00"));
            clv.setGioKetThuc(Time.valueOf(sKT + ":00"));

            if (isEdit && row >= 0 && maCaFinal != null) {
                clv.setMaCa(maCaFinal);
                boolean ok = caDAO.update(clv);
                if (ok) {
                    caModel.setValueAt(ten,  row, 1);
                    caModel.setValueAt(sBD,  row, 2);
                    caModel.setValueAt(sKT,  row, 3);
                    showSuccess("Cập nhật ca thành công!");
                } else {
                    showError("Cập nhật thất bại.");
                }
            } else {
                // Thêm mới — mã tự sinh
                clv.setMaCa("CA" + String.format("%02d", caModel.getRowCount() + 1));
                boolean ok = caDAO.insert(clv);
                if (ok) {
                    caModel.addRow(new Object[]{clv.getMaCa(), ten, sBD, sKT});
                    showSuccess("Thêm ca thành công!");
                } else {
                    showError("Thêm ca thất bại.");
                }
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

        JPanel body = new JPanel(new GridLayout(6, 2, 10, 10));
        body.setBorder(new EmptyBorder(20, 24, 10, 24));

        JTextField txtMaNV   = new JTextField();
        JTextField txtHoTen  = new JTextField();
        JTextField txtTaiKhoan = new JTextField();
        JPasswordField txtPwd  = new JPasswordField();
        JComboBox<String> cboVaiTro = new JComboBox<>(new String[]{"Staff", "Manager", "Admin"});

        String maNVEdit = null;
        if (isEdit && row >= 0 && nvModel != null) {
            maNVEdit = (String) nvModel.getValueAt(row, 0);
            txtMaNV.setText(maNVEdit);
            txtMaNV.setEnabled(false); // không được đổi mã NV
            txtHoTen.setText((String) nvModel.getValueAt(row, 1));
            cboVaiTro.setSelectedItem(nvModel.getValueAt(row, 2));
            txtTaiKhoan.setText((String) nvModel.getValueAt(row, 3));
        }
        final String maNVFinal = maNVEdit;

        body.add(new JLabel("Mã NV:"));     body.add(txtMaNV);
        body.add(new JLabel("Họ tên:"));    body.add(txtHoTen);
        body.add(new JLabel("Tài khoản:")); body.add(txtTaiKhoan);
        body.add(new JLabel("Mật khẩu:")); body.add(txtPwd);
        body.add(new JLabel("Vai trò:"));   body.add(cboVaiTro);
        body.add(new JLabel());             body.add(new JLabel());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnOk     = actionBtn("Lưu", UITheme.BLUE);
        JButton btnCancel = actionBtn("Hủy", new Color(71, 85, 105));

        btnOk.addActionListener(e -> {
            String maNV  = txtMaNV.getText().trim();
            String hoTen = txtHoTen.getText().trim();
            String tk    = txtTaiKhoan.getText().trim();
            String pwd   = new String(txtPwd.getPassword()).trim();
            if (maNV.isEmpty() || hoTen.isEmpty() || tk.isEmpty()) {
                showError("Vui lòng nhập đầy đủ thông tin."); return;
            }

            NhanVien nv = new NhanVien();
            nv.setMaNV(maNV);
            nv.setHoTen(hoTen);
            nv.setTaiKhoan(tk);
            nv.setVaiTro((String) cboVaiTro.getSelectedItem());
            nv.setMatKhau(pwd.isEmpty() ? null : pwd);
            nv.setTrangThai("HoatDong");

            if (isEdit && maNVFinal != null) {
                boolean ok = nvDAO.update(nv);
                if (ok) {
                    nvModel.setValueAt(hoTen, row, 1);
                    nvModel.setValueAt(nv.getVaiTro(), row, 2);
                    nvModel.setValueAt(tk, row, 3);
                    showSuccess("Cập nhật tài khoản thành công!");
                } else {
                    showError("Cập nhật thất bại.");
                }
            } else {
                boolean ok = nvDAO.insert(nv);
                if (ok) {
                    nvModel.addRow(new Object[]{maNV, hoTen, nv.getVaiTro(), tk, "HoatDong"});
                    showSuccess("Thêm tài khoản \"" + hoTen + "\" thành công!");
                } else {
                    showError("Thêm tài khoản thất bại (mã NV hoặc tài khoản đã tồn tại).");
                }
            }
            dlg.dispose();
        });
        btnCancel.addActionListener(e -> dlg.dispose());
        btnRow.add(btnCancel); btnRow.add(btnOk);
        dlg.add(body, BorderLayout.CENTER);
        dlg.add(btnRow, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // ── Các action từ toolbar ─────────────────────────────────────────────────

    private void suaCaSelected() {
        JOptionPane.showMessageDialog(this,
                "Vui lòng vào tab Ca làm và double-click vào hàng cần sửa.",
                "Hướng dẫn", JOptionPane.INFORMATION_MESSAGE);
    }

    private void xoaCaSelected() {
        JOptionPane.showMessageDialog(this,
                "Vui lòng vào tab Ca làm và chọn hàng cần xóa.",
                "Hướng dẫn", JOptionPane.INFORMATION_MESSAGE);
    }

    private void suaTKSelected() {
        JOptionPane.showMessageDialog(this,
                "Vui lòng vào tab Tài khoản và double-click vào hàng cần sửa.",
                "Hướng dẫn", JOptionPane.INFORMATION_MESSAGE);
    }

    private void khoaTKSelected() {
        JOptionPane.showMessageDialog(this,
                "Vui lòng vào tab Tài khoản, chọn hàng rồi nhấn Khóa TK.",
                "Hướng dẫn", JOptionPane.INFORMATION_MESSAGE);
    }

    private void toggleKhoa(JTable tbl, boolean khoa) {
        int row = tbl.getSelectedRow();
        if (row < 0) { showError("Vui lòng chọn tài khoản."); return; }
        String maNV  = (String) nvModel.getValueAt(row, 0);
        String ten   = (String) nvModel.getValueAt(row, 1);
        String action = khoa ? "Khóa" : "Mở khóa";
        int r = JOptionPane.showConfirmDialog(this,
                action + " tài khoản \"" + ten + "\"?",
                "Xác nhận " + action, JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            boolean ok = khoa
                    ? nvDAO.deactivate(maNV)
                    : nvDAO.update(buildNhanVienFromRow(row, "HoatDong"));
            if (ok) {
                nvModel.setValueAt(khoa ? "NgungHoatDong" : "HoatDong", row, 4);
                showSuccess(action + " tài khoản \"" + ten + "\" thành công.");
            } else {
                showError(action + " thất bại.");
            }
        }
    }

    /** Tạo đối tượng NhanVien từ hàng trong bảng để update. */
    private NhanVien buildNhanVienFromRow(int row, String trangThai) {
        NhanVien nv = new NhanVien();
        nv.setMaNV((String) nvModel.getValueAt(row, 0));
        nv.setHoTen((String) nvModel.getValueAt(row, 1));
        nv.setVaiTro((String) nvModel.getValueAt(row, 2));
        nv.setTaiKhoan((String) nvModel.getValueAt(row, 3));
        nv.setTrangThai(trangThai);
        return nv;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private JTextField inputField() {
        JTextField tf = new JTextField();
        tf.setFont(UITheme.FONT_BODY);
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                new EmptyBorder(4, 8, 4, 8)));
        return tf;
    }

    private JSpinner timeSpinner(int h, int m) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.HOUR_OF_DAY, h);
        cal.set(java.util.Calendar.MINUTE, m);
        SpinnerDateModel model = new SpinnerDateModel(cal.getTime(), null, null, java.util.Calendar.MINUTE);
        JSpinner sp = new JSpinner(model);
        sp.setEditor(new JSpinner.DateEditor(sp, "HH:mm"));
        sp.setFont(UITheme.FONT_BODY);
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);
        return sp;
    }

    private JLabel flabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(UITheme.TEXT2);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }
}