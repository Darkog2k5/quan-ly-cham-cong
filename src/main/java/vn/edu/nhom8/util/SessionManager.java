package vn.edu.nhom8.util;

import vn.edu.nhom8.model.NhanVien;

// Singleton lưu thông tin phiên đăng nhập hiện tại.
public class SessionManager {

    private static SessionManager instance;
    private NhanVien currentUser;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public NhanVien getCurrentUser() { return currentUser; }
    public void setCurrentUser(NhanVien nv) { this.currentUser = nv; }
    public void logout() { this.currentUser = null; }
    public boolean isLoggedIn() { return currentUser != null; }
    public boolean isAdmin()    { return currentUser != null && "Admin".equalsIgnoreCase(currentUser.getVaiTro()); }
    public boolean isManager()  { return currentUser != null && "Manager".equalsIgnoreCase(currentUser.getVaiTro()); }
    public boolean isStaff()    { return currentUser != null && "Staff".equalsIgnoreCase(currentUser.getVaiTro()); }
}
