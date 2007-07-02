/*
 * $Revision: 1.1 $ $Date: 2007-07-02 14:31:34 $ $Author: blohman $
 * 
 * Copyright (C) 2007  National Library of the Netherlands, Nationaal Archief of the Netherlands
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information about this project, visit
 * http://dioscuri.sourceforge.net/
 * or contact us via email:
 * jrvanderhoeven at users.sourceforge.net
 * blohman at users.sourceforge.net
 * 
 * Developed by:
 * Nationaal Archief               <www.nationaalarchief.nl>
 * Koninklijke Bibliotheek         <www.kb.nl>
 * Tessella Support Services plc   <www.tessella.com>
 *
 * Project Title: DIOSCURI
 *
 */
package nl.kbna.dioscuri.module.cpu;

	/**
	 * Intel opcode AC<BR>
	 * Load byte from DS:SI into AL; update SI register according to DF.<BR>
	 * Flags modified: none
	 */
public class Instruction_LODS_ALXb implements Instruction {

	// Attributes
	private CPU cpu;
    byte source;
    int incrementSize = 1;  // Byte size increment for SI
	
	// Constructors
	/**
	 * Class constructor
	 * 
	 */
	public Instruction_LODS_ALXb()	{}
	
	/**
	 * Class constructor specifying processor reference
	 * 
	 * @param processor	Reference to CPU class
	 */
	public Instruction_LODS_ALXb(CPU processor)
	{
		this();
		
		// Create reference to cpu class
		cpu = processor;
	}

	
	// Methods
	
	/**
	 * Load byte from DS:SI into AL; update SI
	 */
	public void execute()
	{
		// Get byte at DS:SI and assign to AL; DS segment override is allowed
        if (cpu.segmentOverride)
        {
            source = cpu.getByteFromMemorySegment((byte) 0, cpu.si);
        }
        else
        {
            source = cpu.getByteFromData(cpu.si);
        }
        
		cpu.ax[CPU.REGISTER_GENERAL_LOW] = source;

        // Update SI according to DF flag
        // Check direction of flag: If DF == 0, SI is incremented; if DF == 1, SI is decremented
        if (cpu.flags[CPU.REGISTER_FLAGS_DF])
        {
            // Decrement the SI register by byte size
            cpu.si[CPU.REGISTER_GENERAL_LOW] -= incrementSize;

            // Check for underflow in SI[low]
            // This has happened if SI[low] is -1 now
            if (cpu.si[CPU.REGISTER_GENERAL_LOW] == -1)
            {
                // Decrease SI[high]
                cpu.si[CPU.REGISTER_GENERAL_HIGH]--;
            }
        }
        else
        {
            // Increment the SI register by byte size
            cpu.si[CPU.REGISTER_GENERAL_LOW] += incrementSize;

            // Check for overflow in SI[low]
            // This has happened if SI[low] is 0 now
            if (cpu.si[CPU.REGISTER_GENERAL_LOW] == 0)
            {
                // Increase SI[high]
                cpu.si[CPU.REGISTER_GENERAL_HIGH]++;
            }
        }
	}
}
