# Tập Dữ Liệu Kiểm Thử Thực Nghiệm (Experimental Datasets)

Thư mục này lưu trữ các bộ dữ liệu kiểm thử thực nghiệm phục vụ việc khảo sát, phân tích hiệu năng của thuật toán MergeSort và đánh giá kỹ thuật tối ưu hóa CUTOFF sang Insertion Sort theo yêu cầu của đề tài.

---

## 1. Đặc Tả Định Dạng Dữ Liệu

Định dạng tệp dữ liệu tuân thủ quy ước chuẩn trong giáo trình *Algorithms, 4th Edition* (Robert Sedgewick & Kevin Wayne):
* **Dòng đầu tiên:** Chứa một số nguyên dương $N$ duy nhất, biểu diễn số lượng phần tử của mảng.
* **$N$ dòng tiếp theo:** Mỗi dòng chứa một số nguyên biểu diễn giá trị của từng phần tử trong mảng tương ứng.

---

## 2. Danh Mục Các Tập Dữ Liệu Mẫu

| Tên tệp | Kích thước ($N$) | Phân bố dữ liệu | Mục đích thực nghiệm |
|---|:---:|---|---|
| `tiny.txt` | 15 | Mẫu kiểm chứng (Tracing test) | Sử dụng cho việc kiểm thử tính đúng đắn, debug và theo dõi chi tiết từng bước phân rã - trộn mảng. |
| `random_10k.txt` | 10,000 | Ngẫu nhiên đều (Uniform Random) | Dữ liệu kiểm thử tổng quát, các phần tử phân bố ngẫu nhiên đều trong miền $[0, 10^9]$ với giá trị khởi tạo `seed = 42`. |
| `sorted_10k.txt` | 10,000 | Sắp xếp tăng dần (Sorted) | Mảng có thứ tự tăng dần hoàn toàn $a[i] = i$, phục vụ khảo sát trường hợp tốt nhất và tính ổn định (stability) của thuật toán. |
| `reversed_10k.txt` | 10,000 | Nghịch thế hoàn toàn (Reversed) | Mảng có thứ tự giảm dần hoàn toàn $a[i] = N - 1 - i$, đại diện cho trường hợp có số lượng cặp nghịch thế đạt cực đại. |
| `duplicates_10k.txt` | 10,000 | Trùng lặp cao (High-multiplicity) | Mảng chỉ nhận các giá trị trong phạm vi hẹp $[0, 99]$, tạo ra mật độ phần tử trùng lặp cao nhằm khảo sát ảnh hưởng của các khóa bằng nhau. |

---

## 3. Quy Trình Tái Lập và Khởi Tạo Dữ Liệu

Toàn bộ các tập dữ liệu trên có thể được tái lập hoàn toàn (reproducible) bằng cách thực thi lớp tiện ích `DataGenerator`:

```bash
java -cp bin DataGenerator
```

Lớp `DataGenerator.java` cung cấp sẵn hai phương thức tĩnh chuẩn để tương tác với dữ liệu:
* `DataGenerator.writeToFile(Integer[] a, String filePath)`: Xuất cấu trúc mảng ra tệp theo đặc tả định dạng chuẩn.
* `DataGenerator.readFromFile(String filePath)`: Đọc và phân giải tệp dữ liệu thành mảng đối tượng `Integer[]`.
