public class Nine { //99乘法表
    public static void main(String[] args) {
        int i;
        int j;
        for (i = 1; i <=9; i++) {
            for (j = 1; j <=9; j++) {
                System.out.print(i + "x" + j + "=" + i*j + "\t");
            }
            System.out.println();
        }
    }
}