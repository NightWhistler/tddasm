package net.nightwhistler.tddasm.mos65xx;

import io.vavr.collection.List;

import static net.nightwhistler.ByteUtils.littleEndianBytesToInt;

public class ProgramElementsBuilder {
    private List<ProgramElement> programElements = List.empty();

    private ProgramElementsBuilder(List<ProgramElement> elements) {
        this.programElements = elements;
    }

    public ProgramElementsBuilder() {

    }

    public ProgramElementsBuilder startAt(Operand.TwoByteAddress address) {
        return withElement(
                new StartingAddress(littleEndianBytesToInt(
                        address.lowByte(),
                        address.highByte()
                ))
        );
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

    public ProgramElementsBuilder lda(Operand operand) {
        return withOperation(OpCode.LDA, operand);
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
        return withOperation(OpCode.BEQ, new Operand.LabelOperand(label, true));
    }

    public ProgramElementsBuilder bne(String label) {
        return withOperation(OpCode.BNE, new Operand.LabelOperand(label, true));
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
