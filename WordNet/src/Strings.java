import edu.princeton.cs.algs4.Bag;

import java.util.Iterator;

public class Strings extends Bag<String> implements Comparable<Strings> {
    public Strings(String[] items) {
        for (var item : items) {
            add(item);
        }
    }

    // items in one line which separated by comma
    public Strings(String line) {
        String[] items = line.split(",");

        for (var item : items) {
            add(item);
        }
    }

    @Override
    public int compareTo(Strings o) {
        return toString().compareTo(o.toString());
    }

    public

    @Override
    public String toString() {
        Iterator<String> iter = iterator();
        StringBuilder sb = new StringBuilder();
        while(iter.hasNext()) {
            String str = iter.next();
            sb.append(str);
            sb.append(" ");
        }
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    public static void main(String[] args) {
        Strings str = new Strings("Hello this is a test");
        System.out.println(str);

    }
}
