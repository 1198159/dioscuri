/* $Revision: 160 $ $Date: 2009-08-17 12:56:40 +0000 (ma, 17 aug 2009) $ $Author: blohman $ 
 * 
 * Copyright (C) 2007-2009  National Library of the Netherlands, 
 *                          Nationaal Archief of the Netherlands, 
 *                          Planets
 *                          KEEP
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 *
 * For more information about this project, visit
 * http://dioscuri.sourceforge.net/
 * or contact us via email:
 *   jrvanderhoeven at users.sourceforge.net
 *   blohman at users.sourceforge.net
 *   bkiers at users.sourceforge.net
 * 
 * Developed by:
 *   Nationaal Archief               <www.nationaalarchief.nl>
 *   Koninklijke Bibliotheek         <www.kb.nl>
 *   Tessella Support Services plc   <www.tessella.com>
 *   Planets                         <www.planets-project.eu>
 *   KEEP                            <www.keep-project.eu>
 * 
 * Project Title: DIOSCURI
 */

package dioscuri.module;

import dioscuri.exception.ModuleException;

/**
 * Interface representing a generic hardware module.
 * 
 */

public abstract class ModuleMotherboard extends Module {
    public int ioSpaceSize;

    // Methods

    /**
     * Registers a clock to motherboard
     * 
     * @param clock
     * @return boolean true if registration is successfully, false otherwise
     */
    public abstract boolean registerClock(ModuleClock clock);

    /**
     * Requests a timer for given device at clock
     * 
     * @param device 
     * @param continuous
     * @param updatePeriod
     * @return boolean true if registration is successfully, false otherwise
     */
    public abstract boolean requestTimer(ModuleDevice device, int updatePeriod,
            boolean continuous);

    /**
     * Set a timer to start/stop running
     * 
     * @param device
     * @param runState
     * @return boolean true if timer is reset successfully, false otherwise
     */
    public abstract boolean setTimerActiveState(ModuleDevice device,
            boolean runState);

    /**
     * Resets the timer of device (if any)
     * 
     * @param device
     * @param updateInterval
     * @return boolean true if reset is successfully, false otherwise
     */
    public abstract boolean resetTimer(ModuleDevice device, int updateInterval);

    /**
     * Set I/O address port to given device
     * 
     * @param portAddress 
     * @param device
     * @return boolean true if data is set successfully, false otherwise
     */
    public abstract boolean setIOPort(int portAddress, ModuleDevice device);

    /**
     * Return a byte from I/O address space at given port
     * 
     * @param portAddress
     * @return byte containing the data at given I/O address port
     * @throws ModuleException
     *             , ModuleWriteOnlyPortException
     */
    public abstract byte getIOPortByte(int portAddress) throws ModuleException;

    /**
     * Set a byte in I/O address space at given port
     * 
     * @param portAddress
     * @param dataByte
     * @throws ModuleException
     *             , ModuleWriteOnlyPortException
     */
    public abstract void setIOPortByte(int portAddress, byte dataByte)
            throws ModuleException;

    /**
     * Return a word from I/O address space at given port
     * 
     * @param portAddress
     * @return byte[] containing the word at given I/O address port
     * @throws ModuleException
     *             , ModuleWriteOnlyPortException
     */
    public abstract byte[] getIOPortWord(int portAddress)
            throws ModuleException;

    /**
     * Set a word in I/O address space at given port
     * 
     * @param portAddress
     * @param dataWord
     * @throws ModuleException
     *             , ModuleWriteOnlyPortException
     */
    public abstract void setIOPortWord(int portAddress, byte[] dataWord)
            throws ModuleException;

    /**
     * Return a double word from I/O address space at given port
     * 
     * @param portAddress
     * @return byte[] containing the double word at given I/O address port
     * @throws ModuleException
     *             , ModuleWriteOnlyPortException
     */
    public abstract byte[] getIOPortDoubleWord(int portAddress)
            throws ModuleException;

    /**
     * Set a double word in I/O address space at given port
     * 
     * @param portAddress 
     * @param dataDoubleWord
     * @throws ModuleException
     *             , ModuleWriteOnlyPortException
     */
    public abstract void setIOPortDoubleWord(int portAddress,
            byte[] dataDoubleWord) throws ModuleException;

    /**
     * Get value of A20 address line
     * 
     * @return true if set, false if not
     */
    public abstract boolean getA20();

    /**
     * Set value of A20 address line
     * 
     * @param a20
     *            true to set, false to clear
     */
    public abstract void setA20(boolean a20);

    /**
     * Retrieve current number of instruction (instructions executed so far)
     * 
     * @return long containing number of instructions
     */
    public abstract long getCurrentInstructionNumber();
}
