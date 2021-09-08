package Clone;

public class CloneExample implements Cloneable{
    private int a;
    private int b;

    @Override
    public Object clone() throws CloneNotSupportedException {
        System.out.println("xpxpxp");
        return super.clone();
    }
   /* @Override
    public CloneExample clone() throws CloneNotSupportedException {
        return (CloneExample) super.clone();
    }*/

    public static void main(String[] args) {
        CloneExample e1 = new CloneExample();
        try {
            Object e2 = e1.clone();
        } catch (
                CloneNotSupportedException e) {
            e.printStackTrace();

        }
    }
}
