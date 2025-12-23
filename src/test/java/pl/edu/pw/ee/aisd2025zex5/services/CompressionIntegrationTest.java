package pl.edu.pw.ee.aisd2025zex5.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.assertj.core.api.Assertions.assertThat;

class CompressionIntegrationTest {

    @Test
    void shouldCompressAndDecompressFileCorrectly(@TempDir Path tempDir) throws IOException {
        // GIVEN
        Path sourceFile = tempDir.resolve("input.txt");
        Path compressedFile = tempDir.resolve("archive.comp");
        Path decodedFile = tempDir.resolve("output.txt");

        String content = "AAAAABBBCCDAAABBB";
        Files.writeString(sourceFile, content);

        CompressorService compressor = new CompressorService();
        DecompressorService decompressor = new DecompressorService();

        // WHEN
        compressor.compress(sourceFile.toString(), compressedFile.toString(), 1);
        decompressor.decompress(compressedFile.toString(), decodedFile.toString());

        // THEN
        assertThat(compressedFile).exists();
        assertThat(decodedFile).exists();
        assertThat(Files.readString(decodedFile)).isEqualTo(content);
    }
    
    @Test
    void shouldHandleEmptyFile(@TempDir Path tempDir) throws IOException {
        // GIVEN
        Path emptyFile = tempDir.resolve("empty.txt");
        Path compFile = tempDir.resolve("empty.comp");
        Files.createFile(emptyFile);
        
        CompressorService compressor = new CompressorService();

        // WHEN / THEN
        try {
            compressor.compress(emptyFile.toString(), compFile.toString(), 1);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("empty");
        }
    }
}