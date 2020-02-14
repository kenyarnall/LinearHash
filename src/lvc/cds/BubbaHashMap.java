package lvc.cds;


import java.lang.reflect.Array;

@SuppressWarnings("unchecked")
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
        // step one: check if key is already present
        // search the bucket for this key
        int hash = hash(key);

        // loop that hunts for the key in this bucket
        int pos = find(key, hash);

        // if found, do nothing
        if (pos != -1)
            return false;

        // key not found. linear probe forward, looking for an empty spot
        int index = hash;
        while (table[index] != null)
            index = (index + 1) % table.length;

        // index is the nearest available spot to place the key.
        // might not be in the bucket

        while (dist(hash, index) >= B) {
            // find a spot behind us that allows us to move index to the left
            // if this loop fails to find a spot, we must rehash
            boolean spotFound = false;
            int candidateStart = index - B + 1;
            if (candidateStart < 0)
                candidateStart += table.length;

            for (int i = 0; i < B; ++i) {
                // the location of our candidate for shifting. Question: Do
                // we need to worry about NULL's?
                int candidate = (candidateStart + i) % table.length;
                // can we move the value in location candidate? Hash to find the
                // candidate's bucket
                var candidatePair = table[candidate];
                if (candidatePair == null)
                    System.out.println("we skipped over a null??");
                int candidateBucket = hash(candidatePair.key);
                // is index (the spot we want to move candidatePair to) in
                // candidateBucket?
                if (dist(candidateBucket, index) < B) {
                    // move the candidate to spot index
                    table[index] = candidatePair;
                    // update books
                    int newSlot = dist(candidateBucket, index);
                    int oldSlot = dist(candidateBucket, candidate);
                    markOccupied(candidateBucket, newSlot);
                    markUnoccupied(candidateBucket, oldSlot);
                    // reset index and keep shifting, until we succeed.
                    index = candidate;
                    spotFound = true;
                    break;
                }
            }

            if (!spotFound) {
                // we could not push index back into the bucket. rehash and try
                // again
                rehash();
                return add(key, value);
            }
        }

        // insert our new value, updating the books
        table[index] = new Pair<>(key, value);
        markOccupied(hash, dist(hash, index));
        size++;
        return true;
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

    public void print() {
        System.out.printf("%5s %10s %10s %10s %10s\n", "index", "key", "hash" +
                        "(key)",
                "value",
                "books");
        for (int i=0; i<table.length; ++i) {
            String key = "NULL";
            String val = "NULL";
            String hash = "NULL";
            if (table[i] != null) {
                key = table[i].key.toString();
                hash = "" + hash(table[i].key);
                val = table[i].value.toString();
            }
            String book = booksToString(i);
            System.out.printf("%5d %10s %10s %10s %10s\n",i,key,hash,val,book);
        }
    }

    private String booksToString(int bucket) {
        StringBuffer sb = new StringBuffer();
        int b = books[bucket];
        for (int i=0; i < B; ++i) {
            sb.append(b % 2 == 1 ? '1' : '0');
            b /= 2;
        }
        return sb.toString();
    }

    private int hash(K key) {
        return Math.abs(key.hashCode()) % table.length;
    }

    private void rehash() {
        var temp = table;
        table = (Pair<K,V>[]) Array.newInstance(Pair.class,
                table.length * 2);
        books = new int[table.length];
        size = 0;
        for (int i = 0; i<temp.length; ++i)
            if (temp[i] != null)
                this.add(temp[i].key, temp[i].value);

    }

    // A loop that looks through the bucket to find the key. Return the index
    // (in the table, not in the bucket) where key is found, or -1 if it isn't
    // present.
    private int find(K key, int bucket) {
        for (int i=0; i<B; ++i) {
            int pos = (bucket+i) % table.length;
            if (isOccupied(bucket, i) && table[pos].key.equals(key))
                return pos;
        }
        return -1;
    }

    // return the distance between b and s in the table. That is,
    // how far do you have to move to get FROM b TO s.
    private int dist(int b, int s) {
        if (s >= b)
            return s - b;
        else
            return s + table.length - b;
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
