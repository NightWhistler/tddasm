package net.nightwhistler.tddasm.mos65xx;


import io.vavr.collection.Stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record Data(byte[] bytes) implements ProgramElement {

    @Override
    public String toString() {
        String base = "!bytes ";

        String bytesAsText = Stream.ofAll(bytes)
                .map(b -> "$" + Integer.toHexString(Byte.toUnsignedInt(b)))
                .collect(Collectors.joining(","));

        return base + bytesAsText;
    }
}
