import java.util.HashSet;  // 不允許重複
import java.util.Scanner;  // 讀取使用者輸入
import java.util.Set;  // 集合

public class GmailCheck { // 類別名稱為 GmailCheck
    public static void main(String[] args) { 
        Set<String> registeredEmails = new HashSet<>();  // 儲存已註冊的 email
        Scanner scanner = new Scanner(System.in);
        
        while (true) {  // 使用 while 迴圈讓程式持續執行，直到使用者輸入 'exit'
            System.out.print("請輸入 email (輸入 'exit' 結束): ");  // 提示用戶輸入 email
            String inputEmail = scanner.nextLine().trim();  // 讀取使用者輸入的 email 並去掉前後空白
            
            if (inputEmail.equalsIgnoreCase("exit")) {  // 如果使用者輸入 'exit'（不區分大小寫），跳出迴圈
                break; // 跳出迴圈
            }
            
            if (!inputEmail.endsWith("@gmail.com")) {  // 檢查輸入的 email 是否以 '@gmail.com' 結尾
                System.out.println("請使用 @gmail.com 網域的 email!");  // 如果不是，提示錯誤訊息並繼續要求輸入
                continue;
            }
            
            if (registeredEmails.contains(inputEmail)) {  // 檢查此 email 是否已經註冊過
                System.out.println("此 email 已被註冊，請使用其他 email!");  // 如果已註冊，提示錯誤並繼續要求輸入
            } else {  // 如果該 email 尚未註冊
                registeredEmails.add(inputEmail);  // 將新的 email 加入到已註冊的集合中
                System.out.println("註冊成功!");  // 顯示註冊成功的訊息
            }
        }
        
        scanner.close();  // 關閉 Scanner 物件，釋放資源
        System.out.println("已註冊的 email: " + registeredEmails);  // 顯示所有已註冊的 email 列表
    }
}
