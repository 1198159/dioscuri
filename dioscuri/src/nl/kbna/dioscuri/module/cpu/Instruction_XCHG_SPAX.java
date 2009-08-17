/*
 * $Revision$ $Date$ $Author$
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
	 * Intel opcode 94<BR>
	 * Exchange contents of registers SP and AX.<BR>
	 * Flags modified: none
	 */
public class Instruction_XCHG_SPAX implements Instruction {

	// Attributes
	private CPU cpu;
	byte[] temp;
	
	// Constructors
	/**
	 * Class constructor
	 * 
	 */
	public Instruction_XCHG_SPAX()	{}
	
	/**
	 * Class constructor specifying processor reference
	 * 
	 * @param processor	Reference to CPU class
	 */
	public Instruction_XCHG_SPAX(CPU processor)
	{
		//this();
		
		// Create reference to cpu class
		cpu = processor;
		
		// Initialise variable for temporary storage 
		temp = new byte[2];
	}

	
	// Methods
	
	/**
	 * Execute instruction
	 */
	public void execute()
	{
		// Swap registers, using temp as a go-between
        System.arraycopy(cpu.sp, 0, temp, 0 , cpu.sp.length);
        System.arraycopy(cpu.ax, 0, cpu.sp, 0 , cpu.ax.length);
        System.arraycopy(temp, 0, cpu.ax, 0 , temp.length);
	}
}