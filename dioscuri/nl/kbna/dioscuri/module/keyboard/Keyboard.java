/*
 * $Revision: 1.1 $ $Date: 2007-07-02 14:31:42 $ $Author: blohman $
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

package nl.kbna.dioscuri.module.keyboard;
/*
 * Information used in this module was taken from:
 * - http://mudlist.eorbit.net/~adam/pickey/ports.html
 * - http://homepages.cwi.nl/~aeb/linux/kbd/scancodes.html
 */

/*
 * This class uses a lot (if not all) of Bochs source code from keyboard.{h,cc};
 * Conversions from C++ to Java have been made, and will need revising and/or updating
 * -Bram
 */

import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.kbna.dioscuri.Emulator;
import nl.kbna.dioscuri.exception.ModuleException;
import nl.kbna.dioscuri.exception.ModuleUnknownPort;
import nl.kbna.dioscuri.exception.ModuleWriteOnlyPortException;
import nl.kbna.dioscuri.module.Module;
import nl.kbna.dioscuri.module.ModuleKeyboard;
import nl.kbna.dioscuri.module.ModuleMotherboard;
import nl.kbna.dioscuri.module.ModulePIC;
import nl.kbna.dioscuri.module.ModuleRTC;

/**
 * An implementation of a keyboard module.
 *  
 * @see Module
 * 
 * Metadata module
 * ********************************************
 * general.type                : keyboard
 * general.name                : XT Keyboard
 * general.architecture        : Von Neumann
 * general.description         : Models a 101-key XT keyboard 
 * general.creator             : Tessella Support Services, Koninklijke Bibliotheek, Nationaal Archief of the Netherlands
 * general.version             : 1.0
 * general.keywords            : Keyboard, XT, Intel 8042
 * general.relations           : Motherboard
 * general.yearOfIntroduction  : 
 * general.yearOfEnding        : 
 * general.ancestor            : 
 * general.successor           : 
 * 
 * 
 */

public class Keyboard extends ModuleKeyboard
{
    // Instance
    TheKeyboard keyboard = new TheKeyboard();
    
    // Attributes
    ScanCodeSets scanCodeSet = new ScanCodeSets(); 
    
    // Relations
    private Emulator emu;
    private String[] moduleConnections = new String[] {"motherboard", "pic", "rtc"}; 
    private ModuleMotherboard motherboard;
    private ModulePIC pic;
    private ModuleRTC rtc;

    // Toggles
    private boolean isObserved;
    private boolean debugMode;
    
    // IRQ
    private int irqNumber;                  // Interrupt number assigned by PIC
    private boolean pendingIRQ;             // IRQ is still in progress
    
    // Timing
    private int updateInterval;
    
    // Logging
    private static Logger logger = Logger.getLogger("nl.kbna.dioscuri.module.keyboard");
    
    // Constants
    private final static int DATA_PORT = 0x60;      // Read/Write port
                                                    // Only read from after status port bit0 = 1
                                                    // Only write to if status port bit1 = 0
    private final static int STATUS_PORT = 0x64;    // Read/Write port
    
    static int kbdInitialised = 0;                 // Keep track of initialisation of keyboard

    // Module specifics
    public final static int MODULE_ID       = 1;
    public final static String MODULE_TYPE  = "keyboard";
    public final static String MODULE_NAME  = "101-key PS/2 QWERTY keyboard";
    
    
    // Constructor

    /**
     * Class constructor
     * 
     */
    public Keyboard(Emulator owner)
    {
        emu = owner;
        
        // Initialise variables
        isObserved = false;
        debugMode = false;
        
        // Initialise timing
        updateInterval = -1;
        
        // Initialise IRQ
        irqNumber = -1;
        pendingIRQ = false;
        
        // Set default LEDs and scancode setting
        keyboard.internalBuffer.ledStatus = 0;
        keyboard.internalBuffer.scanningEnabled = 1;

        // Set default statusbyte values
        keyboard.controller.parityError = 0;
        keyboard.controller.timeOut  = 0;
        keyboard.controller.auxBuffer = 0;
        keyboard.controller.keyboardLock = 1;
        keyboard.controller.commandData  = 1;
        keyboard.controller.systemFlag = 0;
        keyboard.controller.inputBuffer = 0;
        keyboard.controller.outputBuffer = 0;

        // Enable keyboard, clear command/data bytes and buffers
        keyboard.controller.kbdClockEnabled = 1;
        keyboard.controller.auxClockEnabled = 0;
        keyboard.controller.allowIRQ1 = 1;
        keyboard.controller.allowIRQ12 = 1;
        keyboard.controller.kbdOutputBuffer = 0;
        keyboard.controller.auxOutputBuffer = 0;
        keyboard.controller.lastCommand = 0;
        keyboard.controller.expectingPort60h = 0;
        keyboard.controller.irq1Requested = 0;
        keyboard.controller.irq12Requested = 0;
        keyboard.controller.batInProgress = 0;

        keyboard.controller.timerPending = 0;
        
        logger.log(Level.INFO, "[" + MODULE_TYPE + "]" + " Module created successfully.");
        //Scancodes.scancodesInit();
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
        // Set connection for motherboard
        if (mod.getType().equalsIgnoreCase("motherboard"))
        {
            this.motherboard = (ModuleMotherboard)mod;
            return true;
        }
        // Set connection for pic
        else if (mod.getType().equalsIgnoreCase("pic"))
        {
            this.pic = (ModulePIC)mod;
            return true;
        }
        // Set connection for pic
        else if (mod.getType().equalsIgnoreCase("rtc"))
        {
            this.rtc = (ModuleRTC)mod;
            return true;
        }
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
        if (motherboard != null && pic != null)
        {
            return true;
        }
        return false;
    }


    /**
     * Default inherited reset. Calls specific reset(int)
     * 
     * @return boolean true if module has been reset successfully, false otherwise
     */
    public boolean reset()
    {
        // Register I/O ports 0x60, 0x64 in I/O address space
        motherboard.setIOPort(DATA_PORT, this);
        motherboard.setIOPort(STATUS_PORT, this);
        
        // Request IRQ number
        irqNumber = pic.requestIRQNumber(this);
        if (irqNumber > -1)
        {
            logger.log(Level.CONFIG, "[" + MODULE_TYPE + "]" + " IRQ number set to: " + irqNumber);
        }
        else
        {
            logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " Request of IRQ number failed.");
        }
        
        // Request a timer
        if (motherboard.requestTimer(this, updateInterval, true) == false)
        {
            return false;
        }
        // Activate timer
        motherboard.setTimerActiveState(this, true);
        
        // Enter in CMOS reg 0x14 bit 2: keyboard enabled
        rtc.setCMOSRegister(0x14, (byte)((rtc.getCMOSRegister(0x14) | 0x04)));
        
        // Initiate cold reset
        return reset(1);
    }

    /**
     * Keyboard specific reset, with value to indicate reset type
     * 
     * @param resetType Type of reset passed to keyboard<BR>
     *                  0: Warm reset (SW reset)<BR>
     *                  1: Cold reset (HW reset)
     * 
     * @return boolean true if module has been reset successfully, false otherwise
     */
    public boolean reset(int resetType)
    {
        // Clear internal keyboard buffer
        keyboard.internalBuffer.buffer.clear();

        keyboard.internalBuffer.expectingTypematic = 0;

        // Default scancode set is mf2 with translation
        keyboard.internalBuffer.expectingScancodeSet = 0;
        keyboard.controller.currentScancodeSet = 1;
        keyboard.controller.translateScancode = 1;

        if (resetType != 0)
        {
            keyboard.internalBuffer.expectingLEDWrite = 0;
            keyboard.internalBuffer.keyPressDelay = 1;          // Set typematic delay to default 500 ms
            keyboard.internalBuffer.keyRepeatRate = 0x0b;       // Set typematic rate to default 10.9 chars/sec
        }

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
        String keyboardDump ="Keyboard status:\n";
       
        keyboardDump += "Internal buffer contents:";
        keyboardDump += keyboard.internalBuffer.buffer + "\n";
        keyboardDump += "Controller queue contents:";
        keyboardDump += keyboard.controllerQueue + "\n";
        
        return keyboardDump;
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
        return updateInterval;
    }

    /**
     * Defines the interval between subsequent updates
     * 
     * @param int interval in microseconds
     */
    public void setUpdateInterval(int interval)
    {
        // Check if interval is > 0
        if (interval > 0)
        {
            updateInterval = interval;
        }
        else
        {
            updateInterval = 200; // default is 200 ms
        }
        
        // Notify motherboard that interval has changed
        // (only if motherboard contains a clock, which may not be the case at startup, but may be during execution)
        motherboard.resetTimer(this, updateInterval);
    }


    /**
     * Update device
     * Calls the keyboard 'poll' function and raises the IRQs resulting from that call
     */
    public void update()
    {
        int returnValue;

        returnValue = poll();

        if ((returnValue & 0x01) == 1)
        {
            setInterrupt();
            logger.log(Level.CONFIG, "[" + MODULE_TYPE + "]" + " timer raises IRQ1");
        }
        if ((returnValue & 0x02) == 2)
        {
            // This keyboard does not support mouse IRQs, so warn user
            logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " Unsupported timer call to raise IRQ12");
        }
    }

    
    /**
     * IN instruction to keyboard<BR>
     * @param portAddress   the target port; should be either 0x60 or 0x64
     * <BR>
     * IN to portAddress 60h
     * IN to portAddress 64h returns the keyboard status
     * 
     * @return byte of data from output buffer
     */
    public byte getIOPortByte(int portAddress) throws ModuleUnknownPort, ModuleWriteOnlyPortException
    {
        byte value;

        switch (portAddress)
        {
            case (0x60):    // Output buffer
                if (keyboard.controller.outputBuffer != 0)    // Keyboard byte available
                { 
                    value = keyboard.controller.kbdOutputBuffer;
                    keyboard.controller.outputBuffer = 0;
                    keyboard.controller.auxBuffer = 0;
                    keyboard.controller.irq1Requested = 0;
                    keyboard.controller.batInProgress = 0;

                    if (!keyboard.controllerQueue.isEmpty())
                    {
                        keyboard.controller.auxOutputBuffer = (keyboard.controllerQueue.remove(0));
                        keyboard.controller.outputBuffer = 1;
                        keyboard.controller.auxBuffer = 1;
                        if (keyboard.controller.allowIRQ1 != 0)
                            keyboard.controller.irq1Requested = 1;
                        logger.log(Level.CONFIG, "s.controller_Qsize: " + keyboard.controllerQueue.size() + 1);
                    }

                    clearInterrupt();
                    activate_timer();
                    logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " Port 0x" + Integer.toHexString(portAddress).toUpperCase() + " read: " + Integer.toHexString(0x100 | value & 0xFF).substring(1).toUpperCase() + "h");
                    return value;
                }
                else    // Nothing to read available
                {
                    logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " Internal buffer elements no.: " + keyboard.internalBuffer.buffer.size());
                    logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " Port 0x60 read but output buffer empty!");
                    return keyboard.controller.kbdOutputBuffer;
                }

            case (0x64): // Status register; create status byte
                value = (byte) ((keyboard.controller.parityError << 7) | 
                              (keyboard.controller.timeOut << 6)  |
                              (keyboard.controller.auxBuffer << 5) |
                              (keyboard.controller.keyboardLock << 4) |
                              (keyboard.controller.commandData << 3)  |
                              (keyboard.controller.systemFlag << 2) |
                              (keyboard.controller.inputBuffer << 1) |
                              keyboard.controller.outputBuffer);
                keyboard.controller.timeOut = 0;
                return value;

            default:
                throw new ModuleUnknownPort(MODULE_TYPE + " does not recognise port 0x" + Integer.toHexString(portAddress).toUpperCase());
        }
    }

    /**
     * OUT instruction to keyboard<BR>
     * @param portAddress   the target port; should be either 0x60 or 0x64
     * @param value  the data written to the keyboard port
     * <BR>
     * OUT to portAddress 60h executes data port commands
     * OUT to portAddress 64h executes status port commands 
     */
    public void setIOPortByte(int portAddress, byte value) throws ModuleUnknownPort
    {
        logger.log(Level.CONFIG, "[" + MODULE_TYPE + "]" + " Port 0x" + Integer.toHexString(portAddress).toUpperCase() + " received write of 0x" + Integer.toHexString(0x100 | value & 0xFF).substring(1).toUpperCase());

        switch (portAddress)
        {
            case 0x60:      // Controller data port or keyboard input buffer
                if (keyboard.controller.expectingPort60h != 0)    // Expecting data from previous command byte (0x64)
                {
                    keyboard.controller.expectingPort60h = 0;     // Reset variable     
                    keyboard.controller.commandData = 0;          // Set last write to data
                    if (keyboard.controller.inputBuffer != 0)     // Should only be written to if status port bit 1 == 0
                    {
                        logger.log(Level.SEVERE, "[" + MODULE_TYPE + "]" + " Port 0x60 write but input buffer != 0");
                    }
                    switch (keyboard.controller.lastCommand)         // Determine data purpose based on last command received
                    {
                        case 0x60: // Write keyboard controller command byte
                        {
                            byte disableKeyboard;

                            keyboard.controller.translateScancode = (byte) ((value >> 6) & 0x01);
                            disableKeyboard                       = (byte) ((value >> 4) & 0x01);
                            keyboard.controller.systemFlag        = (byte) ((value >> 2) & 0x01);
                            keyboard.controller.allowIRQ1         = (byte) ((value >> 0) & 0x01);
                            keyboard.controller.allowIRQ12        = (byte) ((value >> 1) & 0x01);
                            
                            // Enable/disable keyboard and mouse according to command byte.
                            // Note: negating variable because these are expressed in negatives compared to clock setting 
                            setKeyboardClockState(disableKeyboard == 0 ? (byte) 1 : 0);
                            // Raise keyboard IRQ if allowed (and data available)
                            if ((keyboard.controller.allowIRQ1 != 0) && (keyboard.controller.outputBuffer != 0))
                                keyboard.controller.irq1Requested = 1;

                            logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " IRQ1 (keyb) allowance set to " + keyboard.controller.allowIRQ1);
                            logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " Unsupported IRQ12 (mouse) allowance! (not set to " + keyboard.controller.allowIRQ12 + ")");
                            if (keyboard.controller.translateScancode == 0)
                                logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " Scancode translation turned off");
                        }
                            return;

                        case (byte) 0xD1: // Write output port P2: next byte on port 0x60 is send to keyboard
                            // The only data processed here is the A20 line (bit 1) and a CPU reset (bit 0)
                            logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " Writing value 0x" + Integer.toHexString(0x100 | value & 0xFF).substring(1).toUpperCase() + " to output port P2");
                            logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + (((value & 0x02) == 2) ? "En" : "Dis") + "abled A20 gate");
                            motherboard.setA20((value & 0x02) != 0 ? true : false);
                            if (!((value & 0x01) != 0))     // Reset CPU
                            {
                                logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " System reset requested");
                                // TODO: Reset complete system
                                logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " Function not (yet) implemented");
                            }
                            return;

                        case (byte) 0xD2: // Queue byte in keyboard output buffer
                            enqueueControllerBuffer(value);
                            return;
                            
                        case (byte) 0xD3: // write mouse output buffer
                            logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " No mouse support available");
                            return;

                        case (byte) 0xD4: // Write to mouse
                            logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " No mouse support available");
                            return;

                        default:
                            logger.log(Level.SEVERE, "[" + MODULE_TYPE + "]" + " does not recognise command [" + Integer.toHexString(keyboard.controller.lastCommand).toUpperCase() + "] writing value " + Integer.toHexString(value).toUpperCase() + " to port " + Integer.toHexString(portAddress).toUpperCase());
                            throw new ModuleUnknownPort(MODULE_TYPE + " -> does not recognise command " + keyboard.controller.lastCommand + " writing value " + Integer.toHexString(value).toUpperCase() + " to port " + Integer.toHexString(portAddress).toUpperCase());
                    }
                }
                else    // Not expecting data, so pass directly to keyboard 
                {
                    keyboard.controller.commandData = 0;         // Last write is data
                    keyboard.controller.expectingPort60h = 0;    // Not expecting data to follow
                    if (keyboard.controller.kbdClockEnabled == 0)// If keyboard is not active, activate it
                    {
                        setKBClock((byte) 1);
                    }
                    dataPortToInternalKB(value);                // Pass data to keyboard
                }
                return;

            case 0x64:      // control register
                keyboard.controller.commandData = 1;                       // Last write is control byte
                keyboard.controller.lastCommand = value;             // Set this command as last_comm
                keyboard.controller.expectingPort60h = 0;         // Set this to 0 as default; can change depending on command issued

                switch (value)
                {
                    case 0x20:      // Read keyboard controller command byte
                        logger.log(Level.FINE, "Read keyboard controller command byte");
                        if (keyboard.controller.outputBuffer != 0)     // controller output buffer must be empty
                        {
                            logger.log(Level.WARNING,  "[" + MODULE_TYPE + "]" + " command 0x" + Integer.toHexString(value).toUpperCase() + " encountered but output buffer not empty!");
                            return;
                        }
                        byte command_byte = (byte)
                                            ((keyboard.controller.translateScancode << 6) | 
                                            ((keyboard.controller.auxClockEnabled == 0 ? 1 : 0) << 5) |  // negate as 0 indicates 'enabled' 
                                            ((keyboard.controller.kbdClockEnabled == 0 ? 1 : 0) << 4) |  // negate as 0 indicates 'enabled'
                                            (0 << 3) | 
                                            (keyboard.controller.systemFlag << 2) | 
                                            (keyboard.controller.allowIRQ12 << 1) | 
                                            (keyboard.controller.allowIRQ1 << 0));
                        enqueueControllerBuffer(command_byte);
                        return;

                    case 0x60:      // Write keyboard controller command byte
                        logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " Write keyboard controller command byte");
                        keyboard.controller.expectingPort60h = 1;     // Expecting command byte on port 0x60
                        return;

                    case (byte) 0xA0:   // Read copyright
                        // An ASCII copyright string (possibly just NULL) is made available for reading via port 0x60
                        logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + "Unsupported command on port 0x64: 0x" + Integer.toHexString(0x100 | value & 0xFF).substring(1).toUpperCase());
                        return;

                    case (byte) 0xA1:   // Read controller firmware version
                        // A single ASCII byte is made available for reading via port 0x60. On other systems: no effect, the command is ignored.
                        logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " Controller firmware version request: ignored");
                        return;

                    case (byte) 0xA7:   //  Disable mouse port - set clock line low
                        // This keyboard driver does not support a mouse port
                        logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " Mouse port (disable) not supported");
                        return;

                    case (byte) 0xA8:   //  Enable mouse port - set clock line high
                        // This keyboard driver does not support a mouse port
                        logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " Mouse port (enable) not supported");
                        return;

                    case (byte) 0xA9:   // Test mouse port; test the serial link between keyboard controller and mouse
                        if (keyboard.controller.outputBuffer != 0)     // Controller output buffer must be empty
                        {
                            logger.log(Level.WARNING,  "[" + MODULE_TYPE + "]" + " command 0x" + Integer.toHexString(value).toUpperCase() + " encountered but output buffer not empty!");
                            return;
                        }
                        // Possible returns: 0: OK. 1: Mouse clock line stuck low. 2: Mouse clock line stuck high. 3: Mouse data line stuck low. 4: Mouse data line stuck high. 0xFF: No mouse.
                        enqueueControllerBuffer((byte)0xFF);    // Return 'no mouse'
                        return;

                    case (byte) 0xAA:   // Controller self test
                        logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " Controller self test");
                        if (kbdInitialised == 0)
                        {
                            keyboard.controllerQueue.clear();
                            keyboard.controller.outputBuffer = 0;
                            kbdInitialised++;
                        }
                        if (keyboard.controller.outputBuffer != 0)     // Controller output buffer must be empty
                        {
                            logger.log(Level.WARNING,  "[" + MODULE_TYPE + "]" + " command 0x" + Integer.toHexString(value).toUpperCase() + " encountered but output buffer not empty!");
                            return;
                        }
                        keyboard.controller.systemFlag = 1;     // Self test complete
                        enqueueControllerBuffer((byte) 0x55);   // Return 0x55 if okay; 0xFC if not okay
                        return;

                    case (byte) 0xAB:   // Interface Test; test the serial link between keyboard controller and keyboard
                        if (keyboard.controller.outputBuffer != 0)     // Controller output buffer must be empty
                        {
                            logger.log(Level.WARNING,  "[" + MODULE_TYPE + "]" + " command 0x" + Integer.toHexString(value).toUpperCase() + " encountered but output buffer not empty!");
                            return;
                        }
                        // Possible returns: 0: OK. 1: Keyboard clock line stuck low. 2: Keyboard clock line stuck high. 3: Keyboard data line stuck low. 4: Keyboard data line stuck high. 0xff: General error.
                        enqueueControllerBuffer((byte) 0x00);
                        return;

                    case (byte) 0xAD:   //  Disable keyboard; set keyboard clock line low and set bit 4 of the command byte
                        // Note: any keyboard command enables the keyboard again
                        logger.log(Level.CONFIG, "[" + MODULE_TYPE + "]" + " Keyboard disabled");
                        setKBClock((byte) 0);
                        return;

                    case (byte) 0xAE:   // Enable keyboard; set keyboard clock line high and clear bit 4 of the command byte.
                        logger.log(Level.CONFIG, "[" + MODULE_TYPE + "]" + " Keyboard enabled");
                        setKBClock((byte) 1);
                        return;
                        
                    case (byte) 0xAF:   // Read controller version
                        logger.log(Level.SEVERE, "[" + MODULE_TYPE + "]" + "Unsupported command on port 0x64: 0x" + Integer.toHexString(0x100 | value & 0xFF).substring(1).toUpperCase());
                        return;
                        
                    case (byte) 0xC0:   // Read input port; read input port (P1), make the resulting byte available to be read from port 0x60.
                        if (keyboard.controller.outputBuffer != 0)     // Controller output buffer must be empty
                        {
                            logger.log(Level.WARNING,  "[" + MODULE_TYPE + "]" + " command 0x" + Integer.toHexString(value).toUpperCase() + " encountered but output buffer not empty!");
                            return;
                        }
                        // A lot of this information is unnecessary, so just return 'keyboard not inhibited', 0x80
                        enqueueControllerBuffer((byte) 0x80);
                        return;

                    case (byte) 0xC1:   // Continuous Input Port Poll, Low; cont. copy bits 3-0 of the input port to be read from bits 7-4 of port 0x64, until command is received
                    case (byte) 0xC2:   // Continuous Input Port Poll, High; cont. copy bits 7-4 of the input port to be read from bits 7-4 of port 0x64, until command is received
                        logger.log(Level.SEVERE, "[" + MODULE_TYPE + "]" + "Unsupported command on port 0x64: 0x" + Integer.toHexString(0x100 | value & 0xFF).substring(1).toUpperCase());
                        return;
                        
                    case (byte) 0xD0:   // Read output port; read output port (P2), placing result in output buffer
                        logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + "Partially supported command on port 0x64: 0x" + Integer.toHexString(0x100 | value & 0xFF).substring(1).toUpperCase());
                        if (keyboard.controller.outputBuffer != 0)     // controller output buffer must be empty
                        {
                            logger.log(Level.WARNING,  "[" + MODULE_TYPE + "]" + " command 0x" + Integer.toHexString(value).toUpperCase() + " encountered but output buffer not empty!");
                            return;
                        }
                        byte p2 = (byte)((keyboard.controller.irq12Requested << 5) |
                                         (keyboard.controller.irq1Requested << 4) |
                                         (motherboard.getA20() ? 1 : 0 << 1) |
                                         0x01);
                        enqueueControllerBuffer(p2);
                        return;

                    case (byte) 0xD1:   // Write output port; write value to output port P2
                        logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " Port 0x64: write output port P2");
                        keyboard.controller.expectingPort60h = 1;     // Expecting data on port 0x60 next
                        return;

                    case (byte) 0xD2:   // Write keyboard output buffer; write next byte to P2 and act as if this was keyboard data 
                        logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " Port 0x64: write keyboard output buffer");
                        keyboard.controller.expectingPort60h = 1;     // Expecting data on port 0x60 next
                        return;
                        
                    case (byte) 0xD3:   // Write mouse output buffer; write next byte to P2 and act as if this was mouse data
                        // Not supported but will be ignored at next byte
                        logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " Port 0x64: write mouse output buffer");
                        keyboard.controller.expectingPort60h = 1;     // Expecting data on port 0x60 next
                        return;

                    case (byte) 0xD4:   // Write to mouse; next byte to port 0x60 is transmitted to the mouse
                        // Not supported but will be ignored at next byte
                        logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " Port 0x64: write to mouse");
                        keyboard.controller.expectingPort60h = 1;     // Expecting data on port 0x60 next
                        return;

                    case (byte) 0xDD:   // Disable A20 Address Line
                        motherboard.setA20(false);
                        return;
                    case (byte) 0xDF:   // Enable A20 Address Line
                        motherboard.setA20(true);
                        return;

                    case (byte) 0xE0: // Read Test Inputs; make status test inputs T0 and T1 available for read via port 0x60 in bits 0 and 1
                        logger.log(Level.SEVERE, "[" + MODULE_TYPE + "]" + " Unsupported command to port 0x64: 0x" + Integer.toHexString(0x100 | value & 0xFF).substring(1).toUpperCase());
                        return;

                    case (byte) 0xFE: // System reset; resets the CPU
                        logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " Port 0x64: system reset");
                        // TODO: implement complete system reset
                        logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " Function not (yet) implemented");
                        return;

                    default:
                        if ((value >= 0xF0 && value <= 0xFD) || value == 0xFF)  // Pulse output bits
                        {
                            logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " Port 0x64: pulse output bits");
                            return;
                        }
                        logger.log(Level.SEVERE, "[" + MODULE_TYPE + "]" + " Unsupported command to port 0x64: 0x" + Integer.toHexString(0x100 | value & 0xFF).substring(1).toUpperCase());
                        return;
                }

            default:
                throw new ModuleUnknownPort("[" + MODULE_TYPE + "]" + " does not recognise OUT port 0x" + Integer.toHexString(portAddress).toUpperCase());
        }
    }

    public byte[] getIOPortWord(int portAddress) throws ModuleException, ModuleWriteOnlyPortException
    {
        logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" +  " IN command (word) to port 0x" + Integer.toHexString(portAddress).toUpperCase() + " received");
        logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" +  " Returned default value 0xFFFF to AX");
        
        // Return dummy value 0xFFFF
        return new byte[] { (byte) 0x0FF, (byte) 0x0FF };
    }


    public void setIOPortWord(int portAddress, byte[] dataWord) throws ModuleException
    {
        logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" +  " OUT command (word) to port 0x" + Integer.toHexString(portAddress).toUpperCase() + " received. No action taken.");
        
        // Do nothing and just return okay
        return;
    }


    public byte[] getIOPortDoubleWord(int portAddress) throws ModuleException, ModuleWriteOnlyPortException
    {
        logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" +  " IN command (double word) to port 0x" + Integer.toHexString(portAddress).toUpperCase() + " received");
        logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" +  " Returned default value 0xFFFFFFFF to eAX");
        
        // Return dummy value 0xFFFFFFFF
        return new byte[] { (byte) 0x0FF, (byte) 0x0FF, (byte) 0x0FF, (byte) 0x0FF };
    }


    public void setIOPortDoubleWord(int portAddress, byte[] dataDoubleWord) throws ModuleException
    {
        logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" +  " OUT command (double word) to port 0x" + Integer.toHexString(portAddress).toUpperCase() + " received. No action taken.");
        
        // Do nothing and just return okay
        return;
    }

    protected void setInterrupt()
    {
        // Raise an interrupt at IRQ 1 at PIC
        pic.setIRQ(1);
        pendingIRQ = true;
    }

    
    protected void clearInterrupt()
    {
        // Clear an interrupt at IRQ 1 at PIC
        pic.clearIRQ(1);
        if (pendingIRQ == true)
        {
            // Lower an interrupt at IRQ 1 at PIC
            pendingIRQ = false;
        }
    }

    //******************************************************************************
    // Additional methods

    /**
     * Set keyboard state (on/off) by enabling/disabling its clock line<BR>
     * @param value the state of the clock to be set
     * <BR>
     */
    void setKeyboardClockState(byte value)
    {
      byte oldKBDClockEnabled;

        if (value == 0)
        {
            keyboard.controller.kbdClockEnabled = 0;
        }
        else
        {
            // Another byte is waiting to be sent from the keyboard?
            oldKBDClockEnabled = keyboard.controller.kbdClockEnabled;
            keyboard.controller.kbdClockEnabled = 1;

            if (oldKBDClockEnabled == 0 && keyboard.controller.outputBuffer == 0)
            {
                activate_timer();
            }
      }
    }

    /**
     * Queue data in the keyboard controller buffer<BR>
     * @param data  the data to be added to the end of the queue
     * 
     */
    void enqueueControllerBuffer(byte data)
    {
        logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " Queueing 0x" + Integer.toHexString(data).toUpperCase() + " in keyboard controller buffer");

        // Check if output is available for writing
        if (keyboard.controller.outputBuffer != 0)
        {
            if (keyboard.controllerQueue.size() >= TheKeyboard.CONTROLLER_QUEUE_SIZE)
            {
                logger.log(Level.SEVERE, "[" + MODULE_TYPE + "]" + " queueKBControllerBuffer(): Keyboard controller is full!");
                //TODO: Implement possible abort of emulator here
            }
            keyboard.controllerQueue.add(data);
            return;
        }

        // Data can be added to buffer
        keyboard.controller.kbdOutputBuffer = data;
        keyboard.controller.outputBuffer = 1;      // Set status output bit to full, ready for read
        keyboard.controller.auxBuffer = 0;      // Set as keyboard data  
        keyboard.controller.inputBuffer = 0;      // Clear input, enable write
        if (keyboard.controller.allowIRQ1 != 0)
        {
            keyboard.controller.irq1Requested = 1;    // Raise IRQ1
        }
    }

    /**
     * Queue data in the internal keyboard buffer<BR>
     * @param scancode the data to be added to the end of the queue
     * 
     */    
    void enqueueInternalBuffer(byte scancode)
    {
      logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " enqueueInternalBuffer: 0x" + Integer.toHexString(0x100 | scancode & 0xFF).substring(1).toUpperCase());

      if (keyboard.internalBuffer.buffer.size() >= KeyboardInternalBuffer.NUM_ELEMENTS) {
          logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + "internal keyboard buffer full, ignoring scancode " + scancode);
        return;
      }

      // Add scancode to end of keyboard buffer queue
      logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " enqueueInternalBuffer: adding scancode " + Integer.toHexString(0x100 | scancode & 0xFF).substring(1).toUpperCase() + "h to internal buffer");
      keyboard.internalBuffer.buffer.add(scancode);
      
      if (!(keyboard.controller.outputBuffer != 0) && (keyboard.controller.kbdClockEnabled != 0)) {
        activate_timer();
        logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " Timer activated");
        return;
      }
    }

    /**
     * Activate a 'timer' to indicate to the periodic polling function<BR>
     * the internal keyboard queue should be checked for data.<BR><BR>
     * 
     * A timer can only be set, not disabled.
     * 
     */    
    void activate_timer()
    {
        if (keyboard.controller.timerPending == 0)
        {
            keyboard.controller.timerPending = 1;
        }
    }

    /**
     * The keyboard polling function<BR>
     * This determines the IRQs to be raised and retrieves character data from the internal keyboard buffer, if available
     * 
     */
    int poll()
    {
        int returnValue;     // IRQs to raise

        // Determine & reset IRQs
        returnValue = (keyboard.controller.irq1Requested | (keyboard.controller.irq12Requested << 1));
        keyboard.controller.irq1Requested = 0;
        keyboard.controller.irq12Requested = 0;

        // If no 'timers' are raised, no data is waiting so we can exit here
        if (keyboard.controller.timerPending == 0)
        {
            logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " poll(): No timer pending");
            return (returnValue);
        }

        // A 'timer' was raised, so proceed to check keyboard data
        keyboard.controller.timerPending = 0;     // Reset timers

        // There is data on the output port, so it is sufficient to raise the IRQs found and exit
        if (keyboard.controller.outputBuffer != 0)
        {
            logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " poll(): output buffer");
            return (returnValue);
        }

        // A 'timer' was raised, so check if there is character data and get this from the keyboard buffer
        if (!keyboard.internalBuffer.buffer.isEmpty() && (keyboard.controller.kbdClockEnabled != 0 || keyboard.controller.batInProgress != 0))
        {
            logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " poll(): key in internal buffer waiting");
            keyboard.controller.kbdOutputBuffer = keyboard.internalBuffer.buffer.remove(0);
            keyboard.controller.outputBuffer = 1;
            if (keyboard.controller.allowIRQ1 != 0)
                keyboard.controller.irq1Requested = 1;
        }
        else
        {
            logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " poll(): no keys waiting");
        }
        return (returnValue);
    }
    
    /**
     * Set the keyboard clock, which determines the on/off state of the keyboard<BR>
     * @param value the state of the clock should be set to:<BR>
     *              0: keyboard clock is disabled, turning the keyboard off<BR>
     *              other: keyboard clock is enabled, turning the keyboard on 
     * <BR>
     */    
    void setKBClock(byte value)
    {
        byte oldKBDClock;

        if (value == 0) // Disable the keyboard clock, effectively disabling the keyboard
        {
            keyboard.controller.kbdClockEnabled = 0;
        }
        else            // Activate keyboard clock 
        {
            oldKBDClock= keyboard.controller.kbdClockEnabled;
            keyboard.controller.kbdClockEnabled = 1;

            if (oldKBDClock == 0 && keyboard.controller.outputBuffer == 0); // If there is more data in the queue, activate the timer for it to be processed
            {
                activate_timer();
            }
        }
    }
    
    /**
     * Data passing directly from keyboard controller to keyboard<BR>
     * The keyboard usually immediately responds by enqueueing data in its buffer for the keyboard controller<BR>
     * 
     * @param value the data passed from controller to keyboard <BR>
     */
    void dataPortToInternalKB(byte value)
    {
        logger.log(Level.CONFIG, "[" + MODULE_TYPE + "]" + " Controller passing byte " + Integer.toHexString(0x100 | value & 0xFF).substring(1).toUpperCase() + "h directly to keyboard");

        // Previous command was typematic repeat/delay?
        if (keyboard.internalBuffer.expectingTypematic != 0)
        {
            keyboard.internalBuffer.expectingTypematic = 0; // Reset variable
            // Set typematic repeat/delay as follows:
            // Bit 7 = 0 : reserved
            // Bit 6-5 : typematic delay
            // 00b=250ms 10b= 750ms
            // 01b=500ms 11b=1000ms
            // bit 4-0 : typematic rate
            // 00000b=30.0 00001b=26.7 00010b=24.0 00011b=21.8 00100b=20.0 00101b=18.5 00110b=17.1 00111b=16.0
            // 01000b=15.0 01001b=13.3 01010b=12.0 01011b=10.9 01100b=10.0 01101b=9.2  01110b=8.5  01111b=8.0
            // 10000b=7.5  10001b=6.7  10010b=6.0  10011b=5.5  10100b=5.0  10101b=4.6  10110b=4.3  10111b=4.0
            // 11000b=3.7  11001b=3.3  11010b=3.0  11011b=2.7  11100b=2.5  11101b=2.3  11110b=2.1  11111b=2.0
            keyboard.internalBuffer.keyPressDelay = (byte) ((value >> 5) & 0x03); // Set typematic delay
            switch (keyboard.internalBuffer.keyPressDelay)
            {
                case 0:
                    logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " typematic delay (unused) set to 250 ms");
                    break;
                case 1:
                    logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " typematic delay (unused) set to 500 ms");
                    break;
                case 2:
                    logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " typematic delay (unused) set to 750 ms");
                    break;
                case 3:
                    logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " typematic delay (unused) set to 1000 ms");
                    break;
            }

            keyboard.internalBuffer.keyRepeatRate = (byte) (value & 0x1f); // Set repeat rate
            double cps = 1000 / ((8 + (value & 0x07)) * Math.pow(2, ((value >> 3) & 0x03)) * 4.17); // This formula is an approximation to the above table,
                                                                                                    // taken from http://heim.ifi.uio.no/~stanisls/helppc/keyboard_commands.html
            DecimalFormat format = new DecimalFormat("##.#");
            logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " Repeat rate (unused) set to " + format.format(cps) + "char. per second");
            enqueueInternalBuffer((byte) 0xFA); // ACKnowledge data
            return;
        }

        // Previous command was LED change?
        if (keyboard.internalBuffer.expectingLEDWrite != 0)
        {
            keyboard.internalBuffer.expectingLEDWrite = 0; // Reset variable
            keyboard.internalBuffer.ledStatus = value;
            logger.log(Level.SEVERE, "[" + MODULE_TYPE + "]" + " Status of LEDs set to " + Integer.toHexString(keyboard.internalBuffer.ledStatus));
            
            // Toggle lights in statusbar according to values
            switch (value)
            {
                case 0x00:
                    emu.statusChanged(Emulator.MODULE_KEYBOARD_NUMLOCK_OFF);
                    emu.statusChanged(Emulator.MODULE_KEYBOARD_CAPSLOCK_OFF);
                    emu.statusChanged(Emulator.MODULE_KEYBOARD_SCROLLLOCK_OFF);
                    break;
                    
                case 0x01:
                    emu.statusChanged(Emulator.MODULE_KEYBOARD_NUMLOCK_ON);
                    emu.statusChanged(Emulator.MODULE_KEYBOARD_CAPSLOCK_OFF);
                    emu.statusChanged(Emulator.MODULE_KEYBOARD_SCROLLLOCK_OFF);
                    break;

                case 0x02:
                    emu.statusChanged(Emulator.MODULE_KEYBOARD_NUMLOCK_OFF);
                    emu.statusChanged(Emulator.MODULE_KEYBOARD_CAPSLOCK_ON);
                    emu.statusChanged(Emulator.MODULE_KEYBOARD_SCROLLLOCK_OFF);
                    break;

                case 0x03:
                    emu.statusChanged(Emulator.MODULE_KEYBOARD_NUMLOCK_ON);
                    emu.statusChanged(Emulator.MODULE_KEYBOARD_CAPSLOCK_ON);
                    emu.statusChanged(Emulator.MODULE_KEYBOARD_SCROLLLOCK_OFF);
                    break;

                case 0x04:
                    emu.statusChanged(Emulator.MODULE_KEYBOARD_NUMLOCK_OFF);
                    emu.statusChanged(Emulator.MODULE_KEYBOARD_CAPSLOCK_OFF);
                    emu.statusChanged(Emulator.MODULE_KEYBOARD_SCROLLLOCK_ON);
                    break;

                case 0x05:
                    emu.statusChanged(Emulator.MODULE_KEYBOARD_NUMLOCK_ON);
                    emu.statusChanged(Emulator.MODULE_KEYBOARD_CAPSLOCK_OFF);
                    emu.statusChanged(Emulator.MODULE_KEYBOARD_SCROLLLOCK_ON);
                    break;

                case 0x06:
                    emu.statusChanged(Emulator.MODULE_KEYBOARD_NUMLOCK_ON);
                    emu.statusChanged(Emulator.MODULE_KEYBOARD_CAPSLOCK_ON);
                    emu.statusChanged(Emulator.MODULE_KEYBOARD_SCROLLLOCK_ON);
                    break;
                    
                default:
                    break;
            }
            
            enqueueInternalBuffer((byte) 0xFA); // ACKnowledge data
            return;
        }

        // Previous command was change scancode set?
        if (keyboard.internalBuffer.expectingScancodeSet != 0)
        {
            keyboard.internalBuffer.expectingScancodeSet = 0; // Reset variable
            if (value != 0)
            {
                if (value < 4)
                {
                    keyboard.controller.currentScancodeSet = (byte) (value - 1);
                    logger.log(Level.INFO, "[" + MODULE_TYPE + "]" + " Switching to scancode set "
                            + Integer.toHexString(keyboard.controller.currentScancodeSet + 1));
                    enqueueInternalBuffer((byte) 0xFA); // ACKnowledge data
                }
                else
                {
                    logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " Scancode set number out of range: " + Integer.toHexString(value).toUpperCase());
                    enqueueInternalBuffer((byte) 0xFF); // Send ERRor response
                }
            }
            else
            {
                // Send ACK
                enqueueInternalBuffer((byte) 0xFA);
                // Send current scancodes set to port 0x60
                enqueueInternalBuffer((byte) (1 + (keyboard.controller.currentScancodeSet)));
            }
            return;
        }

        // Value send is command, respond appropriately
        switch (value)
        {
            case (byte) 0x00: // Unknown command - ignore and let OS timeout with no response
                enqueueInternalBuffer((byte) 0xFA); // ACKnowledge command
                break;

            case (byte) 0x05: // Unknown command
                keyboard.controller.systemFlag = 1;
                // TODO: Bochs uses separate enqueue function here, why?
                enqueueInternalBuffer((byte) 0xFE); // Issue RESEND
                break;

            case (byte) 0xD3: // Unknown command
                enqueueInternalBuffer((byte) 0xFA); // What the heck, ACKnowledge it
                break;

            case (byte) 0xED: // Set/Reset Mode Indicators
                keyboard.internalBuffer.expectingLEDWrite = 1; // Next byte will be data indicating settings
                // TODO: Bochs uses separate enqueue function here, why?
                enqueueInternalBuffer((byte) 0xFA); // ACKnowledge command
                break;

            case (byte) 0xEE: // Diagnostic Echo
                enqueueInternalBuffer((byte) 0xEE); // Echo command (EEh)
                break;

            case (byte) 0xF0: // Select/Read Alternate Scancode Set
                keyboard.internalBuffer.expectingScancodeSet = 1; // Next byte will be data indicating settings
                logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " Expecting scancode set information");
                enqueueInternalBuffer((byte) 0xFA); // ACKnowledge command
                break;

            case (byte) 0xF2: // Read Keyboard ID
                logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " Read Keyboard ID command received");

                // XT sends nothing, AT sends ACK
                // MFII with translation sends ACK+ABh+41h
                // MFII without translation sends ACK+ABh+83h
                enqueueInternalBuffer((byte) 0xFA); // ACKnowledge command
                enqueueInternalBuffer((byte) 0xAB); // Send ID byte 1

                if (keyboard.controller.translateScancode != 0)
                    enqueueInternalBuffer((byte) 0x41); // Send ID byte 2
                else
                    enqueueInternalBuffer((byte) 0x83); // Send ID byte 2
                break;

            case (byte) 0xF3: // Set Typematic Rate/Delay
                keyboard.internalBuffer.expectingTypematic = 1; // Next byte will be data indicating settings
                logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " Expecting Typematic Rate/Delay information");
                enqueueInternalBuffer((byte) 0xFA); // ACKnowledge command
                break;

            case (byte) 0xF4: // Enable Keyboard
                keyboard.internalBuffer.scanningEnabled = 1;
                enqueueInternalBuffer((byte) 0xFA); // ACKnowledge command
                break;

            case (byte) 0xF5: // Default with Disable
                reset(1); // Reset keyboard to default settings
                enqueueInternalBuffer((byte) 0xFA); // ACKnowledge command
                keyboard.internalBuffer.scanningEnabled = 0; // Disable scanning
                logger.log(Level.CONFIG, "[" + MODULE_TYPE + "]" + " Reset w/ Disable command received");
                break;

            case (byte) 0xF6: // Default with Enable
                reset(1); // Reset keyboard to default settings
                enqueueInternalBuffer((byte) 0xFA); // ACKnowledge command
                keyboard.internalBuffer.scanningEnabled = 1; // Enable scanning
                logger.log(Level.CONFIG, "[" + MODULE_TYPE + "]" + " Reset w Enable command received");
                break;

            case (byte) 0xFE: // Resend (transmission error detected)
                logger.log(Level.WARNING, "[" + MODULE_TYPE + "]" + " Requesting resend: transmission error!!");
                break;

            case (byte) 0xFF: // Reset with BAT
                logger.log(Level.INFO, "[" + MODULE_TYPE + "]" + " Reset w/ BAT command received");
                reset(1); // Reset keyboard to default settings
                enqueueInternalBuffer((byte) 0xFA); // ACKnowledge command
                keyboard.controller.batInProgress = 1; // Pretend to do Basic Assurance Test
                enqueueInternalBuffer((byte) 0xAA); // Ta-dah! BAT finished!
                break;

            case (byte) 0xF7: // PS/2 Set All Keys To Typematic
            case (byte) 0xF8: // PS/2 Set All Keys to Make/Break
            case (byte) 0xF9: // PS/2 PS/2 Set All Keys to Make
            case (byte) 0xFA: // PS/2 Set All Keys to Typematic Make/Break
            case (byte) 0xFB: // PS/2 Set Key Type to Typematic
            case (byte) 0xFC: // PS/2 Set Key Type to Make/Break
            case (byte) 0xFD: // PS/2 Set Key Type to Make
            
            default:
                logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + " dataPortToInternalKB(): got value of " + value);
                enqueueInternalBuffer((byte) 0xFE); // RESEND - is this the correct response???
                break;
        }
    }
    

    /**
     * Method generateScancode
     * Generates a scancode from a KeyEvent.<BR>
     * The scancode depends on what scancode set is currently active, and whether the key is pressed or released
     * 
     * @param keyEvent KeyEvent containing keypress information
     * @param eventType Type of KeyEvent, either pressed (0x00) or released (0x01)
     */
    public void generateScancode(KeyEvent keyEvent, int eventType)
    {
        String[] scancode; // Lookup of scancode generated by currently selected set
        int i, parsedInt;

        logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + "generateScancode(): " + keyEvent.getKeyCode() + ((eventType == 1) ? " pressed" : " released"));

        // Ignore scancode if keyboard or scanning disabled
        if (keyboard.controller.kbdClockEnabled == 0 || keyboard.internalBuffer.scanningEnabled == 0)
            return;

        // Switch between make and break code
        scancode = scanCodeSet.scancodes[keyboard.controller.currentScancodeSet][keyEvent.getKeyCode()][eventType].split(" ");
        // Distuinguish between left and right Ctrl, Alt and Shift
        if (keyEvent.getKeyCode() == KeyEvent.VK_CONTROL || keyEvent.getKeyCode() == KeyEvent.VK_ALT || keyEvent.getKeyCode() == KeyEvent.VK_SHIFT)
        {
            if (keyEvent.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT) // Left side is located 3 earlier in the array for ctrl/alt/shift
                scanCodeSet.scancodes[keyboard.controller.currentScancodeSet][keyEvent.getKeyCode() - 3][eventType].split(" ");
        }

        // Distuinguish between normal ENTER and numpad ENTER
        if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER)
        {
            if (keyEvent.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD) // Next in array
                scanCodeSet.scancodes[keyboard.controller.currentScancodeSet][keyEvent.getKeyCode() + 1][eventType].split(" ");
        }
        // Check if the scancode generated needs to be translated by the 8042 controller or send as raw data
        if (keyboard.controller.translateScancode != 0)
        {
            // Translation necessary; each F0 prefix is turned into an OR with 0x80 with the next byte (e.g. break code F0 c is turned into c+0x80)
            int valueOR = 0x00;
            for (i = 0; i < scancode.length; i++)
            {
                parsedInt = Integer.parseInt(scancode[i], 16);  // Cast to integer in hexadecimal 
                if (parsedInt == 0xF0)
                {
                    valueOR = 0x80;  // Current byte is a prefix, so prepare to OR with 0x80  
                }
                else
                {
                    logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + "generateScancode(): Translated scancode to " + (scanCodeSet.translate8042[parsedInt] | valueOR));
                    enqueueInternalBuffer((byte) (scanCodeSet.translate8042[parsedInt] | valueOR));
                    valueOR = 0x00;     // Reset OR value to 0
                }
            }
        }
        else
        {
            // Send raw data
            for (i = 0; i < scancode.length; i++)
            {
                logger.log(Level.FINE, "[" + MODULE_TYPE + "]" + "generateScancode(): Writing raw " + scancode[i]);
                enqueueInternalBuffer((byte) Integer.parseInt(scancode[i], 16));
            }
        }
    }
}
