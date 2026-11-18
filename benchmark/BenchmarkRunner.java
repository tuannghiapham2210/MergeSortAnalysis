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
    private static final int WARMUP_ROUNDS = 15;

    /**
     * Hàm điều phối toàn bộ quá trình thực nghiệm thuật toán (Algorithm Analysis):
     * 1. Kiểm chứng tính đúng đắn của thuật toán trên bộ dữ liệu mẫu (data/inputs/).
     * 2. Thiết lập các thông số thực nghiệm (kích thước mảng N, danh sách CUTOFF, phân bố dữ liệu).
     * 3. Kích hoạt JVM Warm-up để đưa môi trường thực thi về trạng thái ổn định (steady-state).
     * 4. Tiến hành các vòng lặp đo đạc thời gian bằng System.nanoTime() trên cùng tập dữ liệu đầu vào.
     * 5. Tính toán các chỉ số thống kê (Thời gian trung bình và Độ lệch chuẩn mẫu).
     * 6. In bảng kết quả ra console và ghi dữ liệu thô ra file CSV để phục vụ vẽ đồ thị.
     *
     * @param args tham số dòng lệnh (không sử dụng)
     */
    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println(" BẮT ĐẦU THÍ NGHIỆM PHÂN TÍCH GIÁ TRỊ CUTOFF CHO MERGESORT");
        System.out.println(" (Algorithms 4th Edition - Robert Sedgewick & Kevin Wayne)");
        System.out.println("======================================================================");

        // Bước 1: Kiểm chứng tính đúng đắn trên bộ dữ liệu mẫu tại data/inputs/
        System.out.println("\n[1/4] Kiểm chứng tính đúng đắn trên bộ dữ liệu mẫu (data/inputs/)...");
        verifySampleInputs();

        // Kích thước mảng N thử nghiệm cho dữ liệu ngẫu nhiên
        int[] testSizes = { 100_000, 500_000, 1_000_000 };

        // Dải giá trị CUTOFF cần khảo sát (mở rộng tới 128 để quan sát rõ đường cong chữ U)
        int[] cutoffs = { 0, 2, 4, 6, 7, 8, 9, 10, 12, 14, 16, 20, 25, 32, 40, 50, 64, 80, 100, 128 };

        // Các dạng phân bố dữ liệu khảo sát chuyên sâu tại N = 1,000,000
        String[] extraDistributions = { "Sorted", "Reversed", "Duplicates" };

        // Đảm bảo thư mục kết quả tồn tại
        File resultDir = new File("data/results");
        if (!resultDir.exists()) {
            resultDir.mkdirs();
        }

        String csvPath = "data/results/benchmark_results.csv";

        try (PrintWriter writer = new PrintWriter(new FileWriter(csvPath))) {
            // Header CSV
            writer.println("N,DataType,Cutoff,MeanTimeMs,StdDevMs");

            // Bước 2: JVM Warm-up
            System.out.println("\n[2/4] Đang tiến hành Warm-up JVM (" + WARMUP_ROUNDS + " vòng)...");
            warmUpJVM();
            System.out.println("Warm-up JVM hoàn tất! Bắt đầu đo chính thức...");

            // Bước 3: Khảo sát theo kích thước N trên dữ liệu ngẫu nhiên (Random)
            System.out.println("\n[3/4] Khảo sát ảnh hưởng của CUTOFF theo kích thước mảng N (Random):");
            for (int n : testSizes) {
                runExperimentGroup(n, "Random", cutoffs, writer);
            }

            // Bước 4: Khảo sát ảnh hưởng của phân bố dữ liệu tại N = 1,000,000
            System.out.println("\n[4/4] Khảo sát ảnh hưởng của phân bố dữ liệu tại N = 1,000,000:");
            for (String dataType : extraDistributions) {
                runExperimentGroup(1_000_000, dataType, cutoffs, writer);
            }

            System.out.println("\n======================================================================");
            System.out.println(" THÍ NGHIỆM HOÀN TẤT! Dữ liệu đã được lưu tại: " + csvPath);
            System.out.println("======================================================================");

        } catch (IOException e) {
            System.err.println("Lỗi khi ghi file CSV: " + e.getMessage());
        }
    }

    /**
     * Đọc và kiểm chứng tính đúng đắn của cài đặt Merge và MergeWithCutoff
     * trên các bộ dữ liệu trong thư mục data/inputs/.
     */
    private static void verifySampleInputs() {
        String[] sampleFiles = {
            "data/inputs/tiny.txt",
            "data/inputs/random_10k.txt",
            "data/inputs/sorted_10k.txt",
            "data/inputs/reversed_10k.txt",
            "data/inputs/duplicates_10k.txt"
        };

        for (String path : sampleFiles) {
            File f = new File(path);
            if (!f.exists()) continue;

            Integer[] data = DataGenerator.readFromFile(path);
            Integer[] copy1 = Arrays.copyOf(data, data.length);
            Integer[] copy2 = Arrays.copyOf(data, data.length);
            Integer[] copy3 = Arrays.copyOf(data, data.length);

            Merge.sort(copy1);
            MergeWithCutoff.sort(copy2, 7);
            MergeWithCutoff.sort(copy3, 10);

            if (!Merge.isSorted(copy1) || !MergeWithCutoff.isSorted(copy2) || !MergeWithCutoff.isSorted(copy3)) {
                throw new IllegalStateException("Lỗi sắp xếp không đúng trên file: " + path);
            }
            System.out.printf("  - [PASS] %-30s (N = %,d)\n", path, data.length);
        }
    }

    /**
     * Thực hiện một nhóm thí nghiệm cho cấu hình (N, dataType) qua toàn bộ dải CUTOFF.
     * Dữ liệu của các trials được khởi tạo trước một lần duy nhất để mọi mức CUTOFF
     * đều được đo trên cùng bộ dữ liệu đầu vào và loại bỏ chi phí cấp phát đối tượng Integer.
     */
    private static void runExperimentGroup(int n, String dataType, int[] cutoffs, PrintWriter writer) {
        System.out.println("\n-------------------------------------------------------------");
        System.out.printf("Khảo sát kích thước mảng N = %,d (Phân bố: %s)\n", n, dataType);
        System.out.println("-------------------------------------------------------------");
        System.out.printf("%-10s | %-16s | %-16s\n", "CUTOFF", "Thời gian TB (ms)", "Độ lệch chuẩn (ms)");

        // Khởi tạo trước bộ dữ liệu cho tất cả các trials để đảm bảo công bằng tuyệt đối giữa các mốc CUTOFF
        Integer[][] trialDatasets = new Integer[TRIALS][];
        for (int trial = 0; trial < TRIALS; trial++) {
            long seed = 42L + trial * 1000L;
            trialDatasets[trial] = generateByType(n, dataType, seed);
        }
        Integer[] workArray = new Integer[n];

        // Dọn rác một lần và làm nóng bộ nhớ đệm (cache warm-up) trước khi bước vào vòng đo
        System.gc();
        for (int w = 0; w < 4; w++) {
            System.arraycopy(trialDatasets[0], 0, workArray, 0, n);
            if (w % 2 == 0) {
                Merge.sort(workArray);
            } else {
                MergeWithCutoff.sort(workArray, 10);
            }
        }

        for (int cutoff : cutoffs) {
            // Chạy thêm 2 vòng đệm để loại bỏ nhiễu ngắt hệ điều hành / GC ngẫu nhiên (trimmed trials)
            int totalRuns = TRIALS + 2;
            double[] rawTimes = new double[totalRuns];

            for (int run = 0; run < totalRuns; run++) {
                int datasetIdx = run % TRIALS;
                System.arraycopy(trialDatasets[datasetIdx], 0, workArray, 0, n);

                long start = System.nanoTime();
                if (cutoff == 0) {
                    Merge.sort(workArray);
                } else {
                    MergeWithCutoff.sort(workArray, cutoff);
                }
                long elapsed = System.nanoTime() - start;
                rawTimes[run] = elapsed / 1_000_000.0; // Đổi sang mili giây (ms)

                if (run == 0 && !MergeWithCutoff.isSorted(workArray)) {
                    throw new IllegalStateException("Mảng chưa được sắp xếp đúng tại cutoff = " + cutoff);
                }
            }

            // Loại bỏ 1 cực trị nhỏ nhất và 1 cực trị lớn nhất (nhiễu ngoại lai do OS/GC) để giữ lại đúng TRIALS = 10 mẫu chuẩn
            Arrays.sort(rawTimes);
            double[] times = Arrays.copyOfRange(rawTimes, 1, 1 + TRIALS);

            double mean = calculateMean(times);
            double stdDev = calculateStdDev(times, mean);

            System.out.printf("%-10d | %-16.3f | %-16.3f\n", cutoff, mean, stdDev);
            writer.printf("%d,%s,%d,%.4f,%.4f\n", n, dataType, cutoff, mean, stdDev);
            writer.flush();
        }
    }

    /**
     * Sinh mảng dữ liệu theo loại phân bố được chỉ định.
     */
    private static Integer[] generateByType(int n, String dataType, long seed) {
        switch (dataType) {
            case "Sorted":
                return DataGenerator.generateSorted(n);
            case "Reversed":
                return DataGenerator.generateReversed(n);
            case "Duplicates":
                return DataGenerator.generateDuplicates(n, seed);
            case "Random":
            default:
                return DataGenerator.generateRandom(n, seed);
        }
    }

    /**
     * Khởi động và làm nóng máy ảo Java (JVM Warm-up):
     * 
     * Mục đích thực nghiệm:
     * - Trong Java, các lần chạy đầu tiên thường bị chậm do JVM phải nạp lớp (class loading),
     *   khởi tạo bộ nhớ và chạy ở chế độ thông dịch (interpreter mode).
     * - Hàm này thực hiện sắp xếp thử nghiệm trên cả Merge, MergeWithCutoff và Insertion
     *   để kích hoạt trình biên dịch Just-In-Time (JIT C2) dịch toàn bộ các nhánh gọi hàm
     *   sang mã máy gốc (native code) và đưa JVM về trạng thái ổn định (steady-state).
     */
    private static void warmUpJVM() {
        int[] warmupCutoffs = { 7, 10, 16, 32 };
        Integer[] base = DataGenerator.generateRandom(100_000, 999L);
        Integer[] work = new Integer[base.length];

        for (int i = 0; i < WARMUP_ROUNDS; i++) {
            System.arraycopy(base, 0, work, 0, base.length);
            Merge.sort(work);

            for (int c : warmupCutoffs) {
                System.arraycopy(base, 0, work, 0, base.length);
                MergeWithCutoff.sort(work, c);
            }
        }
        System.gc();
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
     * Tính độ lệch chuẩn mẫu (Sample Standard Deviation) của các lần đo
     * theo chuẩn Chủ đề 1.3 Algorithm Analysis (StdStats.stddev trong giáo trình):
     * 
     * Công thức: StdDev (s) = √[ ∑ (x_i - x̄)² / (K - 1) ]
     * 
     * Ý nghĩa thống kê:
     * - Đo lường mức độ phân tán hoặc biến động của thời gian chạy qua các lần đo độc lập.
     * - Độ lệch chuẩn càng nhỏ chứng tỏ phép đo càng ổn định và kết quả càng có độ tin cậy cao.
     * - Giá trị này được sử dụng để vẽ thanh sai số (error bars) trên biểu đồ phân tích.
     *
     * @param values mảng chứa thời gian thực thi của các lần chạy (đơn vị: ms)
     * @param mean thời gian thực thi trung bình đã tính
     * @return độ lệch chuẩn mẫu của các lần đo (ms)
     */
    private static double calculateStdDev(double[] values, double mean) {
        if (values.length <= 1) return 0.0;
        double sumSq = 0;
        for (double v : values) sumSq += (v - mean) * (v - mean);
        return Math.sqrt(sumSq / (values.length - 1));
    }
}
