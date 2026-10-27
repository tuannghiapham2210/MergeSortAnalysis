import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * Runner thực hiện đo đạc và phân tích thực nghiệm thuật toán (Algorithm Analysis).
 * Hỗ trợ JVM Warm-up, lặp nhiều lần (Trials) để tính trung bình và ghi kết quả ra file CSV.
 */
public class BenchmarkRunner {

    // Số lần lặp lại cho mỗi phép đo để lấy trung bình
    private static final int TRIALS = 10;
    
    // Số lần warm-up JVM trước khi đo chính thức
    private static final int WARMUP_ROUNDS = 5;

    /**
     * Hàm điều phối toàn bộ quá trình thực nghiệm thuật toán (Algorithm Analysis):
     * 1. Thiết lập các thông số thực nghiệm (kích thước mảng N, danh sách CUTOFF, số lần lặp trials).
     * 2. Kích hoạt JVM Warm-up để đưa môi trường thực thi về trạng thái ổn định.
     * 3. Tiến hành các vòng lặp đo đạc thời gian bằng System.nanoTime().
     * 4. Tính toán các chỉ số thống kê (Thời gian trung bình và Độ lệch chuẩn).
     * 5. In bảng kết quả ra console và ghi dữ liệu thô ra file CSV để phục vụ vẽ đồ thị.
     *
     * @param args tham số dòng lệnh (không sử dụng)
     */
    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println(" BẮT ĐẦU THÍ NGHIỆM PHÂN TÍCH GIÁ TRỊ CUTOFF CHO MERGESORT");
        System.out.println(" (Algorithms 4th Edition - Robert Sedgewick & Kevin Wayne)");
        System.out.println("======================================================================");

        // Kích thước mảng N thử nghiệm
        int[] testSizes = { 100_000, 500_000, 1_000_000 };

        // Dải giá trị CUTOFF cần khảo sát: 0 (chuẩn), 2, 4, 6, 7, 8, 9, 10, 12, 14, 16, 20, 25, 30
        int[] cutoffs = { 0, 2, 4, 6, 7, 8, 9, 10, 12, 14, 16, 20, 25, 30 };

        // Đảm bảo thư mục kết quả tồn tại
        File resultDir = new File("data/results");
        if (!resultDir.exists()) {
            resultDir.mkdirs();
        }

        String csvPath = "data/results/benchmark_results.csv";

        try (PrintWriter writer = new PrintWriter(new FileWriter(csvPath))) {
            // Header CSV
            writer.println("N,DataType,Cutoff,MeanTimeMs,StdDevMs");

            // JVM Warm-up
            System.out.println("\n[1/3] Đang tiến hành Warm-up JVM (" + WARMUP_ROUNDS + " vòng)...");
            warmUpJVM();
            System.out.println("Warm-up JVM hoàn tất! Bắt đầu đo chính thức...");

            // Bắt đầu chạy thí nghiệm
            for (int n : testSizes) {
                System.out.println("\n-------------------------------------------------------------");
                System.out.printf("Khảo sát kích thước mảng N = %,d (Dữ liệu ngẫu nhiên - Random)\n", n);
                System.out.println("-------------------------------------------------------------");
                System.out.printf("%-10s | %-16s | %-16s\n", "CUTOFF", "Thời gian TB (ms)", "Độ lệch chuẩn (ms)");

                for (int cutoff : cutoffs) {
                    double[] times = new double[TRIALS];

                    for (int trial = 0; trial < TRIALS; trial++) {
                        // Sinh dữ liệu với seed khác nhau ở mỗi trial nhưng cố định theo thứ tự trial
                        Integer[] original = DataGenerator.generateRandom(n, 42L + trial * 1000);
                        Integer[] copy = Arrays.copyOf(original, original.length);

                        long start = System.nanoTime();
                        if (cutoff == 0) {
                            Merge.sort(copy);
                        } else {
                            MergeWithCutoff.sort(copy, cutoff);
                        }
                        long elapsed = System.nanoTime() - start;
                        times[trial] = elapsed / 1_000_000.0; // Đổi sang mili giây (ms)
                    }

                    double mean = calculateMean(times);
                    double stdDev = calculateStdDev(times, mean);

                    System.out.printf("%-10d | %-16.3f | %-16.3f\n", cutoff, mean, stdDev);
                    writer.printf("%d,Random,%d,%.4f,%.4f\n", n, cutoff, mean, stdDev);
                    writer.flush();
                }
            }

            System.out.println("\n======================================================================");
            System.out.println(" THÍ NGHIỆM HOÀN TẤT! Dữ liệu đã được lưu tại: " + csvPath);
            System.out.println("======================================================================");

        } catch (IOException e) {
            System.err.println("Lỗi khi ghi file CSV: " + e.getMessage());
        }
    }

    /**
     * Khởi động và làm nóng máy ảo Java (JVM Warm-up):
     * 
     * Mục đích thực nghiệm:
     * - Trong Java, các lần chạy đầu tiên thường bị chậm do JVM phải nạp lớp (class loading),
     *   khởi tạo bộ nhớ và chạy ở chế độ thông dịch (interpreter mode).
     * - Hàm này thực hiện sắp xếp thử nghiệm một số mảng phụ để kích hoạt trình biên dịch
     *   Just-In-Time (JIT) dịch bytecode sang mã máy gốc (native code) và đưa JVM về trạng thái ổn định (steady-state).
     * - Nhờ đó, các phép đo chính thức sau đó sẽ phản ánh chính xác hiệu năng thuật toán,
     *   loại bỏ được sai số do hiện tượng "khởi động lạnh" (cold-start penalty) của JVM.
     */
    private static void warmUpJVM() {
        for (int i = 0; i < WARMUP_ROUNDS; i++) {
            Integer[] arr1 = DataGenerator.generateRandom(50_000, 1000 + i);
            Integer[] arr2 = Arrays.copyOf(arr1, arr1.length);
            Merge.sort(arr1);
            MergeWithCutoff.sort(arr2, 10);
        }
    }

    /**
     * Tính giá trị trung bình số học (Arithmetic Mean) của thời gian thực thi:
     * 
     * Công thức: Mean (x̄) = (∑ x_i) / K
     * 
     * Giúp làm mượt và giảm thiểu tác động của các sai số ngẫu nhiên trong từng lần đo đơn lẻ.
     *
     * @param values mảng chứa thời gian thực thi của các lần chạy (đơn vị: ms)
     * @return thời gian thực thi trung bình (ms)
     */
    private static double calculateMean(double[] values) {
        double sum = 0;
        for (double v : values) sum += v;
        return sum / values.length;
    }

    /**
     * Tính độ lệch chuẩn (Standard Deviation) của các lần đo:
     * 
     * Công thức: StdDev (s) = √[ ∑ (x_i - x̄)² / K ]
     * 
     * Ý nghĩa thống kê:
     * - Đo lường mức độ phân tán hoặc biến động của thời gian chạy qua các lần đo độc lập.
     * - Độ lệch chuẩn càng nhỏ chứng tỏ phép đo càng ổn định và kết quả càng có độ tin cậy cao.
     * - Giá trị này được sử dụng để vẽ thanh sai số (error bars) trên biểu đồ phân tích.
     *
     * @param values mảng chứa thời gian thực thi của các lần chạy (đơn vị: ms)
     * @param mean thời gian thực thi trung bình đã tính
     * @return độ lệch chuẩn của các lần đo (ms)
     */
    private static double calculateStdDev(double[] values, double mean) {
        double sumSq = 0;
        for (double v : values) sumSq += Math.pow(v - mean, 2);
        return Math.sqrt(sumSq / values.length);
    }
}
