// 定義一個支付接口（抽象類）
abstract class Payment {
    abstract void pay(double amount);
}

// 信用卡支付
class LinePayment extends Payment {
    void pay(double amount) {
        System.out.println("支付 元" + amount + " 使用 Line Pay.");
    }
}

// PayPal支付
class PayPalPayment extends Payment {
    void pay(double amount) {
        System.out.println("支付 元" + amount + " 使用 PayPal.");
    }
}

// Google Pay支付
class ApplePayPayment extends Payment {
    void pay(double amount) {
        System.out.println("支付 元" + amount + " 使用 Apple Pay.");
    }
}

// 測試多型
public class Pay {
    public static void main(String[] args) {
        Payment payment1 = new LinePayment();
        payment1.pay(500.0); // 使用 LinePay 支付

        Payment payment2 = new PayPalPayment();
        payment2.pay(2000.0); // 使用 PayPal 支付

        Payment payment3 = new ApplePayPayment();
        payment3.pay(10000.0); // 使用 Apple Pay 支付
    }
}

