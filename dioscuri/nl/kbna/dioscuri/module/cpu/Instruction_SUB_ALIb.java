/*
 * $Revision: 1.1 $ $Date: 2007-07-02 14:31:39 $ $Author: blohman $
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
	 * Intel opcode 2C<BR>
	 * Subtract immediate byte from AL.<BR>
	 * Flags modified: OF, SF, ZF, AF, PF, CF
	 */
public class Instruction_SUB_ALIb implements Instruction {

	// Attributes
	private CPU cpu;
	byte immediateByte;
	byte oldValue;
	
	// Constructors
	/**
	 * Class constructor
	 */
	public Instruction_SUB_ALIb()	{}
	
	/**
	 * Class constructor specifying processor reference
	 * 
	 * @param processor	Reference to CPU class
	 */
	public Instruction_SUB_ALIb(CPU processor)
	{
		this();
		
		// Create reference to cpu class
		cpu = processor;
	}

	
	// Methods
	
	/**
	 * Subtract immediate byte from AL
	 */
	public void execute()
	{
		immediateByte = cpu.getByteFromCode();
		
		// Store initial value of AL
		oldValue = cpu.ax[CPU.REGISTER_GENERAL_LOW]; 

		// Subtract immediate byte from register AL
		cpu.ax[CPU.REGISTER_GENERAL_LOW] -= immediateByte; 

        // Test AF
        cpu.flags[CPU.REGISTER_FLAGS_AF] = Util.test_AF_SUB(oldValue, cpu.ax[CPU.REGISTER_GENERAL_LOW]);
		// Test CF
		cpu.flags[CPU.REGISTER_FLAGS_CF] = Util.test_CF_SUB(oldValue, immediateByte, 0);
		// Test OF
		cpu.flags[CPU.REGISTER_FLAGS_OF] = Util.test_OF_SUB(oldValue, immediateByte, cpu.ax[CPU.REGISTER_GENERAL_LOW], 0);
		// Test ZF, only applies to AL
		cpu.flags[CPU.REGISTER_FLAGS_ZF] = cpu.ax[CPU.REGISTER_GENERAL_LOW] == 0 ? true : false;
		// Test SF (set when MSB of AL is 1. In Java can check signed byte)
		cpu.flags[CPU.REGISTER_FLAGS_SF] = cpu.ax[CPU.REGISTER_GENERAL_LOW] < 0 ? true : false;
		// Set PF, only applies to AL
		cpu.flags[CPU.REGISTER_FLAGS_PF] = Util.checkParityOfByte(cpu.ax[CPU.REGISTER_GENERAL_LOW]);
	}
}
