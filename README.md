# Khảo Sát và Thực Nghiệm Xác Định Ngưỡng CUTOFF Tối Ưu cho Thuật Toán MergeSort

Dự án thực nghiệm phân tích hiệu năng của kỹ thuật cải tiến chuyển đổi sang **Insertion Sort** đối với mảng con có kích thước nhỏ (**CUTOFF**) trong thuật toán **MergeSort**, dựa trên nền tảng lý thuyết và bài giảng *Algorithms, 4th Edition* (Robert Sedgewick & Kevin Wayne, Đại học Princeton).

---

## 1. Giới thiệu và Mục tiêu Nghiên cứu

Trong phân tích thuật toán, MergeSort đạt độ phức tạp thời gian lý thuyết tối ưu là $O(N \log N)$. Tuy nhiên, trong triển khai thực tế trên các kiến trúc máy tính hiện đại, việc phân rã đệ quy đến các mảng con kích thước siêu nhỏ ($N \le 1$) phát sinh chi phí overhead đáng kể từ việc quản lý khung ngăn xếp gọi hàm (call stack) và thao tác sao chép dữ liệu sang mảng phụ `aux[]`.

Kỹ thuật CUTOFF giải quyết vấn đề này bằng cách chuyển sang Insertion Sort khi kích thước mảng con nhỏ hơn hoặc bằng một ngưỡng xác định. Trong các ấn bản giáo trình, các tác giả từng đề xuất ngưỡng CUTOFF = 7 (ấn bản cũ) và CUTOFF = 10 (ấn bản mới). Mục tiêu của nghiên cứu thực nghiệm này bao gồm:
- Hiện thực hóa thuật toán MergeSort và Insertion Sort tuân thủ chặt chẽ nội dung bài giảng của GS. Robert Sedgewick.
- Áp dụng phương pháp phân tích thực nghiệm thuật toán (Chủ đề 1.3: Algorithm Analysis) để đo lường, đối chiếu hiệu năng giữa các mốc CUTOFF (đặc biệt là 7 và 10).
- Khảo sát sự biến thiên của thời gian thực thi trên nhiều kích thước mảng $N$ và đa dạng phân bố dữ liệu (ngẫu nhiên, có thứ tự, nghịch đảo, trùng lặp).
- Xác định thực nghiệm ngưỡng CUTOFF tối ưu tương ứng với cấu hình phần cứng và môi trường thực thi cụ thể.

---

## 2. Cấu trúc Thư mục Dự án

```text
MergeSortAnalysis/
├── .gitignore              # Cấu hình loại trừ các tệp nhị phân và dữ liệu tạm
├── README.md               # Tài liệu tổng quan và hướng dẫn thực nghiệm
│
├── src/                    # Mã nguồn hiện thực thuật toán (Java)
│   ├── Merge.java          # Cài đặt MergeSort nguyên bản theo giáo trình
│   ├── MergeWithCutoff.java# Cài đặt MergeSort tích hợp tham số CUTOFF
│   └── Insertion.java      # Cài đặt Insertion Sort trên đoạn con [lo..hi]
│
├── benchmark/              # Công cụ phục vụ thực nghiệm và phân tích tự động
│   ├── DataGenerator.java  # Lớp sinh dữ liệu kiểm thử theo các phân bố khác nhau
│   ├── BenchmarkRunner.java# Chương trình đo lường thời gian, tính toán Mean và StdDev
│   └── plot_results.py     # Kịch bản trực quan hóa số liệu thực nghiệm (Seaborn/Matplotlib)
│
├── data/                   # Dữ liệu thực nghiệm
│   ├── inputs/             # Tập dữ liệu kiểm thử mẫu (tiny, random, sorted, reversed, duplicates)
│   └── results/            # Kết quả đo lường thô lưu trữ dưới định dạng CSV
│
└── report/                 # Báo cáo và sản phẩm nghiên cứu
    ├── REPORT_TEMPLATE.md  # Báo cáo phân tích thực nghiệm chi tiết
    └── figures/            # Biểu đồ và đồ thị phân tích độ phân giải cao
```

---

## 3. Đặc tả Môi trường Thực Nghiệm

Các phép đo trong nghiên cứu được tiến hành trên hệ thống phần cứng và phần mềm cụ thể sau:
- Bộ xử lý (CPU): AMD Ryzen 9 8945HX (16 Cores, 32 Threads)
- Bộ nhớ trong (RAM): 32 GB
- Hệ điều hành: Microsoft Windows 11 Home 64-bit
- Môi trường thực thi Java: OpenJDK 25.0.2 LTS (Temurin-25.0.2+10)
- Môi trường phân tích dữ liệu: Python 3.13.5 (thư viện: Pandas, Seaborn, Matplotlib)

---

## 4. Phương pháp Thực Nghiệm (Algorithm Analysis)

Để triệt tiêu tối đa các nguồn sai số ngẫu nhiên từ hệ điều hành và môi trường thực thi, quy trình đo lường tuân thủ các nguyên tắc khoa học sau:
1. **Khử sai số khởi động lạnh (JVM Warm-up):** Thực hiện 5 chu kỳ sắp xếp mồi trước khi kích hoạt bộ đếm thời gian, cho phép trình biên dịch Just-In-Time (JIT) của JVM hoàn tất dịch mã bytecode sang mã máy gốc và đưa hệ thống vào trạng thái ổn định.
2. **Lặp lại nhiều lần (Trials = 10):** Mỗi cấu hình thực nghiệm được đo lặp lại 10 lần độc lập. Sử dụng bộ phát số ngẫu nhiên với seed cố định để đảm bảo tính tái lập (reproducibility).
3. **Thống kê mô tả:** Tính toán Thời gian trung bình số học (Mean) và Độ lệch chuẩn (Standard Deviation) để biểu diễn mức độ phân tán của dữ liệu qua các thanh sai số (error bars).

---

## 5. Quy Trình Biên Dịch và Tái Lập Thí Nghiệm

### Bước 1: Biên dịch toàn bộ mã nguồn Java
```bash
javac -d bin src/*.java benchmark/*.java
```

### Bước 2: Sinh các tập dữ liệu kiểm thử mẫu (tùy chọn)
```bash
java -cp bin DataGenerator
```

### Bước 3: Thực thi chương trình đo lường benchmark
```bash
java -cp bin BenchmarkRunner
```
Kết quả đo lường sẽ được lưu tự động tại `data/results/benchmark_results.csv`.

### Bước 4: Trực quan hóa số liệu
```bash
py benchmark/plot_results.py
```
Biểu đồ phân tích sẽ được xuất tự động dưới dạng PNG độ phân giải cao (300 DPI) tại `report/figures/cutoff_vs_time.png`.

---

## 6. Tổng Hợp Kết Quả Thực Nghiệm

Dưới đây là số liệu tóm tắt đối với dữ liệu ngẫu nhiên (phép đo trung bình 10 lần độc lập):

| Kích thước mảng ($N$) | CUTOFF = 0 (Nguyên bản) | CUTOFF = 7 (Ấn bản cũ) | CUTOFF = 10 (Ấn bản mới) | CUTOFF Tối ưu | Thời gian nhỏ nhất | Mức cải thiện |
|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| $100,000$ | $16.99 \pm 0.33$ ms | $16.27 \pm 0.08$ ms | $16.53 \pm 0.08$ ms | **25** | **15.45 ms** | Giảm ~9.1% |
| $500,000$ | $104.44 \pm 2.68$ ms | $106.55 \pm 3.08$ ms | $103.27 \pm 2.24$ ms | **8** | **101.95 ms** | Giảm ~2.4% |
| $1,000,000$ | $244.78 \pm 4.12$ ms | $256.53 \pm 5.71$ ms | $248.35 \pm 12.06$ ms | **16** | **223.00 ms** | **Giảm ~8.9%** |

### Kết luận chính:
1. Việc tích hợp CUTOFF mang lại cải thiện hiệu năng rõ rệt trên mảng kích thước lớn (tiết kiệm gần 22 ms, tương đương ~8.9% thời gian chạy trên mảng 1 triệu phần tử).
2. Ngưỡng CUTOFF = 10 trong giáo trình mới cho hiệu năng vượt trội hơn ngưỡng CUTOFF = 7 của giáo trình cũ trên kiến trúc CPU hiện đại.
3. Vùng tối ưu thực nghiệm trên cấu hình phần cứng khảo sát rơi vào khoảng **CUTOFF $\in [12, 20]$**, đạt giá trị tối ưu tại **CUTOFF = 16**.
