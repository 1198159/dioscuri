/*
 * $Revision: 1.1 $ $Date: 2007-07-02 14:31:27 $ $Author: blohman $
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

package nl.kbna.dioscuri.module;

/**
 * Interface representing a generic hardware module.
 *  
 */

public abstract class ModulePIT extends ModuleDevice
{
    // Methods
    
    /**
     * Retrieves the current clockrate of this clock in milliseconds
     * 
     * @return long milliseconds defining how long the clock sleeps before sending a pulse
     */
    public abstract long getClockRate();

    /**
     * Sets the clock rate for this PIT
     * 
     * @param long milliseconds, the time between two consequtive clock pulses
     */
    public abstract void setClockRate(long milliseconds);
    
    /**
     * Requests a counter of the PIT.
     * 
     * @param module that would like to have a counter
     * @return boolean true if request is acknowledged, false otherwise
     */
    public abstract boolean requestCounter(Module module);
    
}
