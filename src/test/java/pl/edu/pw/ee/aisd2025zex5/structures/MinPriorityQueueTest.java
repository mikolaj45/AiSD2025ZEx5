package pl.edu.pw.ee.aisd2025zex5.structures;

import org.junit.jupiter.api.Test;
import pl.edu.pw.ee.aisd2025zex5.core.HuffmanNode;
import static org.assertj.core.api.Assertions.assertThat;

class MinPriorityQueueTest {

    @Test
    void shouldReturnElementsInAscendingOrder() {
        MinPriorityQueue queue = new MinPriorityQueue();
        
        queue.add(new HuffmanNode(new byte[]{1}, 10));
        queue.add(new HuffmanNode(new byte[]{2}, 5));
        queue.add(new HuffmanNode(new byte[]{3}, 20));
        queue.add(new HuffmanNode(new byte[]{4}, 1));

        assertThat(queue.size()).isEqualTo(4);
        assertThat(queue.poll().compareTo(new HuffmanNode(null, 1))).isEqualTo(0);
        assertThat(queue.poll().compareTo(new HuffmanNode(null, 5))).isEqualTo(0);
        assertThat(queue.poll().compareTo(new HuffmanNode(null, 10))).isEqualTo(0);
        assertThat(queue.poll().compareTo(new HuffmanNode(null, 20))).isEqualTo(0);
        assertThat(queue.poll()).isNull();
    }
}