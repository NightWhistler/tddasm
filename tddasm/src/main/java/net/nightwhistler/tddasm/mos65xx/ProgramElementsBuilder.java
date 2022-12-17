package net.nightwhistler.tddasm.mos65xx;

import io.vavr.collection.List;

import java.nio.charset.Charset;

import static net.nightwhistler.tddasm.mos65xx.AddressingMode.Relative;

public class ProgramElementsBuilder {
    private List<ProgramElement> programElements = List.empty();

    private ProgramElementsBuilder(List<ProgramElement> elements) {
        this.programElements = elements;
    }

    public ProgramElementsBuilder() {

    }

    /**
     * Adds all the elements from another source, like another builder
     * @param elements
     * @return
     */
    public ProgramElementsBuilder include(List<ProgramElement> elements) {
        return new ProgramElementsBuilder(this.programElements.appendAll(elements));
    }

    public ProgramElementsBuilder label(String label) {
        return withElement(new Label(label));
    }

    public ProgramElementsBuilder text(String text) {
        return data(text.getBytes(Charset.forName("ASCII")));
    }

    public ProgramElementsBuilder screenCodes(String text) {
        byte[] ascii = text.getBytes(Charset.forName("ASCII"));
        byte[] petscii = new byte[ascii.length];
        for (int i =0; i < ascii.length; i++ ) {
            byte a = ascii[i];
            byte p;

            if (a >= 97 && a < 122 ) {
                p = (byte) (a - 96);
            } else {
                p = a;
            }

            petscii[i] = p;
        }

        return data(petscii);
    }

    public ProgramElementsBuilder data(byte[] data) {
        return withElement(new Data(data));
    }

    public ProgramElementsBuilder lda(Operand operand) {
        return withOperation(OpCode.LDA, operand);
    }

    public ProgramElementsBuilder ldx(Operand operand) {
        return withOperation(OpCode.LDX, operand);
    }

    public ProgramElementsBuilder dex() {
        return withOperation(OpCode.DEX, Operand.noValue());
    }

    public ProgramElementsBuilder sta(Operand operand) {
        return withOperation(OpCode.STA, operand);
    }

    public ProgramElementsBuilder rts() {
        return withOperation(OpCode.RTS, Operand.noValue());
    }

    public ProgramElementsBuilder sty(Operand operand) {
        return withOperation(OpCode.STY, operand);
    }

    public ProgramElementsBuilder iny() {
        return withOperation(OpCode.INY, Operand.noValue());
    }

    public ProgramElementsBuilder inx() {
        return withOperation(OpCode.INX, Operand.noValue());
    }

    public ProgramElementsBuilder ldy(Operand operand) {
        return withOperation(OpCode.LDY, operand);
    }

    public ProgramElementsBuilder jmp(String label) {
        return withOperation(OpCode.JMP, new Operand.LabelOperand(label));
    }

    public ProgramElementsBuilder jmp(Operand.AddressOperand operand) {
        return withOperation(OpCode.JMP, operand);
    }

    public ProgramElementsBuilder jsr(String label) {
        return withOperation(OpCode.JSR, new Operand.LabelOperand(label));
    }

    public ProgramElementsBuilder jsr(Operand.TwoByteAddress operand) {
        return withOperation(OpCode.JSR, operand);
    }

    public ProgramElementsBuilder beq(String label) {
        return withOperation(OpCode.BEQ, new Operand.LabelOperand(label, Relative));
    }

    public ProgramElementsBuilder bne(String label) {
        return withOperation(OpCode.BNE, new Operand.LabelOperand(label, Relative));
    }

    public ProgramElementsBuilder cpx(Operand operand) {
        return withOperation(OpCode.CPX, operand);
    }

    public ProgramElementsBuilder cpy(Operand operand) {
        return withOperation(OpCode.CPY, operand);
    }

    public ProgramElementsBuilder cmp(Operand operand) {
        return withOperation(OpCode.CMP, operand);
    }

    private ProgramElementsBuilder withOperation(OpCode opCode, Operand operand) {
        return withElement(new Operation(opCode, operand));
    }

    private ProgramElementsBuilder withElement(ProgramElement element) {
        return new ProgramElementsBuilder(programElements.append(element));
    }

    public List<ProgramElement> build() {
        return programElements;
    }
}
