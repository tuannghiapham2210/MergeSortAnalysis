# Nghiên cứu và Thực nghiệm giá trị CUTOFF tối ưu cho MergeSort

Dự án thực nghiệm thuật toán phân tích hiệu năng của cải tiến **CUTOFF sang Insertion Sort** trong thuật toán **MergeSort**, dựa trên giáo trình và bài giảng *Algorithms, 4th Edition* (Robert Sedgewick & Kevin Wayne - Princeton University).

---

## 📌 1. Mục tiêu đề tài
- Cài đặt thuật toán MergeSort chuẩn theo slide bài giảng của GS. Robert Sedgewick.
- Cải tiến thuật toán bằng kỹ thuật chuyển sang **Insertion Sort** khi kích thước mảng con $\le$ `CUTOFF`.
- Thiết kế thí nghiệm khoa học theo phương pháp thực nghiệm (Chủ đề 1.3: *Algorithm Analysis*):
  - Khảo sát sự thay đổi thời gian chạy theo các giá trị `CUTOFF` khác nhau (đặc biệt đối chiếu giữa mốc 7 và 10).
  - Khảo sát trên nhiều kích thước mảng $N$ và các phân bố dữ liệu (ngẫu nhiên, đã sắp xếp, ngược chiều, trùng lặp).
  - Thu thập số liệu, tính trung bình (mean), độ lệch chuẩn (std dev) và vẽ đồ thị trực quan.
- Xác định ngưỡng `CUTOFF` tối ưu tương ứng với cấu hình phần cứng và môi trường thực thi máy cá nhân.

---

## 📂 2. Cấu trúc thư mục dự án

```text
MergeSortAnalysis/
├── .gitignore              # Bỏ qua các file nhị phân, file build và rác hệ thống
├── README.md               # Tài liệu hướng dẫn tổng quan của dự án
│
├── src/                    # Mã nguồn thuật toán (Java)
│   ├── Merge.java          # Cài đặt MergeSort nguyên bản theo slide
│   ├── MergeCutoff.java    # MergeSort cải tiến có tham số CUTOFF
│   └── Insertion.java      # Cài đặt InsertionSort cho mảng con [lo, hi]
│
├── benchmark/              # Các công cụ phục vụ thực nghiệm tự động
│   ├── DataGenerator.java  # Sinh dữ liệu kiểm thử theo các phân bố khác nhau
│   ├── BenchmarkRunner.java# Chạy thí nghiệm đo thời gian nhiều lần, xuất CSV
│   └── plot_results.py     # Script Python đọc CSV và vẽ đồ thị trực quan
│
├── data/                   # Dữ liệu phục vụ thí nghiệm
│   ├── inputs/             # Bộ test đầu vào
│   └── results/            # Kết quả đo thô (file CSV)
│
└── report/                 # Báo cáo thực nghiệm (Deliverable)
    ├── Report.pdf          # Báo cáo hoàn chỉnh nộp bài
    ├── REPORT_TEMPLATE.md  # Khung đề cương báo cáo chi tiết
    └── figures/            # Đồ thị, biểu đồ trực quan xuất từ thí nghiệm
```

---

## 💻 3. Môi trường và Cấu hình máy thử nghiệm
*(Cập nhật thông số máy thực tế của bạn khi chạy thí nghiệm)*
- **CPU:** ...
- **RAM:** ...
- **Hệ điều hành:** Windows 11 / macOS / Linux
- **Java Version:** OpenJDK 25 (hoặc phiên bản bạn sử dụng)
- **Python Version:** 3.12+ (dùng vẽ đồ thị `matplotlib`, `pandas`)

---

## 🚀 4. Hướng dẫn biên dịch và chạy thí nghiệm

### Bước 1: Biên dịch mã nguồn Java
```bash
javac -d bin src/*.java benchmark/*.java
```

### Bước 2: Chạy thí nghiệm đo đạc
```bash
java -cp bin BenchmarkRunner
```
Kết quả đo sẽ được tự động ghi ra file `data/results/benchmark_results.csv`.

### Bước 3: Vẽ biểu đồ trực quan
```bash
python benchmark/plot_results.py
```
Các hình ảnh đồ thị phân tích sẽ được lưu tự động vào thư mục `report/figures/`.
