import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Lớp hỗ trợ sinh và lưu trữ các tập dữ liệu mảng phục vụ thực nghiệm Algorithm Analysis.
 * Cung cấp các phương thức sinh dữ liệu theo phân bố và đọc/ghi file chuẩn giáo trình.
 */
public class DataGenerator {

    /**
     * Sinh mảng Integer ngẫu nhiên phân bố đều.
     */
    public static Integer[] generateRandom(int n, long seed) {
        Random rand = new Random(seed);
        Integer[] a = new Integer[n];
        for (int i = 0; i < n; i++) {
            a[i] = rand.nextInt(1_000_000_000);
        }
        return a;
    }

    /**
     * Sinh mảng đã sắp xếp tăng dần hoàn toàn.
     */
    public static Integer[] generateSorted(int n) {
        Integer[] a = new Integer[n];
        for (int i = 0; i < n; i++) {
            a[i] = i;
        }
        return a;
    }

    /**
     * Sinh mảng có thứ tự giảm dần hoàn toàn (Reversed).
     */
    public static Integer[] generateReversed(int n) {
        Integer[] a = new Integer[n];
        for (int i = 0; i < n; i++) {
            a[i] = n - 1 - i;
        }
        return a;
    }

    /**
     * Sinh mảng có nhiều phần tử trùng lặp (Duplicates).
     */
    public static Integer[] generateDuplicates(int n, long seed) {
        Random rand = new Random(seed);
        Integer[] a = new Integer[n];
        for (int i = 0; i < n; i++) {
            a[i] = rand.nextInt(100); // Chỉ có 100 giá trị khác nhau
        }
        return a;
    }

    /**
     * Ghi mảng số nguyên ra file theo định dạng chuẩn giáo trình Algorithms 4th:
     * Dòng đầu tiên là số lượng phần tử N.
     * N dòng tiếp theo, mỗi dòng chứa một phần tử số nguyên.
     *
     * @param a mảng dữ liệu cần ghi
     * @param filePath đường dẫn file đích
     */
    public static void writeToFile(Integer[] a, String filePath) {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println(a.length);
            for (int val : a) {
                pw.println(val);
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi ghi file " + filePath + ": " + e.getMessage());
        }
    }

    /**
     * Đọc mảng số nguyên từ file:
     * Dòng đầu là số lượng phần tử N, các dòng sau là giá trị từng phần tử.
     *
     * @param filePath đường dẫn file dữ liệu
     * @return mảng Integer đọc được
     */
    public static Integer[] readFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            if (line == null) return new Integer[0];
            int n = Integer.parseInt(line.trim());
            Integer[] a = new Integer[n];
            for (int i = 0; i < n; i++) {
                a[i] = Integer.parseInt(br.readLine().trim());
            }
            return a;
        } catch (IOException e) {
            System.err.println("Lỗi khi đọc file " + filePath + ": " + e.getMessage());
            return new Integer[0];
        }
    }

    /**
     * Main tạo sẵn các bộ dữ liệu thử nghiệm mẫu vào thư mục data/inputs/.
     */
    public static void main(String[] args) {
        System.out.println("Đang tạo các bộ dữ liệu thí nghiệm mẫu vào data/inputs/...");

        // 1. Mảng nhỏ kiểm thử nhanh (Tiny test - 15 số)
        Integer[] tiny = { 10, 3, 15, 7, 8, 23, 74, 18, 2, 45, 6, 99, 12, 54, 31 };
        writeToFile(tiny, "data/inputs/tiny.txt");

        // 2. Mảng 10,000 số ngẫu nhiên (Random)
        Integer[] random10k = generateRandom(10_000, 42L);
        writeToFile(random10k, "data/inputs/random_10k.txt");

        // 3. Mảng 10,000 số đã sắp xếp tăng dần (Sorted)
        Integer[] sorted10k = generateSorted(10_000);
        writeToFile(sorted10k, "data/inputs/sorted_10k.txt");

        // 4. Mảng 10,000 số giảm dần ngược chiều (Reversed)
        Integer[] reversed10k = generateReversed(10_000);
        writeToFile(reversed10k, "data/inputs/reversed_10k.txt");

        // 5. Mảng 10,000 số nhiều phần tử trùng lặp (Duplicates)
        Integer[] duplicates10k = generateDuplicates(10_000, 42L);
        writeToFile(duplicates10k, "data/inputs/duplicates_10k.txt");

        System.out.println("[OK] Da tao xong toan bo du lieu mau tai data/inputs/:");
        System.out.println(" - data/inputs/tiny.txt (15 phan tu)");
        System.out.println(" - data/inputs/random_10k.txt (10,000 phan tu)");
        System.out.println(" - data/inputs/sorted_10k.txt (10,000 phan tu)");
        System.out.println(" - data/inputs/reversed_10k.txt (10,000 phan tu)");
        System.out.println(" - data/inputs/duplicates_10k.txt (10,000 phan tu)");
    }
}
