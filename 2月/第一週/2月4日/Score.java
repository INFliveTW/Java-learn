import java.util.Scanner; //匯入java.util套件中的Scanner類別
public class Score {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); //建立Scanner物件
        System.out.print("請輸入成績(0-100)："); //顯示提示訊息
        int score = scanner.nextInt(); //輸入成績
        int grade = score / 10; //將成績除以10取整數

        switch (grade) { //判斷成績等級
            case 10: //滿分
            case 9:
                System.out.println("成績評分：A(佳)"); //顯示優等
                break;
            case 8:
                System.out.println("成績評分：B(良好)"); //顯示甲等
                break;
            case 7:
                System.out.println("成績評分：C(好)"); //顯示乙等
                break;
            case 6:
                System.out.println("成績評分：D(尚可)"); //顯示丙等
                break;
            default:
                System.out.println("成績評分：E(需加強)"); //顯示丁等
        }
    }
}