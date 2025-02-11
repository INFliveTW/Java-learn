import java.util.Scanner; //匯入java.util套件中的Scanner類別
public class Army {
    public static void main(String[] args) { //程式進入
        Scanner scanner = new Scanner(System.in); //建立Scanner物件
        System.out.print("請輸入您的年齡："); //顯示提示
        int man = scanner.nextInt(); //輸入年齡
        if (man >= 18 && man < 35) {  
            System.out.println("您尚未除役，需當兵");  
        } else if (man < 18) {  
            System.out.println("您尚未達到服役年紀");  
        } else { //(man > 35)  
            System.out.println("您已超過服役年齡，無需當兵");  
        }  
    }
}