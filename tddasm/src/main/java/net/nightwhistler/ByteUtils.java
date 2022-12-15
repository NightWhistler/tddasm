package net.nightwhistler;

public class ByteUtils {

    public static int JAVA_BYTE_0_MASK = 0x000000FF;
    public static int JAVA_BYTE_1_MASK = 0x0000FF00;
    public static int JAVA_BYTE_2_MASK = 0x00FF0000;
    public static int JAVA_BYTE_3_MASK = 0xFF000000;

    /**
     * Converts a Java BigEndian value to a littleEndian
     * series of bytes. We use the fact here that Java byte
     * arrays have known size, so we'll return the smallest
     * array the value will fit in.
     *
     * @param value
     * @return
     */
    public static byte[] toLittleEndianBytes(int value) {
        byte byte0 = (byte) (value & JAVA_BYTE_0_MASK);
        byte byte1 = (byte) ((value & JAVA_BYTE_1_MASK) >>> 8);
        byte byte2 = (byte) ((value & JAVA_BYTE_2_MASK) >>> 16);
        byte byte3 = (byte) ((value & JAVA_BYTE_3_MASK) >>> 32);

        if (byte3 == 0 && byte2 == 0 && byte1 == 0) {
            return new byte[] {byte0};
        } else if (byte3 ==0 && byte2 == 0) {
            return new byte[] { byte0, byte1 };
        } else if (byte3 == 0 ) {
            return new byte[] { byte0, byte1, byte2 };
        }

        return new byte[]{byte0, byte1, byte2, byte3};
    }

    public static int littleEndianBytesToInt(byte... values) {
        int result = 0;

        for (int i=0; i < values.length; i++) {
            int unsigned = Byte.toUnsignedInt(values[i]);
            result += unsigned << (i*8);
        }

        return result;
    }

    /**
     * Pads a byte array with trailing zeros.
     *
     * This is useful when using bytes to represent numbers in a
     * littleEndian system. Since the least significant byte comes
     * first, adding zeros as the end does not change the value.
     *
     * @param input
     * @param length
     * @return
     */
    public static byte[] padToLengthLittleEndian(byte[] input, int length) {
        if (input.length >= length) {
            return input;
        }

        //We use the fact that an array is initalized with all bytes set to 0
        byte[] result = new byte[length];

        //Copy input into the new array
        System.arraycopy(input, 0, result, 0, input.length);

        return result;
    }

    /**
     * Small util method which allows easy creation of
     * byte arrays from int values.
     *
     * @param bytes
     * @return
     */
    public static byte[] bytes(int... bytes) {
        byte[] result =  new byte[bytes.length];
        for (int i=0; i < bytes.length; i++) {
            result[i] = (byte) (bytes[i] & JAVA_BYTE_0_MASK);
        }

        return result;
    }

    public static byte highByte(int value) {
        return (byte) ((value & JAVA_BYTE_1_MASK) >>> 8);
    }

    public static byte lowByte(int value) {
        return (byte) (value & JAVA_BYTE_0_MASK);
    }

}
