package dioscuri.module.cpu;

import dioscuri.AbstractInstructionTest;
import org.junit.Test;

import static org.junit.Assert.*;

public class Instruction_AND_EvGvTest extends AbstractInstructionTest {

    public Instruction_AND_EvGvTest() throws Exception {
        super(80448, "AND_EvGv.bin");
    }

    /*
    * Test method for 'com.tessella.emulator.module.cpu.Instruction_AND_EvGv.execute()'
    */
    @Test
    public void testExecute() {
        String AX_ERROR = "AX contains wrong value";
        String CX_ERROR = "CX contains wrong value";
        String SF_ERROR = "SF incorrect";
        String ZF_ERROR = "ZF incorrect";
        String PF_ERROR = "PF incorrect";

        // Load registers, memory with prearranged values (4 instructions)
        cpu.startDebug();    // MOV AX, 0xFFFF
        cpu.startDebug();    // MOV [0000], AX
        cpu.startDebug();    // MOV [0002], AX
        cpu.startDebug();    // MOV AX, 0x0000
        cpu.startDebug();    // MOV CX, 0xAA55
        assertEquals(CX_ERROR, cpu.getRegisterValue("CX")[0], (byte) 0xAA);
        assertEquals(CX_ERROR, cpu.getRegisterValue("CX")[1], (byte) 0x55);

        // AND mem, reg
        cpu.startDebug();    // AND [BX+SI], CX
        assertTrue(SF_ERROR, cpu.getFlagValue('S'));
        assertTrue(PF_ERROR, cpu.getFlagValue('P'));
        assertFalse(ZF_ERROR, cpu.getFlagValue('Z'));
        cpu.startDebug();    // MOV AX, [0000]
        assertEquals(AX_ERROR, cpu.getRegisterValue("AX")[0], (byte) 0xAA);
        assertEquals(AX_ERROR, cpu.getRegisterValue("AX")[1], (byte) 0x55);
        cpu.startDebug();    // MOV AX, 0x0000

        // AND mem+8b,reg
        cpu.startDebug();    // AND [BX+DI+02], CX
        assertTrue(SF_ERROR, cpu.getFlagValue('S'));
        assertTrue(PF_ERROR, cpu.getFlagValue('P'));
        assertFalse(ZF_ERROR, cpu.getFlagValue('Z'));
        cpu.startDebug();    // MOV AX, [0002]
        assertEquals(AX_ERROR, cpu.getRegisterValue("AX")[0], (byte) 0xAA);
        assertEquals(AX_ERROR, cpu.getRegisterValue("AX")[1], (byte) 0x55);
        cpu.startDebug();    // MOV AX, 0x0000

        // AND mem+16b,reg
        cpu.startDebug();    // INC BP
        cpu.startDebug();    // AND [BP+0x0100], CX
        assertTrue(SF_ERROR, cpu.getFlagValue('S'));
        assertTrue(PF_ERROR, cpu.getFlagValue('P'));
        assertFalse(ZF_ERROR, cpu.getFlagValue('Z'));
        cpu.startDebug();    // MOV AX, [0x0101]
        assertEquals(AX_ERROR, cpu.getRegisterValue("AX")[0], (byte) 0xAA);
        assertEquals(AX_ERROR, cpu.getRegisterValue("AX")[1], (byte) 0x55);

        // AND reg, reg
        cpu.startDebug();    // AND AX, BX
        assertFalse(SF_ERROR, cpu.getFlagValue('S'));
        assertTrue(PF_ERROR, cpu.getFlagValue('P'));
        assertTrue(ZF_ERROR, cpu.getFlagValue('Z'));
        assertEquals(AX_ERROR, cpu.getRegisterValue("AX")[0], (byte) 0x00);
        assertEquals(AX_ERROR, cpu.getRegisterValue("AX")[1], (byte) 0x00);
        cpu.startDebug();    // HLT


    }

}