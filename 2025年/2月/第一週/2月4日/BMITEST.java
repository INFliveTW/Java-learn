import java.util.Scanner; //匯入Scanner類別
public class BMITEST { //類別
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("請輸入身高(公分)");
        Double height = scanner.nextDouble();
        System.out.println("請輸入體重(公斤)");
        Double weight = scanner.nextDouble();
        Double BMI;
        BMI = weight / ((height / 100) * (height / 100)); //體重/身高平方
        System.out.println("BMI = " + BMI);
        if (BMI >= 18.5 && BMI < 24)
        System.out.println("體重正常");
        else if (BMI < 18.5)
        System.out.println("體重過輕");
        else
        System.out.println("體重過重");
    }
}