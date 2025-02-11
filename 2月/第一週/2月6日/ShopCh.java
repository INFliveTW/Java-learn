import java.util.ArrayList;
import java.util.List;

public class ShopCh {
    public static void main(String[] args) {
        List<String> cart = new ArrayList<>();
        
        // 加入商品
        cart.add("iPhone 15 Pro");
        cart.add("MacBook Air M3");
        cart.add("iPad Pro M4");
        cart.add("iPhone 15 Pro"); // 允許重複加入
        
        // 顯示購物車內容
        System.out.println("購物車內的商品：" + cart);
        
        // 移除一項商品
        cart.remove("MacBook Air M3");
        
        // 顯示修改後的購物車
        System.out.println("移除商品後的購物車：" + cart);
        
        // 取得特定索引位置的商品
        System.out.println("購物車第一項商品：" + cart.get(0));
    }
}
