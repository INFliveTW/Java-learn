import java.util.HashMap;
import java.util.Map;

public class student {
    public static void main(String[] args) {
        // 創建一個 Map 來存學號與姓名
        Map<Integer, String> studentMap = new HashMap<>(); // Key: 學號, Value: 姓名

        //  (Key: 學號, Value: 姓名)
        studentMap.put(001, "小a");
        studentMap.put(002, "小b");
        studentMap.put(003, "小c");

        // 取得某個學號的學生姓名
        int studentId = 002;
        /*if (studentMap.containsKey(studentId)) {
            System.out.println("學號 " + studentId + " 的學生是：" + studentMap.get(studentId));
        } else {
            System.out.println("找不到學號 " + studentId + " 的學生");
        } */

        System.out.println("\n學生名單：");
        for (Map.Entry<Integer, String> entry : studentMap.entrySet()) {
            System.out.println("學號：" + entry.getKey() + "，姓名：" + entry.getValue());
        }
    }
}
