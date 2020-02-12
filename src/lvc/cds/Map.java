package lvc.cds;

public interface Map<K, V> {
    int size();
    void clear();

    boolean add(K key, V value);
    V find(K key);
    V remove(K key);
}
