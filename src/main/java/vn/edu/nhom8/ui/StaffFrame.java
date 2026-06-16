package vn.edu.nhom8.ui;

import vn.edu.nhom8.dao.ChamCongDAO;
import vn.edu.nhom8.dao.ILichPhanCaDAO;
import vn.edu.nhom8.dao.IYeuCauDoiCaDAO;
import vn.edu.nhom8.model.ChamCong;
import vn.edu.nhom8.model.LichPhanCa;
import vn.edu.nhom8.model.NhanVien;
import vn.edu.nhom8.model.YeuCauDoiCa;
import vn.edu.nhom8.service.StaffService;
import vn.edu.nhom8.util.SessionManager;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Màn hình Nhân viên – F4.
 *
 * BUG FIXES so với bản cũ:
 *  1. btnCheckIn / btnCheckOut là field class-level, được gán đúng trong buildInfoBar()
 *     → toolbar doCheckIn/doCheckOut và info-bar dùng cùng một instance, không còn NPE.
 *  2. doCheckIn / doCheckOut dùng CheckInResult / CheckOutResult từ StaffService
 *     → trangThai thực tế (DungGio / DiMuon / VeSom) được hiển thị đúng.
 *  3. refreshAll() reset cả daCheckOut → không còn bị block check-out sau refresh.
 *  4. loadCaHomNay() kiểm tra isCheckedOut() để enable/disable nút đúng.
 *  5. Trạng thái lblStatus hiển thị đúng: "Đi muộn", "Về sớm", "Đang trong ca".
 */
public class StaffFrame extends BaseFrame {

    private final StaffService service;

    // ── Trạng thái chấm công phiên này ──────────────────────────────────────
    private LichPhanCa caHomNay   = null;
    private boolean    daCheckIn  = false;
    private boolean    daCheckOut = false;

    // ── Tab nội dung bên trong tab Nhân viên ────────────────────────────────
    private JTabbedPane innerTabs;
    private static final int TAB_CHAMCONG = 0;
    private static final int TAB_LICHCA   = 1;
    private static final int TAB_DOICA    = 2;

    // ── Widgets info-bar ─────────────────────────────────────────────────────
    private JLabel  lblCaTen, lblCaGio, lblStatus;
    /** BUG FIX: field class-level – dùng chung giữa info-bar và toolbar */
    private JButton btnCheckIn, btnCheckOut;

    // ── Widgets tab chấm công ────────────────────────────────────────────────
    private DefaultTableModel historyModel;

    // ── Widgets tab lịch ca ──────────────────────────────────────────────────
    private JLabel lblThang;
    private JPanel calGrid;
    private int    calNam, calThang;

    // ── Widgets tab đổi ca ───────────────────────────────────────────────────
    private JComboBox<LichPhanCa> cboCaTuongLai;
    private JComboBox<LichPhanCa> cboLichTarget;
    private DefaultTableModel     ycModel;

    // ─────────────────────────────────────────────────────────────────────────

    public StaffFrame(ChamCongDAO chamCongDAO,
                      ILichPhanCaDAO lichDAO,
                      IYeuCauDoiCaDAO ycDAO) {
        super("Nhân viên");
        this.service = new StaffService(chamCongDAO, lichDAO, ycDAO);

        buildStaffContent();
        initRoleTabs(ROLE_TAB_STAFF, ROLE_TAB_STAFF);
        loadCaHomNay();
        setVisible(true);
    }

    public StaffFrame() {
        this(new ChamCongDAO(), null, null);
    }

    // ── Toolbar ───────────────────────────────────────────────────────────────

    @Override
    protected JPanel buildToolbarForRole(int roleTabIndex) {
        if (roleTabIndex != ROLE_TAB_STAFF) return null;

        JPanel tb = createToolbarPanel();

        JButton btnCi     = toolbarBtn(FontAwesomeSolid.CLOCK,        "Check-in");
        JButton btnCo     = toolbarBtn(FontAwesomeSolid.SIGN_OUT_ALT, "Check-out");
        JButton btnLich   = toolbarBtn(FontAwesomeSolid.CALENDAR_ALT,  "Xem ca làm");
        JButton btnDoi    = toolbarBtn(FontAwesomeSolid.EXCHANGE_ALT,  "Đổi ca");
        JButton btnLamMoi = toolbarBtn(FontAwesomeSolid.SYNC,          "Làm mới");

        // BUG FIX: toolbar buttons gọi đúng action, action tự kiểm tra state
        btnCi.addActionListener(e     -> { innerTabs.setSelectedIndex(TAB_CHAMCONG); doCheckIn(); });
        btnCo.addActionListener(e     -> { innerTabs.setSelectedIndex(TAB_CHAMCONG); doCheckOut(); });
        btnLich.addActionListener(e   -> innerTabs.setSelectedIndex(TAB_LICHCA));
        btnDoi.addActionListener(e    -> innerTabs.setSelectedIndex(TAB_DOICA));
        btnLamMoi.addActionListener(e -> refreshAll());

        tb.add(btnCi); tb.add(btnCo); tb.add(toolbarSep());
        tb.add(btnLich); tb.add(btnDoi); tb.add(toolbarSep());
        tb.add(btnLamMoi);
        return tb;
    }

    // ── Xây nội dung ─────────────────────────────────────────────────────────

    private void buildStaffContent() {
        JPanel staffPanel = getRoleContentPanel(ROLE_TAB_STAFF);

        NhanVien nv = SessionManager.getInstance().getCurrentUser();
        staffPanel.add(buildInfoBar(nv), BorderLayout.NORTH);

        innerTabs = new JTabbedPane(JTabbedPane.TOP);
        innerTabs.setFont(UITheme.FONT_BODY);
        innerTabs.addTab("Chấm công",   buildChamCongTab());
        innerTabs.addTab("Lịch ca làm", buildLichCaTab());
        innerTabs.addTab("Đổi ca",      buildDoiCaTab());

        innerTabs.addChangeListener(e -> {
            if (innerTabs.getSelectedIndex() == TAB_LICHCA)  loadCalendar();
            if (innerTabs.getSelectedIndex() == TAB_DOICA) { loadCaTuongLai(); loadYeuCauHistory(); }
        });

        staffPanel.add(innerTabs, BorderLayout.CENTER);
    }

    // ── Info bar ──────────────────────────────────────────────────────────────

    private JPanel buildInfoBar(NhanVien nv) {
        JPanel bar = new JPanel(new GridLayout(1, 3, 12, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(0, 0, 14, 0));

        // Card 1: Avatar + tên
        JPanel card1 = createCard(null);
        card1.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 8));
        card1.add(buildAvatar(nv));
        JPanel meta = new JPanel(new GridLayout(3, 1, 0, 2));
        meta.setOpaque(false);
        JLabel lbName = new JLabel(nv != null ? nv.getHoTen() : "");
        lbName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        JLabel lbMaNV = new JLabel("Mã NV: " + (nv != null ? nv.getMaNV() : ""));
        JLabel lbRole = new JLabel("Vai trò: " + (nv != null ? nv.getVaiTro() : ""));
        lbMaNV.setFont(UITheme.FONT_SMALL); lbMaNV.setForeground(UITheme.MUTED);
        lbRole.setFont(UITheme.FONT_SMALL); lbRole.setForeground(UITheme.MUTED);
        meta.add(lbName); meta.add(lbMaNV); meta.add(lbRole);
        card1.add(meta);

        // Card 2: Ca hôm nay
        JPanel card2 = createCard("Ca làm hôm nay");
        JPanel c2 = new JPanel(new GridLayout(3, 1, 0, 4)); c2.setOpaque(false);
        lblCaTen = new JLabel("Đang tải...");
        lblCaTen.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblCaTen.setForeground(UITheme.BLUE);
        lblCaGio = new JLabel("--:-- → --:--");
        lblCaGio.setFont(UITheme.FONT_BODY); lblCaGio.setForeground(UITheme.TEXT2);
        lblStatus = new JLabel("Chưa check-in");
        lblStatus.setFont(UITheme.FONT_BOLD); lblStatus.setForeground(UITheme.AMBER);
        c2.add(lblCaTen); c2.add(lblCaGio); c2.add(lblStatus);
        card2.add(c2, BorderLayout.CENTER);

        // Card 3: Nút chấm công nhanh
        // BUG FIX: gán vào field class-level (btnCheckIn / btnCheckOut)
        JPanel card3 = createCard("Chấm công nhanh");
        JPanel btns = new JPanel(new GridLayout(1, 2, 10, 0)); btns.setOpaque(false);
        btnCheckIn  = actionBtn("CHECK-IN",  FontAwesomeSolid.SIGN_IN_ALT,  UITheme.GREEN);
        btnCheckOut = actionBtn("CHECK-OUT", FontAwesomeSolid.SIGN_OUT_ALT, UITheme.AMBER);
        btnCheckIn.setEnabled(false);   // disable cho đến khi loadCaHomNay xong
        btnCheckOut.setEnabled(false);
        btnCheckIn.addActionListener(e  -> doCheckIn());
        btnCheckOut.addActionListener(e -> doCheckOut());
        btns.add(btnCheckIn); btns.add(btnCheckOut);
        card3.add(btns, BorderLayout.CENTER);

        bar.add(card1); bar.add(card2); bar.add(card3);
        return bar;
    }

    private JPanel buildAvatar(NhanVien nv) {
        JPanel av = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, UITheme.NAVY, getWidth(), getHeight(), UITheme.BLUE));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
                String ini = (nv != null && !nv.getHoTen().isEmpty())
                        ? nv.getHoTen().substring(0, 1).toUpperCase() : "?";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(ini,
                        (getWidth() - fm.stringWidth(ini)) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        av.setPreferredSize(new Dimension(54, 54));
        av.setOpaque(false);
        return av;
    }

    // ── Tab 1: Chấm công ──────────────────────────────────────────────────────

    private JPanel buildChamCongTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 0, 0, 0));

        JPanel header = new JPanel(new BorderLayout()); header.setOpaque(false);
        JLabel title = new JLabel("Lịch sử chấm công"); title.setFont(UITheme.FONT_HEAD);
        JButton btnLamMoi = actionBtn("Làm mới", new Color(71, 85, 105));
        btnLamMoi.addActionListener(e -> loadLichSuChamCong());
        header.add(title, BorderLayout.WEST);
        header.add(btnLamMoi, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        String[] cols = {"Ngày", "Mã ca", "Giờ vào", "Giờ ra", "Trạng thái"};
        historyModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = createTable(cols);
        tbl.setModel(historyModel);
        JScrollPane sp = new JScrollPane(tbl);
        sp.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        panel.add(sp, BorderLayout.CENTER);

        return panel;
    }

    // ── Tab 2: Lịch ca làm ────────────────────────────────────────────────────

    private JPanel buildLichCaTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 0, 0, 0));

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); nav.setOpaque(false);
        JButton prev  = actionBtn("◀", UITheme.BLUE);
        JButton next  = actionBtn("▶", UITheme.BLUE);
        JButton today = actionBtn("Hôm nay", new Color(71, 85, 105));
        lblThang = new JLabel(); lblThang.setFont(new Font("Segoe UI", Font.BOLD, 15));
        nav.add(prev); nav.add(next); nav.add(today);
        nav.add(Box.createHorizontalStrut(12)); nav.add(lblThang);
        panel.add(nav, BorderLayout.NORTH);

        Calendar now = Calendar.getInstance();
        calNam = now.get(Calendar.YEAR); calThang = now.get(Calendar.MONTH) + 1;

        prev.addActionListener(e -> { if (--calThang < 1)  { calThang = 12; calNam--; } loadCalendar(); });
        next.addActionListener(e -> { if (++calThang > 12) { calThang = 1;  calNam++; } loadCalendar(); });
        today.addActionListener(e -> {
            Calendar c = Calendar.getInstance();
            calNam = c.get(Calendar.YEAR); calThang = c.get(Calendar.MONTH) + 1;
            loadCalendar();
        });

        calGrid = new JPanel(new GridLayout(0, 7, 3, 3));
        calGrid.setBackground(UITheme.BG_CARD);
        calGrid.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                new EmptyBorder(8, 8, 8, 8)));

        JPanel wrap = new JPanel(new BorderLayout(0, 8)); wrap.setOpaque(false);
        wrap.add(calGrid, BorderLayout.CENTER);

        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4)); legend.setOpaque(false);
        legend.add(dotLabel(UITheme.BLUE,             "Hôm nay"));
        legend.add(dotLabel(UITheme.BLUE_PALE,        "Có ca"));
        legend.add(dotLabel(new Color(248, 250, 252), "Không có ca"));
        wrap.add(legend, BorderLayout.SOUTH);

        panel.add(wrap, BorderLayout.CENTER);
        return panel;
    }

    // ── Tab 3: Đổi ca ─────────────────────────────────────────────────────────

    private JPanel buildDoiCaTab() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 14, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 0, 0, 0));

        JPanel formCard = createCard("Gửi yêu cầu đổi ca");
        JPanel body = new JPanel(); body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        JLabel hint = new JLabel("Chỉ được gửi cho ca trong tương lai.");
        hint.setFont(UITheme.FONT_SMALL); hint.setForeground(UITheme.AMBER);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);

        cboCaTuongLai = new JComboBox<>();
        cboCaTuongLai.setFont(UITheme.FONT_BODY);
        cboCaTuongLai.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        cboCaTuongLai.setAlignmentX(Component.LEFT_ALIGNMENT);
        cboCaTuongLai.setRenderer(lichRenderer());

        JTextField txtNVNhan = new JTextField();
        txtNVNhan.setFont(UITheme.FONT_BODY);
        txtNVNhan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        txtNVNhan.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtNVNhan.setToolTipText("Để trống nếu không chỉ định");
        txtNVNhan.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER), new EmptyBorder(4, 8, 4, 8)));

        cboLichTarget = new JComboBox<>();
        cboLichTarget.setFont(UITheme.FONT_BODY);
        cboLichTarget.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        cboLichTarget.setAlignmentX(Component.LEFT_ALIGNMENT);
        cboLichTarget.setEnabled(false);
        cboLichTarget.setRenderer(lichRenderer());

        JButton btnTaiCaNV = actionBtn("🔍  Xem ca của NV", new Color(71, 85, 105));
        btnTaiCaNV.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnTaiCaNV.addActionListener(e -> {
            String maNVTarget = txtNVNhan.getText().trim();
            cboLichTarget.removeAllItems();
            cboLichTarget.addItem(null);
            if (maNVTarget.isEmpty()) {
                cboLichTarget.setEnabled(false);
                showError("Vui lòng nhập mã NV đổi cùng trước.");
                return;
            }
            new SwingWorker<List<LichPhanCa>, Void>() {
                @Override protected List<LichPhanCa> doInBackground() {
                    return service.getCaTuongLaiCuaNV(maNVTarget);
                }
                @Override protected void done() {
                    try {
                        List<LichPhanCa> list = get();
                        list.forEach(cboLichTarget::addItem);
                        cboLichTarget.setEnabled(true);
                        if (list.isEmpty()) {
                            showError("NV " + maNVTarget + " không có ca nào trong tương lai.\n"
                                    + "Bạn vẫn có thể gửi yêu cầu \"nhờ làm giúp\".");
                        }
                    } catch (Exception ex) { ex.printStackTrace(); }
                }
            }.execute();
        });

        JTextArea txtLyDo = new JTextArea(3, 20);
        txtLyDo.setFont(UITheme.FONT_BODY); txtLyDo.setLineWrap(true); txtLyDo.setWrapStyleWord(true);
        txtLyDo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER), new EmptyBorder(6, 8, 6, 8)));
        JScrollPane scrollLyDo = new JScrollPane(txtLyDo);
        scrollLyDo.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollLyDo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JButton btnGui = actionBtn("Gửi yêu cầu", FontAwesomeSolid.PAPER_PLANE, UITheme.BLUE);
        btnGui.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnGui.addActionListener(e -> {
            NhanVien nv = SessionManager.getInstance().getCurrentUser();
            if (nv == null) return;
            Object sel = cboCaTuongLai.getSelectedItem();
            String maLich = (sel instanceof LichPhanCa) ? ((LichPhanCa) sel).getMaLich() : null;
            Object selTarget = cboLichTarget.getSelectedItem();
            String maLichTarget = (selTarget instanceof LichPhanCa) ? ((LichPhanCa) selTarget).getMaLich() : null;

            String err = service.guiYeuCauDoiCa(
                    nv.getMaNV(), maLich,
                    txtNVNhan.getText().trim(),
                    maLichTarget,
                    txtLyDo.getText().trim());
            if (err == null) {
                showSuccess("Gửi yêu cầu thành công!\nVui lòng chờ Quản lý duyệt.");
                txtLyDo.setText(""); txtNVNhan.setText("");
                cboLichTarget.removeAllItems();
                cboLichTarget.setEnabled(false);
                loadYeuCauHistory();
            } else {
                showError(err);
            }
        });

        body.add(hint);                                  body.add(Box.createVerticalStrut(10));
        body.add(fLabel("Ca cần đổi *"));               body.add(Box.createVerticalStrut(4));
        body.add(cboCaTuongLai);                         body.add(Box.createVerticalStrut(10));
        body.add(fLabel("Mã NV đổi cùng / nhờ giúp (tuỳ chọn)")); body.add(Box.createVerticalStrut(4));
        body.add(txtNVNhan);                             body.add(Box.createVerticalStrut(6));
        body.add(btnTaiCaNV);                            body.add(Box.createVerticalStrut(10));
        body.add(fLabel("Đổi lấy ca nào của NV đó? (để trống = nhờ làm giúp)")); body.add(Box.createVerticalStrut(4));
        body.add(cboLichTarget);                         body.add(Box.createVerticalStrut(10));
        body.add(fLabel("Lý do *"));                    body.add(Box.createVerticalStrut(4));
        body.add(scrollLyDo);                            body.add(Box.createVerticalStrut(14));
        body.add(btnGui);
        formCard.add(body, BorderLayout.CENTER);

        JPanel histCard = createCard("Lịch sử yêu cầu đổi ca");
        String[] cols = {"Mã YC", "Mã ca", "NV đổi cùng", "Đổi lấy ca", "Trạng thái", "Ngày tạo"};
        JTable tbl = createTable(cols);
        ycModel = (DefaultTableModel) tbl.getModel();
        JScrollPane sp = new JScrollPane(tbl);
        sp.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        histCard.add(sp, BorderLayout.CENTER);

        panel.add(formCard); panel.add(histCard);
        return panel;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  LOAD DATA
    // ═════════════════════════════════════════════════════════════════════════

    private void loadCaHomNay() {
        NhanVien nv = SessionManager.getInstance().getCurrentUser();
        if (nv == null) return;

        new SwingWorker<LichPhanCa, Void>() {
            @Override protected LichPhanCa doInBackground() {
                return service.getCaHomNay(nv.getMaNV());
            }
            @Override protected void done() {
                try {
                    caHomNay = get();
                    if (caHomNay != null) {
                        lblCaTen.setText("Ca: " + caHomNay.getMaCa());
                        lblCaTen.setForeground(UITheme.BLUE);
                        lblCaGio.setText("Ngày: " + new SimpleDateFormat("dd/MM/yyyy")
                                .format(caHomNay.getNgayLamViec()));

                        boolean ci  = service.isCheckedIn(nv.getMaNV(), caHomNay.getMaLich());
                        // BUG FIX: kiểm tra thêm isCheckedOut
                        boolean co  = ci && service.isCheckedOut(caHomNay.getMaLich());

                        if (co) {
                            // Đã checkout xong
                            daCheckIn = true; daCheckOut = true;
                            btnCheckIn.setEnabled(false);
                            btnCheckOut.setEnabled(false);
                            lblStatus.setText("Đã hoàn thành ca");
                            lblStatus.setForeground(UITheme.MUTED);
                        } else if (ci) {
                            // Đã check-in, chưa checkout
                            daCheckIn = true; daCheckOut = false;
                            btnCheckIn.setEnabled(false);
                            btnCheckOut.setEnabled(true);
                            lblStatus.setText("Đang trong ca");
                            lblStatus.setForeground(UITheme.GREEN);
                        } else {
                            // Chưa check-in
                            daCheckIn = false; daCheckOut = false;
                            btnCheckIn.setEnabled(true);
                            btnCheckOut.setEnabled(false);
                            lblStatus.setText("Chưa check-in");
                            lblStatus.setForeground(UITheme.AMBER);
                        }
                    } else {
                        lblCaTen.setText("Không có ca hôm nay");
                        lblCaTen.setForeground(UITheme.MUTED);
                        lblCaGio.setText("");
                        lblStatus.setText("Nghỉ");
                        lblStatus.setForeground(UITheme.MUTED);
                        btnCheckIn.setEnabled(false);
                        btnCheckOut.setEnabled(false);
                    }
                    loadLichSuChamCong();
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    private void loadLichSuChamCong() {
        NhanVien nv = SessionManager.getInstance().getCurrentUser();
        if (nv == null) return;

        new SwingWorker<List<ChamCong>, Void>() {
            @Override protected List<ChamCong> doInBackground() {
                return service.getLichSuChamCong(nv.getMaNV());
            }
            @Override protected void done() {
                try {
                    historyModel.setRowCount(0);
                    SimpleDateFormat fmtDate = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat fmtTime = new SimpleDateFormat("HH:mm");
                    for (ChamCong cc : get()) {
                        historyModel.addRow(new Object[]{
                                // Cột "Ngày" lấy từ maLich (hiện dùng maLich để tra ngày)
                                cc.getGioVao() != null ? fmtDate.format(cc.getGioVao()) : "—",
                                cc.getMaLich(),
                                cc.getGioVao() != null ? fmtTime.format(cc.getGioVao()) : "—",
                                cc.getGioRa()  != null ? fmtTime.format(cc.getGioRa())  : "—",
                                trangThaiLabel(cc.getTrangThai())
                        });
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    private void loadCalendar() {
        NhanVien nv = SessionManager.getInstance().getCurrentUser();
        if (nv == null) return;
        lblThang.setText("Tháng " + calThang + " / " + calNam);
        new SwingWorker<List<LichPhanCa>, Void>() {
            @Override protected List<LichPhanCa> doInBackground() {
                return service.getLichTheoThang(nv.getMaNV(), calNam, calThang);
            }
            @Override protected void done() {
                try { buildCalGrid(get()); }
                catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    private void buildCalGrid(List<LichPhanCa> lichTrongThang) {
        calGrid.removeAll();
        for (String d : new String[]{"CN","T2","T3","T4","T5","T6","T7"}) {
            JLabel h = new JLabel(d, SwingConstants.CENTER);
            h.setFont(UITheme.FONT_BOLD); h.setForeground(UITheme.MUTED);
            calGrid.add(h);
        }
        Map<Integer, LichPhanCa> dayMap = new HashMap<>();
        for (LichPhanCa lp : lichTrongThang) {
            Calendar c = Calendar.getInstance(); c.setTime(lp.getNgayLamViec());
            dayMap.put(c.get(Calendar.DAY_OF_MONTH), lp);
        }
        Calendar first = Calendar.getInstance(); first.set(calNam, calThang - 1, 1);
        int startDow    = first.get(Calendar.DAY_OF_WEEK) - 1;
        int daysInMonth = first.getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar todayCal = Calendar.getInstance();
        int todayDay = (todayCal.get(Calendar.YEAR) == calNam && todayCal.get(Calendar.MONTH) + 1 == calThang)
                       ? todayCal.get(Calendar.DAY_OF_MONTH) : -1;
        for (int i = 0; i < startDow; i++) calGrid.add(new JLabel());
        for (int d = 1; d <= daysInMonth; d++) {
            LichPhanCa lp = dayMap.get(d);
            boolean isToday = (d == todayDay);
            JPanel cell = new JPanel(new BorderLayout());
            cell.setBorder(new EmptyBorder(3, 3, 3, 3));
            cell.setBackground(isToday ? UITheme.BLUE : lp != null ? UITheme.BLUE_PALE : new Color(248, 250, 252));
            JLabel lDay = new JLabel(String.valueOf(d), SwingConstants.CENTER);
            lDay.setFont(isToday ? new Font("Segoe UI", Font.BOLD, 13) : UITheme.FONT_SMALL);
            lDay.setForeground(isToday ? Color.WHITE : lp != null ? UITheme.NAVY : UITheme.MUTED);
            cell.add(lDay, BorderLayout.CENTER);
            if (lp != null) {
                cell.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                LichPhanCa finalLp = lp;
                int day = d;
                cell.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                        JOptionPane.showMessageDialog(StaffFrame.this,
                                "Ngày: " + day + "/" + calThang + "/" + calNam
                                + "\nMã ca: " + finalLp.getMaCa()
                                + "\nTrạng thái: " + trangThaiLabel(finalLp.getTrangThai()),
                                "Chi tiết ca", JOptionPane.INFORMATION_MESSAGE);
                    }
                });
            }
            calGrid.add(cell);
        }
        calGrid.revalidate(); calGrid.repaint();
    }

    private void loadCaTuongLai() {
        NhanVien nv = SessionManager.getInstance().getCurrentUser();
        if (nv == null) return;
        new SwingWorker<List<LichPhanCa>, Void>() {
            @Override protected List<LichPhanCa> doInBackground() {
                return service.getCaTuongLai(nv.getMaNV());
            }
            @Override protected void done() {
                try {
                    cboCaTuongLai.removeAllItems();
                    List<LichPhanCa> list = get();
                    list.forEach(cboCaTuongLai::addItem);
                    if (list.isEmpty()) cboCaTuongLai.addItem(null);
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    private void loadYeuCauHistory() {
        NhanVien nv = SessionManager.getInstance().getCurrentUser();
        if (nv == null) return;
        new SwingWorker<List<YeuCauDoiCa>, Void>() {
            @Override protected List<YeuCauDoiCa> doInBackground() {
                return service.getYeuCauCuaNV(nv.getMaNV());
            }
            @Override protected void done() {
                try {
                    ycModel.setRowCount(0);
                    SimpleDateFormat fmt = new SimpleDateFormat("dd/MM HH:mm");
                    for (YeuCauDoiCa yc : get()) {
                        ycModel.addRow(new Object[]{
                                yc.getMaYeuCau(),
                                yc.getMaLichGoc(),
                                yc.getMaNVTarget()   != null ? yc.getMaNVTarget()   : "—",
                                yc.getMaLichTarget() != null ? yc.getMaLichTarget() : "—",
                                trangThaiLabel(yc.getTrangThai()),
                                yc.getNgayTao() != null ? fmt.format(yc.getNgayTao()) : "—"
                        });
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  LOGIC CHẤM CÔNG
    // ═════════════════════════════════════════════════════════════════════════

    private void doCheckIn() {
        NhanVien nv = SessionManager.getInstance().getCurrentUser();
        if (nv == null) return;

        // Guard: đã check-in rồi
        if (daCheckIn) { showError("Bạn đã check-in rồi!"); return; }

        String maLich = caHomNay != null ? caHomNay.getMaLich() : null;
        StaffService.CheckInResult result = service.checkIn(nv.getMaNV(), maLich);

        if (!result.ok) {
            showError(result.message);
            return;
        }

        daCheckIn = true;
        btnCheckIn.setEnabled(false);
        btnCheckOut.setEnabled(true);

        // BUG FIX: Hiển thị đúng trạng thái: DungGio / DiMuon
        if ("DiMuon".equals(result.trangThai)) {
            lblStatus.setText("Đang trong ca  ⚠ ĐI MUỘN");
            lblStatus.setForeground(UITheme.AMBER);
            showError(result.message); // dùng showError để màu đỏ nổi bật cảnh báo
        } else {
            lblStatus.setText("Đang trong ca");
            lblStatus.setForeground(UITheme.GREEN);
            showSuccess(result.message);
        }

        loadLichSuChamCong();
    }

    private void doCheckOut() {
        NhanVien nv = SessionManager.getInstance().getCurrentUser();
        if (nv == null) return;

        // Guard
        if (!daCheckIn)  { showError("Bạn chưa check-in!"); return; }
        if (daCheckOut)  { showError("Bạn đã check-out rồi!"); return; }

        // Xác nhận nếu có thể về sớm
        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận check-out?\n(Nếu về trước giờ kết thúc ca sẽ bị đánh dấu Về sớm.)",
                "Check-out", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        String maLich = caHomNay != null ? caHomNay.getMaLich() : null;
        StaffService.CheckOutResult result = service.checkOut(nv.getMaNV(), maLich);

        if (!result.ok) {
            showError(result.message);
            return;
        }

        daCheckOut = true;
        btnCheckOut.setEnabled(false);

        // BUG FIX: Hiển thị đúng trạng thái sau checkout
        switch (result.trangThai != null ? result.trangThai : "") {
            case "VeSom":
                lblStatus.setText("Đã hoàn thành ca  ⚠ VỀ SỚM");
                lblStatus.setForeground(UITheme.RED);
                showError(result.message);
                break;
            case "DiMuon":
                lblStatus.setText("Đã hoàn thành ca  (đi muộn)");
                lblStatus.setForeground(UITheme.AMBER);
                showSuccess(result.message);
                break;
            default:
                lblStatus.setText("Đã hoàn thành ca");
                lblStatus.setForeground(UITheme.MUTED);
                showSuccess(result.message);
                break;
        }

        loadLichSuChamCong();
    }

    /** BUG FIX: reset đầy đủ, bao gồm daCheckOut */
    private void refreshAll() {
        caHomNay  = null;
        daCheckIn  = false;
        daCheckOut = false;                  // ← bản cũ thiếu cái này

        lblCaTen.setText("Đang tải...");
        lblCaTen.setForeground(UITheme.BLUE);
        lblStatus.setText("Đang tải...");
        lblStatus.setForeground(UITheme.MUTED);
        btnCheckIn.setEnabled(false);
        btnCheckOut.setEnabled(false);

        loadCaHomNay();
        if (innerTabs.getSelectedIndex() == TAB_LICHCA) loadCalendar();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  HELPERS
    // ═════════════════════════════════════════════════════════════════════════

    static String trangThaiLabel(String code) {
        if (code == null) return "—";
        switch (code) {
            case "DungGio":  return "Đúng giờ";
            case "DiMuon":   return "Đi muộn";
            case "VeSom":    return "Về sớm";
            case "ChoDuyet": return "Chờ duyệt";
            case "DaDuyet":  return "Đã duyệt";
            case "TuChoi":   return "Từ chối";
            case "DaPhan":   return "Đã phân";
            default:          return code;
        }
    }

    private DefaultListCellRenderer lichRenderer() {
        return new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v instanceof LichPhanCa) {
                    LichPhanCa lp = (LichPhanCa) v;
                    String ngay = new SimpleDateFormat("dd/MM").format(lp.getNgayLamViec());
                    setText("Ca " + lp.getMaCa() + "  –  " + ngay);
                } else {
                    setText("— Không có ca tương lai —");
                }
                return this;
            }
        };
    }

    private JLabel fLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(UITheme.TEXT2);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JPanel dotLabel(Color color, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)); p.setOpaque(false);
        JLabel box = new JLabel("   "); box.setBackground(color); box.setOpaque(true);
        box.setBorder(BorderFactory.createLineBorder(color.darker()));
        JLabel lbl = new JLabel(text); lbl.setFont(UITheme.FONT_SMALL);
        p.add(box); p.add(lbl);
        return p;
    }
}