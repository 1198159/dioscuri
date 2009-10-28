/* $Revision: 159 $ $Date: 2009-08-17 12:52:56 +0000 (ma, 17 aug 2009) $ $Author: blohman $ 
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


/*
 * Information used in this module was taken from:
 * - http://bochs.sourceforge.net/techspec/CMOS-reference.txt
 * - 
 */
package dioscuri.module.pic;

/*
 * This class is based on Bochs source code (pic.h, pic.cc}, see bochs.sourceforge.net for details;
 * Conversions from C++ to Java have been made; comments have been added
 * -Bram
 */


import java.util.logging.Level;
import java.util.logging.Logger;

import dioscuri.Emulator;
import dioscuri.exception.ModuleUnknownPort;
import dioscuri.module.Module;
import dioscuri.module.ModuleATA;
import dioscuri.module.ModuleCPU;
import dioscuri.module.ModuleMotherboard;
import dioscuri.module.ModulePIC;


/**
 * @see Module
 * 
 * Metadata module
 * ********************************************
 * general.type                : pic
 * general.name                : Programmable Interrupt Controller (Intel 8259A compatible)
 * general.architecture        : Von Neumann
 * general.description         : Implements a standard PIC, a manager of the interrupt-driven system.
 * general.creator             : Tessella Support Services, Koninklijke Bibliotheek, Nationaal Archief of the Netherlands
 * general.version             : 1.0
 * general.keywords            : PIC, controller, interrupt, IRQ
 * general.relations           : cpu, motherboard
 * general.yearOfIntroduction  : 
 * general.yearOfEnding        : 
 * general.ancestor            : 
 * general.successor           : 
 * 
 * Notes:
 * - PIC is also an I/O device itself
 * - All IRQ numbers are managed by PIC
 * 
 */
@SuppressWarnings("unused")
public class PIC extends ModulePIC
{
    // Instance (array of master and slave PIC)
    TheProgrammableInterruptController[] thePIC = new TheProgrammableInterruptController[]{new TheProgrammableInterruptController(),
                                                                                           new TheProgrammableInterruptController()};

    // Attributes
    
    // Relations
    private Emulator emu;
    private String[] moduleConnections = new String[] {"cpu", "motherboard"}; 
    private ModuleCPU cpu;
    private ModuleMotherboard motherboard;
    
    // Toggles
    private boolean isObserved;
    private boolean debugMode;
    
    // Logging
    private static Logger logger = Logger.getLogger("dioscuri.module.pic");
    
    // IRQ list
    private Module[] irqList;       // Contains references to modules that registered an IRQ
    private boolean[] irqEnabled;   // Contains a list of set or cleared IRQs
    
    
    // Constants
    // Module specifics
    public final static int MODULE_ID       = 1;
    public final static String MODULE_TYPE  = "pic";
    public final static String MODULE_NAME  = "Programmable Interrupt Controller (Intel 8259A compatible)";
    
    public final static int MASTER          = 0;
    public final static int SLAVE           = 1;
    
    // I/O ports 0x20-0x21 (Master PIC)
    private final static int MASTER_PIC_CMD_IRQ   = 0x20;
    private final static int MASTER_PIC_MASK_REG  = 0x21;
    // I/O ports 0xA0-0xA1 (Slave PIC)
    private final static int SLAVE_PIC_CMD_IRQ   = 0xA0;
    private final static int SLAVE_PIC_MASK_REG  = 0xA1;
    
    
    // IRQ total numbers available
    private final static int PIC_IRQ_SPACE      = 16;
    
    // IRQ numbers (fixed) for reserved devices
    private final static int PIC_IRQ_NUMBER_PIT         = 0;        // PIT / system clock
    private final static int PIC_IRQ_NUMBER_KEYBOARD    = 1;        // Keyboard
    private final static int PIC_IRQ_NUMBER_SERIALPORT  = 4;        // Serial port COM1
    private final static int PIC_IRQ_NUMBER_FDC         = 6;        // FDC = Floppy Disk Controller
    private final static int PIC_IRQ_NUMBER_RTC         = 8;        // RTC / CMOS
    private final static int PIC_IRQ_NUMBER_MOUSE       = 12;       // Mouse
        
    private final static int[] PIC_IRQ_NUMBER_ATA        = { 14, 15, 11, 9 };       // ATA controller

    
    // Constructor

    /**
     * Class constructor
     * 
     */
    public PIC(Emulator owner)
    {
        emu = owner;
        
        // Initialise variables
        isObserved = false;
        debugMode = false;
        
        // Initialise IRQ list
        irqList = null;
        irqEnabled = null;
        
        logger.log(Level.INFO, "[" + MODULE_TYPE + "]" + " Module created successfully.");
    }

    
    //******************************************************************************
    // Module Methods
    
    /**
     * Returns the ID of the module
     * 
     * @return string containing the ID of module 
     * @see Module
     */
    public int getID()
    {
        return MODULE_ID;
    }

    
    /**
     * Returns the type of the module
     * 
     * @return string containing the type of module 
     * @see Module
     */
    public String getType()
    {
        return MODULE_TYPE;
    }


    /**
     * Returns the name of the module
     * 
     * @return string containing the name of module 
     * @see Module
     */
    public String getName()
    {
        return MODULE_NAME;
    }

    
    /**
     * Returns a String[] with all names of modules it needs to be connected to
     * 
     * @return String[] containing the names of modules, or null if no connections
     */
    public String[] getConnection()
    {
        // Return all required connections;
        return moduleConnections;
    }

    
    /**
     * Sets up a connection with another module
     * 
     * @param mod   Module that is to be connected to this class
     * 
     * @return true if connection has been established successfully, false otherwise
     * 
     * @see Module
     */
    public boolean setConnection(Module mod)
    {
        // Set connection for cpu
        if (mod.getType().equalsIgnoreCase("cpu"))
        {
            this.cpu = (ModuleCPU)mod;
            return true;
        }
        // Set connection for motherboard
        else if (mod.getType().equalsIgnoreCase("motherboard"))
        {
            this.motherboard = (ModuleMotherboard)mod;
            return true;
        }
        
        // No connection has been established
        return false;
    }


    /**
     * Checks if this module is connected to operate normally
     * 
     * @return true if this module is connected successfully, false otherwise
     */
    public boolean isConnected()
    {
        // Check if module if connected
        if (this.cpu != null && this.motherboard != null)
        {
            return true;
        }
        
        // One or more connections may be missing
        return false;
    }


    /**
     * Reset all parameters of module
     * 
     * @return boolean true if module has been reset successfully, false otherwise
     */
    public boolean reset()
    {
        // Reset master and slave PICs
        thePIC[MASTER].reset();
        thePIC[SLAVE].reset();
        
        // Set custom setting for master and slave PICs
        thePIC[MASTER].interruptOffset = 0x08;  // IRQ0 = INT 0x08
        thePIC[MASTER].isMaster = true;     // master PIC

        thePIC[SLAVE].interruptOffset = 0x70;   // IRQ8 = INT 0x70
        thePIC[SLAVE].isMaster = false;     // slave PIC
        
        // Register I/O ports PORT 0x20, 0x21, 0xA0, 0xA1
        motherboard.setIOPort(MASTER_PIC_CMD_IRQ, this);
        motherboard.setIOPort(MASTER_PIC_MASK_REG, this);
        motherboard.setIOPort(SLAVE_PIC_CMD_IRQ, this);
        motherboard.setIOPort(SLAVE_PIC_MASK_REG, this);
        
        // Reset IRQ list and clear all pending IRQs
        irqList = new Module[PIC_IRQ_SPACE];
        irqEnabled = new boolean[PIC_IRQ_SPACE];

        logger.log(Level.INFO, "[" + MODULE_TYPE + "]" + " Module has been reset.");
        return true;
    }

    
    /**
     * Starts the module
     * @see Module
     */
    public void start()
    {
        // Nothing to start
    }
    

    /**
     * Stops the module
     * @see Module
     */
    public void stop()
    {
        // Nothing to stop
    }
    
    
    /**
     * Returns the status of observed toggle
     * 
     * @return state of observed toggle
     * 
     * @see Module
     */
    public boolean isObserved()
    {
        return isObserved;
    }


    /**
     * Sets the observed toggle
     * 
     * @param status
     * 
     * @see Module
     */
    public void setObserved(boolean status)
    {
        isObserved = status;
    }


    /**
     * Returns the status of the debug mode toggle
     * 
     * @return state of debug mode toggle
     * 
     * @see Module
     */
    public boolean getDebugMode()
    {
        return debugMode;
    }


    /**
     * Sets the debug mode toggle
     * 
     * @param status
     * 
     * @see Module
     */
    public void setDebugMode(boolean status)
    {
        debugMode = status;
    }


    /**
     * Returns data from this module
     *
     * @param Module requester, the requester of the data
     * @return byte[] with data
     * 
     * @see Module
     */
    public byte[] getData(Module requester)
    {
        return null;
    }


    /**
     * Set data for this module
     *
     * @param byte[] containing data
     * @param Module sender, the sender of the data
     * 
     * @return true if data is set successfully, false otherwise
     * 
     * @see Module
     */
    public boolean setData(byte[] data, Module sender)
    {
        return false;
    }


    /**
     * Set String[] data for this module
     * 
     * @param String[] data
     * @param Module sender, the sender of the data
     * 
     * @return boolean true is successful, false otherwise
     * 
     * @see Module
     */
    public boolean setData(String[] data, Module sender)
    {
        return false;
    }

    
    /**
     * Returns a dump of this module
     * 
     * @return string
     * 
     * @see Module
     */
    public String getDump()
    {
        // Show some status information of this module
        String dump = "";
        String ret = "\r\n";
        String tab = "\t";
        
        dump = "PIC dump:" + ret;
        
        dump += "IRQ |" + tab + "device |" + tab + "raised" + ret;
        dump += "-----------------------------------------" + ret;
        for (int i = 0; i < irqList.length; i++)
        {
            if (irqList[i] != null)
            {
                dump += "" + i + tab + irqList[i].getType() + tab + irqEnabled[i] + ret;
            }
            else
            {
                dump += "" + i + ret;
            }
        }
        return dump;
    }


    //******************************************************************************
    // ModuleDevice Methods
    
    /**
     * Retrieve the interval between subsequent updates
     * 
     * @return int interval in microseconds
     */
    public int getUpdateInterval()
    {
        return -1;
    }

    /**
     * Defines the interval between subsequent updates
     * 
     * @param int interval in microseconds
     */
    public void setUpdateInterval(int interval) {}


    /**
     * Update device
     * 
     */
    public void update() {}
    

    /**
     * Return a byte from I/O address space at given port
     * 
     * @param int portAddress containing the address of the I/O port
     * 
     * @return byte containing the data at given I/O address port
     * @throws ModuleUnknownPort 
     */
    public byte getIOPortByte(int portAddress) throws ModuleUnknownPort
    {
        logger.log(Level.CONFIG, "[" + MODULE_TYPE + "]" + " IO read from 0x" + Integer.toHexString(portAddress));

        if ((portAddress == 0x20 || portAddress == 0x21) && thePIC[MASTER].isPolled)
        {
            // In polled mode. Treat this as an interrupt acknowledge
            clearHighestInterrupt(MASTER);
            thePIC[MASTER].isPolled = false;
            serviceMasterPIC();
            return thePIC[MASTER].currentIrqNumber; // Return the current irq requested
        }

        if ((portAddress == 0xA0 || portAddress == 0xA1) && thePIC[SLAVE].isPolled)
        {
            // In polled mode. Treat this as an interrupt acknowledge
            clearHighestInterrupt(SLAVE);
            thePIC[SLAVE].isPolled = false;
            serviceSlavePIC();
            return thePIC[SLAVE].currentIrqNumber; // Return the current irq requested
        }

        switch (portAddress)
        {
            case 0x20:
                if (thePIC[MASTER].readRegisterSelect != 0)
                {
                    // ISR
                    logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " read master ISR = " + thePIC[MASTER].inServiceRegister);
                    return (thePIC[MASTER].inServiceRegister);
                }
                else
                {
                    // IRR
                    logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " read master IRR = " + thePIC[MASTER].interruptRequestRegister);
                    return (thePIC[MASTER].interruptRequestRegister);
                }

            case 0x21:
                logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " read master IMR = " + thePIC[MASTER].interruptMaskRegister);
                return (thePIC[MASTER].interruptMaskRegister);

            case 0xA0:
                if (thePIC[SLAVE].readRegisterSelect != 0)
                {
                    // ISR
                    logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " read slave ISR = " + thePIC[SLAVE].inServiceRegister);
                    return (thePIC[SLAVE].inServiceRegister);
                }
                else
                {
                    // IRR
                    logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " read slave IRR = " + thePIC[SLAVE].interruptRequestRegister);
                    return (thePIC[SLAVE].interruptRequestRegister);
                }

            case 0xA1:
                logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " read slave IMR = " + thePIC[SLAVE].interruptMaskRegister);
                return (thePIC[SLAVE].interruptMaskRegister);

            default:
                logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " Unknown I/O address " + portAddress);
            throw new ModuleUnknownPort("[" + MODULE_TYPE + "]" + " does not recognise port 0x" + Integer.toHexString(portAddress).toUpperCase());

        }

    }

    /**
     * Set a byte in I/O address space at given port
     * 
     * @param int portAddress containing the address of the I/O port
     * @param byte data
     */
    public void setIOPortByte(int portAddress, byte data)
    {
        logger.log(Level.CONFIG, "[" + MODULE_TYPE + "]" + " IO write to 0x" + Integer.toHexString(portAddress) + " = 0x" + Integer.toHexString(data));

        switch (portAddress)
        {
            case 0x20:
                if ((data & 0x10) != 0)
                {
                    // initialization command 1: master
                    logger.log(Level.CONFIG, "[" + MODULE_TYPE + "]" + " master: init command 1 found");
                    logger.log(Level.CONFIG, "[" + MODULE_TYPE + "]" + "         requires 4 = " + (data & 0x01));
                    logger.log(Level.CONFIG, "[" + MODULE_TYPE + "]" + "         cascade mode: [0=cascade,1=single] " + ((data & 0x02) >> 1));
                    thePIC[MASTER].initSequence.inInitSequence = true;
                    thePIC[MASTER].initSequence.numComWordsReq = (byte) (data & 0x01);
                    thePIC[MASTER].initSequence.currentComWordExpected = 2;           // operation command 2
                    thePIC[MASTER].interruptMaskRegister = 0x00;    // clear the irq mask register
                    thePIC[MASTER].inServiceRegister = 0x00;        // no IRQ's in service
                    thePIC[MASTER].interruptRequestRegister = 0x00; // no IRQ's requested
                    thePIC[MASTER].lowestPriorityIRQ = 7;
                    thePIC[MASTER].intRequestPin = false;           // reprogramming clears previous INTR request
                    thePIC[MASTER].autoEndOfInt = false;
                    thePIC[MASTER].rotateOnAutoEOI = false;
                    if ((data & 0x02) != 0)
                        logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " master: ICW1: single mode not supported");
                    if ((data & 0x08) != 0)
                    {
                        logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " master: ICW1: level sensitive mode not supported");
                    }
                    else
                    {
                        logger.log(Level.CONFIG, "[" + MODULE_TYPE + "]" + " master: ICW1: edge triggered mode selected");
                    }
                    cpu.interruptRequest(false);
                    return;
                }

                if ((data & 0x18) == 0x08)
                {
                    // OCW3
                    logger.log(Level.CONFIG, "[" + MODULE_TYPE + "]" + " master: OCW3; data: " + data);
                    int specialMask, poll, readOperation;

                    specialMask = ((((int)data) & 0xFF) & 0x60) >> 5;
                    poll = ((((int)data) & 0xFF) & 0x04) >> 2;
                    readOperation = ((((int)data) & 0xFF) & 0x03);
                    if (poll != 0)
                    {
                        thePIC[MASTER].isPolled = true;
                        return;
                    }
                    if (readOperation == 0x02) // read IRR
                        thePIC[MASTER].readRegisterSelect = 0;
                    else if (readOperation == 0x03) // read ISR
                        thePIC[MASTER].readRegisterSelect = 1;
                    if (specialMask == 0x02)
                    {
                        // cancel special mask
                        thePIC[MASTER].specialMask = false;
                    }
                    else if (specialMask == 0x03)
                    {
                        // set specific mask
                        thePIC[MASTER].specialMask = true;
                        serviceMasterPIC();
                    }
                    return;
                }

                // OCW2
                switch (data)
                {

                    case 0x00:  // Rotate in auto eoi mode clear
                    case (byte) 0x80: // Rotate in auto eoi mode set
                        thePIC[MASTER].rotateOnAutoEOI = (data != 0);
                        break;
                        
                    case 0x0A: // select read interrupt request register
                        thePIC[MASTER].readRegisterSelect = 0;
                        break;
                        
                    case 0x0B: // select read interrupt in-service register
                        thePIC[MASTER].readRegisterSelect = 1;
                        break;

                    case (byte) 0xA0: // Rotate on non-specific end of interrupt
                    case 0x20: // end of interrupt command
                        logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " OCW2: Clear highest interrupt");
                        this.clearHighestInterrupt(MASTER);

                        if (data == 0xA0)
                        {
                        	// Rotate in Auto-EOI mode
                            thePIC[MASTER].lowestPriorityIRQ++;
                            if (thePIC[MASTER].lowestPriorityIRQ > 7)
                            {
                                thePIC[MASTER].lowestPriorityIRQ = 0;
                            }
                        }

                        this.serviceMasterPIC();
                        break;

                    case 0x40: // Intel PIC spec-sheet seems to indicate this should be ignored
                        logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " IRQ no-op");
                        break;

                    case 0x60: // specific EOI 0
                    case 0x61: // specific EOI 1
                    case 0x62: // specific EOI 2
                    case 0x63: // specific EOI 3
                    case 0x64: // specific EOI 4
                    case 0x65: // specific EOI 5
                    case 0x66: // specific EOI 6
                    case 0x67: // specific EOI 7
                        thePIC[MASTER].inServiceRegister &= ~(1 << ((((int)data) & 0xFF) - 0x60));
                        serviceMasterPIC();
                        break;

                    // IRQ lowest priority commands
                    case (byte) 0xC0: // 0 7 6 5 4 3 2 1
                    case (byte) 0xC1: // 1 0 7 6 5 4 3 2
                    case (byte) 0xC2: // 2 1 0 7 6 5 4 3
                    case (byte) 0xC3: // 3 2 1 0 7 6 5 4
                    case (byte) 0xC4: // 4 3 2 1 0 7 6 5
                    case (byte) 0xC5: // 5 4 3 2 1 0 7 6
                    case (byte) 0xC6: // 6 5 4 3 2 1 0 7
                    case (byte) 0xC7: // 7 6 5 4 3 2 1 0
                        logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " IRQ lowest command " + data);
                        thePIC[MASTER].lowestPriorityIRQ = (((int) data) & 0xFF) - 0xC0;
                        break;

                    case (byte) 0xE0: // specific EOI and rotate 0
                    case (byte) 0xE1: // specific EOI and rotate 1
                    case (byte) 0xE2: // specific EOI and rotate 2
                    case (byte) 0xE3: // specific EOI and rotate 3
                    case (byte) 0xE4: // specific EOI and rotate 4
                    case (byte) 0xE5: // specific EOI and rotate 5
                    case (byte) 0xE6: // specific EOI and rotate 6
                    case (byte) 0xE7: // specific EOI and rotate 7
                        thePIC[MASTER].inServiceRegister &= ~(1 << ((((int)data) & 0xFF) - 0xE0));
                        thePIC[MASTER].lowestPriorityIRQ = ((((int)data) & 0xFF) - 0xE0);
                        serviceMasterPIC();
                        break;

                    default:
                        logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " write to port 0x20 did not match");
                    return;
                } // switch
                return;

            case 0x21:
                // initialization mode operation
                if (thePIC[MASTER].initSequence.inInitSequence)
                {
                    switch (thePIC[MASTER].initSequence.currentComWordExpected)
                    {
                        case 2:
                            thePIC[MASTER].interruptOffset = (((int)data) & 0xFF) & 0xF8;
                            thePIC[MASTER].initSequence.currentComWordExpected = 3;
                            logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " master: init command 2 = " + data);
                            logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + "         offset = INT " + thePIC[MASTER].interruptOffset);
                            return;

                        case 3:
                            logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " master: init command 3 = " + data);
                            if (thePIC[MASTER].initSequence.numComWordsReq != 0)
                            {
                                thePIC[MASTER].initSequence.currentComWordExpected = 4;
                            }
                            else
                            {
                                thePIC[MASTER].initSequence.inInitSequence = false;
                            }
                            return;

                        case 4:
                            logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " master: init command 4 = " + data);
                            if ((data & 0x02) != 0)
                            {
                                logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + "        auto EOI");
                                thePIC[MASTER].autoEndOfInt = true;
                            }
                            else
                            {
                                logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " normal EOI interrupt");
                                thePIC[MASTER].autoEndOfInt = false;
                            }
                            if ((data & 0x01) != 0)
                            {
                                logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + "        80x86 mode");
                            }
                            else
                                logger.log(Level.SEVERE, "[" + MODULE_TYPE + "]" + "        not 80x86 mode");
                            thePIC[MASTER].initSequence.inInitSequence = false;
                            return;
                        default:
                            logger.log(Level.SEVERE, "[" + MODULE_TYPE + "]" + " master expecting bad init command");
                        return;
                    }
                }

                // normal operation
                logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " setting master pic IMR to %02x", data);
                thePIC[MASTER].interruptMaskRegister = data;
                serviceMasterPIC();
                return;

            case 0xA0:
                if ((data & 0x10) != 0)
                {
                    // initialization command 1: slave
                    logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " slave: init command 1 found");
                    logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + "        requires 4 = " + (data & 0x01));
                    logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + "        cascade mode: [0=cascade,1=single] " + ((data & 0x02) >> 1));
                    thePIC[SLAVE].initSequence.inInitSequence = true;
                    thePIC[SLAVE].initSequence.numComWordsReq = (byte) (data & 0x01);
                    thePIC[SLAVE].initSequence.currentComWordExpected = 2;        // operation command 2
                    thePIC[SLAVE].interruptMaskRegister = 0x00; // clear irq mask
                    thePIC[SLAVE].inServiceRegister = 0x00;     // no IRQ's in service
                    thePIC[SLAVE].interruptRequestRegister = 0x00; // no IRQ's requested
                    thePIC[SLAVE].lowestPriorityIRQ = 7;
                    thePIC[SLAVE].intRequestPin = false;           // reprogramming clears previous INTR request
                    thePIC[SLAVE].autoEndOfInt = false;
                    thePIC[SLAVE].rotateOnAutoEOI = false;
                    if ((data & 0x02) != 0)
                        logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " slave: ICW1: single mode not supported");
                    if ((data & 0x08) != 0)
                    {
                        logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " slave: ICW1: level sensitive mode not supported");
                    }
                    else
                    {
                        logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " slave: ICW1: edge triggered mode selected");
                    }
                    return;
                }

                if ((data & 0x18) == 0x08)
                {
                    // OCW3
                    int specialMask, poll, readOperation;

                    specialMask = ((((int)data) & 0xFF) & 0x60) >> 5;
                    poll = ((((int)data) & 0xFF) & 0x04) >> 2;
                    readOperation = ((((int)data) & 0xFF) & 0x03);
                    if (poll != 0)
                    {
                        thePIC[SLAVE].isPolled = true;
                        return;
                    }
                    if (readOperation == 0x02)  //read IRR
                        thePIC[SLAVE].readRegisterSelect = 0;
                    else if (readOperation == 0x03) // read ISR
                        thePIC[SLAVE].readRegisterSelect = 1;
                    if (specialMask == 0x02)
                    {
                        // cancel special mask
                        thePIC[SLAVE].specialMask = false;
                    }
                    else if (specialMask == 0x03)
                    { 
                        // set specific mask
                        thePIC[SLAVE].specialMask = true;
                        serviceSlavePIC();
                    }
                    return;
                }

                switch (data)
                {
                    case 0x00: // Rotate in auto eoi mode clear
                    case (byte) 0x80: // Rotate in auto eoi mode set
                        thePIC[SLAVE].rotateOnAutoEOI = (data != 0);
                        break;

                    case 0x0A: // select read interrupt request register
                        thePIC[SLAVE].readRegisterSelect = 0;
                        break;
                    case 0x0B: // select read interrupt in-service register
                        thePIC[SLAVE].readRegisterSelect = 1;
                        break;

                    case (byte) 0xA0: // Rotate on non-specific end of interrupt
                    case 0x20: // end of interrupt command
                        clearHighestInterrupt(SLAVE);

                        if (data == 0xA0)
                        {
                            // Rotate in Auto-EOI mode
                            thePIC[SLAVE].lowestPriorityIRQ++;
                            if (thePIC[SLAVE].lowestPriorityIRQ > 7)
                                thePIC[SLAVE].lowestPriorityIRQ = 0;
                        }

                        serviceSlavePIC();
                        break;

                    case 0x40: // Intel PIC spec-sheet seems to indicate this should be ignored
                        logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " IRQ no-op");
                        break;

                    case 0x60: // specific EOI 0
                    case 0x61: // specific EOI 1
                    case 0x62: // specific EOI 2
                    case 0x63: // specific EOI 3
                    case 0x64: // specific EOI 4
                    case 0x65: // specific EOI 5
                    case 0x66: // specific EOI 6
                    case 0x67: // specific EOI 7
                        thePIC[SLAVE].inServiceRegister &= ~(1 << ((((int)data) & 0xFF) - 0x60));
                        serviceSlavePIC();
                        break;

                    // IRQ lowest priority commands
                    case (byte) 0xC0: // 0 7 6 5 4 3 2 1
                    case (byte) 0xC1: // 1 0 7 6 5 4 3 2
                    case (byte) 0xC2: // 2 1 0 7 6 5 4 3
                    case (byte) 0xC3: // 3 2 1 0 7 6 5 4
                    case (byte) 0xC4: // 4 3 2 1 0 7 6 5
                    case (byte) 0xC5: // 5 4 3 2 1 0 7 6
                    case (byte) 0xC6: // 6 5 4 3 2 1 0 7
                    case (byte) 0xC7: // 7 6 5 4 3 2 1 0
                        logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " IRQ lowest command " + data);
                        thePIC[SLAVE].lowestPriorityIRQ = (((int)data) & 0xFF) - 0xC0;
                        break;

                    case (byte) 0xE0: // specific EOI and rotate 0
                    case (byte) 0xE1: // specific EOI and rotate 1
                    case (byte) 0xE2: // specific EOI and rotate 2
                    case (byte) 0xE3: // specific EOI and rotate 3
                    case (byte) 0xE4: // specific EOI and rotate 4
                    case (byte) 0xE5: // specific EOI and rotate 5
                    case (byte) 0xE6: // specific EOI and rotate 6
                    case (byte) 0xE7: // specific EOI and rotate 7
                        thePIC[SLAVE].inServiceRegister &= ~(1 << ((((int)data) & 0xFF) - 0xE0));
                        thePIC[SLAVE].lowestPriorityIRQ = ((((int)data) & 0xFF) - 0xE0);
                        serviceSlavePIC();
                        break;

                    default:
                        logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " write to port 0xA0 did not match");
                        return;
                } // switch
                return;

            case 0xA1:
                // initialization mode operation
                if (thePIC[SLAVE].initSequence.inInitSequence)
                {
                    switch (thePIC[SLAVE].initSequence.currentComWordExpected)
                    {
                        case 2:
                            thePIC[SLAVE].interruptOffset = (((int)data) & 0xFF) & 0xF8;
                            thePIC[SLAVE].initSequence.currentComWordExpected = 3;
                            logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " slave: init command 2 = " + data);
                            logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + "        offset = INT " + thePIC[SLAVE].interruptOffset);
                            return;
                            
                        case 3:
                            logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " slave: init command 3 = " + data);
                            if (thePIC[SLAVE].initSequence.numComWordsReq != 0)
                            {
                                thePIC[SLAVE].initSequence.currentComWordExpected = 4;
                            }
                            else
                            {
                                thePIC[SLAVE].initSequence.inInitSequence = false;
                            }
                            return;
                            
                        case 4:
                            logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " slave: init command 4 = " + data);
                            if ((data & 0x02) != 0)
                            {
                                logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + "        auto EOI");
                                thePIC[SLAVE].autoEndOfInt = true;
                            }
                            else
                            {
                                logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " normal EOI interrupt");
                                thePIC[SLAVE].autoEndOfInt = false;
                            }
                            if ((data & 0x01) != 0)
                            {
                                logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + "        80x86 mode");
                            }
                            else
                                logger.log(Level.SEVERE, "[" + MODULE_TYPE + "]" + "        not 80x86 mode");
                            thePIC[SLAVE].initSequence.inInitSequence = false;
                            return;

                        default:
                            logger.log(Level.SEVERE, "[" + MODULE_TYPE + "]" + " slave: encountered bad init command");
                            return;
                    }
                }
                return;

            default:
                // normal operation
                logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " setting slave pic IMR to %02x", data);
                thePIC[SLAVE].interruptMaskRegister = data;
                serviceSlavePIC();
                return;
        } // switch
    }

    /**
     * Return a word from I/O address space at given port
     * 
     * @param int portAddress containing the address of the I/O port
     * 
     * @return byte[] containing the data at given I/O address port
     */
    public byte[] getIOPortWord(int portAddress)
    {
        logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " -> IN command (word) to port " + Integer.toHexString(portAddress).toUpperCase() + " received");
        logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " -> Returned default value 0xFFFF");
        
        // Return dummy value 0xFFFF
        return new byte[] { (byte) 0xFF, (byte) 0xFF };
    }


    /**
     * Set a word in I/O address space at given port
     * 
     * @param int portAddress containing the address of the I/O port
     * @param byte[] dataWord
     */
    public void setIOPortWord(int portAddress, byte[] dataWord)
    {
        logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " -> OUT command (word) to port " + Integer.toHexString(portAddress).toUpperCase() + " received. No action taken.");
        
        // Do nothing and just return okay
        return;
    }

    /**
     * Return a doubleword from I/O address space at given port
     * 
     * @param int portAddress containing the address of the I/O port
     * 
     * @return byte[] containing the data at given I/O address port
     */
    public byte[] getIOPortDoubleWord(int portAddress)
    {
        logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " -> IN command (double word) to port " + Integer.toHexString(portAddress).toUpperCase() + " received");
        logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " -> Returned default value 0xFFFFFFFF");
        
        // Return dummy value 0xFFFFFFFF
        return new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
    }

    /**
     * Set a doubleword in I/O address space at given port
     * 
     * @param int portAddress containing the address of the I/O port
     * @param byte[] dataDoubleWord
     */
    public void setIOPortDoubleWord(int portAddress, byte[] dataDoubleWord)
    {
        logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " -> OUT command (double word) to port " + Integer.toHexString(portAddress).toUpperCase() + " received. No action taken.");
        
        // Do nothing and just return okay
        return;
    }


    //******************************************************************************
    // ModulePIC Methods
    
    /**
     * Returns an IRQ number.
     * Checks if requesting module has a fixed IRQ number.
     * 
     * @param module that would like to have an IRQ number
     * @return int IRQ number from IRQ address space, or -1 if not allowed/possible
     */
    public int requestIRQNumber(Module module)
    {
        int irqNumber = -1;
        
        // Check which device is requesting an IRQ number
        // If module is part of reserved IRQ-list, return fixed IRQ number
        if (module.getType().equalsIgnoreCase("pit"))
        {
            // Module PIT
            irqNumber = PIC_IRQ_NUMBER_PIT;
            irqList[irqNumber] = module;
            irqEnabled[irqNumber] = false;
        }
        else if (module.getType().equalsIgnoreCase("keyboard"))
        {
            // Module Keyboard
            irqNumber = PIC_IRQ_NUMBER_KEYBOARD;
            irqList[irqNumber] = module;
            irqEnabled[irqNumber] = false;
        }
        else if (module.getType().equalsIgnoreCase("serialport"))
        {
            // Module Serialport
            irqNumber = PIC_IRQ_NUMBER_SERIALPORT;
            irqList[irqNumber] = module;
            irqEnabled[irqNumber] = false;
        }
        else if (module.getType().equalsIgnoreCase("fdc"))
        {
            // Module FDC
            irqNumber = PIC_IRQ_NUMBER_FDC;
            irqList[irqNumber] = module;
            irqEnabled[irqNumber] = false;
        }
        else if (module.getType().equalsIgnoreCase("rtc"))
        {
            // Module RTC
            irqNumber = PIC_IRQ_NUMBER_RTC;
            irqList[irqNumber] = module;
            irqEnabled[irqNumber] = false;
        }
        else if (module.getType().equalsIgnoreCase("mouse"))
        {
            // Module Mouse
            irqNumber = PIC_IRQ_NUMBER_MOUSE;
            irqList[irqNumber] = module;
            irqEnabled[irqNumber] = false;
        }
        else if (module.getType().equalsIgnoreCase("ata"))
        {
            // Module ATA
            ModuleATA ata = (ModuleATA)module;
            int currentChannelIndex = ata.getCurrentChannelIndex();
            irqNumber = PIC_IRQ_NUMBER_ATA[currentChannelIndex];
            irqList[irqNumber] = module;
            irqEnabled[irqNumber] = false;
        }
        else if (module.getType().equalsIgnoreCase("serialport"))
        {
            // Module SerialPort
            irqNumber = PIC_IRQ_NUMBER_SERIALPORT;
            irqList[irqNumber] = module;
            irqEnabled[irqNumber] = false;
        }
        else
        {
            // FIXME: Return any of the free available IRQ numbers
            logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " Should return free IRQ number, but is not implemented");
        }
        
        return irqNumber;
    }

    
    /**
     * Raises an interrupt request (IRQ) of given IRQ number
     * 
     * @param int irqNumber the number of IRQ to be raised
     */
    public void setIRQ(int irqNumber)
    {
        logger.log(Level.CONFIG, "[" + MODULE_TYPE + "]" + " Attempting to set IRQ line " + irqNumber + " high");

        int mask = (1 << (irqNumber & 7));
        // Check if IRQ should be handled by master or slave and check if irqPin was low
        if ((irqNumber <= 7) && !((thePIC[MASTER].irqPins & mask) != 0))
        {
            logger.log(Level.CONFIG, "[" + MODULE_TYPE + "]" + " IRQ line " + irqNumber + " now high");
            thePIC[MASTER].irqPins |= mask;
            thePIC[MASTER].interruptRequestRegister |= mask;
            this.serviceMasterPIC();
        }
        else if ((irqNumber > 7) && (irqNumber <= 15) && !((thePIC[SLAVE].irqPins & mask) != 0))
        {
            logger.log(Level.CONFIG, "[" + MODULE_TYPE + "]" + " IRQ line " + irqNumber + " now high");
            thePIC[SLAVE].irqPins |= mask;
            thePIC[SLAVE].interruptRequestRegister |= mask;
            this.serviceSlavePIC();
        }
    }
    
    
    /**
     * Lowers an interrupt request (IRQ) of given IRQ number
     * 
     * @param int irqNumber the number of IRQ to be cleared
     */
    public void clearIRQ(int irqNumber)
    {
        logger.log(Level.CONFIG, "[" + MODULE_TYPE + "]" + " Attempting to set IRQ line " + irqNumber + " low");

        int mask = (1 << (irqNumber & 7));
        if ((irqNumber <= 7) && ((thePIC[MASTER].irqPins & mask) != 0))
        {
            logger.log(Level.CONFIG, "[" + MODULE_TYPE + "]" + " IRQ line " + irqNumber + " now low");
            thePIC[MASTER].irqPins &= ~(mask);
            thePIC[MASTER].interruptRequestRegister &= ~(mask);
        }
        else if ((irqNumber > 7) && (irqNumber <= 15) && ((thePIC[SLAVE].irqPins & mask) != 0))
        {
            logger.log(Level.CONFIG, "[" + MODULE_TYPE + "]" + " IRQ line " + irqNumber + " now low");
            thePIC[SLAVE].irqPins &= ~(mask);
            thePIC[SLAVE].interruptRequestRegister &= ~(mask);
        }
    }
    
    /**
     * Acknowledges an interrupt request from PIC by CPU
     * Note: only the CPU can acknowledge an interrupt
     * 
     * @return int address defining the jump address for handling the IRQ by the CPU
     */
    public int interruptAcknowledge()
    {
        int vector;
        int irq;

        cpu.interruptRequest(false);
        thePIC[MASTER].intRequestPin = false;
        
        // Check for spurious interrupt
        if (thePIC[MASTER].interruptRequestRegister == 0)
        {
            return (byte) (thePIC[MASTER].interruptOffset + 7);
        }
        // In level sensitive mode don't clear the irr bit.
        if (!((thePIC[MASTER].edgeLevel & (1 << thePIC[MASTER].currentIrqNumber)) != 0))
            thePIC[MASTER].interruptRequestRegister &= ~(1 << thePIC[MASTER].currentIrqNumber);
        // In autoeoi mode don't set the isr bit.
        if (!thePIC[MASTER].autoEndOfInt)
            thePIC[MASTER].inServiceRegister |= (1 << thePIC[MASTER].currentIrqNumber);
        else if (thePIC[MASTER].rotateOnAutoEOI)
            thePIC[MASTER].lowestPriorityIRQ = thePIC[MASTER].currentIrqNumber;

        if (thePIC[MASTER].currentIrqNumber != 2)
        {
            irq = thePIC[MASTER].currentIrqNumber;
            vector = irq + thePIC[MASTER].interruptOffset;
        }
        else
        { /* IRQ2 = slave pic IRQ8..15 */
            thePIC[SLAVE].intRequestPin = false;
            thePIC[MASTER].irqPins &= ~(1 << 2);
            // Check for spurious interrupt
            if (thePIC[SLAVE].interruptRequestRegister == 0)
            {
                return (thePIC[SLAVE].interruptOffset + 7);
            }
            irq = thePIC[SLAVE].currentIrqNumber;
            vector = irq + thePIC[SLAVE].interruptOffset;
            // In level sensitive mode don't clear the irr bit.
            if (!((thePIC[SLAVE].edgeLevel & (1 << thePIC[SLAVE].currentIrqNumber)) != 0))
                thePIC[SLAVE].interruptRequestRegister &= ~(1 << thePIC[SLAVE].currentIrqNumber);
            // In autoeoi mode don't set the isr bit.
            if (!thePIC[SLAVE].autoEndOfInt)
                thePIC[SLAVE].inServiceRegister |= (1 << thePIC[SLAVE].currentIrqNumber);
            else if (thePIC[SLAVE].rotateOnAutoEOI)
                thePIC[SLAVE].lowestPriorityIRQ = thePIC[SLAVE].currentIrqNumber;
            serviceSlavePIC();
        }

        serviceMasterPIC();

        return (vector);
    }

    
    //******************************************************************************
    // Custom Methods

    /**
     * Handle interrupts on the slave PIC
     */
    private void serviceSlavePIC()
    {
        int unmaskedRequests;
        int irq;
        int isr, maxIRQ;
        int highestPriority = thePIC[SLAVE].lowestPriorityIRQ + 1;
        if (highestPriority > 7)
            highestPriority = 0;

        if (thePIC[SLAVE].intRequestPin)
        {
            // last interrupt still not acknowleged
            return;
        }

        if (thePIC[SLAVE].specialMask)
        {
             // All priorities may be enabled. check all IRR bits except ones which have corresponding ISR bits set
            maxIRQ = highestPriority;
        }
        else
        {
            // normal mode
            // Find the highest priority IRQ that is enabled due to current ISR
            isr = thePIC[SLAVE].inServiceRegister;
            if (isr != 0)
            {
                maxIRQ = highestPriority;
                while ((isr & (1 << maxIRQ)) == 0)
                {
                    maxIRQ++;
                    if (maxIRQ > 7)
                    {
                        maxIRQ = 0;
                    }
                }
                if (maxIRQ == highestPriority)
                {
                    // Highest priority interrupt in-service, no other priorities allowed
                    return;
                }

                if (maxIRQ > 7)
                {
                    logger.log(Level.SEVERE, "[" + MODULE_TYPE + "]" + " error in serviceSlavePic()");
                }
            }
            else
            {
                maxIRQ = highestPriority; // 0..7 bits in ISR are cleared
            }
          }

        // Check if there are any higher priority requests
        if ((unmaskedRequests = (thePIC[SLAVE].interruptRequestRegister & ~thePIC[SLAVE].interruptMaskRegister)) != 0)
        {
            irq = highestPriority;
            do
            {
                 // for special mode, since we're looking at all IRQ's, skip if current IRQ is already in-service
                if (!(thePIC[SLAVE].specialMask && (((thePIC[SLAVE].inServiceRegister >> irq) & 0x01) != 0)))
                {
                    if ((unmaskedRequests & (1 << irq)) != 0)
                    {
                        logger.log(Level.CONFIG, "[" + MODULE_TYPE + "]" + " slave: signalling IRQ(" + 8 + irq + ")");

                        thePIC[SLAVE].intRequestPin = true;
                        thePIC[SLAVE].currentIrqNumber = (byte) irq;
                        setIRQ(2);   // request IRQ 2 on master pic
                        return;
                    }
                }

                irq++;
                if (irq > 7)
                {
                    irq = 0;
                }

            }
            while (irq != maxIRQ);
        }
        
    }

    /**
     * Handle interrupts on the master PIC
     */
    private void serviceMasterPIC()
    {
        int unmaskedRequests;
        int irq;
        int isr, maxIRQ;
        int highestPriority = thePIC[MASTER].lowestPriorityIRQ + 1;
        if (highestPriority > 7)
        {
            highestPriority = 0;
        }
        
        if (thePIC[MASTER].intRequestPin)
        {
            // last interrupt still not acknowleged
            return;
        }

        if (thePIC[MASTER].specialMask)
        {
            // All priorities may be enabled. check all IRR bits except ones which have corresponding ISR bits set
            maxIRQ = highestPriority;
        }
        else
        {
            // Normal mode
            // Find the highest priority IRQ that is enabled due to current ISR
            isr = thePIC[MASTER].inServiceRegister;
            if (isr != 0)
            {
                maxIRQ = highestPriority;
                while ((isr & (1 << maxIRQ)) == 0)
                {
                    maxIRQ++;
                    if (maxIRQ > 7)
                    {
                        maxIRQ = 0;
                    }
                }
                if (maxIRQ == highestPriority)
                {
                     // Highest priority interrupt in-service, no other priorities allowed
                    return;
                }
                if (maxIRQ > 7)
                {
                    logger.log(Level.SEVERE, "[" + MODULE_TYPE + "]" + " error in servicMasterPic()");
                }
            }
            else
            {
                maxIRQ = highestPriority; /* 0..7 bits in ISR are cleared */
            }
        }

        // Check if there are any higher priority requests
        if ((unmaskedRequests = (thePIC[MASTER].interruptRequestRegister & ~thePIC[MASTER].interruptMaskRegister)) != 0)
        {
            irq = highestPriority;
            do
            {
                 // for special mode, since we're looking at all IRQ's, skip if current IRQ is already in-service
                if (!(thePIC[MASTER].specialMask && (((thePIC[MASTER].inServiceRegister >> irq) & 0x01) != 0)))
                {
                    if ((unmaskedRequests & (1 << irq)) != 0)
                    {
                        logger.log(Level.CONFIG, "[" + MODULE_TYPE + "]" + " signalling IRQ(" + irq + ")");
                        thePIC[MASTER].intRequestPin = true;
                        thePIC[MASTER].currentIrqNumber = (byte) irq;
                        cpu.interruptRequest(true);
                        return;
                    }
                }

                irq++;
                if (irq > 7)
                {
                    irq = 0;
                }
            }
            while (irq != maxIRQ);
        }
    }


    /**
     * Handle interrupt with highest priority
     * @param masterSlave Indicates the source of the interrupt
     */
    private void clearHighestInterrupt(int masterSlave)
    {
        int irq;
        int lowestPriority;
        int highestPriority;

        // clear highest current in service bit
        lowestPriority = thePIC[masterSlave].lowestPriorityIRQ;
        highestPriority = lowestPriority + 1;
        if (highestPriority > 7)
        {
            highestPriority = 0;
        }

        irq = highestPriority;
        do
        {
            if ((thePIC[masterSlave].inServiceRegister & (1 << irq)) != 0)
            {
                thePIC[masterSlave].inServiceRegister &= ~(1 << irq);
                break; // Return mask of bit cleared
            }

            irq++;
            if (irq > 7)
            {
                irq = 0;
            }
        }
        while (irq != highestPriority);
    }
}
