package pl.edu.pw.ee.aisd2025zex5.structures;

public class ByteBlockCodeMap {
    private static final int INITIAL_CAPACITY = 4096;
    private Entry[] buckets;

    private static class Entry {
        byte[] key;
        String code;
        Entry next;

        Entry(byte[] key, String code, Entry next) {
            this.key = key;
            this.code = code;
            this.next = next;
        }
    }

    public ByteBlockCodeMap() {
        this.buckets = new Entry[INITIAL_CAPACITY];
    }

    public void put(byte[] keyBlock, String code) {
        int hash = getHash(keyBlock);
        int index = Math.abs(hash % buckets.length);

        Entry current = buckets[index];
        while (current != null) {
            if (arraysEqual(current.key, keyBlock)) {
                current.code = code;
                return;
            }
            current = current.next;
        }

        byte[] keyCopy = new byte[keyBlock.length];
        System.arraycopy(keyBlock, 0, keyCopy, 0, keyBlock.length);
        
        buckets[index] = new Entry(keyCopy, code, buckets[index]);
    }

    public String get(byte[] keyBlock) {
        int hash = getHash(keyBlock);
        int index = Math.abs(hash % buckets.length);

        Entry current = buckets[index];
        while (current != null) {
            if (arraysEqual(current.key, keyBlock)) {
                return current.code;
            }
            current = current.next;
        }
        return null;
    }

    private int getHash(byte[] bytes) {
        int result = 1;
        for (byte element : bytes) {
            result = 31 * result + element;
        }
        return result;
    }

    private boolean arraysEqual(byte[] a, byte[] b) {
        if (a == b) return true;
        if (a == null || b == null || a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) return false;
        }
        return true;
    }
}