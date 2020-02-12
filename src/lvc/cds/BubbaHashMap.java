package lvc.cds;


import java.lang.reflect.Array;

public class BubbaHashMap<K,V> implements Map<K,V> {
    private static final int DEF_SIZE = 16;
    private Pair<K,V>[] table;
    private int[] books;
    private int B;
    private int size;

    public BubbaHashMap() {
        this(4);
    }

    public BubbaHashMap(int bucketSize) {
        B = bucketSize;
        clear();
    }

    @Override
    public V find(K key) {
        int h = hash(key);
        for (int i=0; i<B; ++i) {
            Pair<K, V> candidate = table[(h+i) % table.length];
        }
        return null;
    }

    @Override
    public boolean add(K key, V value) {
        return false;
    }

    @Override
    public V remove(K key) {

        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        table = (Pair<K,V>[]) Array.newInstance(Pair.class, DEF_SIZE);
        books = new int[DEF_SIZE];
        size = 0;
    }

    private int hash(K key) {
        return Math.abs(key.hashCode()) % table.length;
    }

    // check whether bit "slot" is marked with a 1 in books[bucket]
    private boolean isOccupied(int bucket, int slot) {
        return (books[bucket] & (1 << slot)) != 0;
    }

    // mark "slot" as occupied in books[bucket]
    private void markOccupied(int bucket, int slot) {
        books[bucket] |= (1 << slot);
    }

    private void markUnoccupied(int bucket, int slot) {
        books[bucket] &= ~(1 << slot);
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
