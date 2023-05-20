import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static BlockingQueue<String> bufferA = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> bufferB = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> bufferC = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) throws InterruptedException {

        Thread textGen = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                String text = generateText("abc", 100_000);
                try {
                    bufferA.put(text);
                    bufferB.put(text);
                    bufferC.put(text);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        textGen.start();

        Thread countA = new Thread(() -> {
            int maxA = findMaxLetterCount(bufferA, 'a');
            System.out.println("Максимальное количество a во всех текстах: " + maxA);
        });
        countA.start();

        Thread countB = new Thread(() -> {
            int maxB = findMaxLetterCount(bufferB, 'b');
            System.out.println("Максимальное количество b во всех текстах: " + maxB);
        });
        countB.start();

        Thread countC = new Thread(() -> {
            int maxC = findMaxLetterCount(bufferC, 'c');
            System.out.println("Максимальное количество c во всех текстах: " + maxC);
        });
        countC.start();

        countA.join();
        countB.join();
        countC.join();
    }
    public static int findMaxLetterCount(BlockingQueue<String> buffer, char letter) {
        int count = 0;
        int max = 0;
        String text;

        for (int i = 0; i < 10_000; i++) {
            try {
                text = buffer.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            for (char current : text.toCharArray()) {
                if (current == letter) {
                    count++;
                }
                if (count > max) {
                    max = count;
                }
            }
        }
        return max;
    }
    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}