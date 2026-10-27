# Bộ dữ liệu thí nghiệm (Experimental Datasets)

Thư mục này chứa các bộ dữ liệu kiểm thử thực tế phục vụ khảo sát và đánh giá thuật toán MergeSort và cải tiến CUTOFF theo yêu cầu đề bài.

---

## 📄 Định dạng dữ liệu (Data Format)
Định dạng file tuân theo chuẩn giáo trình *Algorithms 4th Edition* (Robert Sedgewick & Kevin Wayne):
* **Dòng đầu tiên:** Chứa số nguyên dương $N$ (Số lượng phần tử trong mảng).
* **$N$ dòng tiếp theo:** Mỗi dòng chứa một số nguyên là giá trị của một phần tử.

---

## 📊 Danh sách các bộ dữ liệu mẫu trong thư mục

| Tên file | Kích thước $N$ | Dạng phân bố dữ liệu | Mô tả |
|---|:---:|---|---|
| `tiny.txt` | 15 | Mảng nhỏ mẫu | Dùng để chạy thử nghiệm nhanh, theo dõi từng bước chia và trộn mảng (Trace/Debug). |
| `random_10k.txt` | 10,000 | Ngẫu nhiên đều (Uniform Random) | Các số nguyên ngẫu nhiên từ $0$ đến $10^9$, sinh với seed cố định (`42`). |
| `sorted_10k.txt` | 10,000 | Đã sắp xếp tăng dần (Sorted) | Mảng $0, 1, 2, \dots, N-1$ (kiểm tra trường hợp tốt nhất hoặc tính ổn định). |
| `reversed_10k.txt` | 10,000 | Giảm dần ngược chiều (Reversed) | Mảng $N-1, N-2, \dots, 0$ (trường hợp nghịch đảo tối đa). |
| `duplicates_10k.txt` | 10,000 | Trùng lặp nhiều (Duplicates) | Các số nguyên chỉ nằm trong khoảng $[0, 99]$, có rất nhiều giá trị trùng nhau. |

---

## 🛠 Cách tái tạo hoặc sinh thêm dữ liệu mới
Bạn có thể tự động sinh lại hoặc tạo thêm các bộ dữ liệu kích thước lớn hơn bằng lệnh:
```bash
java -cp bin DataGenerator
```
Lớp `DataGenerator.java` cũng cung cấp hai phương thức tiện ích `writeToFile()` và `readFromFile()` để đọc/ghi mảng trực tiếp từ mã nguồn Java.
