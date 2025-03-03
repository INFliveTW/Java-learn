class Person {
    // 私有屬性，不能直接訪問
    private String name;
    private int age;

    // 公共的 setter 和 getter 方法
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAge(int age) {
        if (age > 0) {
            this.age = age;
        } else {
            System.out.println("年齡必須為正數.");
        }
    }

    public int getAge() {
        return age;
    }
}

public class Own {
    public static void main(String[] args) {
        Person person1 = new Person();
        person1.setName("Jason");  // 使用 setter 設置名字
        person1.setAge(23);       // 使用 setter 設置年齡

        System.out.println("Name: " + person1.getName());  // 使用 getter 獲取名字
        System.out.println("Age: " + person1.getAge());    // 使用 getter 獲取年齡
    }
}