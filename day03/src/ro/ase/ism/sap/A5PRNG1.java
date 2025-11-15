package ro.ase.ism.sap;

import java.util.Arrays;

public class A5PRNG1 {

    // Sizes
    private static final int R1_SIZE = 19;
    private static final int R2_SIZE = 22;
    private static final int R3_SIZE = 23;

    // Tap positions (1 = LSB)
    private static final int[] R1_TAPS = {14, 17, 18, 19};
    private static final int[] R2_TAPS = {21, 22};
    private static final int[] R3_TAPS = {8, 21, 22, 23};

    // Offsets inside the 64-bit long
    private static final int R1_OFFSET = 0;          // bits 0-18
    private static final int R2_OFFSET = R1_OFFSET + R1_SIZE; // bits 19-40
    private static final int R3_OFFSET = R2_OFFSET + R2_SIZE; // bits 41-63

    // Initialize register from seed
    public static long initRegister(long seed) {
        return seed;
    }

    // Get bit at position (1 = LSB of register)
    private static byte getBitAtIndex(long reg, int pos) {
        return (byte)((reg >> (pos - 1)) & 1);
    }

    // Apply tap sequence to a register
    private static byte applyTapSequence(long reg, int[] taps, int offset) {
        byte result = 0;
        for(int t : taps) {
            result ^= getBitAtIndex(reg, offset + t);
        }
        return result;
    }

    // Shift register to right, insert input at MSB
    private static long shiftRegister(long reg, int size, int offset, byte input) {
        long mask = (1L << size) - 1; // mask for register
        long value = (reg >> offset) & mask; // extract register
        value >>>= 1;
        if(input == 1) {
            value |= (1L << (size - 1)); // insert at MSB
        }
        // clear old bits in long and insert new value
        reg &= ~(mask << offset);
        reg |= (value << offset);
        return reg;
    }

    // Generate n pseudo-random bits
    public static byte[] getPseudoRandomBits(long reg, int nBits) {
        byte[] result = new byte[nBits];
        for(int i = 0; i < nBits; i++) {
            byte t1 = applyTapSequence(reg, R1_TAPS, R1_OFFSET);
            byte t2 = applyTapSequence(reg, R2_TAPS, R2_OFFSET);
            byte t3 = applyTapSequence(reg, R3_TAPS, R3_OFFSET);

            // XOR outputs of three LFSRs for final bit
            byte outBit = (byte)(getBitAtIndex(reg, R1_OFFSET + 1) ^
                    getBitAtIndex(reg, R2_OFFSET + 1) ^
                    getBitAtIndex(reg, R3_OFFSET + 1));
            result[i] = outBit;

            // Shift registers
            reg = shiftRegister(reg, R1_SIZE, R1_OFFSET, t1);
            reg = shiftRegister(reg, R2_SIZE, R2_OFFSET, t2);
            reg = shiftRegister(reg, R3_SIZE, R3_OFFSET, t3);
        }
        return result;
    }

    public static void main(String[] args) {
        long seed = 0xAAF00F55AAF00F55L;
        long reg = initRegister(seed);

        byte[] bits = getPseudoRandomBits(reg, 50);

        System.out.println(Arrays.toString(bits));
    }
}
