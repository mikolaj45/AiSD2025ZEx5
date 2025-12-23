package pl.edu.pw.ee.aisd2025zex5.structures;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ByteBlockCodeMapTest {

    @Test
    void shouldRetrieveCodesCorrectly() {
        ByteBlockCodeMap map = new ByteBlockCodeMap();
        byte[] key1 = {1, 2};
        byte[] key2 = {3, 4};

        map.put(key1, "001");
        map.put(key2, "111");

        assertThat(map.get(key1)).isEqualTo("001");
        assertThat(map.get(key2)).isEqualTo("111");
        assertThat(map.get(new byte[]{9, 9})).isNull();
    }
}