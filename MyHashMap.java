import java.util.*;

/**
 * Created by w00278961 on 2014/11/6.
 */
public class MyHashMap<K, V> implements Map<K, V> {

    static class Node<K, V> implements Map.Entry<K,V> {
        private K key;
        private V value;
        private Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

        protected void setNext(Node<K, V> next) {
            this.next = next;
        }

        protected Node<K, V> getNext () {
            return next;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public int hashCode() {
            return key.hashCode() ^ value.hashCode();
        }
    }

    private static final int DEFAULT_CAPACITY = 16;
    private static final int MAXIMUM_CAPACITY = 1 << 30;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private int capacity;
    private int size;
    private float loadFactor;

    private Node<K, V>[] table;
    private Set<Entry<K, V>> entrySet;

    public MyHashMap() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public MyHashMap(int capacity) {
        this(capacity, DEFAULT_LOAD_FACTOR);
    }

    public MyHashMap(int capacity, float loadFactor) {
        if (capacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " + capacity);
        if (capacity > MAXIMUM_CAPACITY)
            capacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);

        this.capacity = capacity;
        this.loadFactor = loadFactor;
    }

    private void resize() {
        for (int i = 0; i < capacity; i++) {
            table[i] = null;
        }
        capacity--;
        capacity |= capacity >>> 1;
        capacity |= capacity >>> 2;
        capacity |= capacity >>> 4;
        capacity |= capacity >>> 8;
        capacity |= capacity >>> 16;
        capacity++;
        table = (Node<K, V>[])new Node[capacity];
        for (Entry<K, V> entry : entrySet) {
            int index = indexOf(entry.getKey());
            if (null == table[index]) {
                table[index] = (Node<K, V>)entry;
            } else {
                Node<K, V> node = table[index];
                while (null != node.getNext()) {
                    node = node.getNext();
                }
                node.setNext((Node<K, V>)entry);
            }
        }
    }

    private int indexOf(Object key) {
        return null == key ? 0 : key.hashCode() & (capacity - 1);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return 0 == size;
    }

    @Override
    public boolean containsKey(Object key) {
        if (null == entrySet) {
            return false;
        }
        for (Entry<K, V> entry : entrySet) {
            if (key.equals(entry.getKey())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        if (null == entrySet) {
            return false;
        }
        for (Entry<K, V> entry : entrySet) {
            if (value.equals(entry.getValue())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(Object key) {
        Node<K, V> node = table[indexOf(key)];
        while (null != node) {
            if (key.equals(node.getKey())) {
                return node.getValue();
            }
            node = node.getNext();
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        if (null == table) {
            table = (Node<K, V>[])new Node[capacity];
        }
        if (null == entrySet) {
            entrySet = new HashSet<Entry<K, V>>();
        }

        if (size > (capacity * loadFactor)) {
            resize();
        }

        V oldValue = value;

        int index = indexOf(key);
        Node<K, V> oldNode = table[index];
        if (null == oldNode) {
            oldNode = new Node<>(key, value);
            table[index] = oldNode;
            entrySet.add(oldNode);
            oldValue = value;
        } else {
            boolean isPut = false;
            Node<K, V> preNode = oldNode;
            for (; oldNode != null; oldNode = oldNode.getNext()) {
                preNode = oldNode;
                if (key.equals(oldNode.getKey())) {
                    oldValue = oldNode.getValue();
                    oldNode.setValue(value);
                    isPut = true;
                    break;
                }
            }
            if (!isPut) {
                preNode.setNext(new Node<>(key, value));
                entrySet.add(preNode.getNext());
                oldValue = value;
            }
        }

        size++;
        return oldValue;
    }

    @Override
    public V remove(Object key) {
        int index = indexOf(key);
        Node<K, V> node = table[index];
        if (null == node) {
            return null;
        }

        Node<K, V> prev = table[index];
        while (null != node) {
            if (key.equals(node.getKey())) {
                if (prev == node) {
                    table[index] = node.getNext();
                } else {
                    prev.setNext(node.getNext());
                }
                entrySet.remove(node);
                size--;
                return node.getValue();
            }
            prev = node;
            node = node.getNext();
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry entry : m.entrySet()) {
            put((K)entry.getKey(), (V)entry.getValue());
        }
    }

    @Override
    public void clear() {
        for (int i = 0; i < capacity; i++) {
            table[i] = null;
        }
        entrySet.clear();
    }

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return entrySet;
    }

    public static void main(String[] args) {
        int a = 7;
        a--;
        a |= a >>> 1;
        a |= a >>> 2;
        a |= a >>> 4;
        a |= a >>> 8;
        a |= a >>> 16;
        a++;
        System.out.println(a);
    }
}
