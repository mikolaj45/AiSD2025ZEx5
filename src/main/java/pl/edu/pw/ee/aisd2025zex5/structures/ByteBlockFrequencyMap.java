package pl.edu.pw.ee.aisd2025zex5.structures;

import java.util.ArrayList;
import java.util.List;
import pl.edu.pw.ee.aisd2025zex5.core.HuffmanNode;

public class ByteBlockFrequencyMap {
    private static final int INITIAL_CAPACITY = 4096;
    private Entry[] buckets;
    private int size;

    private static class Entry {
        byte[] key;
        long frequency;
        Entry next;

        Entry(byte[] key, long frequency, Entry next) {
            this.key = key;
            this.frequency = frequency;
            this.next = next;
        }
    }

    public ByteBlockFrequencyMap() {
        this.buckets = new Entry[INITIAL_CAPACITY];
        this.size = 0;
    }

    public void increment(byte[] keyBlock) {
        int hash = getHash(keyBlock);
        int index = Math.abs(hash % buckets.length);

        Entry current = buckets[index];
        while (current != null) {
            if (arraysEqual(current.key, keyBlock)) {
                current.frequency++;
                return;
            }
            current = current.next;
        }

        byte[] keyCopy = new byte[keyBlock.length];
        System.arraycopy(keyBlock, 0, keyCopy, 0, keyBlock.length);
        
        buckets[index] = new Entry(keyCopy, 1, buckets[index]);
        size++;
    }

    public List<HuffmanNode> toNodeList() {
        List<HuffmanNode> nodes = new ArrayList<>();
        for (Entry bucket : buckets) {
            Entry current = bucket;
            while (current != null) {
                nodes.add(new HuffmanNode(current.key, current.frequency));
                current = current.next;
            }
        }
        return nodes;
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