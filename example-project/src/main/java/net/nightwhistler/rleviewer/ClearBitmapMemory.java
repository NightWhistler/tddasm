package net.nightwhistler.rleviewer;

import io.vavr.collection.List;
import net.nightwhistler.tddasm.annotation.CompileProgram;
import net.nightwhistler.tddasm.mos65xx.Operand;
import net.nightwhistler.tddasm.mos65xx.ProgramBuilder;
import net.nightwhistler.tddasm.mos65xx.ProgramElement;

import static net.nightwhistler.tddasm.mos65xx.Operand.address;
import static net.nightwhistler.tddasm.mos65xx.Operand.value;
import static net.nightwhistler.tddasm.mos65xx.Operand.zeroPage;

@CompileProgram("clr_bitmap.prg")
public class ClearBitmapMemory {

    private static final int VIC_REG_1=0xD011;
    private static final int VIC_REG_2=0xD016;
    private static final int VIC_BITMAP_VECTOR=0xD018;
    private static final int BITMAP_DATA_START=0x2000;
    private static final Operand.TwoByteAddress BITMAP_DATA_END= address(0x3F3F);
    private static final Operand.OneByteAddress MEM_VECTOR_LOW=zeroPage(0xFB);
    private static final Operand.OneByteAddress MEM_VECTOR_HIGH=zeroPage(0xFC);
    private static final int KERNAL_CLEAR_SCR=0x544;
    private static final Operand.ByteValue CLEAR_VALUE=value(0x0F);


    /**
     * *=$0801   ; Starting Address BASIC + 1 =&gt; SYS 2049
     *
    *
     *  jsr .fill_memory
     *  lda #01
     *  jsr .set_video_mem
     *  rts
     */
    /**
     * .fill_memory
     *  ; We want to clear addresses $2000 - $3F3F, so 8,000 addresses
     * ; Start y at 0
     *  ldy #$00 ; start the loop at $FF so the first iny gives 0
     * .outer_loop:
     *  lda #CLEAR_VALUE
     * .inner_loop:
     *  ; We store in the low byte of the memory vector, but the instruction will also
     *  ; read the next (high) byte
     *    sta (MEM_VECTOR_LOW), y
     *    iny; we increase y, until it rolls over to 0
     *    beq .end_inner_loop ; If the roll-over occurred jump out of the loop
     *    cpy #<BITMAP_DATA_END ; When the low byte matches the end, check the high byte
     *    bne .inner_loop
     *
     *    lda #>BITMAP_DATA_END ; Check against high byte of bitmap vec
     *    cmp MEM_VECTOR_HIGH
     *    beq .loop_end
     *    lda #CLEAR_VALUE
     *    jmp .inner_loop
     * .end_inner_loop:
     *  ; After a roll-over we increase the high byte by 1
     *  inc MEM_VECTOR_HIGH
     *  jmp .outer_loop
     *
     * .loop_end:
     *  sty MEM_VECTOR_LOW ; Store the y register, so the vector points to the final address for verification
     *  lda #CLEAR_VALUE
     *  sta BITMAP_DATA_END ; Our loop ends 1 address early
     *  rts
     *
    */

    static List<ProgramElement> fillMemory() {

        //This is a subroutine to clear 8000 memory addresses,
        return new ProgramBuilder().
              label("fill_memory")
                .ldy(value(0x00)) // Start Y register at 0
              .label("outer_loop")
                .lda(CLEAR_VALUE)
              .label("inner_loop")
                // We store in the low byte of the memory vector, but the instruction will also
                // read the next (high) byte
                .sta(MEM_VECTOR_LOW.indirectIndexedY())
                .iny() //We increase y, until it rolls over to 0
                .beq("loop_end") //If the roll-over occurred, jump out of the loop
                .cpy(value(BITMAP_DATA_END.lowByte()))
                .bne("inner_loop")
                .lda(value(BITMAP_DATA_END.highByte())) //Check against high byte of bitmap vec
                .cmp(MEM_VECTOR_HIGH)
                .beq("loop_end")
                .lda(CLEAR_VALUE)
                .jmp("inner_loop")
              .label("loop_end")
                .sty(MEM_VECTOR_LOW) //Store the y register, so the vector points to the final address for verification
                .lda(CLEAR_VALUE)
                .sta(BITMAP_DATA_END) //Our loops ends 1 address early
                .rts()

                .buildElements();
    }
}
