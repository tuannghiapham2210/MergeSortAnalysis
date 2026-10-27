import java.util.Random;

/**
 * Lớp hỗ trợ sinh các tập dữ liệu mảng phục vụ thực nghiệm Algorithm Analysis.
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
}
