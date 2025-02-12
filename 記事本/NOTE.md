說明文件  

-------------------------------------------------  
vscode文件(GitHub)換行  
(0)使用兩次 Enter（空一行）  
(1)在行尾加上兩個空格  
(2)使用 Markdown 的 <br>

-------------------------------------------------  
0. 設定Git  

git config --global user.name "你的GitHub帳號名稱"  
git config --global user.email "你的GitHub註冊郵箱"  

Ctrl + Shift + P 打開搜尋列  
搜尋 GitHub: Sign In 並登入你的 GitHub 帳號。  

git remote add origin <你的GitHub倉庫URL>  
git remote -v 確保設定正確  

-------------------------------------------------  
1. 上傳至GitHub  

vscode終端機  

git add 檔案名稱.副檔名  
git commit -m"程式碼說明"  
git push origin main //推送到GitHub  
  
解綁  
git remote remove origin  
更換  
git remote set-url origin <新的遠端倉庫網址>  
拉取變更  
git pull origin main --rebase  
強制上傳(本地才是最新)  
git push origin main --force  
  
方法 1	最推薦！先拉取遠端變更，再推送  
git pull origin main --rebase  
git push origin main  
方法 2	本地變更才是最終版，要強制覆蓋遠端  
git push origin main --force（⚠️ 慎用）  
方法 3	拉取遠端時遇到衝突，需要手動解決  
git pull origin main（手動修復後 git push）  
  
-------------------------------------------------  
2. 重新上傳至GitHub(取得最新更新再重新上傳)  

git pull origin main --rebase  
git add .  
git rebase --continue  

-------------------------------------------------  
3. Git基本命令  

Clone(下載Git至本機)  
git clone   

使用方法 git clone repository網址  
git clone https://github.com/username/repository.git  

Git: Clone 並選擇目標資料夾  

-------------------------------------------------  
4. 變數與資料型別  
  
[整數]  
[byte] 1 byte (負128 ~ 正127)  
舉例: byte a = 100; //宣告一個a的變數為100  
[short] 2 byte (負32768 ~ 正32767)  
舉例: short b = 32000; //宣告一個b的變數為32000  
[int] 4 byte (負2的31次方 ~ 正2的31次方-1)    
舉例: int x = 1000; //宣告一個x的變數為1000  
[long] 8 byte (負2的63次方 ~ 正2的63次方-1)    
舉例: long y = 10000000L; //宣告一個y的變數為10萬L  
  
[浮點數]  
[float] 4 byte (32-bit)  
舉例: float f = 10.5f;  
[double] 8 byte (64-bit)  
舉例: double d = 99.99;;  
  
[字串]  
[char] 2 byte (Unicode字元)  
舉例: char c = 'A';  
  
[布林值]  
[boolean] 1 bit (true或false)  
舉例: boolean isTrue = true;  
  
-------------------------------------------------  
5. 條件判斷  
  
[if-else] //是否當兵(18至35歲)  
if (man >= 18 &&(且) man < 35) {  
    System.out.println("您尚未除役，需當兵");  
} else if (man < 18) {  
    System.out.println("您尚未達到服役年紀");  
} else { //(man > 35)  
    System.out.println("您已超過服役年齡，無需當兵");  
}  
  
[switch-case] //星期幾的判斷(週一至日)  
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
  
-------------------------------------------------  
6. 迴圈  
  
[for] //99乘法表  
for (i = 1; i <=9; i++) {  
    for (j = 1; j <=9; j++) {  
        System.out.print(i + "x" + j + "=" + i*j + "\t");  
    }  
    System.out.println();  
}  
  
[while] //倒數計時  
while (count > 0) {  
  System.out.println("倒數: " + count);  
    count--; // 每次遞減 1  
 }  
System.out.println("時間到！"); 

[do-while] //骰骰子  
import java.util.Random;  
Random random = new Random();  
int rd; // 宣告 dice 變數  
System.out.println("擲骰子開始...");  
 do {  
    rd = random.nextInt(6) + 1; // 產生 1~6 隨機數  
    System.out.println("擲出: " + rd);  
    } while (rd != 6);  
System.out.println("擲到 6 了，遊戲結束！");  
  
-------------------------------------------------  
7. 類、繼承、封裝、多型  
  
類（Class） 像模板或藍圖，定義對象結構行為。理解為設計圖  
這個設計圖描述了對象有什麼屬性（特徵）和方法（行為）  
  
屬性（Attributes/Fields）：類中的變數，用來存儲對象的特徵或狀態  
方法（Methods）：類中的函數，用來描述對象可以執行的操作或行為  
Dog 類 定義了狗的兩個屬性（name 和 age）以及兩個方法（bark() 和 eat()）  
設置了它的屬性（名字和年齡），然後呼叫了它的行為（叫聲和吃飯）  
  
繼承（Inheritance） 允許創建一個新類（子類）會自動繼承現有類（父類）的特徵行為  
不需重複寫相同的代碼，可以讓代碼更加簡潔  
  
父類（Superclass）：被繼承的類  
子類（Subclass）：從父類繼承過來的類  
Dog 類繼承自 Me 類，這意味著 Dog 類也擁有 Me 類的 eat() 方法  
創建 Dog 類，不僅能夠調用 Dog 類中定義的 bark() 方法，還能夠調用從 Me 類繼承來的 eat() 方法  
  
封裝（Encapsulation）操作屬性方法封一起，同時隱藏內部的細節  
只提供對外界可用的接口。這樣可以保護對象的內部數據，防止外部直接修改  
  
私有屬性：用 private 修飾，這些屬性無法被外部直接訪問  
公共方法：用 public 修飾，這些方法可以用來訪問和修改對象的私有屬性  
Person 類中的 name 和 age 屬性是私有的，不能直接被外部訪問  
必須使用公共的 setName()、getName()、setAge() 和 getAge() 方法來操作這些屬性  
這樣可以確保外部代碼只能通過合法的方式來修改對象的內部數據，避免錯誤  
  
多型（Polymorphism） 是指對象可以在不同的情況下以不同的方式表現出來  
在 Java 中，這主要有兩種形式：方法覆蓋（Overriding） 和 方法重載（Overloading）  
  
方法覆蓋（Overriding）：當子類重新定義父類的方法時，就會發生方法覆蓋  
方法重載（Overloading）：當同一個方法名，但方法的參數數量或類型不同時，發生方法重載  
在這個例子中，Dog 類重寫了 Me 類的 sound() 方法  
所以當我們用 Me 類的參考變數來引用 Dog 類的對象時，會呼叫 Dog 類中覆蓋的 sound() 方法  
  
-------------------------------------------------  
7. 集合框架（List、Map、Set）  
泛型類別 - 整數(int)/字串(str)  
class MyData<T> ：MyData是泛型類別  
T：是任意資料型別(可替換int/str/double)  
  【java.util 套件】
List（列表 | ArrayList）：有序且允許重複，待辦事項、商品列表  
購物清單(可重複)  
List<String> name = new ArrayList<>();  
創建ArrayList存放物品，開頭的List是介面，後面變數可自行取名  
name.add("storesell");  
name.add("storedel"); //欲刪除品項  
name.add("storesell"); //重複之商品(1)  
  
System.out.println("購物車商品：+ name); //+為字串串接  
name.remove("stiredel"); //欲刪除商品刪除  
可再重複一次串接行(顯示刪除後剩餘商品)  
System.out.println("購物車第一商品：" + name.get(0));  
//取得地一項商品名稱並輸出  
  
Set（集合 | HashMap）：無序且不允許重複，ID、已註冊信箱  
身分證字號(不可重複且隨機)、個人電子郵件名稱  
Set<String> registeredEmails = new HashSet<>();  
  
Map（映射 | HashSet）： (key-value(代表)) 方式儲存資料  
用戶 ID <-> 個人資料、產品 ID <-> 商品資訊  
Map<Integer, String> studentMap = new HashMap<>();  
studentMap.put(001, "小a");  
studentMap.put(002, "小b");  
studentMap.put(003, "小c");  
1號=小a...  
  
-------------------------------------------------  
7. Stream API概念&用途  
  
常用:（filter、map、collect）  
使用Stream對集合進行基礎處理（過濾、轉換、收集）  
  
Lambda 表達式(簡潔易讀)  
不可變性(lmmutable):不修改原數據，而是產生新數據  
懶加載(Lazy Evaluation):當終端執行如 .collect()，才執行  
  
Stream API三大類:  
1.建立 Stream  
stream() :串型流(小規模)  
數據間有關聯(結算順序影響結果)  
parallelStream() :並行流(大規模、多核CPU計算(提速))  
統計結果(大量訂單、日誌數據分析、大量數據)  
2.中間操作  
filter(Predicate)：篩選符合條件的元素  
List<Integer> ages = Arrays.asList(12, 20, 15, 30, 18);  
List<Integer> adults = ages.stream() //過濾>=18的List  
  .filter(age -> age >= 18) // 過濾出大於等於 18 歲的數據  
  .collect(Collectors.toList()); //將過濾的資料蒐集到List  
System.out.println(adults) //輸出[20, 30, 18]  
  
map(Function)：對元素進行轉換操作  
List<String> names = Arrays.asList("aoa", "bob", "coc");  
List<String> upperCaseNames = names.stream() //將小寫字母變大  
        .map(String::toUpperCase) //將List資料過濾後將字母變大寫  
        .collect(Collectors.toList()); //將過濾的資料蒐集到List  
System.out.println(upperCaseNames); // 輸出：[AOA, BOB, COC]  
  
sorted(Comparator)：對元素進行排序(升序與降序)    
Stream<T> sorted(); // 自然排序（適用於數字、字串等可比較的類型）  
Stream<T> sorted(Comparator<? super T> comparator); // 自定義排序  
  
3.終端操作  
collect(Collector)：結果收集到 List、Set、Map  
List<String> items = Arrays.asList("apple", "banana", "choose", "date");  
List<String> filteredList = items.stream()  
  .filter(item -> item.length() > 5) // 篩選長度大於 5 的字串  
  .collect(Collectors.toList()); //將過濾的資料蒐集到List  
System.out.println(filteredList); // 輸出：[banana, choose]  
  
forEach(Consumer)：對每個元素執行操作  
count()：計算數量  
reduce(BinaryOperator)：累加總和  
List.of() //不可變  
LocalDate //日期  
plusDays() //增加天數  
threshold //最後一天(30天後)  
expiryDate //到期日(期限)  
  
-------------------------------------------------  
8. Spring Boot Garenal  
  
@SpringBootApplication  
標記 Spring Boot 應用的啟動類(啟動 Spring Boot)  
整合 @Configuration、@EnableAutoConfiguration、@ComponentScan  
SpringApplication.run(DemoApplication.class, args);  
  
@Controller  
標記 Spring MVC 的控制器，用於處理 HTTP 請求。  
http://localhost:8080/  
model.addAttribute("message", "Hello, Spring Boot!");  
return "home"; // 對應到 `src/main/resources/templates/home.html`  
  
@Service  
標記業務邏輯層（Service Layer），用於處理業務邏輯。  
return "Hello, Spring Boot Service!"  
這個服務類可被 Controller 調用，用於返回問候語。  
  
@Repository  
標記數據訪問層（DAO），用於與資料庫交互，並可結合 Spring Data JPA。  
自動提供基本 CRUD 操作，通過 findByUsername 方法查找用戶。  
User findByUsername(String username)  
