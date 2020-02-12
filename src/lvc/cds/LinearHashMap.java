package lvc.cds;

import java.lang.reflect.Array;

@SuppressWarnings("unchecked")
public class LinearHashMap<K, V> implements Map<K, V> {
    private static final int DEF_SIZE = 16;

    private Pair<K,V>[] table;
    private Pair del;

    // total number of non-null entries
    private int size;

    // number of non-active, non-deleted entries
    private int active;

    public LinearHashMap() {
        del = new Pair<K,V>();
        clear();
    }

    // Return the value associated with this key, or null if none found
    public V find(K key) {
        int i = hash(key);
        while (table[i] != null) {
            if (table[i] != del && table[i].key.equals(key))
                return table[i].value;
            i = (i == table.length - 1) ? 0 : i + 1;
        }
        // we didn't find it
        return null;
    }

    // add this K, V pair to the table, if the key is not already present.
    public boolean add(K key, V value) {
        // do we need more space?
        if (2 * (active + 1) > table.length)
            resize();

        // start where the hash tells us
        int pos = hash(key);

        // strategy: walk forward until we see either a null or the key. If we
        // see a del, then mark it's pos, but keep going as the key might still
        // show up
        int firstDel = -1;
        while (table[pos] != null) {
            // mark the first del we see
            if (table[pos] == del && firstDel == -1)
                firstDel = pos;
            // did we find the key?
            if (table[pos] != del && table[pos].key.equals(key))
                return false;
            pos = (pos == table.length - 1) ? 0 : pos + 1;
        }
        if (firstDel != -1) {
            pos = firstDel;
            size++;
        }

        table[pos] = new Pair<K,V>(key, value);

        // bookkeeping
        active++;
        return true;
    }

    // remove the entry with this key from the table, if present. Return the
    // value found at that key.
    public V remove(K key) {
        int i = hash(key);
        while (table[i] != null) {
            var temp = table[i];
            if (temp != del && temp.key.equals(key)) {
                table[i] = del;
                active--;
                if (8 * active < table.length)
                    resize();
                return temp.value;
            }
            i = (i == table.length - 1) ? 0 : i + 1;
        }
        return null;
    }

    public int size() {
        return this.active;
    }

    public void clear() {
        size = 0;
        active = 0;
        table = (Pair<K,V>[]) Array.newInstance(Pair.class, DEF_SIZE);
    }

    public void print() {
        System.out.printf("\n\n%5s  %10s  %5s  %5s\n", "index", "key", "value",
                "hash");
        for (int i=0; i<table.length; ++i) {
            String key = "NULL";
            String val = "NULL";
            int hash = -1;
            if (table[i] == del) {
                key = "DEL";
                val = "DEL";
            }
            else if (table[i] != null) {
                key = table[i].key.toString();
                val = table[i].value.toString();
                hash = hash(table[i].key);
            }
            System.out.printf("%5d  %10s  %5s  %5s\n", i, key, val, hash);
        }
    }

    private void resize() {
        int d = 1;
        while ((1 << d) < 3 * active)
            d++;
        var temp = table;
        table = (Pair<K,V>[]) Array.newInstance(Pair.class, 1<<d);

        size = active;
        // insert everything into the new table. a specialized version
        // of add()
        for (var p : temp) {
            if (p != null && p != del) {
                int i = hash(p.key);
                // there are no del's in our new table!
                while (table[i] != null)
                    i = (i == table.length - 1) ? 0 : i + 1;
                table[i] = p;
            }
        }
    }

    // compute the hash based only on the key.
    private int hash(K k) {
        int hash = k.hashCode();
        return Math.abs(hash) % table.length;
    }

    // a simple pair class to hold our Key/Value pairs
    private static class Pair<K, V> {
        K key;
        V value;

        Pair(K k, V v) {
            key = k;
            value = v;
        }
        Pair() {
            key = null;
            value = null;
        }
    }
}