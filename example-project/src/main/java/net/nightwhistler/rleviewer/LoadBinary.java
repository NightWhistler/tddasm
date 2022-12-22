package net.nightwhistler.rleviewer;

import net.nightwhistler.tddasm.c64.kernal.ChrOut;
import net.nightwhistler.tddasm.mos65xx.Operand;
import net.nightwhistler.tddasm.mos65xx.Processor;
import net.nightwhistler.tddasm.c64.screen.TextModeScreen;

public class LoadBinary {
    public static void main(String argv[]) {

        try {
            var stream = new LoadBinary().getClass().getResourceAsStream("/helloworld");
            byte[] data = stream.readAllBytes();

            var start = Operand.address(0x80D);
            Processor processor = new Processor();
            processor.registerJavaRoutine(new ChrOut());
            TextModeScreen screen = new TextModeScreen(processor);
            processor.registerEventListener( e -> System.out.println(e));
            processor.loadBinary(data);

            processor.run(start);

            System.out.println(screen.getScreenContents());
        } catch (Exception e) {
            e.printStackTrace();;
        }

    }
}
