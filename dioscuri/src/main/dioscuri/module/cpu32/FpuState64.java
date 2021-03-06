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

//import java.math.BigDecimal;
//import org.jpc.emulator.processor.*;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author Bram Lohman
 * @author Bart Kiers
 */
public class FpuState64 extends FpuState {
    public final static int FPU_SPECIAL_TAG_NONE = 0;
    public final static int FPU_SPECIAL_TAG_NAN = 1;
    public final static int FPU_SPECIAL_TAG_UNSUPPORTED = 2;
    public final static int FPU_SPECIAL_TAG_INFINITY = 3;
    public final static int FPU_SPECIAL_TAG_DENORMAL = 4;
    public final static int FPU_SPECIAL_TAG_SNAN = 5;
    public final static double UNDERFLOW_THRESHOLD = Math.pow(2.0, -1022.0);

    private final Processor cpu;

    double[] data;
    int[] tag;
    int[] specialTag;

    // status word

    private int statusWord;

    private boolean invalidOperation;
    private boolean denormalizedOperand;
    private boolean zeroDivide;
    private boolean overflow;
    private boolean underflow;
    private boolean precision;
    private boolean stackFault;

    // constructor

    /**
     * @param owner
     */
    public FpuState64(Processor owner) {
        cpu = owner;

        data = new double[STACK_DEPTH];
        tag = new int[STACK_DEPTH];
        specialTag = new int[STACK_DEPTH];
        init();
    }

    /**
     * @param output
     * @throws IOException
     */
    public void dumpState(DataOutput output) throws IOException {
        output.writeInt(statusWord);
        output.writeInt(maskWord);
        output.writeInt(precisionControl);
        output.writeInt(roundingControl);
        output.writeBoolean(invalidOperation);
        output.writeBoolean(denormalizedOperand);
        output.writeBoolean(zeroDivide);
        output.writeBoolean(overflow);
        output.writeBoolean(underflow);
        output.writeBoolean(precision);
        output.writeBoolean(stackFault);
        output.writeInt(data.length);
        for (int i = 0; i < data.length; i++)
            output.writeDouble(data[i]);
        output.writeInt(tag.length);
        for (int i = 0; i < tag.length; i++)
            output.writeInt(tag[i]);
        output.writeInt(specialTag.length);
        for (int i = 0; i < specialTag.length; i++)
            output.writeInt(specialTag[i]);
    }

    /**
     * @param input
     * @throws IOException
     */
    public void loadState(DataInput input) throws IOException {
        statusWord = input.readInt();
        maskWord = input.readInt();
        precisionControl = input.readInt();
        roundingControl = input.readInt();
        invalidOperation = input.readBoolean();
        denormalizedOperand = input.readBoolean();
        zeroDivide = input.readBoolean();
        overflow = input.readBoolean();
        underflow = input.readBoolean();
        precision = input.readBoolean();
        stackFault = input.readBoolean();
        int len = input.readInt();
        data = new double[len];
        for (int i = 0; i < data.length; i++)
            data[i] = input.readDouble();
        len = input.readInt();
        tag = new int[len];
        for (int i = 0; i < tag.length; i++)
            tag[i] = input.readInt();
        len = input.readInt();
        specialTag = new int[len];
        for (int i = 0; i < specialTag.length; i++)
            specialTag[i] = input.readInt();
    }

    /**
     * @return -
     */
    public boolean getInvalidOperation() {
        return ((statusWord & 0x01) != 0);
    }

    /**
     * @return -
     */
    public boolean getDenormalizedOperand() {
        return ((statusWord & 0x02) != 0);
    }

    /**
     * @return -
     */
    public boolean getZeroDivide() {
        return ((statusWord & 0x04) != 0);
    }

    /**
     * @return -
     */
    public boolean getOverflow() {
        return ((statusWord & 0x08) != 0);
    }

    /**
     * @return -
     */
    public boolean getUnderflow() {
        return ((statusWord & 0x10) != 0);
    }

    /**
     * @return -
     */
    public boolean getPrecision() {
        return ((statusWord & 0x20) != 0);
    }

    /**
     * @return -
     */
    public boolean getStackFault() {
        return ((statusWord & 0x40) != 0);
    }

    public void setInvalidOperation() {
        statusWord |= 0x01;
    }

    public void setDenormalizedOperand() {
        statusWord |= 0x02;
    }

    public void setZeroDivide() {
        statusWord |= 0x04;
    }

    public void setOverflow() {
        statusWord |= 0x08;
    }

    public void setUnderflow() {
        statusWord |= 0x10;
    }

    public void setPrecision() {
        statusWord |= 0x20;
    }

    public void setStackFault() {
        statusWord |= 0x40;
    }

    /**
     * @return -
     */
    public boolean getBusy() {
        return getErrorSummaryStatus();
    }

    /**
     * @return -
     */
    public boolean getErrorSummaryStatus() {
        // (note stack fault is a subset of invalid operation)
        return (((statusWord & 0x3f) & ~maskWord) != 0);
    }

    /**
     * @throws ProcessorException
     */
    public void checkExceptions() throws ProcessorException {
        if (getErrorSummaryStatus())
            cpu.reportFPUException();
    }

    public void clearExceptions() {
        statusWord = 0;
    }

    // control word

    private int maskWord;
    private int precisionControl;
    private int roundingControl;

    /**
     * @return -
     */
    public boolean getInvalidOperationMask() {
        return ((maskWord & 1) != 0);
    }

    /**
     * @return -
     */
    public boolean getDenormalizedOperandMask() {
        return ((maskWord & 2) != 0);
    }

    /**
     * @return -
     */
    public boolean getZeroDivideMask() {
        return ((maskWord & 4) != 0);
    }

    /**
     * @return -
     */
    public boolean getOverflowMask() {
        return ((maskWord & 8) != 0);
    }

    /**
     * @return -
     */
    public boolean getUnderflowMask() {
        return ((maskWord & 0x10) != 0);
    }

    /**
     * @return -
     */
    public boolean getPrecisionMask() {
        return ((maskWord & 0x20) != 0);
    }

    /**
     * @return -
     */
    public int getPrecisionControl() {
        return precisionControl;
    }

    /**
     * @return -
     */
    public int getRoundingControl() {
        return roundingControl;
    }

    /**
     * @param value
     */
    public void setInvalidOperationMask(boolean value) {
        if (value)
            maskWord |= 1;
        else
            maskWord &= ~1;
    }

    /**
     * @param value
     */
    public void setDenormalizedOperandMask(boolean value) {
        if (value)
            maskWord |= 2;
        else
            maskWord &= ~2;
    }

    /**
     * @param value
     */
    public void setZeroDivideMask(boolean value) {
        if (value)
            maskWord |= 4;
        else
            maskWord &= ~4;
    }

    /**
     * @param value
     */
    public void setOverflowMask(boolean value) {
        if (value)
            maskWord |= 8;
        else
            maskWord &= ~8;
    }

    /**
     * @param value
     */
    public void setUnderflowMask(boolean value) {
        if (value)
            maskWord |= 0x10;
        else
            maskWord &= ~0x10;
    }

    /**
     * @param value
     */
    public void setPrecisionMask(boolean value) {
        if (value)
            maskWord |= 0x20;
        else
            maskWord &= ~0x20;
    }

    /**
     * @param value
     */
    public void setAllMasks(boolean value) {
        if (value)
            maskWord |= 0x3f;
        else
            maskWord = 0;
    }

    /**
     * @param value
     */
    public void setPrecisionControl(int value) {
        if (value != FPU_PRECISION_CONTROL_DOUBLE) {
            // trying to set precision to other than double
            System.err.println("WARNING:  attempt to set non-double FP "
                    + "precision in Fpu64 mode");
        }
        precisionControl = FPU_PRECISION_CONTROL_DOUBLE;
    }

    /**
     * @param value
     */
    public void setRoundingControl(int value) {
        if (value != FPU_ROUNDING_CONTROL_EVEN) {
            // trying to set directed or truncate rounding
            System.err.println("WARNING:  attempt to set non-nearest rounding "
                    + "in Fpu64 mode");
        }
        roundingControl = FPU_ROUNDING_CONTROL_EVEN;
    }

    public void init() {
        // tag word (and non-x87 special tags)
        for (int i = 0; i < STACK_DEPTH; ++i)
            tag[i] = FPU_TAG_EMPTY;
        for (int i = 0; i < STACK_DEPTH; ++i)
            specialTag[i] = FPU_SPECIAL_TAG_NONE;
        // status word
        clearExceptions();
        conditionCode = 0;
        top = 0;
        // control word
        setAllMasks(true);
        infinityControl = false;
        setPrecisionControl(FPU_PRECISION_CONTROL_DOUBLE); // 64 bits default
        // (x87 uses 80-bit precision as default!)
        setRoundingControl(FPU_ROUNDING_CONTROL_EVEN); // default
        lastIP = lastData = lastOpcode = 0;
    }

    /**
     * @param x
     * @return -
     */
    public int tagCode(double x) {
        if (x == 0.0)
            return FPU_TAG_ZERO;
        else if (Double.isNaN(x) || Double.isInfinite(x))
            return FPU_TAG_SPECIAL;
        else
            return FPU_TAG_VALID;
    }

    /**
     * @param x
     * @return -
     */
    public static boolean isDenormal(double x) {
        long n = Double.doubleToRawLongBits(x);
        int exponent = (int) ((n >> 52) & 0x7ff);
        if (exponent != 0)
            return false;
        long fraction = (n & ~(0xfffL << 52));
        if (fraction == 0L)
            return false;
        return true;
    }

    /**
     * @param n
     * @return -
     */
    public static boolean isSNaN(long n) {
        // have to determine this based on 64-bit bit pattern,
        // since reassignment might cause Java to rationalize it to infinity
        int exponent = (int) ((n >> 52) & 0x7ff);
        if (exponent != 0x7ff)
            return false;
        long fraction = (n & ~(0xfffL << 52));
        if ((fraction & (1L << 51)) != 0)
            return false;
        return (fraction != 0L);
    }

    // SNaN's aren't generated internally by x87. Instead, they are
    // detected when they are read in from memory. So if you push()
    // from memory, find out before whether it's an SNaN, then push(),
    // then set the tag word accordingly.

    /**
     * @param x
     * @return -
     */
    public static int specialTagCode(double x) {
        // decode special: NaN, unsupported, infinity, or denormal
        if (Double.isNaN(x))
            return FPU_SPECIAL_TAG_NAN; // QNaN by default
        else if (Double.isInfinite(x))
            return FPU_SPECIAL_TAG_INFINITY;
            // else if (Math.abs(x) < UNDERFLOW_THRESHOLD)
        else if (isDenormal(x))
            return FPU_SPECIAL_TAG_DENORMAL;
        else
            return FPU_SPECIAL_TAG_NONE;
    }

    /**
     * @param x
     * @throws ProcessorException
     */
    public void push(double x) throws ProcessorException {
        if (--top < 0)
            top = STACK_DEPTH - 1;
        if (tag[top] != FPU_TAG_EMPTY) {
            setInvalidOperation();
            setStackFault();
            conditionCode |= 2; // C1 set to indicate stack overflow
            checkExceptions();
            // if IE is masked, then we just continue and overwrite
        }
        data[top] = x;
        tag[top] = tagCode(x);
        specialTag[top] = specialTagCode(x);
    }

    // public void pushBig(BigDecimal x) throws ProcessorException
    // {
    // push(x.doubleValue());
    // }

    /**
     * @return -
     * @throws ProcessorException
     */
    public double pop() throws ProcessorException {
        if (tag[top] == FPU_TAG_EMPTY) {
            setInvalidOperation();
            setStackFault();
            conditionCode &= ~2; // C1 cleared to indicate stack underflow
            checkExceptions();
            // TODO: if IE masked, do we just return whatever
            // random contents there are? That's what it seems
            // from the reference.
        } else if (specialTag[top] == FPU_SPECIAL_TAG_SNAN) {
            setInvalidOperation();
            checkExceptions();
            return Double.NaN; // QNaN if masked
        }
        double x = data[top];
        tag[top] = FPU_TAG_EMPTY;
        if (++top >= STACK_DEPTH)
            top = 0;
        return x;
    }

    // public BigDecimal popBig() throws ProcessorException
    // {
    // return new BigDecimal(pop());
    // }

    /**
     * @param index
     * @return -
     * @throws ProcessorException
     */
    public double ST(int index) throws ProcessorException {
        int i = ((top + index) & 0x7);
        if (tag[i] == FPU_TAG_EMPTY) {
            // an attempt to read an empty register is technically
            // a "stack underflow"
            setInvalidOperation();
            setStackFault();
            conditionCode &= ~2; // C1 cleared to indicate stack underflow
            checkExceptions();
        } else if (specialTag[i] == FPU_SPECIAL_TAG_SNAN) {
            setInvalidOperation();
            checkExceptions();
            return Double.NaN; // QNaN if masked
        }
        return data[i];
    }

    // public BigDecimal bigST(int index) throws ProcessorException
    // {
    // return new BigDecimal(ST(index));
    // }

    /**
     * @param index
     * @return -
     */
    public int getTag(int index) {
        int i = ((top + index) & 0x7);
        return tag[i];
    }

    /**
     * @param index
     * @return -
     */
    public int getSpecialTag(int index) {
        int i = ((top + index) & 0x7);
        return specialTag[i];
    }

    /**
     * @param index
     * @param value
     */
    public void setST(int index, double value) {
        // FST says that no exception is generated if the destination
        // is a non-empty register, so we don't generate an exception
        // here. TODO: check to see if this is a general rule.
        int i = ((top + index) & 0x7);
        data[i] = value;
        tag[i] = tagCode(value);
        specialTag[i] = specialTagCode(value);
    }

    // public void setBigST(int index, BigDecimal value)
    // {
    // setST(index, value.doubleValue());
    // }

    /**
     * @return -
     */
    public int getStatus() {
        int w = statusWord;
        if (getErrorSummaryStatus())
            w |= 0x80;
        if (getBusy())
            w |= 0x8000;
        w |= (top << 11);
        w |= ((conditionCode & 0x7) << 8);
        w |= ((conditionCode & 0x8) << 11);
        return w;
    }

    /**
     * @param w
     */
    public void setStatus(int w) {
        statusWord &= ~0x7f;
        statusWord |= (w & 0x7f);
        top = ((w >> 11) & 0x7);
        conditionCode = ((w >> 8) & 0x7);
        conditionCode |= ((w >>> 14) & 1);
    }

    /**
     * @return -
     */
    public int getControl() {
        int w = maskWord;
        w |= ((precisionControl & 0x3) << 8);
        w |= ((roundingControl & 0x3) << 10);
        if (infinityControl)
            w |= 0x1000;
        return w;
    }

    /**
     * @param w
     */
    public void setControl(int w) {
        maskWord &= ~0x3f;
        maskWord |= (w & 0x3f);
        infinityControl = ((w & 0x1000) != 0);
        setPrecisionControl((w >> 8) & 3);
        setRoundingControl((w >> 10) & 3);
    }

    /**
     * @return -
     */
    public int getTagWord() {
        int w = 0;
        for (int i = STACK_DEPTH - 1; i >= 0; --i)
            w = ((w << 2) | (tag[i] & 0x3));
        return w;
    }

    /**
     * @param w
     */
    public void setTagWord(int w) {
        for (int i = 0; i < STACK_DEPTH; ++i) {
            int t = (w & 0x3);
            if (t == FPU_TAG_EMPTY) {
                tag[i] = FPU_TAG_EMPTY;
            } else {
                tag[i] = tagCode(data[i]);
                if (specialTag[i] != FPU_SPECIAL_TAG_SNAN)
                    specialTag[i] = specialTagCode(data[i]);
                // SNaN is sticky, and Java doesn't preserve the bit pattern.
            }
            w >>= 2;
        }
    }

    // STRICTLY SPEAKING, the x87 should preserve the SNaN and QNaN
    // bit pattern; v1 sec 4.8.3.6 of the manual, in fact, says that
    // these bits can be used to store diagnostic information.
    // But Java will probably eliminate all these bits to get a code
    // it understands (which looks like an infinity). For now we
    // simply don't support using NaN bits in this way.

    /**
     * @param x
     * @param isSignalNaN
     * @return -
     */
    public static byte[] doubleToExtended(double x, boolean isSignalNaN) {
        byte[] b = new byte[10];
        long fraction = 0;
        int iexp = 0;
        // other special forms?
        if (isSignalNaN) {
            fraction = 0xc000000000000000L; // is this right?
        } else {
            long n = Double.doubleToRawLongBits(x);
            fraction = (n & ~(0xfff << 52));
            iexp = ((int) (n >> 52) & 0x7ff);
            boolean sgn = ((n & (1 << 63)) != 0);
            // insert implicit 1
            fraction |= (1 << 52);
            fraction <<= 11;
            // re-bias exponent
            iexp += (16383 - 1023);
            if (sgn)
                iexp |= 0x8000;
        }
        for (int i = 0; i < 8; ++i) {
            b[i] = (byte) fraction;
            fraction >>>= 8;
        }
        b[8] = (byte) iexp;
        b[9] = (byte) (iexp >>> 8);
        return b;
    }

    /**
     * @param b
     * @return -
     */
    public static int specialTagCode(byte[] b) {
        long fraction = 0;
        for (int i = 7; i >= 0; --i) {
            long w = ((long) b[i] & 0xff);
            fraction |= w;
            fraction <<= 8;
        }
        int iexp = (((int) b[8] & 0xff) | (((int) b[9] & 0x7f) << 8));
        @SuppressWarnings("unused")
        boolean sgn = ((b[9] & 0x80) != 0);
        boolean integ = ((b[7] & 0x80) != 0); // explicit integer bit

        if (iexp == 0) {
            if (integ) {
                // "pseudo-denormals" - treated like a normal denormal
                return FPU_SPECIAL_TAG_DENORMAL;
            } else {
                // normal denormals
                return FPU_SPECIAL_TAG_DENORMAL;
            }
        } else if (iexp == 0x7fff) {
            if (fraction == 0L) {
                // "pseudo-infinity"
                return FPU_SPECIAL_TAG_UNSUPPORTED;
            } else if (integ) {
                if ((fraction << 1) == 0) {
                    // infinity
                    return FPU_SPECIAL_TAG_INFINITY;
                } else {
                    // NaN's
                    if ((fraction >>> 62) == 0)
                        return FPU_SPECIAL_TAG_SNAN;
                    else
                        return FPU_SPECIAL_TAG_NAN;
                }
            } else {
                // pseudo-NaN
                return FPU_SPECIAL_TAG_UNSUPPORTED;
            }
        } else {
            if (integ) {
                // normal float
                return FPU_SPECIAL_TAG_NONE;
            } else {
                // "unnormal"
                return FPU_SPECIAL_TAG_UNSUPPORTED;
            }
        }
    }

    /**
     * @param b
     * @return -
     */
    public static double extendedToDouble(byte[] b) {
        long fraction = 0;
        for (int i = 7; i >= 0; --i) {
            long w = ((long) b[i] & 0xff);
            fraction |= w;
            fraction <<= 8;
        }
        int iexp = (((int) b[8] & 0xff) | (((int) b[9] & 0x7f) << 8));
        boolean sgn = ((b[9] & 0x80) != 0);
        boolean integ = ((b[7] & 0x80) != 0); // explicit integer bit

        if (iexp == 0) {
            if (integ) {
                // "pseudo-denormals" - treat exponent as value 1 and
                // mantissa as the same
                // (http://www.ragestorm.net/downloads/387intel.txt)
                iexp = 1;
            }
            // now treat as a normal denormal (from denormal).
            // actually, given that min unbiased exponent is -16383 for
            // extended, and only -1023 for double, a denormalized
            // extended is pretty much zero in double!
            return 0.0;
        } else if (iexp == 0x7fff) {
            if (fraction == 0L) {
                // "pseudo-infinity": if #IA masked, return QNaN
                // more technically, sign bit should be set to indicate
                // "QNaN floating-point indefinite"
                return Double.NaN;
            } else if (integ) {
                if ((fraction << 1) == 0) {
                    return (sgn) ? Double.NEGATIVE_INFINITY
                            : Double.POSITIVE_INFINITY;
                } else {
                    // a conventional NaN
                    return Double.NaN;
                }
            } else {
                // pseudo-NaN
                return Double.NaN;
            }
        } else {
            if (integ) {
                // normal float: decode
                iexp += 1023 - 16383; // rebias for double format
                fraction >>>= 11; // truncate rounding (is this the right way?)
                if (iexp > 0x7ff) {
                    // too big an exponent
                    return (sgn) ? Double.NEGATIVE_INFINITY
                            : Double.POSITIVE_INFINITY;
                } else if (iexp < 0) {
                    // denormal (from normal)
                    fraction >>>= (-iexp);
                    iexp = 0;
                }
                fraction &= ~(0xfffL << 52); // this cuts off explicit 1
                fraction |= (((long) iexp & 0x7ff) << 52);
                if (sgn)
                    fraction |= (1 << 63);
                return Double.longBitsToDouble(fraction);
            } else {
                // "unnormal": if #IA masked, return QNaN FP indefinite
                return Double.NaN;
            }
        }
    }

}
