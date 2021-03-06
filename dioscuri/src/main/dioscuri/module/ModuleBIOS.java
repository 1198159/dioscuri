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
import dioscuri.interfaces.Module;

/**
 * Abstract class class defining a template for a BIOS module, which
 * may contain a system BIOS, VIDEO BIOS and optional BIOSes.
 */
public abstract class ModuleBIOS extends AbstractModule {

    /**
     *
     */
    public ModuleBIOS() {
        super(Module.Type.BIOS /* no expected connections */);
    }

    /**
     * Returns the system BIOS code from ROM
     *
     * @return byte[] biosCode containing the binary code of System BIOS
     */
    public abstract byte[] getSystemBIOS();

    /**
     * Sets the system BIOS code in ROM
     *
     * @param biosCode
     * @return true if BIOS code is of specified SYSTEMBIOS_ROM_SIZE and store
     *         is successful, false otherwise
     * @throws ModuleException
     */
    public abstract boolean setSystemBIOS(byte[] biosCode)
            throws ModuleException;

    /**
     * Returns the Video BIOS code from ROM
     *
     * @return byte[] biosCode containing the binary code of Video BIOS
     */
    public abstract byte[] getVideoBIOS();

    /**
     * Sets the Video BIOS code in ROM
     *
     * @param biosCode
     * @return true if BIOS code is of specified VIDEOBIOS_ROM_SIZE and store is
     *         successful, false otherwise
     * @throws ModuleException
     */
    public abstract boolean setVideoBIOS(byte[] biosCode)
            throws ModuleException;
}
