/*
    JPC: A x86 PC Hardware Emulator for a pure Java Virtual Machine
    Release Version 2.0

    A project from the Physics Dept, The University of Oxford

    Copyright (C) 2007 Isis Innovation Limited

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License version 2 as published by
    the Free Software Foundation.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 
    Details (including contact information) can be found at: 

    www.physics.ox.ac.uk/jpc
 */
package dioscuri.module.cpu32;

//import org.jpc.emulator.processor.Processor;
//import org.jpc.emulator.memory.codeblock.CodeBlock;

/**
 * @author Bram Lohman
 * @author Bart Kiers
 */
public abstract class Memory implements ByteArray {
    public abstract void clear();

    /**
     * @param start
     * @param length
     */
    public abstract void clear(int start, int length);

    /**
     * @param address
     * @param buffer
     * @param off
     * @param len
     */
    public abstract void copyContentsInto(int address, byte[] buffer, int off,
                                          int len);

    /**
     * @param address
     * @param buffer
     * @param off
     * @param len
     */
    public abstract void copyContentsFrom(int address, byte[] buffer, int off,
                                          int len);

    /**
     * @return -
     */
    public boolean isAllocated() {
        return true;
    }

    /**
     * @return -
     */
    public abstract long getSize();

    /**
     * @param offset
     * @return -
     */
    public abstract byte getByte(int offset);

    /**
     * @param offset
     * @return -
     */
    public abstract short getWord(int offset);

    /**
     * @param offset
     * @return -
     */
    public abstract int getDoubleWord(int offset);

    /**
     * @param offset
     * @return -
     */
    public abstract long getQuadWord(int offset);

    /**
     * @param offset
     * @return -
     */
    public abstract long getLowerDoubleQuadWord(int offset);

    /**
     * @param offset
     * @return -
     */
    public abstract long getUpperDoubleQuadWord(int offset);

    /**
     * @param offset
     * @param data
     */
    public abstract void setByte(int offset, byte data);

    /**
     * @param offset
     * @param data
     */
    public abstract void setWord(int offset, short data);

    /**
     * @param offset
     * @param data
     */
    public abstract void setDoubleWord(int offset, int data);

    /**
     * @param offset
     * @param data
     */
    public abstract void setQuadWord(int offset, long data);

    /**
     * @param offset
     * @param data
     */
    public abstract void setLowerDoubleQuadWord(int offset, long data);

    /**
     * @param offset
     * @param data
     */
    public abstract void setUpperDoubleQuadWord(int offset, long data);

    /**
     * @param cpu
     * @param address
     * @return -
     */
    public abstract int execute(Processor cpu, int address);

    /**
     * @param cpu
     * @param address
     * @return -
     */
    public abstract CodeBlock decodeCodeBlockAt(Processor cpu, int address);
}
