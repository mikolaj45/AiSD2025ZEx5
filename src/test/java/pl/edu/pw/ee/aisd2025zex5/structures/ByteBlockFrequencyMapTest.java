package pl.edu.pw.ee.aisd2025zex5.structures;

import org.junit.jupiter.api.Test;
import pl.edu.pw.ee.aisd2025zex5.core.HuffmanNode;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class ByteBlockFrequencyMapTest {

    @Test
    void shouldCountFrequenciesCorrectly() {
        ByteBlockFrequencyMap map = new ByteBlockFrequencyMap();
        byte[] keyA = {65};
        byte[] keyB = {66};

        map.increment(keyA);
        map.increment(keyA);
        map.increment(keyB);

        List<HuffmanNode> nodes = map.toNodeList();
        
        assertThat(nodes).hasSize(2);
        
        HuffmanNode nodeA = nodes.stream()
            .filter(n -> n.getSymbol()[0] == 65)
            .findFirst().orElse(null);
            
        assertThat(nodeA).isNotNull();
        assertThat(nodeA.compareTo(new HuffmanNode(null, 2))).isEqualTo(0);
    }

    @Test
    void shouldHandleDeepCopyOfKeys() {
        ByteBlockFrequencyMap map = new ByteBlockFrequencyMap();
        byte[] buffer = {10, 20};

        map.increment(buffer);
        
        buffer[0] = 99;
        map.increment(buffer);

        assertThat(map.toNodeList()).hasSize(2);
    }
}