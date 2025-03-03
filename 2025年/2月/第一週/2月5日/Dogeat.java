// 定義一個類 "Dog"
class Dog {
    // 屬性（特徵）
    String name;
    int age;

    // 方法（行為）
    void bark() {
        System.out.println("Woof!");
    }

    void eat() {
        System.out.println(name + " is eating.");
    }
}

public class Dogeat {
    public static void main(String[] args) {
        // 創建 Dog 類的對象
        Dog dog1 = new Dog();
        dog1.name = "Buddy"; // 設定狗的名字
        dog1.age = 3; // 設定狗的年齡

        // 呼叫狗的行為（方法）
        dog1.bark(); // 呼叫叫聲方法
        dog1.eat(); // 呼叫吃飯方法
    }
}
