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
package dioscuri.module.pit;

import dioscuri.Emulator;
import dioscuri.exception.ModuleException;
import dioscuri.exception.UnknownPortException;
import dioscuri.exception.WriteOnlyPortException;
import dioscuri.interfaces.Module;
import dioscuri.module.ModuleMotherboard;
import dioscuri.module.ModulePIC;
import dioscuri.module.ModulePIT;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An implementation of a Programmable Interval Timer (PIT) module based on the
 * Intel 82C54 PIT chip.
 *
 * @see dioscuri.module.AbstractModule
 *      <p/>
 *      Metadata module ********************************************
 *      general.type : pit general.name : Programmable Interval Timer (PIT)
 *      based on Intel 82C54 PIT general.architecture : Von Neumann
 *      general.description : Implements the Intel 82C54 PIT extended with a
 *      system clock general.creator : Tessella Support Services, Koninklijke
 *      Bibliotheek, Nationaal Archief of the Netherlands general.version : 1.0
 *      general.keywords : pit, timer, counter general.relations : motherboard,
 *      cpu general.yearOfIntroduction : 1994 general.yearOfEnding :
 *      general.ancestor : Intel 8253 general.successor : pit.numberOfCounters :
 *      3 pit.numberOfModes : 6 pit.clockFrequency : 1193181 (ideally, but maybe
 *      not feasible in Java)
 *      <p/>
 *      <p/>
 *      Notes: This PIT is implemented to be as close as possible to the
 *      functional behaviour of an original Intel 82C54. However, not all logic
 *      has to be implemented. Not part of this implementation is: - Signal
 *      lines WR, RD, CD, A0 and A1.
 *      <p/>
 *      Normally counters 0,1,2 are defined to a particular task (RTC, etc.). In
 *      this implementation counter zero generates a IRQ 0 if the count becomes
 *      zero. Other counters are not assigned.
 *      <p/>
 *      In the original PIT, the clock signal is generated by a separate
 *      hardware component (crystal). In the emulated PIT, the clock signal is
 *      generated by a separate moduleClock (imitates the crystal, but may have
 *      a different frequency).
 */
public class PIT extends ModulePIT {

    // Relations
    private Counter[] counters;

    // IRQ number
    protected int irqNumber;

    // Timing
    private int updateInterval; // Denotes the update interval for the clock timer

    // Logging
    private static final Logger logger = Logger.getLogger(PIT.class.getName());

    // I/O ports PIT
    private final static int PORT_PIT_COUNTER0 = 0x040; // RW
    private final static int PORT_PIT_COUNTER1 = 0x041; // RW
    private final static int PORT_PIT_COUNTER2 = 0x042; // RW
    private final static int PORT_PIT_CONTROLWORD1 = 0x043; // RW - control word for counters 0-2
    private final static int PORT_PIT_COUNTER3 = 0x044; // RW - counter 3 (PS/2, EISA), fail-safe timer
    private final static int PORT_PIT_CONTROLWORD2 = 0x047; // W - control word for counter 3
    private final static int PORT_PIT_EISA = 0x048; // ?? -
    private final static int PORT_PIT_TIMER2 = 0x049; // ?? - timer 2 (not used)
    private final static int PORT_PIT_EISA_PIT2A = 0x04A; // ?? - EISA PIT 2
    private final static int PORT_PIT_EISA_PIT2B = 0x04B; // ?? - EISA PIT 2
    private final static int PORT_KB_CTRL_B = 0x61; // Keyboard Controller Port B, in Bochs assigned to PIT (PC speaker??)

    /**
     * Class constructor
     *
     * @param owner
     */
    public PIT(Emulator owner) {

        // Initialise timing
        updateInterval = -1;

        // Create three new individual counters
        counters = new Counter[3];
        for (int c = 0; c < counters.length; c++) {
            counters[c] = new Counter(this, c);
        }

        logger.log(Level.INFO, "[" + super.getType() + "] " + getClass().getName()
                + " -> AbstractModule created successfully.");
    }

    /**
     * {@inheritDoc}
     *
     * @see dioscuri.module.AbstractModule
     */
    @Override
    public boolean reset() {

        ModuleMotherboard motherboard = (ModuleMotherboard) super.getConnection(Module.Type.MOTHERBOARD);
        ModulePIC pic = (ModulePIC) super.getConnection(Module.Type.PIC);

        // Register I/O ports 0x000 - 0x00F in I/O address space
        motherboard.setIOPort(PORT_PIT_COUNTER0, this);
        motherboard.setIOPort(PORT_PIT_COUNTER1, this);
        motherboard.setIOPort(PORT_PIT_COUNTER2, this);
        motherboard.setIOPort(PORT_PIT_CONTROLWORD1, this);
        motherboard.setIOPort(PORT_PIT_COUNTER3, this);
        motherboard.setIOPort(PORT_PIT_CONTROLWORD2, this);
        motherboard.setIOPort(PORT_PIT_EISA, this);
        motherboard.setIOPort(PORT_PIT_TIMER2, this);
        motherboard.setIOPort(PORT_PIT_EISA_PIT2A, this);
        motherboard.setIOPort(PORT_PIT_EISA_PIT2B, this);

        // Register IRQ number
        irqNumber = pic.requestIRQNumber(this);
        if (irqNumber > -1) {
            logger.log(Level.CONFIG, "[" + super.getType()
                    + "] IRQ number set to: " + irqNumber);
        } else {
            logger.log(Level.WARNING, "[" + super.getType()
                    + "] Request of IRQ number failed.");
        }

        // Request a timer
        if (motherboard.requestTimer(this, updateInterval, true)) {
            logger.log(Level.CONFIG, "[" + super.getType() + "]"
                    + " Timer requested successfully.");
        } else {
            logger.log(Level.WARNING, "[" + super.getType() + "]"
                    + " Failed to request a timer.");
        }

        // Activate timer
        motherboard.setTimerActiveState(this, true);

        logger.log(Level.INFO, "[" + super.getType() + "] AbstractModule has been reset.");
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @see dioscuri.module.AbstractModule
     */
    @Override
    public String getDump() {
        // Show some status information of this module
        String dump = "";
        String ret = "\r\n";
        String tab = "\t";

        dump = "PIT dump:" + ret;

        dump += "Update interval is " + this.updateInterval
                + " instructions/update." + ret;
        dump += "In total " + counters.length + " counters exist:" + ret;
        for (int i = 0; i < counters.length; i++) {
            if (counters[i].isEnabled()) {
                dump += "Counter " + i + tab + ": mode "
                        + counters[i].counterMode;
                dump += ", count: start="
                        + +(((counters[i].cr[Counter.MSB] & 0x000000FF) << 8) + ((counters[i].cr[Counter.LSB]) & 0x000000FF));
                dump += ", current="
                        + (((counters[i].ce[Counter.MSB] & 0x000000FF) << 8) + ((counters[i].ce[Counter.LSB]) & 0x000000FF));
                dump += ", R/W-mode=" + counters[i].rwMode;
                dump += ", signals: OUT=" + counters[i].getGateSignal()
                        + " GATE=" + counters[i].getOutSignal();
                dump += ", parity: "
                        + (counters[i].getParity() ? "EVEN" : "ODD");
                dump += ", bcd: "
                        + (counters[i].getBCD() ? "BCD mode"
                        : "Decimal mode") + ret;
            } else {
                dump += "Counter " + i + tab + ": mode "
                        + counters[i].counterMode + ", not used" + ret;
            }
        }

        return dump;
    }

    /**
     * {@inheritDoc}
     *
     * @see dioscuri.interfaces.Updateable
     */
    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }

    /**
     * {@inheritDoc}
     *
     * @see dioscuri.interfaces.Updateable
     */
    @Override
    public void setUpdateInterval(int interval) {
        // Check if interval is > 0
        if (interval > 0) {
            updateInterval = interval;
        } else {
            updateInterval = 1000; // default is 1 ms
        }
        ModuleMotherboard motherboard = (ModuleMotherboard) super.getConnection(Module.Type.MOTHERBOARD);
        motherboard.resetTimer(this, updateInterval);
    }

    /**
     * {@inheritDoc}
     *
     * @see dioscuri.interfaces.Updateable
     */
    @Override
    public void update() {
        // Send pulse to all counters
        for (int c = 0; c < counters.length; c++) {
            counters[c].clockPulse();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see dioscuri.interfaces.Addressable
     */
    @Override
    public byte getIOPortByte(int portAddress) throws ModuleException,
            UnknownPortException {
        logger.log(Level.INFO, "[" + super.getType() + "]"
                + " I/O read from address 0x"
                + Integer.toHexString(portAddress));

        byte returnValue = 0x00;

        // Handle data based on portAddress
        switch (portAddress) {
            case PORT_PIT_COUNTER0: // Counter 0
                returnValue = counters[0].getCounterValue();
                break;

            case PORT_PIT_COUNTER1: // Counter 1
                logger.log(Level.WARNING, "[" + super.getType() + "]"
                        + " Attempted read of Counter 1 [0x41]");
                returnValue = counters[1].getCounterValue();
                break;

            case PORT_PIT_COUNTER2: // Counter 2
                returnValue = counters[2].getCounterValue();
                break;

            case PORT_PIT_CONTROLWORD1: // Control word
                // Do nothing as reading from control word register is not possible
                logger.log(Level.WARNING, "[" + super.getType() + "]"
                        + " Attempted read of control word port [0x43]");
                break;

            case PORT_KB_CTRL_B: // Port 0x61
                // Report reading from port
                logger.log(Level.WARNING, "[" + super.getType() + "]"
                        + " Attempted read of KB_CTRL_B [0x61]");
                break;

            default:
                throw new UnknownPortException("[" + super.getType()
                        + "] Unknown I/O port requested");
        }

        // Return dummy value 0
        return returnValue;
    }

    /**
     * {@inheritDoc}
     *
     * @see dioscuri.interfaces.Addressable
     */
    @Override
    public void setIOPortByte(int portAddress, byte data)
            throws ModuleException, UnknownPortException {
        logger.log(Level.INFO, "[" + super.getType() + "]" + " I/O write to 0x"
                + Integer.toHexString(portAddress) + " = 0x"
                + Integer.toHexString(data));

        // Handle writing data based on portAddress
        switch (portAddress) {
            case PORT_PIT_COUNTER0: // Counter 0
                logger.log(Level.CONFIG, "[" + super.getType()
                        + "] Counter 0: value set to 0x"
                        + Integer.toHexString(data & 0xFF));
                counters[0].setCounterValue(data);
                break;

            case PORT_PIT_COUNTER1: // Counter 1
                logger.log(Level.CONFIG, "[" + super.getType()
                        + "] Counter 1: value set to 0x"
                        + Integer.toHexString(data & 0xFF));
                counters[1].setCounterValue(data);
                break;

            case PORT_PIT_COUNTER2: // Counter 2
                logger.log(Level.CONFIG, "[" + super.getType()
                        + "] Counter 2: value set to 0x"
                        + Integer.toHexString(data & 0xFF));
                counters[2].setCounterValue(data);
                break;

            case PORT_PIT_CONTROLWORD1: // Control word
                // Analyse control word:
                // --------------------------------
                // D7 D6 D5 D4 D3 D2 D1 D0
                // SC1 SC0 RW1 RW0 M2 M1 M0 BCD

                int iData = (((int) data) & 0xFF);
                // Counter select (SC1/SC0)
                int cNum = (iData >> 6);
                // Read/Write mode (RW1/RW0)
                int rwMode = (iData >> 4) & 0x03;
                int counterMode = (iData >> 1) & 0x07;
                int bcd = iData & 0x01;

                // Check for valid data
                if ((counterMode > 6) || (rwMode > 4)) {
                    logger.log(Level.SEVERE, "[" + super.getType()
                            + "] ControlWord counterMode (" + counterMode
                            + ") / rwMode (" + rwMode + ") out of range");
                    break;
                }

                // check if ControlWord is a read-back command
                if (cNum == 0x03) {
                    // Read-back command: set appropriate counter in read-back mode
                    // TODO: implement this following Intel 82C54 specs
                    logger.log(Level.WARNING, "[" + super.getType()
                            + "] Read-Back Command is not implemented");
                    break;
                }

                // Perform counter setup, where cNum denotes counter 0, 1 or 2
                switch (rwMode) {
                    case 0x00: // Counter latch
                        // Read operation: Counter latch command
                        logger.log(Level.CONFIG, "[" + super.getType() + "] Counter "
                                + cNum + " in latch mode.");
                        // Set specified counter in latch register
                        counters[cNum].latchCounter();
                        break;

                    case 0x01: // LSB mode
                    case 0x02: // MSB mode
                        logger.log(Level.WARNING, "[" + super.getType()
                                + "] LSB/MSB command not implemented");
                        break;

                    case 0x03: // 16-bit mode
                        logger.log(Level.CONFIG, "[" + super.getType() + "] Counter "
                                + cNum + " in 16-bit mode.");
                        counters[cNum].setCounterMode(counterMode);
                        counters[cNum].rwMode = rwMode;
                        break;

                    default:
                        logger.log(Level.WARNING, "[" + super.getType() + "] rwMode ["
                                + rwMode + "] not recognised");
                        break;

                }
                break;
            default:
                throw new UnknownPortException("[" + super.getType()
                        + "] Unknown I/O port requested");
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see dioscuri.interfaces.Addressable
     */
    @Override
    public byte[] getIOPortWord(int portAddress) throws ModuleException,
            WriteOnlyPortException {
        logger.log(Level.WARNING, "[" + super.getType()
                + "] IN command (word) to port "
                + Integer.toHexString(portAddress).toUpperCase() + " received");
        logger.log(Level.WARNING, "[" + super.getType()
                + "] Returned default value 0xFFFF to AX");

        // Return dummy value 0xFFFF
        return new byte[]{(byte) 0x0FF, (byte) 0x0FF};
    }

    /**
     * {@inheritDoc}
     *
     * @see dioscuri.interfaces.Addressable
     */
    @Override
    public void setIOPortWord(int portAddress, byte[] dataWord)
            throws ModuleException {
        logger.log(Level.WARNING, "[" + super.getType()
                + "] OUT command (word) to port "
                + Integer.toHexString(portAddress).toUpperCase()
                + " received. No action taken.");
    }

    /**
     * {@inheritDoc}
     *
     * @see dioscuri.interfaces.Addressable
     */
    @Override
    public byte[] getIOPortDoubleWord(int portAddress) throws ModuleException,
            WriteOnlyPortException {
        logger.log(Level.WARNING, "[" + super.getType()
                + "] IN command (double word) to port "
                + Integer.toHexString(portAddress).toUpperCase() + " received");
        logger.log(Level.WARNING, "[" + super.getType()
                + "] Returned default value 0xFFFFFFFF to eAX");

        // Return dummy value 0xFFFFFFFF
        return new byte[]{(byte) 0x0FF, (byte) 0x0FF, (byte) 0x0FF,
                (byte) 0x0FF};
    }

    /**
     * {@inheritDoc}
     *
     * @see dioscuri.interfaces.Addressable
     */
    @Override
    public void setIOPortDoubleWord(int portAddress, byte[] dataDoubleWord)
            throws ModuleException {
        logger.log(Level.WARNING, "[" + super.getType()
                + "] OUT command (double word) to port "
                + Integer.toHexString(portAddress).toUpperCase()
                + " received. No action taken.");
    }

    /**
     * @param counter
     */
    protected void raiseIRQ(Counter counter) {
        ModulePIC pic = (ModulePIC) super.getConnection(Module.Type.PIC);
        pic.setIRQ(irqNumber);
    }

    /**
     * @param counter
     */
    protected void lowerIRQ(Counter counter) {
        ModulePIC pic = (ModulePIC) super.getConnection(Module.Type.PIC);
        pic.clearIRQ(irqNumber);
    }
}
