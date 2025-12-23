package pl.edu.pw.ee.aisd2025zex5.core;

public class HuffmanNode implements Comparable<HuffmanNode> {
    private final byte[] symbol;
    private final long frequency;
    private HuffmanNode left;
    private HuffmanNode right;

    public HuffmanNode(byte[] symbol, long frequency) {
        this.symbol = symbol;
        this.frequency = frequency;
    }

    public HuffmanNode(HuffmanNode left, HuffmanNode right) {
        this.symbol = null;
        this.frequency = left.frequency + right.frequency;
        this.left = left;
        this.right = right;
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }

    public byte[] getSymbol() {
        return symbol;
    }

    public long getFrequency() {
        return frequency;
    }

    public HuffmanNode getLeft() {
        return left;
    }

    public HuffmanNode getRight() {
        return right;
    }

    @Override
    public int compareTo(HuffmanNode other) {
        return Long.compare(this.frequency, other.frequency);
    }
}