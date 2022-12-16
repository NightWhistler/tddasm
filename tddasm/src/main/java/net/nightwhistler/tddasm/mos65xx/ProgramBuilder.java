package net.nightwhistler.tddasm.mos65xx;

import io.vavr.collection.List;
import net.nightwhistler.ByteUtils;

import static net.nightwhistler.ByteUtils.littleEndianBytesToInt;

public class ProgramBuilder {
    private List<ProgramElement> programElements = List.empty();

    private ProgramBuilder(List<ProgramElement> elements) {
        this.programElements = elements;
    }

    public ProgramBuilder() {

    }

    public ProgramBuilder startAt(Operand.TwoByteAddress address) {
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
    public ProgramBuilder include(List<ProgramElement> elements) {
        return new ProgramBuilder(this.programElements.appendAll(elements));
    }

    public ProgramBuilder label(String label) {
        return withElement(new Label(label));
    }

    public ProgramBuilder lda(Operand operand) {
        return withOperation(OpCode.LDA, operand);
    }

    public ProgramBuilder sta(Operand operand) {
        return withOperation(OpCode.STA, operand);
    }

    public ProgramBuilder rts() {
        return withOperation(OpCode.RTS, Operand.noValue());
    }

    public ProgramBuilder sty(Operand operand) {
        return withOperation(OpCode.STY, operand);
    }

    public ProgramBuilder iny() {
        return withOperation(OpCode.INY, Operand.noValue());
    }

    public ProgramBuilder inx() {
        return withOperation(OpCode.INX, Operand.noValue());
    }

    public ProgramBuilder ldy(Operand operand) {
        return withOperation(OpCode.LDY, operand);
    }

    public ProgramBuilder jmp(String label) {
        return withOperation(OpCode.JMP, new Operand.LabelOperand(label));
    }

    public ProgramBuilder jmp(Operand.AddressOperand operand) {
        return withOperation(OpCode.JMP, operand);
    }

    public ProgramBuilder jsr(String label) {
        return withOperation(OpCode.JSR, new Operand.LabelOperand(label));
    }

    public ProgramBuilder jsr(Operand.TwoByteAddress operand) {
        return withOperation(OpCode.JSR, operand);
    }

    public ProgramBuilder beq(String label) {
        return withOperation(OpCode.BEQ, new Operand.LabelOperand(label));
    }


    public ProgramBuilder bne(String label) {
        return withOperation(OpCode.BNE, new Operand.LabelOperand(label));
    }

    public ProgramBuilder cpy(Operand operand) {
        return withOperation(OpCode.CPY, operand);
    }

    public ProgramBuilder cmp(Operand operand) {
        return withOperation(OpCode.CMP, operand);
    }

    private ProgramBuilder withOperation(OpCode opCode, Operand operand) {
        return withElement(new Operation(opCode, operand));
    }

    private ProgramBuilder withElement(ProgramElement element) {
        return new ProgramBuilder(programElements.append(element));
    }

    public List<ProgramElement> build() {
        return programElements;
    }
}
