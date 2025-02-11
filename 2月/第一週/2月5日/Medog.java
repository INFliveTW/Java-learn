// 定義一個父類 "Animal"
class Me {
    // 父類方法
    void eat() {
        System.out.println("Me 在吃東西.");
    }
}

// 定義一個子類 "Dog"，它繼承自 "Me"
class Dog extends Me {
    // 子類新增的方法
    void bark() {
        System.out.println("Dog 在睡覺.");
    }
}

public class Medog {
    public static void main(String[] args) {
        // 創建 Dog 類的對象
        Dog dog1 = new Dog();
        dog1.eat();  // 呼叫繼承自 Animal 類的 eat 方法
        dog1.bark(); // 呼叫 Dog 類的方法
    }
}