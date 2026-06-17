# Hệ thống Quản lý Ca làm việc và Chấm công Nhân sự (Nhóm 8)

Đây là dự án môn học **Thực tập cơ sở** của **Nhóm 8**. Hệ thống được xây dựng nhằm mục đích số hóa quy trình phân ca, quản lý nhân sự và tự động hóa việc chấm công cho các cửa hàng, doanh nghiệp vừa và nhỏ, khắc phục những hạn chế của phương pháp quản lý thủ công (giấy tờ, excel).

## Thành viên nhóm

- **Tuấn Kiệt (Trưởng nhóm)**: Quản lý tiến độ, tích hợp hệ thống, Code module Đăng nhập (F1), Xuất báo cáo Excel (F3.4), Kiểm thử tổng thể và làm tài liệu.
- **Huy (Developer)**: Xây dựng toàn bộ giao diện Nhân viên (F4) và Quản lý (F3.1-3.3), hệ thống thông báo, menu cá nhân (UI Java Swing, Service layer, Model class).
- **Khởi (DB Designer)**: Thiết kế & xây dựng toàn bộ CSDL (ERD, SQL Server, Stored Procedures), tầng DAO, màn hình Quản trị Admin (F2).

## Công nghệ sử dụng

- **Ngôn ngữ lập trình**: Java (JDK 8 trở lên)
- **Giao diện người dùng**: Java Swing (Tích hợp thư viện Ikonli/FontAwesome cho icon giao diện)
- **Cơ sở dữ liệu**: Microsoft SQL Server
- **Quản lý dự án / Dependency**: Maven
- **Thư viện nổi bật**:
  - `Apache POI`: Hỗ trợ tổng hợp và xuất báo cáo chấm công ra file Excel.
  - `Ikonli`: Quản lý icon UI (sử dụng FontAwesome).

## Tính năng chính (Functional Requirements)

Hệ thống được chia quyền rõ ràng với 3 vai trò (Role): **Admin**, **Manager**, **Staff**.

### 1. Module Đăng nhập & Phân quyền (F1)

- Đăng nhập với tài khoản/mật khẩu được mã hóa.
- Tự động nhận diện vai trò và điều hướng đến màn hình tương ứng (Admin/Manager/Staff).
- Khóa tài khoản nếu nhập sai quá 5 lần.

### 2. Module Quản trị hệ thống - Admin (F2)

- Quản lý danh sách nhân viên (Thêm, Sửa, Khóa tài khoản, Cấp quyền).
- Cấu hình danh mục ca làm việc (Tên ca, Giờ bắt đầu, Giờ kết thúc).

### 3. Module Quản lý lịch - Manager (F3)

- Xếp lịch, phân ca cho nhân viên theo ngày/tuần.
- Xem và phê duyệt/từ chối các yêu cầu đổi ca từ nhân viên.
- Xem lịch làm việc tổng quan của toàn bộ nhân sự.
- Tổng hợp số giờ làm, số ca làm, đi trễ/về sớm và **Xuất báo cáo Timesheet (Excel)** cho bộ phận kế toán.

### 4. Module Nghiệp vụ Nhân viên - Staff (F4)

- Xem lịch làm việc cá nhân.
- **Chấm công (Check-in / Check-out)** theo thời gian thực (Real-time).
- Tạo form gửi yêu cầu đổi ca làm việc đến Manager.

## Hướng dẫn cài đặt và chạy dự án

### Yêu cầu hệ thống:

- Đã cài đặt **Java JDK 8+** và cấu hình biến môi trường.
- Đã cài đặt **Microsoft SQL Server** (và SQL Server Management Studio).
- IDE khuyên dùng: **VS Code** (kèm Extension Java), **IntelliJ IDEA**, hoặc **Eclipse**.

### Các bước cài đặt:

1.  **Clone dự án về máy**:

    ```bash
    git clone <link-repo-github>
    cd temp_proj_for_AI
    ```

2.  **Thiết lập Cơ sở dữ liệu (Database)**:
    - Mở SQL Server Management Studio (SSMS).
    - Mở file `src/main/database.sql` và chạy toàn bộ script để tạo bảng, quan hệ (ERD).
      -Chạy tiếp các script tạo Stored Procedures để đảm bảo các module (như Đăng nhập `sp_Login`, Đổi ca) hoạt động bình thường.
    - Chạy file `src/main/data_insert.sql` để nạp dữ liệu mẫu.

3.  **Cấu hình kết nối DB**:
    - Copy file `src/main/resources/db.properties.example` và đổi tên thành `db.properties`.
    - Mở file `db.properties` và cập nhật thông tin kết nối SQL Server của bạn:
      ```properties
      db.url=jdbc:sqlserver://localhost:1433;databaseName=Ten_Database;encrypt=true;trustServerCertificate=true
      db.user=sa
      db.password=MatKhauCuaBan
      ```

4.  **Tải thư viện Maven**:
    - Mở dự án bằng IDE, chuột phải vào file `pom.xml` chọn **Reload / Update Project** để Maven tự động tải về các thư viện cần thiết (`Apache POI`, `Ikonli`, `mssql-jdbc`).

5.  **Chạy ứng dụng**:
    - Tìm đến file `src/main/java/vn/edu/nhom8/Main.java`.
    - Nhấn **Run** để khởi động màn hình Đăng nhập (LoginFrame).

## Quy chuẩn Code (Convention)

Dự án áp dụng quy chuẩn chặt chẽ để đảm bảo source code rõ ràng và thống nhất:

- **Tên Class / Interface**: `PascalCase` (VD: `NhanVienDAO`, `AdminFrame`).
- **Tên Hàm (Method) / Biến (Variable)**: `camelCase` (VD: `loadData()`, `maNhanVien`).
- **Hằng số (Constant)**: `UPPER_SNAKE_CASE` (VD: `MAX_LOGIN_ATTEMPT`).
- Tuyệt đối **không dùng tiếng Việt có dấu** trong việc đặt tên biến/hàm.
- Comment code rõ ràng (JavaDoc) cho các phương thức xử lý logic phức tạp.
