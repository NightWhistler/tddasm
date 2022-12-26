package net.nightwhistler.tddasm.asmparser;

import io.vavr.collection.List;
import net.nightwhistler.tddasm.mos65xx.ProgramElement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AsmFileParser {

//    private static final Pattern pattern = Pattern.compile("\s*([a-zA-Z]{0,3})\s.*([#$0-9A-Fa-f].*) (;\\w.*)");
   private static final Pattern pattern = Pattern.compile("\s*([a-zA-Z]{0,3})\s*([#0-9].*)");

    public List<ProgramElement> parseLine(String line) {
//        List<String> lineElements = List.of(line.split("\s"));
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
        } else {
            throw new IllegalArgumentException("Doesn't match");
        }

        return List.empty();
    }
}
