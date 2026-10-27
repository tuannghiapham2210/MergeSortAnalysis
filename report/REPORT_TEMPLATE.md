# BÁO CÁO THÍ NGHIỆM: KHẢO SÁT GIÁ TRỊ CUTOFF TỐI ƯU CHO MERGESORT
**Môn học:** Cấu trúc dữ liệu và giải thuật (DSA)  
**Sinh viên thực hiện:** [Họ và tên] - [MSSV]  
**Giảng viên hướng dẫn:** [Tên Thầy/Cô]  

---

## 1. GIỚI THIỆU & MỤC TIÊU THÍ NGHIỆM
- **Bối cảnh:** MergeSort có độ phức tạp thời gian lý thuyết là $O(N \log N)$. Tuy nhiên, với các mảng con có kích thước nhỏ, chi phí gọi đệ quy (overhead of recursive calls) và việc cấp phát/sao chép mảng phụ làm giảm hiệu năng thực tế.
- **Kỹ thuật cải tiến CUTOFF:** Giáo trình *Algorithms 4th Edition* (Robert Sedgewick & Kevin Wayne) đề xuất cải tiến: khi kích thước mảng con $\le$ `CUTOFF`, chuyển sang sử dụng `InsertionSort`.
- **Mục tiêu:**
  - So sánh hiệu năng giữa MergeSort chuẩn và MergeSort có CUTOFF.
  - Khảo sát các mốc CUTOFF từ 0 đến 30 (chú trọng mốc 7 và 10 được nêu trong giáo trình).
  - Tìm giá trị CUTOFF tối ưu nhất cho cấu hình máy tính thực nghiệm.

---

## 2. THIẾT KẾ THÍ NGHIỆM (ALGORITHM ANALYSIS)
### 2.1. Cấu hình phần cứng và môi trường chạy
- **CPU:** 
- **RAM:** 
- **Hệ điều hành:** 
- **Java Virtual Machine (JVM):** 
- **Các tham số JVM (nếu có):** 

### 2.2. Dữ liệu thử nghiệm (Input Datasets)
- **Kích thước mảng ($N$):** $10^4, 10^5, 10^6, 10^7$
- **Các dạng phân bố:**
  1. *Random:* Các phần tử ngẫu nhiên phân bố đều.
  2. *Sorted:* Mảng đã được sắp xếp tăng dần.
  3. *Reversed:* Mảng có thứ tự giảm dần hoàn toàn.
  4. *Duplicates:* Mảng có số lượng giá trị duy nhất nhỏ (nhiều phần tử trùng lặp).

### 2.3. Phương pháp đo lường & Giảm thiểu sai số (Noise Reduction)
- **JVM Warm-up:** Chạy thử nghiệm mồi (warm-up runs) để JIT compiler tối ưu bytecode trước khi bắt đầu bấm giờ.
- **Số lần lặp (Trials):** Chạy lặp lại $K = 10$ hoặc $20$ lần cho mỗi cấu hình `(N, CUTOFF, Type)`.
- **Chỉ số thống kê:** Tính thời gian trung bình (Mean Execution Time) và Độ lệch chuẩn (Standard Deviation).

---

## 3. KẾT QUẢ THÍ NGHIỆM & BẢNG SỐ LIỆU
*(Chèn bảng số liệu tổng hợp kết quả tại đây)*

| Kích thước N | Dạng dữ liệu | CUTOFF = 0 (Chuẩn) | CUTOFF = 7 | CUTOFF = 10 | CUTOFF tối ưu | Thời gian nhỏ nhất (ms) |
|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| $10^5$ | Random | ... | ... | ... | ... | ... |
| $10^6$ | Random | ... | ... | ... | ... | ... |
| $10^6$ | Sorted | ... | ... | ... | ... | ... |
| $10^6$ | Reversed | ... | ... | ... | ... | ... |

---

## 4. BIỂU ĐỒ & PHÂN TÍCH KẾT QUẢ
*(Chèn các hình ảnh biểu đồ từ thư mục `report/figures/`)*

### 4.1. Đồ thị tương quan giữa CUTOFF và Thời gian chạy
![Đồ thị CUTOFF vs Time](figures/cutoff_vs_time.png)

### 4.2. Phân tích chi tiết
- **Hiệu quả của CUTOFF so với MergeSort nguyên bản (CUTOFF = 0):** ...
- **So sánh giữa mốc CUTOFF = 7 và CUTOFF = 10:** ...
- **Vùng cực tiểu (Optimal Region):** Giá trị CUTOFF đạt hiệu năng tốt nhất nằm trong khoảng nào?
- **Nguyên nhân vật lý / kiến trúc máy tính:** Giải thích sự cân bằng giữa chi phí đệ quy của MergeSort và chi phí $O(k^2)$ của Insertion Sort trên mảng nhỏ, tính cục bộ của bộ nhớ cache (CPU Cache locality).

---

## 5. KẾT LUẬN & ĐỀ XUẤT
- Tóm tắt kết quả tìm được: Giá trị CUTOFF tốt nhất trên máy thực nghiệm là bao nhiêu?
- Khuyến nghị giá trị CUTOFF nên sử dụng trong các bài toán thực tế.
