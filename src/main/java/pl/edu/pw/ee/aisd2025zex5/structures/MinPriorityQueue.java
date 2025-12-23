package pl.edu.pw.ee.aisd2025zex5.structures;

import java.util.ArrayList;
import java.util.List;
import pl.edu.pw.ee.aisd2025zex5.core.HuffmanNode;

public class MinPriorityQueue {
    private final List<HuffmanNode> heap;

    public MinPriorityQueue() {
        this.heap = new ArrayList<>();
    }
    
    public void addAll(List<HuffmanNode> nodes) {
        for (HuffmanNode node : nodes) {
            add(node);
        }
    }

    public void add(HuffmanNode node) {
        heap.add(node);
        siftUp(heap.size() - 1);
    }

    public HuffmanNode poll() {
        if (heap.isEmpty()) {
            return null;
        }
        HuffmanNode result = heap.get(0);
        HuffmanNode last = heap.remove(heap.size() - 1);
        
        if (!heap.isEmpty()) {
            heap.set(0, last);
            siftDown(0);
        }
        return result;
    }

    public int size() {
        return heap.size();
    }

    private void siftUp(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            if (compare(index, parentIndex) >= 0) {
                break;
            }
            swap(index, parentIndex);
            index = parentIndex;
        }
    }

    private void siftDown(int index) {
        int half = heap.size() / 2;
        while (index < half) {
            int leftChild = 2 * index + 1;
            int rightChild = 2 * index + 2;
            int smallest = leftChild;

            if (rightChild < heap.size() && compare(rightChild, leftChild) < 0) {
                smallest = rightChild;
            }

            if (compare(index, smallest) <= 0) {
                break;
            }
            swap(index, smallest);
            index = smallest;
        }
    }

    private int compare(int i, int j) {
        return heap.get(i).compareTo(heap.get(j));
    }

    private void swap(int i, int j) {
        HuffmanNode temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }
}