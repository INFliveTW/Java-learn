import java.util.Random; // 匯入 java.util.Random 類別
public class Rdnumber {
    public static void main(String[] args) {
        Random random = new Random();
        int rd; // 宣告 dice 變數
        System.out.println("擲骰子開始...");
        do {
            rd = random.nextInt(6) + 1; // 產生 1~6 的隨機數
            System.out.println("擲出: " + rd);
        } while (rd != 6);

        System.out.println("擲到 6 了，遊戲結束！");
    }
}
