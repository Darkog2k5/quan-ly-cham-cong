package vn.edu.nhom8.ui;

import vn.edu.nhom8.dao.CaLamViecDAO;
import vn.edu.nhom8.dao.ILichPhanCaDAO;
import vn.edu.nhom8.dao.INhanVienDAO;
import vn.edu.nhom8.dao.IYeuCauDoiCaDAO;
import vn.edu.nhom8.model.CaLamViec;
import vn.edu.nhom8.model.LichPhanCa;
import vn.edu.nhom8.model.NhanVien;
import vn.edu.nhom8.model.YeuCauDoiCa;
import vn.edu.nhom8.service.ManagerService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Màn hình Quản lý – F3.1 đến F3.3.
 *
 * Tab 1: Xếp lịch nhân viên  (F3.1)
 * Tab 2: Duyệt đổi ca        (F3.2 + F3.3)
 * Tab 3: Xem lịch tổng       (F3.2 hỗ trợ)
 */
public class ManagerFrame extends BaseFrame {

    private final ManagerService service;
    private final CaLamViecDAO   caDAO = new CaLamViecDAO();

    private JTabbedPane tabs;
    private JComboBox<CaLamViec> cboCa;   // combobox ca – load từ DB
    private static final int TAB_XEPLICH  = 0;
    private static final int TAB_DUYET    = 1;
    private static final int TAB_LICHTONG = 2;
    private static final int TAB_BAOCAO   = 3;

    // Widgets tab Báo cáo
    private DefaultTableModel baoCaoModel;
    private JTable            tblBaoCao;
    private JSpinner          spThang, spNam;
    private JComboBox<String> cboNVBaoCao;

    // Widgets tab Xếp lịch
    private List<JCheckBox>   checkBoxesNV = new ArrayList<>();
    private List<NhanVien>    danhSachNV   = new ArrayList<>();
    private DefaultTableModel lichDaXepModel;
    private JTable            tblLichDaXep;
    private List<LichPhanCa>  dsLichDaXep  = new ArrayList<>();

    // Widgets tab Duyệt đổi ca
    private DefaultTableModel duyetModel;
    private List<YeuCauDoiCa> dsYeuCau = new ArrayList<>();

    // Widgets tab Lịch tổng
    private JPanel   lichTongGrid;
    private JLabel   lblTuan;
    private Date     tuanBatDau;  // Thứ 2 của tuần hiện tại

    //Constructor
    public ManagerFrame(INhanVienDAO nvDAO,
                        ILichPhanCaDAO lichDAO,
                        IYeuCauDoiCaDAO ycDAO) {
        super("Quản lý");
        this.service = new ManagerService(lichDAO, nvDAO, ycDAO);
        buildContent();
        initRoleTabs(ROLE_TAB_MANAGER, ROLE_TAB_MANAGER);
        // Quản lý không được truy cập tab "Nhân viên"
        setRoleTabEnabled(ROLE_TAB_STAFF, false);
        loadStatBar();
        setVisible(true);
    }

    public ManagerFrame() {
        this(null, null, null);
    }

    //Toolbar

    //thanh công cụ (Toolbar) cho từng vai trò trong ManagerFrame
    @Override
    protected JPanel buildToolbarForRole(int roleTabIndex) {
        if (roleTabIndex != ROLE_TAB_MANAGER) return null;
        JPanel tb = createToolbarPanel();

        JButton btnXep   = toolbarBtn(FontAwesomeSolid.CALENDAR_PLUS, "Xếp lịch");
        JButton btnDuyet = toolbarBtn(FontAwesomeSolid.CHECK_SQUARE, "Duyệt đổi ca");
        JButton btnLich  = toolbarBtn(FontAwesomeSolid.CALENDAR_ALT, "Lịch tổng");
        JButton btnBaoCao = toolbarBtn(FontAwesomeSolid.FILE_EXCEL, "Báo cáo");
        JButton btnLamMoi = toolbarBtn(FontAwesomeSolid.SYNC, "Làm mới");

        btnXep.addActionListener(e    -> tabs.setSelectedIndex(TAB_XEPLICH));
        btnDuyet.addActionListener(e  -> tabs.setSelectedIndex(TAB_DUYET));
        btnLich.addActionListener(e   -> tabs.setSelectedIndex(TAB_LICHTONG));
        btnBaoCao.addActionListener(e -> tabs.setSelectedIndex(TAB_BAOCAO));
        btnLamMoi.addActionListener(e -> refreshTab());

        tb.add(btnXep); tb.add(btnDuyet); tb.add(toolbarSep());
        tb.add(btnLich); tb.add(toolbarSep());
        tb.add(btnBaoCao); tb.add(toolbarSep());
        tb.add(btnLamMoi);
        return tb;
    }

    //Build content
    // Stat bar (4 số tổng quan)
    private JPanel statBar;

    //khung giao diện trung tâm của ManagerFrame
    private void buildContent() {
        statBar = buildStatBar();
        getRoleContentPanel(ROLE_TAB_MANAGER).add(statBar, BorderLayout.NORTH);

        tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(UITheme.FONT_BODY);
        tabs.addTab("Xếp lịch",    buildXepLichTab());
        tabs.addTab("Duyệt đổi ca", buildDuyetTab());
        tabs.addTab("Lịch tổng",    buildLichTongTab());
        tabs.addTab("Báo cáo chấm công", buildBaoCaoTab());

        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == TAB_DUYET)    loadYeuCauChoDuyet();
            if (tabs.getSelectedIndex() == TAB_LICHTONG) loadLichTong();
            if (tabs.getSelectedIndex() == TAB_BAOCAO)   loadBaoCao();
        });

        getRoleContentPanel(ROLE_TAB_MANAGER).add(tabs, BorderLayout.CENTER);
    }

    //Stat bar (dashboard)
    private JPanel buildStatBar() {
        JPanel bar = new JPanel(new GridLayout(1, 3, 12, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(0, 0, 14, 0));
        // Placeholder – loadStatBar() sẽ điền số thật
        bar.add(statCard("Nhân viên", "—", UITheme.BLUE));
        bar.add(statCard("Chờ duyệt đổi ca", "—", UITheme.AMBER));
        bar.add(statCard("Ca hôm nay", "—", UITheme.GREEN));
        return bar;
    }

    //tạo một thẻ thống kê (Statistic Card) trong statBar
    private JPanel statCard(String label, String value, Color color) {
        JPanel card = createCard(null);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, color),
                new EmptyBorder(12, 14, 12, 14)));
        JLabel lVal = new JLabel(value);
        lVal.setFont(new Font("Consolas", Font.BOLD, 28)); lVal.setForeground(color);
        JLabel lLbl = new JLabel("<html>" + label + "</html>");
        lLbl.setFont(UITheme.FONT_SMALL); lLbl.setForeground(UITheme.MUTED);
        card.add(lVal, BorderLayout.CENTER);
        card.add(lLbl, BorderLayout.SOUTH);
        return card;
    }

    /** Cập nhật số trên stat bar từ dữ liệu thật. */
    private void loadStatBar() {
        new SwingWorker<int[], Void>() {
            @Override protected int[] doInBackground() {
                int tongNV      = service.getDanhSachNV().size();
                int choDuyet    = service.getYeuCauChoDuyet().size();

                // Đếm ca hôm nay
                Calendar now = Calendar.getInstance();
                int lichHomNay = service.getLichTheoThang(
                        now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1).size();

                return new int[]{ tongNV, choDuyet, lichHomNay };
            }
            @Override protected void done() {
                try {
                    int[] nums = get();
                    updateStatCard(0, String.valueOf(nums[0]));
                    updateStatCard(1, String.valueOf(nums[1]));
                    updateStatCard(2, String.valueOf(nums[2]));
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    //cập nhật giá trị hiển thị trên một thẻ thống kê (stat card)
    private void updateStatCard(int idx, String value) {
        // Lấy JLabel số trong card (component đầu tiên trong BorderLayout.CENTER)
        JPanel card = (JPanel) statBar.getComponent(idx);
        for (Component c : card.getComponents()) {
            if (c instanceof JLabel && ((JLabel)c).getFont().getName().equals("Consolas")) {
                ((JLabel)c).setText(value);
                break;
            }
        }
    }

    //Tab 1: Xếp lịch (F3.1)
    private JPanel buildXepLichTab() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 14, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 0, 0, 0));

        //Form xếp lịch
        JPanel formCard = createCard("Xếp lịch cho nhân viên");
        JPanel body = new JPanel(); body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        // Chọn ngày
        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner spNgay = new JSpinner(dateModel);
        spNgay.setEditor(new JSpinner.DateEditor(spNgay, "dd/MM/yyyy"));
        spNgay.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        spNgay.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Combobox ca – load từ DB
        cboCa = new JComboBox<>();
        cboCa.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> list, Object value, int idx, boolean sel, boolean foc) {
                super.getListCellRendererComponent(list, value, idx, sel, foc);
                if (value instanceof CaLamViec) {
                    CaLamViec c = (CaLamViec) value;
                    String bd = c.getGioBatDau()  != null ? c.getGioBatDau().toString().substring(0,5)  : "?";
                    String kt = c.getGioKetThuc() != null ? c.getGioKetThuc().toString().substring(0,5) : "?";
                    setText(c.getMaCa() + " – " + c.getTenCa() + " (" + bd + "–" + kt + ")");
                }
                return this;
            }
        });
        cboCa.setFont(UITheme.FONT_BODY);
        cboCa.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        cboCa.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Tải ca từ DB
        new SwingWorker<List<CaLamViec>, Void>() {
            @Override protected List<CaLamViec> doInBackground() { return caDAO.findAll(); }
            @Override protected void done() {
                try {
                    cboCa.removeAllItems();
                    for (CaLamViec c : get()) cboCa.addItem(c);
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();

        // Danh sách NV checkbox (load từ DB)
        JPanel nvListPanel = new JPanel(); nvListPanel.setOpaque(false);
        nvListPanel.setLayout(new BoxLayout(nvListPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollNV = new JScrollPane(nvListPanel);
        scrollNV.setPreferredSize(new Dimension(0, 130));
        scrollNV.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        scrollNV.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollNV.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));

        // Tải NV vào list checkbox
        new SwingWorker<List<NhanVien>, Void>() {
            @Override protected List<NhanVien> doInBackground() { return service.getDanhSachNV(); }
            @Override protected void done() {
                try {
                    danhSachNV = get();
                    checkBoxesNV.clear();
                    nvListPanel.removeAll();
                    for (NhanVien nv : danhSachNV) {
                        JCheckBox cb = new JCheckBox(nv.getMaNV() + "  –  " + nv.getHoTen());
                        cb.setFont(UITheme.FONT_BODY); cb.setOpaque(false);
                        cb.setAlignmentX(Component.LEFT_ALIGNMENT);
                        checkBoxesNV.add(cb);
                        nvListPanel.add(cb);
                        nvListPanel.add(Box.createVerticalStrut(4));
                    }
                    nvListPanel.revalidate();
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();

        // Nút Chọn tất cả / Bỏ chọn
        JPanel btnChon = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); btnChon.setOpaque(false);
        btnChon.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnChon.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton btnAll  = actionBtn("Chọn tất cả",  new Color(71, 85, 105));
        JButton btnNone = actionBtn("Bỏ chọn",       new Color(100, 116, 135));
        btnAll.addActionListener(e  -> checkBoxesNV.forEach(c -> c.setSelected(true)));
        btnNone.addActionListener(e -> checkBoxesNV.forEach(c -> c.setSelected(false)));
        btnChon.add(btnAll); btnChon.add(btnNone);

        // Nút Lưu lịch
        JButton btnLuu = actionBtn("Lưu lịch", FontAwesomeSolid.SAVE, UITheme.GREEN);
        btnLuu.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLuu.addActionListener(e -> {
            // Lấy danh sách NV được tick
            List<String> dsMaNV = new ArrayList<>();
            for (int i = 0; i < checkBoxesNV.size(); i++) {
                if (checkBoxesNV.get(i).isSelected()) {
                    dsMaNV.add(danhSachNV.get(i).getMaNV());
                }
            }
            if (dsMaNV.isEmpty()) { showError("Vui lòng chọn ít nhất 1 nhân viên."); return; }

            // Lấy maCa từ object CaLamViec được chọn
            CaLamViec caChon = (CaLamViec) cboCa.getSelectedItem();
            if (caChon == null) { showError("Vui lòng chọn ca làm việc."); return; }
            String maCa = caChon.getMaCa();
            java.util.Date ngay = (java.util.Date) spNgay.getValue();

            List<String> trungCa = new ArrayList<>();
            int soOK = service.xepLich(dsMaNV, maCa, ngay, trungCa);

            String msg = "Đã xếp lịch thành công cho " + soOK + " nhân viên.";
            if (!trungCa.isEmpty()) {
                msg += "\n\nBị trùng ca (bỏ qua): " + String.join(", ", trungCa);
            }

            if (soOK > 0) { showSuccess(msg); loadLichDaXep(); loadStatBar(); }
            else showError("Tất cả NV đã có ca trong ngày này.");

            checkBoxesNV.forEach(c -> c.setSelected(false));
        });

        body.add(fLabel("Ngày làm việc *"));      body.add(Box.createVerticalStrut(4));
        body.add(spNgay);                         body.add(Box.createVerticalStrut(10));
        body.add(fLabel("Ca làm việc *"));        body.add(Box.createVerticalStrut(4));
        body.add(cboCa);                          body.add(Box.createVerticalStrut(10));
        body.add(fLabel("Nhân viên *"));          body.add(Box.createVerticalStrut(4));
        body.add(btnChon);                        body.add(Box.createVerticalStrut(4));
        body.add(scrollNV);                       body.add(Box.createVerticalStrut(12));
        body.add(btnLuu);
        formCard.add(body, BorderLayout.CENTER);

        // Bảng lịch đã xếp
        JPanel rightCard = createCard("Lịch đã xếp tháng này");

        JPanel rightHeader = new JPanel(new BorderLayout()); rightHeader.setOpaque(false);
        JButton btnRefresh = actionBtn("Làm mới", new Color(71, 85, 105));
        btnRefresh.addActionListener(e -> loadLichDaXep());
        rightHeader.add(btnRefresh, BorderLayout.EAST);
        rightCard.add(rightHeader, BorderLayout.NORTH);

        String[] cols = {"Mã lịch", "Mã NV", "Mã ca", "Ngày", "Trạng thái"};
        JTable tbl = createTable(cols);
        tblLichDaXep   = tbl;
        lichDaXepModel = (DefaultTableModel) tbl.getModel();
        JScrollPane sp = new JScrollPane(tbl);
        sp.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        rightCard.add(sp, BorderLayout.CENTER);

        // Nút Sửa / Xóa lịch đã xếp
        JPanel actRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8)); actRow.setOpaque(false);
        JButton btnSua = actionBtn("Sửa",  UITheme.BLUE);
        JButton btnXoa = actionBtn("Xóa",  UITheme.RED);
        JLabel hintXep = new JLabel("← Chọn một hàng rồi nhấn nút");
        hintXep.setFont(UITheme.FONT_SMALL); hintXep.setForeground(UITheme.MUTED);

        btnSua.addActionListener(e -> suaLichDaXep());
        btnXoa.addActionListener(e -> xoaLichDaXep());

        actRow.add(btnSua); actRow.add(btnXoa); actRow.add(hintXep);
        rightCard.add(actRow, BorderLayout.SOUTH);

        panel.add(formCard); panel.add(rightCard);
        return panel;
    }

    /** Mở dialog sửa lịch đã chọn (đổi ca và/hoặc ngày làm việc). */
    private void suaLichDaXep() {
        int row = tblLichDaXep.getSelectedRow();
        if (row < 0) { showError("Vui lòng chọn một dòng lịch để sửa."); return; }
        LichPhanCa lich = dsLichDaXep.get(row);

        // Combobox chọn ca
        JComboBox<CaLamViec> cboSua = new JComboBox<>();
        cboSua.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> list, Object value, int idx, boolean sel, boolean foc) {
                super.getListCellRendererComponent(list, value, idx, sel, foc);
                if (value instanceof CaLamViec) {
                    CaLamViec c = (CaLamViec) value;
                    String bd = c.getGioBatDau()  != null ? c.getGioBatDau().toString().substring(0,5)  : "?";
                    String kt = c.getGioKetThuc() != null ? c.getGioKetThuc().toString().substring(0,5) : "?";
                    setText(c.getMaCa() + " – " + c.getTenCa() + " (" + bd + "–" + kt + ")");
                }
                return this;
            }
        });
        List<CaLamViec> dsCa = caDAO.findAll();
        CaLamViec caHienTai = null;
        for (CaLamViec c : dsCa) {
            cboSua.addItem(c);
            if (c.getMaCa().equals(lich.getMaCa())) caHienTai = c;
        }
        if (caHienTai != null) cboSua.setSelectedItem(caHienTai);

        // Spinner ngày
        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner spNgaySua = new JSpinner(dateModel);
        spNgaySua.setEditor(new JSpinner.DateEditor(spNgaySua, "dd/MM/yyyy"));
        spNgaySua.setValue(lich.getNgayLamViec());

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 10));
        form.setBorder(new EmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Mã lịch:"));     form.add(new JLabel(lich.getMaLich()));
        form.add(new JLabel("Mã NV:"));       form.add(new JLabel(lich.getMaNV()));
        form.add(new JLabel("Ca làm việc:")); form.add(cboSua);
        // Thêm dòng ngày bằng panel riêng để layout đẹp hơn
        JPanel wrap = new JPanel(new BorderLayout(10, 10));
        wrap.add(form, BorderLayout.NORTH);
        JPanel ngayRow = new JPanel(new BorderLayout(8, 0));
        ngayRow.setBorder(new EmptyBorder(0, 10, 10, 10));
        ngayRow.add(new JLabel("Ngày làm việc:"), BorderLayout.WEST);
        ngayRow.add(spNgaySua, BorderLayout.CENTER);
        wrap.add(ngayRow, BorderLayout.CENTER);

        int r = JOptionPane.showConfirmDialog(this, wrap, "Sửa lịch đã xếp",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;

        CaLamViec caChon = (CaLamViec) cboSua.getSelectedItem();
        if (caChon == null) { showError("Vui lòng chọn ca làm việc."); return; }

        java.util.Date ngayMoi = (java.util.Date) spNgaySua.getValue();
        lich.setMaCa(caChon.getMaCa());
        lich.setNgayLamViec(new java.sql.Date(ngayMoi.getTime()));

        String err = service.suaLich(lich);
        if (err == null) { showSuccess("Đã cập nhật lịch."); loadLichDaXep(); loadStatBar(); }
        else showError(err);
    }

    /** Xóa lịch đã chọn. */
    private void xoaLichDaXep() {
        int row = tblLichDaXep.getSelectedRow();
        if (row < 0) { showError("Vui lòng chọn một dòng lịch để xóa."); return; }
        LichPhanCa lich = dsLichDaXep.get(row);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xóa lịch " + lich.getMaLich() + " (NV " + lich.getMaNV() + ")?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        String err = service.xoaLich(lich.getMaLich());
        if (err == null) { showSuccess("Đã xóa lịch."); loadLichDaXep(); loadStatBar(); }
        else showError(err);
    }

    /** Tải lịch đã xếp tháng hiện tại vào bảng bên phải. */
    private void loadLichDaXep() {
        Calendar now = Calendar.getInstance();
        new SwingWorker<List<LichPhanCa>, Void>() {
            @Override protected List<LichPhanCa> doInBackground() {
                return service.getLichTheoThang(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1);
            }
            @Override protected void done() {
                try {
                    dsLichDaXep = get();
                    lichDaXepModel.setRowCount(0);
                    SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
                    for (LichPhanCa l : dsLichDaXep) {
                        lichDaXepModel.addRow(new Object[]{
                                l.getMaLich(), l.getMaNV(), l.getMaCa(),
                                fmt.format(l.getNgayLamViec()),
                                StaffFrame.trangThaiLabel(l.getTrangThai())
                        });
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    //Tab 2: Duyệt đổi ca (F3.2 + F3.3)
    private JPanel buildDuyetTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 0, 0, 0));

        // Tiêu đề + nút làm mới
        JPanel header = new JPanel(new BorderLayout()); header.setOpaque(false);
        JLabel title = new JLabel("Yêu cầu đổi ca đang chờ duyệt"); title.setFont(UITheme.FONT_HEAD);
        JButton btnLamMoi = actionBtn("Làm mới", new Color(71, 85, 105));
        btnLamMoi.addActionListener(e -> loadYeuCauChoDuyet());
        header.add(title, BorderLayout.WEST); header.add(btnLamMoi, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        // Bảng yêu cầu
        String[] cols = {"Mã YC", "Người gửi", "Ca gốc (Ngày)", "Người nhận", "Ca đổi (Ngày)", "Lý do", "Ngày tạo"};
        JTable tbl = createTable(cols);
        duyetModel = (DefaultTableModel) tbl.getModel();
        JScrollPane sp = new JScrollPane(tbl);
        sp.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        panel.add(sp, BorderLayout.CENTER);

        // Nút Duyệt / Từ chối
        JPanel actRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5)); actRow.setOpaque(false);
        JButton btnDuyet = actionBtn("Duyệt", FontAwesomeSolid.CHECK, UITheme.GREEN);
        JButton btnTuChoi = actionBtn("Từ chối", FontAwesomeSolid.TIMES, UITheme.RED);
        JLabel hint = new JLabel("← Chọn một hàng rồi nhấn nút");
        hint.setFont(UITheme.FONT_SMALL); hint.setForeground(UITheme.MUTED);

        btnDuyet.addActionListener(e -> {
            int row = tbl.getSelectedRow();
            if (row < 0) { showError("Vui lòng chọn một yêu cầu."); return; }
            YeuCauDoiCa yc = dsYeuCau.get(row);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Duyệt yêu cầu " + yc.getMaYeuCau() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            String err = service.duyetYeuCau(yc);
            if (err == null) { showSuccess("Đã duyệt. Lịch phân ca đã cập nhật."); loadYeuCauChoDuyet(); loadStatBar(); }
            else showError(err);
        });

        btnTuChoi.addActionListener(e -> {
            int row = tbl.getSelectedRow();
            if (row < 0) { showError("Vui lòng chọn một yêu cầu."); return; }
            YeuCauDoiCa yc = dsYeuCau.get(row);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Từ chối yêu cầu " + yc.getMaYeuCau() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            String err = service.tuChoiYeuCau(yc);
            if (err == null) { showSuccess("Đã từ chối yêu cầu."); loadYeuCauChoDuyet(); loadStatBar(); }
            else showError(err);
        });

        actRow.add(btnDuyet); actRow.add(btnTuChoi); actRow.add(hint);
        panel.add(actRow, BorderLayout.SOUTH);

        return panel;
    }

    /** Tải danh sách yêu cầu chờ duyệt. */
    private void loadYeuCauChoDuyet() {
        new SwingWorker<List<YeuCauDoiCa>, Void>() {
            @Override protected List<YeuCauDoiCa> doInBackground() {
                return service.getYeuCauChoDuyet();
            }
            @Override protected void done() {
                try {
                    dsYeuCau = get();
                    duyetModel.setRowCount(0);
                    SimpleDateFormat fmtTime = new SimpleDateFormat("dd/MM HH:mm");
                    SimpleDateFormat fmtDate = new SimpleDateFormat("dd/MM/yyyy"); // Format ngày tháng năm

                    for (YeuCauDoiCa yc : dsYeuCau) {
                        
                        // --- 1. XỬ LÝ THÔNG TIN NGƯỜI GỬI (GỐC) ---
                        LichPhanCa lichGoc = service.getLichById(yc.getMaLichGoc());
                        String maNguoiGui = (lichGoc != null) ? lichGoc.getMaNV() : "—";
                        String caGoc = (lichGoc != null) ? 
                            (lichGoc.getMaCa() + " (" + fmtDate.format(lichGoc.getNgayLamViec()) + ")") : "—";

                        // --- 2. XỬ LÝ THÔNG TIN NGƯỜI NHẬN (ĐÍCH) ---
                        String maNguoiNhan = (yc.getMaNVTarget() != null) ? yc.getMaNVTarget() : "—";
                        String caDoi = "—";

                        // Nếu có mã lịch đích -> Đây là đổi ca cho nhau
                        if (yc.getMaLichTarget() != null && !yc.getMaLichTarget().trim().isEmpty()) {
                            LichPhanCa lichDich = service.getLichById(yc.getMaLichTarget());
                            if (lichDich != null) {
                                caDoi = lichDich.getMaCa() + " (" + fmtDate.format(lichDich.getNgayLamViec()) + ")";
                            }
                        } 
                        // Nếu không có lịch đích -> Đây là nhờ làm giúp (không đổi qua lại)
                        else if (yc.getMaNVTarget() != null && !yc.getMaNVTarget().trim().isEmpty()) {
                            caDoi = "Làm giúp (Không đổi)";
                        }

                        // --- 3. ĐẨY DỮ LIỆU LÊN BẢNG ---
                        // Nhớ chú ý thứ tự đẩy vào mảng phải khớp với String[] cols ở Bước 1
                        duyetModel.addRow(new Object[]{
                                yc.getMaYeuCau(),
                                maNguoiGui,
                                caGoc,
                                maNguoiNhan,
                                caDoi,
                                yc.getLyDo(),
                                yc.getNgayTao() != null ? fmtTime.format(yc.getNgayTao()) : "—"
                        });
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    //Tab 4: Báo cáo chấm công (F3.4)

    private JPanel buildBaoCaoTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 0, 0, 0));

        // Header bộ lọc
        JPanel filterCard = createCard("Lọc báo cáo");
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 4));
        filterRow.setOpaque(false);

        // Tháng
        Calendar cal = Calendar.getInstance();
        spThang = new JSpinner(new SpinnerNumberModel(cal.get(Calendar.MONTH) + 1, 1, 12, 1));
        spThang.setPreferredSize(new Dimension(60, 28));
        spThang.setFont(UITheme.FONT_BODY);

        // Năm
        spNam = new JSpinner(new SpinnerNumberModel(cal.get(Calendar.YEAR), 2020, 2099, 1));
        JSpinner.NumberEditor yearEd = new JSpinner.NumberEditor(spNam, "#");
        spNam.setEditor(yearEd);
        spNam.setPreferredSize(new Dimension(80, 28));
        spNam.setFont(UITheme.FONT_BODY);

        // ComboBox chọn NV (All + từng NV)
        cboNVBaoCao = new JComboBox<>();
        cboNVBaoCao.setFont(UITheme.FONT_BODY);
        cboNVBaoCao.setPreferredSize(new Dimension(180, 28));
        cboNVBaoCao.addItem("-- Tất cả nhân viên --");
        new SwingWorker<List<NhanVien>, Void>() {
            @Override protected List<NhanVien> doInBackground() { return service.getDanhSachNV(); }
            @Override protected void done() {
                try {
                    for (NhanVien nv : get())
                        cboNVBaoCao.addItem(nv.getMaNV() + " – " + nv.getHoTen());
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();

        JButton btnXem   = actionBtn("Xem báo cáo", FontAwesomeSolid.SEARCH, UITheme.BLUE);
        JButton btnExcel = actionBtn("Xuất Excel", FontAwesomeSolid.FILE_EXCEL, UITheme.GREEN);

        btnXem.addActionListener(e -> loadBaoCao());
        btnExcel.addActionListener(e -> xuatExcel());

        filterRow.add(new JLabel("Tháng:"));  filterRow.add(spThang);
        filterRow.add(new JLabel("Năm:"));    filterRow.add(spNam);
        filterRow.add(new JLabel("Nhân viên:")); filterRow.add(cboNVBaoCao);
        filterRow.add(btnXem);
        filterRow.add(btnExcel);

        filterCard.add(filterRow, BorderLayout.CENTER);

        //Bảng kết quả
        JPanel tableCard = createCard("Dữ liệu chấm công");
        String[] cols = {"Mã NV", "Họ tên", "Ngày làm việc", "Ca", "Giờ vào",
                         "Giờ ra", "Trạng thái CC", "Số giờ làm"};
        tblBaoCao = createTable(cols);
        baoCaoModel = (DefaultTableModel) tblBaoCao.getModel();
        JScrollPane sp = new JScrollPane(tblBaoCao);
        sp.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        tableCard.add(sp, BorderLayout.CENTER);

        // Stat tóm tắt dưới bảng
        JPanel sumRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 4));
        sumRow.setOpaque(false);
        JLabel lblTong = new JLabel("Tổng bản ghi: —");
        lblTong.setFont(UITheme.FONT_SMALL); lblTong.setForeground(UITheme.MUTED);
        sumRow.add(lblTong);
        tableCard.add(sumRow, BorderLayout.SOUTH);

        // Cập nhật lblTong sau mỗi lần load
        baoCaoModel.addTableModelListener(e2 -> {
            int n = baoCaoModel.getRowCount();
            lblTong.setText("Tổng bản ghi: " + n);
        });

        panel.add(filterCard, BorderLayout.NORTH);
        panel.add(tableCard, BorderLayout.CENTER);
        return panel;
    }

    /** Tải dữ liệu chấm công vào bảng theo bộ lọc hiện tại. */
    private void loadBaoCao() {
        if (spThang == null) return;
        int thang  = (int) spThang.getValue();
        int nam    = (int) spNam.getValue();
        String nvFilter = cboNVBaoCao.getSelectedIndex() == 0 ? null
                : cboNVBaoCao.getSelectedItem().toString().split(" – ")[0].trim();

        new SwingWorker<List<Object[]>, Void>() {
            @Override protected List<Object[]> doInBackground() {
                return service.getBaoCaoChamCong(thang, nam, nvFilter);
            }
            @Override protected void done() {
                try {
                    List<Object[]> rows = get();
                    baoCaoModel.setRowCount(0);
                    for (Object[] r : rows) baoCaoModel.addRow(r);
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    /** Xuất bảng báo cáo ra file .xlsx (Apache POI). */
    private void xuatExcel() {
        if (baoCaoModel.getRowCount() == 0) {
            showError("Không có dữ liệu để xuất. Vui lòng xem báo cáo trước.");
            return;
        }

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Lưu báo cáo chấm công");
        fc.setFileFilter(new FileNameExtensionFilter("Excel Workbook (*.xlsx)", "xlsx"));
        String tenFile = "BaoCaoChamCong_"
                + String.format("%02d", spThang.getValue()) + "_" + spNam.getValue() + ".xlsx";
        fc.setSelectedFile(new File(tenFile));

        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = fc.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".xlsx"))
            file = new File(file.getAbsolutePath() + ".xlsx");

        final File finalFile = file;
        final int thang = (int) spThang.getValue();
        final int nam   = (int) spNam.getValue();

        new SwingWorker<Boolean, Void>() {
            String errMsg = null;

            @Override protected Boolean doInBackground() {
                try (XSSFWorkbook wb = new XSSFWorkbook()) {
                    XSSFSheet sheet = wb.createSheet("ChamCong");

                    //Styles
                    XSSFCellStyle headerStyle = wb.createCellStyle();
                    headerStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte)26,(byte)54,(byte)93}, null));
                    headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    headerStyle.setBorderBottom(BorderStyle.THIN);
                    headerStyle.setAlignment(HorizontalAlignment.CENTER);
                    XSSFFont hFont = wb.createFont();
                    hFont.setColor(IndexedColors.WHITE.getIndex());
                    hFont.setBold(true);
                    hFont.setFontHeightInPoints((short) 11);
                    headerStyle.setFont(hFont);

                    XSSFCellStyle titleStyle = wb.createCellStyle();
                    XSSFFont tFont = wb.createFont();
                    tFont.setBold(true);
                    tFont.setFontHeightInPoints((short) 14);
                    titleStyle.setFont(tFont);

                    XSSFCellStyle evenStyle = wb.createCellStyle();
                    evenStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte)248,(byte)250,(byte)252}, null));
                    evenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                    //Tiêu đề báo cáo 
                    XSSFRow titleRow = sheet.createRow(0);
                    XSSFCell titleCell = titleRow.createCell(0);
                    titleCell.setCellValue("BÁO CÁO CHẤM CÔNG – THÁNG " + thang + "/" + nam);
                    titleCell.setCellStyle(titleStyle);

                    XSSFRow subRow = sheet.createRow(1);
                    subRow.createCell(0).setCellValue("Xuất lúc: "
                            + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()));

                    sheet.createRow(2); // dòng trống

                    //Header cột
                    String[] headers = {"Mã NV", "Họ tên", "Ngày làm việc", "Ca",
                                        "Giờ vào", "Giờ ra", "Trạng thái CC", "Số giờ làm"};
                    XSSFRow hRow = sheet.createRow(3);
                    for (int c = 0; c < headers.length; c++) {
                        XSSFCell cell = hRow.createCell(c);
                        cell.setCellValue(headers[c]);
                        cell.setCellStyle(headerStyle);
                    }

                    //Dữ liệu
                    int rowIdx = 4;
                    for (int r = 0; r < baoCaoModel.getRowCount(); r++) {
                        XSSFRow dataRow = sheet.createRow(rowIdx++);
                        XSSFCellStyle rowStyle = r % 2 == 0 ? evenStyle : null;
                        for (int c = 0; c < baoCaoModel.getColumnCount(); c++) {
                            XSSFCell cell = dataRow.createCell(c);
                            Object val = baoCaoModel.getValueAt(r, c);
                            cell.setCellValue(val != null ? val.toString() : "");
                            if (rowStyle != null) cell.setCellStyle(rowStyle);
                        }
                    }

                    //Auto-size
                    for (int c = 0; c < headers.length; c++) sheet.autoSizeColumn(c);

                    try (FileOutputStream fos = new FileOutputStream(finalFile)) {
                        wb.write(fos);
                    }
                    return true;
                } catch (Exception ex) {
                    errMsg = ex.getMessage();
                    ex.printStackTrace();
                    return false;
                }
            }

            @Override protected void done() {
                try {
                    if (get()) {
                        showSuccess("Xuất Excel thành công!\n" + finalFile.getAbsolutePath());
                    } else {
                        showError("Xuất thất bại: " + errMsg);
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    //Tab 3: Lịch tổng 
    private JPanel buildLichTongTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 0, 0, 0));

        // Điều hướng tuần
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); nav.setOpaque(false);
        JButton prev  = actionBtn("◀  Tuần trước", UITheme.BLUE);
        JButton next  = actionBtn("Tuần sau  ▶",   UITheme.BLUE);
        JButton today = actionBtn("Tuần này",       new Color(71, 85, 105));
        lblTuan = new JLabel(); lblTuan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nav.add(prev); nav.add(today); nav.add(next);
        nav.add(Box.createHorizontalStrut(16)); nav.add(lblTuan);
        panel.add(nav, BorderLayout.NORTH);

        // Khởi tạo tuần hiện tại (từ Thứ 2)
        tuanBatDau = getMondayOfCurrentWeek();

        prev.addActionListener(e  -> { tuanBatDau = addDays(tuanBatDau, -7); loadLichTong(); });
        next.addActionListener(e  -> { tuanBatDau = addDays(tuanBatDau, +7); loadLichTong(); });
        today.addActionListener(e -> { tuanBatDau = getMondayOfCurrentWeek(); loadLichTong(); });

        // Grid lịch (sẽ được build lại bởi buildLichTongGrid)
        lichTongGrid = new JPanel(new BorderLayout());
        lichTongGrid.setBackground(UITheme.BG_CARD);
        lichTongGrid.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));

        JScrollPane sp = new JScrollPane(lichTongGrid); sp.setBorder(null);
        panel.add(sp, BorderLayout.CENTER);

        return panel;
    }

    /** Tải lịch tổng tuần hiện tại. */
    private void loadLichTong() {
        Date denNgay = addDays(tuanBatDau, 6);

        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM");
        lblTuan.setText("Tuần: " + fmt.format(tuanBatDau) + " – " + fmt.format(denNgay));

        new SwingWorker<Object[], Void>() {
            @Override protected Object[] doInBackground() {
                List<NhanVien>  nvs  = service.getDanhSachNV();
                List<LichPhanCa> lich = service.getLichTrongTuan(
                        new java.sql.Date(tuanBatDau.getTime()),
                        new java.sql.Date(denNgay.getTime()));
                return new Object[]{ nvs, lich };
            }
            @Override protected void done() {
                try {
                    Object[] data = get();
                    buildLichTongGrid((List<NhanVien>) data[0], (List<LichPhanCa>) data[1]);
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    /** Vẽ grid lịch tổng (NV × 7 ngày). */
    private void buildLichTongGrid(List<NhanVien> nvs, List<LichPhanCa> lichs) {
        lichTongGrid.removeAll();

        String[] tenNgay = {"T2","T3","T4","T5","T6","T7","CN"};
        SimpleDateFormat fmtNgay = new SimpleDateFormat("dd/MM");

        int cols = 8;  // 1 cột tên NV + 7 ngày
        JPanel grid = new JPanel(new GridLayout(nvs.size() + 1, cols, 1, 1));
        grid.setBackground(UITheme.BORDER);

        // Header
        grid.add(headerCell("Nhân viên"));
        for (int i = 0; i < 7; i++) {
            Date ngay = addDays(tuanBatDau, i);
            grid.add(headerCell(tenNgay[i] + "\n" + fmtNgay.format(ngay)));
        }

        // Map: (maNV + ngày) → maCa
        Map<String, String> map = new HashMap<>();
        for (LichPhanCa l : lichs) {
            String key = l.getMaNV() + "_" + l.getNgayLamViec().toString();
            map.put(key, l.getMaCa());
        }

        Calendar todayCal = Calendar.getInstance();
        SimpleDateFormat fmtKey = new SimpleDateFormat("yyyy-MM-dd");

        // Hàng mỗi NV
        for (int r = 0; r < nvs.size(); r++) {
            NhanVien nv = nvs.get(r);
            Color rowBg = r % 2 == 0 ? Color.WHITE : new Color(248, 250, 252);

            JLabel lblNV = new JLabel("  " + nv.getHoTen());
            lblNV.setFont(UITheme.FONT_SMALL); lblNV.setForeground(UITheme.TEXT2);
            lblNV.setBackground(rowBg); lblNV.setOpaque(true);
            grid.add(lblNV);

            for (int d = 0; d < 7; d++) {
                Date ngay = addDays(tuanBatDau, d);
                String key = nv.getMaNV() + "_" + fmtKey.format(ngay);
                String maCa = map.get(key);

                boolean isToday = fmtKey.format(ngay).equals(fmtKey.format(todayCal.getTime()));
                JPanel cell = new JPanel(new GridBagLayout());
                cell.setBackground(isToday ? new Color(239, 246, 255) : rowBg);
                cell.setPreferredSize(new Dimension(80, 44));

                if (maCa != null) {
                    cell.add(caChip(maCa));
                }
                grid.add(cell);
            }
        }

        lichTongGrid.removeAll();
        lichTongGrid.add(grid, BorderLayout.CENTER);

        // Chú thích
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 4)); legend.setOpaque(false);
        legend.add(dotLegend(UITheme.BLUE_PALE,            new Color(30, 64, 175),  "Ca sáng"));
        legend.add(dotLegend(UITheme.AMBER_PALE,           new Color(120, 53, 15),  "Ca chiều"));
        legend.add(dotLegend(new Color(237, 233, 254),     new Color(76, 29, 149),  "Ca tối"));
        lichTongGrid.add(legend, BorderLayout.SOUTH);

        lichTongGrid.revalidate(); lichTongGrid.repaint();
    }

    private JLabel headerCell(String text) {
        JLabel l = new JLabel("<html><center>" + text.replace("\n", "<br>") + "</center></html>",
                SwingConstants.CENTER);
        l.setFont(UITheme.FONT_BOLD); l.setBackground(UITheme.NAVY2); l.setForeground(Color.WHITE);
        l.setOpaque(true); l.setPreferredSize(new Dimension(80, 44));
        return l;
    }

    /** Chip màu hiển thị tên ca trong ô lịch. */
    private JLabel caChip(String maCa) {
        String text; Color bg; Color fg;
        if ("CA01".equals(maCa))      { text = "Sáng";  bg = UITheme.BLUE_PALE;         fg = new Color(30, 64, 175);  }
        else if ("CA02".equals(maCa)) { text = "Chiều"; bg = UITheme.AMBER_PALE;        fg = new Color(120, 53, 15);  }
        else if ("CA03".equals(maCa)) { text = "Tối";   bg = new Color(237, 233, 254);  fg = new Color(76, 29, 149);  }
        else                          { text = maCa;    bg = UITheme.BLUE_PALE;         fg = UITheme.NAVY; }

        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setBackground(bg); l.setForeground(fg); l.setOpaque(true);
        l.setBorder(new EmptyBorder(3, 8, 3, 8));
        return l;
    }

    //Refresh
    private void refreshTab() {
        int idx = tabs.getSelectedIndex();
        if (idx == TAB_XEPLICH)  loadLichDaXep();
        if (idx == TAB_DUYET)    loadYeuCauChoDuyet();
        if (idx == TAB_LICHTONG) loadLichTong();
        if (idx == TAB_BAOCAO)   loadBaoCao();
        loadStatBar();
    }

    //Helpers
    /** Tìm ngày Thứ 2 của tuần hiện tại. */
    private Date getMondayOfCurrentWeek() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /*
        tính các ngày trước/sau để hiển thị lịch làm việc, 
        thống kê chấm công hoặc tìm dữ liệu theo khoảng thời gian
    */
    private Date addDays(Date d, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d); cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }

    //giao diện thống nhất (font, màu, căn trái)
    private JLabel fLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11)); l.setForeground(UITheme.TEXT2);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    //tạo một chú thích (legend) có ô màu và dòng chữ mô tả bên cạnh
    private JPanel dotLegend(Color bg, Color fg, String label) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)); p.setOpaque(false);
        JLabel box = new JLabel("   "); box.setBackground(bg); box.setOpaque(true);
        box.setBorder(BorderFactory.createLineBorder(bg.darker()));
        JLabel lbl = new JLabel(label); lbl.setFont(UITheme.FONT_SMALL); lbl.setForeground(fg);
        p.add(box); p.add(lbl);
        return p;
    }
}