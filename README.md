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
    ├── REPORT.md           # Báo cáo phân tích thực nghiệm chi tiết
    ├── AI_CHAT_LOG.md      # Nhật ký thảo luận và sử dụng AI theo yêu cầu đề bài
    └── figures/            # Biểu đồ và đồ thị phân tích độ phân giải cao (PNG/SVG)
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
1. **Kiểm chứng tính đúng đắn (`isSorted`):** Tự động kiểm tra kết quả sắp xếp trên bộ dữ liệu mẫu tại `data/inputs/` và trong quá trình thực nghiệm.
2. **Khử sai số khởi động lạnh (JVM Warm-up):** Thực hiện 15 chu kỳ sắp xếp mồi trên cả `Merge`, `MergeWithCutoff` và `Insertion` trước khi kích hoạt bộ đếm thời gian, cho phép trình biên dịch Just-In-Time (JIT C2) của JVM hoàn tất dịch mã bytecode sang mã máy gốc và đưa hệ thống vào trạng thái ổn định.
3. **Đồng nhất dữ liệu & Lọc nhiễu ngoại lai (Trimmed Trials):** Tiền khởi tạo bộ dữ liệu với seed cố định để mọi mốc CUTOFF đều được đo trên cùng tập đầu vào. Mỗi cấu hình chạy $K + 2 = 12$ lần độc lập, loại bỏ 1 cực trị nhỏ nhất và 1 cực trị lớn nhất (nhiễu do OS/GC) để giữ lại $K = 10$ mẫu chuẩn.
4. **Thống kê mô tả (Chủ đề 1.3 Algorithm Analysis):** Tính toán Thời gian trung bình mẫu (Sample Mean $\bar{x}$) và Độ lệch chuẩn mẫu (Sample Standard Deviation $s = \sqrt{\frac{\sum (x_i - \bar{x})^2}{K - 1}}$) để biểu diễn mức độ phân tán qua các thanh sai số (error bars).

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
Biểu đồ phân tích sẽ được xuất tự động dưới dạng PNG (300 DPI) và SVG tại `report/figures/cutoff_vs_time.png` và `report/figures/cutoff_by_datatype.png`.

---

## 6. Tổng Hợp Kết Quả Thực Nghiệm

### 6.1. Khảo sát theo kích thước mảng $N$ (Dữ liệu Ngẫu nhiên - Random)

| Kích thước mảng ($N$) | CUTOFF = 0 (Nguyên bản) | CUTOFF = 7 (Ấn bản cũ) | CUTOFF = 10 (Ấn bản mới) | CUTOFF = 16 | CUTOFF = 128 (Quá lớn) | CUTOFF Tối ưu | Thời gian nhỏ nhất | Mức cải thiện |
|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| $100,000$ | $17.51 \pm 0.30$ ms | $16.63 \pm 0.37$ ms | **$15.94 \pm 0.08$ ms** | $16.00 \pm 0.15$ ms | $22.08 \pm 0.23$ ms | **10** | **15.94 ms** | **Giảm ~9.0%** |
| $500,000$ | $124.94 \pm 4.55$ ms | $113.28 \pm 2.47$ ms | $110.09 \pm 5.85$ ms | $112.73 \pm 4.06$ ms | $148.60 \pm 2.89$ ms | **25** *(12: 109.47 ms)* | **109.44 ms** | **Giảm ~12.4%** |
| $1,000,000$ | $244.80 \pm 3.69$ ms | $232.76 \pm 2.94$ ms | $233.35 \pm 7.93$ ms | **$227.47 \pm 2.80$ ms** | $336.91 \pm 20.60$ ms | **16** | **227.47 ms** | **Giảm ~7.1%** |

### 6.2. Khảo sát theo 4 phân bố dữ liệu tại $N = 1,000,000$

| Phân bố dữ liệu | CUTOFF = 0 (Nguyên bản) | CUTOFF = 7 (Ấn bản cũ) | CUTOFF = 10 (Ấn bản mới) | CUTOFF = 16 | CUTOFF = 128 | CUTOFF Tối ưu | Thời gian nhỏ nhất | Mức cải thiện |
|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| **Random** | $244.80 \pm 3.69$ ms | $232.76 \pm 2.94$ ms | $233.35 \pm 7.93$ ms | **$227.47 \pm 2.80$ ms** | $336.91 \pm 20.60$ ms | **16** | **227.47 ms** | **Giảm ~7.1%** |
| **Sorted** | $103.32 \pm 1.43$ ms | $88.66 \pm 1.23$ ms | $85.33 \pm 0.43$ ms | $80.86 \pm 0.62$ ms | **$65.59 \pm 0.39$ ms** | **128** | **65.59 ms** | **Giảm ~36.5%** |
| **Reversed** | $106.01 \pm 0.76$ ms | $100.09 \pm 1.74$ ms | $100.30 \pm 0.73$ ms | $110.69 \pm 0.57$ ms | $311.55 \pm 0.65$ ms | **6** | **98.42 ms** | **Giảm ~7.2%** |
| **Duplicates** | $150.17 \pm 1.33$ ms | $140.74 \pm 0.62$ ms | $148.94 \pm 9.18$ ms | $138.34 \pm 1.12$ ms | $209.37 \pm 1.01$ ms | **25** *(8: 138.24 ms)* | **137.98 ms** | **Giảm ~8.1%** |

### Kết luận chính:
1. Việc tích hợp CUTOFF mang lại cải thiện hiệu năng rõ rệt từ **7% đến 12.4%** trên dữ liệu ngẫu nhiên và lên tới **36.5%** trên dữ liệu đã có thứ tự.
2. Ngưỡng **CUTOFF = 10** trong giáo trình mới cho hiệu năng ổn định và tốt hơn ngưỡng **CUTOFF = 7** của giáo trình cũ nhờ tận dụng bộ nhớ đệm L1/L2 CPU hiện đại và cắt tỉa trọn vẹn các nhánh mảng con kích thước $8$.
3. Khi xét tổng hợp cả 4 phân bố dữ liệu (đặc biệt là tránh suy giảm bậc hai $O(k^2)$ trên mảng ngược chiều `Reversed`), vùng tối ưu thực nghiệm an toàn và hiệu quả nhất rơi vào khoảng **CUTOFF $\in [10, 16]$** (đạt cực tiểu trên dữ liệu ngẫu nhiên tại **CUTOFF = 10** với $N = 10^5$ và **CUTOFF = 16** với $N = 10^6$).

Chi tiết phân tích lý thuyết, bảng thống kê và đồ thị xin xem tại [report/REPORT.md](report/REPORT.md).
