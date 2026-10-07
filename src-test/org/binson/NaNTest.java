package org.binson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.binson.lowlevel.BinsonOutput;
import org.binson.lowlevel.Bytes;
import org.binson.lowlevel.Hex;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

/**
 * Tests that NaN is written in canonical form, 0x7ff8000000000000,
 * as recommended by BINSON-SPEC-1.1 (recommendation 6), and that
 * the reader keeps the bits of a double as they are stored.
 *
 * @author Frans Lundberg
 */
public class NaNTest {
    private static final long CANONICAL_NAN = 0x7ff8000000000000L;

    /** NaN bit patterns other than the canonical one. */
    private static final long[] OTHER_NANS = {
        0x7ff8000000000001L,   // Go, math.NaN()
        0xfff8000000000000L,   // x86, 0.0/0.0
        0x7ff0000000000001L,   // signaling NaN
        0x7fffffffffffffffL,
        0xffffffffffffffffL,   // corrupted by binson-c
        0xfff0000000000001L
    };

    /** Doubles that are not NaN; their bits must be written unchanged. */
    private static final long[] NON_NANS = {
        0x7ff0000000000000L,   // +inf
        0xfff0000000000000L,   // -inf
        0x0000000000000000L,   // +0.0
        0x8000000000000000L,   // -0.0
        0x0000000000000001L,   // smallest subnormal
        0x7fefffffffffffffL,   // Double.MAX_VALUE
        0x3ff0000000000000L    // 1.0
    };

    /** Offset of the 8 double bytes in the bytes of new Binson().put("d", value). */
    private static final int DOUBLE_OFFSET = 5;

    // ======== Writer ========

    @Test
    public void testDoubleNaNBytes() {
        byte[] bytes = new Binson().put("nan", Double.NaN).toBytes();
        Assert.assertEquals("4014036e616e46000000000000f87f41", Hex.create(bytes));
    }

    @Test
    public void testOtherNaNsAreWrittenCanonical() {
        for (long bits : OTHER_NANS) {
            byte[] bytes = new Binson().put("d", nan(bits)).toBytes();
            assertDoubleBits(CANONICAL_NAN, bytes, DOUBLE_OFFSET);
        }
    }

    @Test
    public void testComputedNaNsAreWrittenCanonical() {
        double zero = zero();
        double inf = Double.POSITIVE_INFINITY;
        double[] values = {zero / zero, Math.sqrt(-1 + zero), inf - inf, inf * zero};

        for (double value : values) {
            Assert.assertTrue(Double.isNaN(value));
            byte[] bytes = new Binson().put("d", value).toBytes();
            assertDoubleBits(CANONICAL_NAN, bytes, DOUBLE_OFFSET);
        }
    }

    @Test
    public void testNaNsInArrayAreWrittenCanonical() {
        BinsonArray array = new BinsonArray();
        BinsonArray expected = new BinsonArray();
        for (long bits : OTHER_NANS) {
            array.add(nan(bits));
            expected.add(Double.NaN);
        }

        Assert.assertEquals(
                Hex.create(new Binson().put("a", expected).toBytes()),
                Hex.create(new Binson().put("a", array).toBytes()));
    }

    @Test
    public void testNaNsInNestedStructuresAreWrittenCanonical() {
        for (long bits : OTHER_NANS) {
            Binson actual = nested(nan(bits));
            Binson expected = nested(Double.NaN);
            Assert.assertEquals(Hex.create(expected.toBytes()), Hex.create(actual.toBytes()));
        }
    }

    @Test
    public void testBinsonOutputWritesCanonical() throws IOException {
        for (long bits : OTHER_NANS) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            new BinsonOutput(out).writeDouble(nan(bits));
            Assert.assertEquals("46000000000000f87f", Hex.create(out.toByteArray()));
        }
    }

    @Test
    public void testNonNaNsAreWrittenUnchanged() {
        for (long bits : NON_NANS) {
            double value = Double.longBitsToDouble(bits);
            byte[] bytes = new Binson().put("d", value).toBytes();
            assertDoubleBits(bits, bytes, DOUBLE_OFFSET);
        }
    }

    // ======== Reader ========

    @Test
    public void testReaderAcceptsOtherNaNs() {
        for (long bits : OTHER_NANS) {
            Binson obj = Binson.fromBytes(binsonWithDouble(bits));
            Assert.assertTrue(Double.isNaN(obj.getDouble("d")));
        }
    }

    @Test
    public void testReaderKeepsBits() {
        for (long bits : OTHER_NANS) {
            nan(bits);
            Binson obj = Binson.fromBytes(binsonWithDouble(bits));
            Assert.assertEquals(Long.toHexString(bits),
                    Long.toHexString(Double.doubleToRawLongBits(obj.getDouble("d"))));
        }
    }

    @Test
    public void testOtherNaNsAreCanonicalAfterRoundTrip() {
        byte[] canonical = binsonWithDouble(CANONICAL_NAN);
        for (long bits : OTHER_NANS) {
            byte[] bytes = Binson.fromBytes(binsonWithDouble(bits)).toBytes();
            Assert.assertEquals(Hex.create(canonical), Hex.create(bytes));
        }
    }

    @Test
    public void testCanonicalNaNRoundTripsUnchanged() {
        byte[] canonical = binsonWithDouble(CANONICAL_NAN);
        byte[] bytes = Binson.fromBytes(canonical).toBytes();
        Assert.assertEquals(Hex.create(canonical), Hex.create(bytes));
    }

    // ======== Equality ========

    @Test
    public void testObjectsWithDifferentNaNsAreEqual() {
        Binson canonical = new Binson().put("d", Double.NaN);
        for (long bits : OTHER_NANS) {
            Binson other = new Binson().put("d", nan(bits));
            Assert.assertEquals(canonical, other);
            Assert.assertEquals(canonical.hashCode(), other.hashCode());
        }
    }

    @Test
    public void testArraysWithDifferentNaNsAreEqual() {
        BinsonArray canonical = new BinsonArray().add(Double.NaN);
        for (long bits : OTHER_NANS) {
            BinsonArray other = new BinsonArray().add(nan(bits));
            Assert.assertEquals(canonical, other);
            Assert.assertEquals(canonical.hashCode(), other.hashCode());
        }
    }

    @Test
    public void testParsedOtherNaNEqualsDoubleNaN() {
        Binson canonical = new Binson().put("d", Double.NaN);
        for (long bits : OTHER_NANS) {
            Binson parsed = Binson.fromBytes(binsonWithDouble(bits));
            Assert.assertEquals(canonical, parsed);
        }
    }

    // ======== Bytes.doubleToBytesLE ========

    @Test
    public void testDoubleToBytesLEUsesOffset() {
        byte[] arr = new byte[12];
        Bytes.doubleToBytesLE(1.0, arr, 3);
        Assert.assertEquals("000000000000000000f03f00", Hex.create(arr));

        byte[] arr0 = new byte[8];
        Bytes.doubleToBytesLE(1.0, arr0, 0);
        Assert.assertEquals("000000000000f03f", Hex.create(arr0));
    }

    // ======== Helpers ========

    /**
     * Returns the double with the given bits. The test is skipped if
     * the JVM does not keep the bits (some platforms quiet signaling NaNs).
     */
    private static double nan(long bits) {
        double value = Double.longBitsToDouble(bits);
        Assume.assumeTrue(Double.doubleToRawLongBits(value) == bits);
        Assert.assertTrue(Double.isNaN(value));
        return value;
    }

    /** Returns 0.0, computed at runtime so the compiler does not fold NaN expressions. */
    private static double zero() {
        return Double.parseDouble("0");
    }

    /** Returns the bytes of {"d": double} with the given bits, without using the writer. */
    private static byte[] binsonWithDouble(long bits) {
        byte[] bytes = Hex.toBytes("4014016446000000000000000041");
        for (int i = 0; i < 8; i++) {
            bytes[DOUBLE_OFFSET + i] = (byte) (bits >>> (8 * i));
        }
        return bytes;
    }

    private static void assertDoubleBits(long expectedBits, byte[] bytes, int offset) {
        Assert.assertEquals(0x46, bytes[offset - 1]);
        Assert.assertEquals(Long.toHexString(expectedBits),
                Long.toHexString(Bytes.bytesToLongLE(bytes, offset)));
    }

    private static Binson nested(double value) {
        return new Binson()
                .put("a", new BinsonArray().add(value).add(new Binson().put("x", value)))
                .put("o", new Binson().put("x", value)
                        .put("y", new BinsonArray().add(new BinsonArray().add(value))));
    }
}
