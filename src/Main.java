import lvc.cds.BubbaHashMap;
import lvc.cds.LinearHashMap;

import java.util.Random;

public class Main {
    static Random rand = new Random();

    public static String randString(int s) {
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<s; ++i)
            sb.append((char)('a' + rand.nextInt(26)));
        return sb.toString();
    }

    public static void testLinearHashMap() {
        LinearHashMap<String, Integer> map = new LinearHashMap<>();

        map.add("bubba", 42);
        map.add("lurlene", 142);
        map.add("cletus", 242);
        map.add("zeb", 12);
        map.add("jeb", 342);
        map.print();

        for (int i = 0; i < 5; ++i)
            map.add(randString(10), rand.nextInt(100000));

        map.print();

        for (int i = 0; i < 5; ++i)
            map.add(randString(10), rand.nextInt(100000));
        map.print();

        map.remove("bubba");
        map.remove("lurlene");
        map.remove("cletus");

        map.print();

        System.out.println("zeb = " + map.find("zeb"));
        System.out.println("jeb = " + map.find("jeb"));
    }

    public static void testBubba() {
        BubbaHashMap<String, Integer> bubba = new BubbaHashMap<>();
        for (int i=0; i<30; ++i) {
            bubba.add(randString(5), rand.nextInt(100));
        }
        bubba.print();

    }

    public static void main(String[] args) {
        testBubba();
    }
}
