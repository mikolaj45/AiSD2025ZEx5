package pl.edu.pw.ee.aisd2025zex5.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BitInputStream implements AutoCloseable {
    private final InputStream inputStream;
    private int currentByte;
    private int numBitsRemaining;

    public BitInputStream(String filePath) throws IOException {
        this.inputStream = new FileInputStream(filePath);
        this.currentByte = 0;
        this.numBitsRemaining = 0;
    }

    public int readBit() throws IOException {
        if (numBitsRemaining == 0) {
            currentByte = inputStream.read();
            if (currentByte == -1) {
                return -1;
            }
            numBitsRemaining = 8;
        }

        numBitsRemaining--;
        return (currentByte >> numBitsRemaining) & 1;
    }
    
    public int readByte() throws IOException {
        int result = 0;
        for (int i = 0; i < 8; i++) {
            int bit = readBit();
            if (bit == -1) return -1;
            result = (result << 1) | bit;
        }
        return result;
    }
    
    public long readLong() throws IOException {
        long result = 0;
        for (int i = 0; i < 64; i++) {
            int bit = readBit();
            if (bit == -1) throw new IOException("Unexpected EOF while reading long");
            result = (result << 1) | bit;
        }
        return result;
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}