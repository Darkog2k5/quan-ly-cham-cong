# QUY ƯỚC LÀM VIỆC NHÓM 8 - DỰ ÁN QUẢN LÝ CA LÀM VIỆC

## 1. Phân chia Cấu trúc Package

- `vn.edu.nhom8.model`: Chứa 5 class Model (Huy phụ trách viết theo ERD của Khởi).
- `vn.edu.nhom8.dao`: Chứa Interface DAO và class kết nối CSDL thực tế (Khởi phụ trách).
- `vn.edu.nhom8.service`: Chứa logic nghiệp vụ, xử lý tính toán trước khi đẩy lên giao diện (Huy phụ trách).
- `vn.edu.nhom8.ui`: Chứa các màn hình JFrame/JPanel (Kiệt làm Login, Huy làm Manager/Staff, Khởi làm Admin).
- `vn.edu.nhom8.util`: Chứa `DBConnection.java` (Khởi làm), các hàm tiện ích như `ExcelExporter` (Kiệt làm).

## 2. Quy tắc luồng gọi Code (Tuyệt đối tuân thủ)

- **UI -> Service -> DAO -> Database**. Tuyệt đối không ai được viết câu lệnh SQL (`SELECT`, `INSERT`) trực tiếp bên trong class giao diện (UI).
- **Interface First:** Khởi phải viết các file Interface (VD: `IChamCongDAO`) trước để chốt tên hàm, tham số và kiểu trả về. Huy và Kiệt sẽ đọc Interface đó để làm tiếp, không cần đợi Khởi code xong logic bên trong.

## 3. Quy tắc Đặt tên (Naming Convention)

- **Class:** `PascalCase` (VD: `NhanVien`, `ChamCongDao`).
- **Biến & Hàm:** `camelCase` (VD: `maNhanVien`, `getListNhanVien()`).
- **Hằng số:** `UPPER_SNAKE_CASE` (VD: `MAX_LOGIN_RETRY`).
- **Tên method DAO chuẩn:** `insert()`, `update()`, `deactivate()` (để xóa mềm), `findById()`, `findAll()`.

## 4. Bảo mật & Database

- Không lưu plaintext password.
- File cấu hình DB phải nằm ở `src/main/resources/db.properties`. Không ai được hard-code chuỗi kết nối (connection string) vào source code Java.

## 5. Quy tắc Git & Họp nhóm

- Nhánh làm việc: `feature/ten-module` (VD: `feature/huy-ui-staff`).
- KHÔNG push thẳng code vào nhánh `main`. Phải tạo Pull Request (PR) để Kiệt review.
