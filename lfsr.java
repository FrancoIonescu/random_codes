import java.util.BitSet;

class Main {

    private static final int R1_SIZE = 19;
    private static final int R2_SIZE = 22;
    private static final int R3_SIZE = 23;

    private static final int[] R1_TAPS = {13, 16, 17, 18};
    private static final int[] R2_TAPS = {20, 21};
    private static final int[] R3_TAPS = {7, 20, 21, 22};

    private static BitSet registers = new BitSet(64);

    static {
        // Seed simplu: 010101...
        for (int i = 0; i < 64; i++) {
            registers.set(i, (i % 2 == 0));
        }
    }

    private static boolean getFeedback(int start, int[] taps) {
        boolean feedback = false;
        for (int tap : taps) {
            feedback ^= registers.get(start + tap);
        }
        return feedback;
    }

    private static void shiftRegister(int start, int size, int[] taps) {
        boolean feedback = getFeedback(start, taps);
        for (int i = size - 1; i > 0; i--) {
            registers.set(start + i, registers.get(start + i - 1));
        }
        registers.set(start, feedback);
    }

    // Returnăm un singur bit ca byte (0 sau 1)
    private static byte step() {
        shiftRegister(0, R1_SIZE, R1_TAPS);
        shiftRegister(R1_SIZE, R2_SIZE, R2_TAPS);
        shiftRegister(R1_SIZE + R2_SIZE, R3_SIZE, R3_TAPS);

        boolean out1 = registers.get(R1_SIZE - 1);
        boolean out2 = registers.get(R1_SIZE + R2_SIZE - 1);
        boolean out3 = registers.get(R1_SIZE + R2_SIZE + R3_SIZE - 1);

        return (byte) ((out1 ^ out2 ^ out3) ? 1 : 0);
    }

    // Generăm n biți ca array de byte
    public static byte[] generate(int n) {
        byte[] bits = new byte[n];
        for (int i = 0; i < n; i++) {
            bits[i] = step();
        }
        return bits;
    }

    public static void main(String[] args) {
        byte[] secventa = generate(100);
        for (byte bit : secventa) {
            System.out.print(bit);
        }
    }
}
