import java.time.LocalDate; // 處理日期
import java.util.List; // 有序/允許重複
import java.util.stream.Collectors; // 收集資料

public class Member {
    private String name;         // 會員姓名(私有)
    private String email;        // 會員 Email(私有)
    private LocalDate expiryDate; // 會員訂閱到期日(私有)

    // 建構子，初始化會員資料
    public Member(String name, String email, LocalDate expiryDate) {
        this.name = name;
        this.email = email;
        this.expiryDate = expiryDate; //到期日(expire)
    }

    // 取得會員 Email
    public String getEmail() {
        return email;
    }

    // 取得到期日
    public LocalDate getExpiryDate() {
        return expiryDate; // 回傳到期日
    }
    public static void main(String[] args) {
        // 不可變的 List
        List<Member> members = List.of(
            new Member("Alice", "alice@example.com", LocalDate.now().plusDays(10)), // 10 天後到期
            new Member("Bob", "bob@example.com", LocalDate.now().plusDays(40)),     // 40 天後到期
            new Member("Charlie", "charlie@example.com", LocalDate.now().plusDays(25)) // 25 天後到期
        );

        // 今天日期
        LocalDate today = LocalDate.now();

        // 條件：到期日 30 天內
        LocalDate threshold = today.plusDays(30);

        // Stream API 過濾將到期會員，獲取 Email
        List<String> expiringEmails = members.stream()
            .filter(member -> member.getExpiryDate().isBefore(threshold)) // 篩選到期日在 30 天內的會員
            .map(Member::getEmail)
            .collect(Collectors.toList()); // 收集結果到 List

        // 列印Email
        System.out.println("即將到期的會員 Email：" + expiringEmails);
    }
}
