package net.nightwhistler.tddasm.screen;

import java.nio.charset.Charset;
import java.util.function.Function;

public class ScreenCode {
    public static byte[] fromScreenCodes(byte[] input) {
        return map(input, ScreenCode::fromScreenCode);
    }

    public static byte[] toScreenCodes(byte[] input) {
        return map(input, ScreenCode::toScreenCode);
    }

    private static byte[] map(byte[] input, Function<Byte, Byte> f) {
        byte[] result = new byte[input.length];
        for ( int i=0; i < input.length; i++) {
            result[i] = f.apply(input[i]);
        }

        return result;
    }

    public static byte fromScreenCode(byte input) {
        if (input == 0) {
            return '@';
        } else if (input > 0 && input < 26) {
            return (byte) (input + 96);
        } else {
            return input;
        }
    }

    public static byte toScreenCode(byte input) {
        if (input == '@') {
            return 0;
        } else if (input >= 97 && input <= 122 ) {
            return (byte) (input - 96);
        } else {
            return input;
        }
    }
}
