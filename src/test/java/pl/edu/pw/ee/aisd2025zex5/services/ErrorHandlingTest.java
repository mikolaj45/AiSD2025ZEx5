package pl.edu.pw.ee.aisd2025zex5.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class ErrorHandlingTest {

    private final CompressorService compressor = new CompressorService();
    private final DecompressorService decompressor = new DecompressorService();

    @Test
    @DisplayName("Powinien rzucić błąd, gdy plik wejściowy nie istnieje")
    void shouldThrowWhenInputFileDoesNotExist(@TempDir Path tempDir) {
        // GIVEN
        String nonExistentPath = tempDir.resolve("duch.txt").toString();
        String destPath = tempDir.resolve("wynik.comp").toString();

        // WHEN
        Throwable thrown = catchThrowable(() -> 
            compressor.compress(nonExistentPath, destPath, 1)
        );

        // THEN
        assertThat(thrown)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("does not exist");
    }

    @Test
    @DisplayName("Powinien rzucić błąd, gdy podana ścieżka to katalog, a nie plik")
    void shouldThrowWhenInputIsDirectory(@TempDir Path tempDir) {
        // GIVEN
        String dirPath = tempDir.toString(); // Ścieżka do katalogu
        String destPath = tempDir.resolve("wynik.comp").toString();

        // WHEN
        Throwable thrown = catchThrowable(() -> 
            compressor.compress(dirPath, destPath, 1)
        );

        // THEN
        assertThat(thrown)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("directory");
    }

    @Test
    @DisplayName("Powinien rzucić błąd przy próbie kompresji pustego pliku")
    void shouldThrowWhenCompressingEmptyFile(@TempDir Path tempDir) throws IOException {
        // GIVEN
        Path emptyFile = tempDir.resolve("pusty.txt");
        Files.createFile(emptyFile);
        String destPath = tempDir.resolve("wynik.comp").toString();

        // WHEN
        Throwable thrown = catchThrowable(() -> 
            compressor.compress(emptyFile.toString(), destPath, 1)
        );

        // THEN
        assertThat(thrown)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("empty");
    }

    @Test
    @DisplayName("Powinien rzucić błąd dla nieprawidłowego rozmiaru bloku")
    void shouldThrowForInvalidBlockSize(@TempDir Path tempDir) throws IOException {
        // GIVEN
        Path file = tempDir.resolve("dane.txt");
        Files.writeString(file, "test");
        String destPath = tempDir.resolve("wynik.comp").toString();

        // WHEN (blockSize = 0)
        Throwable thrown = catchThrowable(() -> 
            compressor.compress(file.toString(), destPath, 0)
        );

        // THEN
        assertThat(thrown)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Block size must be >= 1");
    }

    @Test
    @DisplayName("Powinien rzucić błąd przy próbie dekompresji uszkodzonego/losowego pliku")
    void shouldFailDecompressionOnCorruptedFile(@TempDir Path tempDir) throws IOException {
        // GIVEN
        Path corruptFile = tempDir.resolve("smieci.comp");
        // Zapisujemy losowe bajty, które nie mają poprawnego nagłówka Huffmana
        Files.write(corruptFile, new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9}); 
        String destPath = tempDir.resolve("odzyskany.txt").toString();

        // WHEN
        Throwable thrown = catchThrowable(() -> 
            decompressor.decompress(corruptFile.toString(), destPath)
        );

        // THEN
        assertThat(thrown)
            .isInstanceOf(RuntimeException.class);
    }
    
    @Test
    @DisplayName("Powinien rzucić błąd przy próbie dekompresji pustego pliku")
    void shouldThrowWhenDecompressingEmptyFile(@TempDir Path tempDir) throws IOException {
        // GIVEN
        Path emptyFile = tempDir.resolve("puste_archiwum.comp");
        Files.createFile(emptyFile);
        String destPath = tempDir.resolve("odzyskany.txt").toString();

        // WHEN
        Throwable thrown = catchThrowable(() -> 
            decompressor.decompress(emptyFile.toString(), destPath)
        );

        // THEN
        assertThat(thrown)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("empty");
    }
}