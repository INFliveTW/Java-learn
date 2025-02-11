import java.util.List;
import java.util.stream.Collectors; // 匯入 Collectors 類別

public class Money {
    public static void main(String[] args) {
        List<Employee> employees = List.of( // 建立員工清單
            new Employee("Alice", 75000),
            new Employee("Bob", 90000),
            new Employee("Charlie", 85000)
        );

        List<String> highEarners = employees.stream()
            .filter(emp -> emp.getSalary() > 80000) // 過濾薪資超過 80000
            .map(Employee::getName) // 只提取員工名稱
            .collect(Collectors.toList());

        System.out.println("高薪員工:");
        highEarners.forEach(System.out::println);
    }
}

class Employee {
    private String name;
    private double salary;

    public Employee(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }

    public String getName() { return name; }
    public double getSalary() { return salary;
}
