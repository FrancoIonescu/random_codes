package ro.ase.ism.sap;

public class A5PRNG {

    // Sizes of the three LFSRs
    private static final int R1_SIZE = 19;
    private static final int R2_SIZE = 22;
    private static final int R3_SIZE = 23;

    // Tap positions (1 = LSB)
    private static final int[] R1_TAPS = {14, 17, 18, 19};
    private static final int[] R2_TAPS = {21, 22};
    private static final int[] R3_TAPS = {8, 21, 22, 23};

    // The single 64-bit register containing all three LFSRs
    private static long registers = 0xAAF00F55AAF00F55L;


    // --- Get bit at index (1..64, LSB = 1) ---
    public static byte getBitAtIndex(long value, int index) {
        long mask = 1L << (index - 1);
        return (byte) ((value & mask) != 0 ? 1 : 0);
    }

    // --- Set bit at index (1..64, LSB = 1) ---
    public static void setBitAtIndex(int index, boolean bitValue) {
        long mask = 1L << (index - 1);
        if (bitValue) {
            registers |= mask;
        } else {
            registers &= ~mask;
        }
    }

    // --- Calculate feedback for a given LFSR ---
    private static boolean getFeedback(int startIndex, int size, int[] taps) {
        boolean fb = false;
        for (int tap : taps) {
            fb ^= getBitAtIndex(registers, startIndex + tap - 1) != 0;
        }
        return fb;
    }

    // --- Shift register right (MSB <- feedback) ---
    private static void shiftRegister(int startIndex, int size, int[] taps) {
        boolean feedback = getFeedback(startIndex, size, taps);
        for (int i = size; i > 1; i--) {
            setBitAtIndex(startIndex + i - 1, getBitAtIndex(registers, startIndex + i - 2) != 0);
        }
        setBitAtIndex(startIndex, feedback); // insert feedback at LSB
    }

    // --- Step function: shift all 3 LFSRs and produce 1 bit output ---
    private static byte step() {
        shiftRegister(1, R1_SIZE, R1_TAPS);
        shiftRegister(R1_SIZE + 1, R2_SIZE, R2_TAPS);
        shiftRegister(R1_SIZE + R2_SIZE + 1, R3_SIZE, R3_TAPS);

        boolean out1 = getBitAtIndex(registers, R1_SIZE) != 0; // MSB of R1
        boolean out2 = getBitAtIndex(registers, R1_SIZE + R2_SIZE) != 0; // MSB of R2
        boolean out3 = getBitAtIndex(registers, R1_SIZE + R2_SIZE + R3_SIZE) != 0; // MSB of R3

        return (byte) ((out1 ^ out2 ^ out3) ? 1 : 0);
    }

    // --- Generate n pseudo-random bits ---
    public static byte[] getPseudoRandomBits(int n) {
        byte[] bits = new byte[n];
        for (int i = 0; i < n; i++) {
            bits[i] = step();
        }
        return bits;
    }

    // --- Main method to test the generator ---
    public static void main(String[] args) {
        // Generate 100 bits
        byte[] seq = getPseudoRandomBits(100);
        for (byte bit : seq) {
            System.out.print(bit);
        }
    }
}
