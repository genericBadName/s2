package com.genericbadname.s2lib.bakery;

import com.genericbadname.s2lib.S2Lib;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

class HazardLevelTest {
    private static final HazardLevel[] testHazards = new HazardLevel[16];
    private static byte[] testBytes = new byte[8];
    private static final Random random = new Random();

    @BeforeAll
    static void beforeAll() {
        for (int i=0;i<testHazards.length;i++) {
            testHazards[i] = HazardLevel.values()[random.nextInt(HazardLevel.values().length)];
        }// TODO: FIX TESTS GRAHHHH

        S2Lib.LOGGER.info("Hazard Array: {}", Arrays.toString(testHazards));
    }

    @Test
    void testHazardDeserialization() {
        byte[] actualBytes = HazardLevel.toBytes16(testHazards);

        S2Lib.LOGGER.info("Binary Input: {}", toBinaryBytes(actualBytes));
        S2Lib.LOGGER.info("Bitmask: {}", StringUtils.leftPad(Long.toBinaryString(HazardLevel.bufMask), Long.numberOfLeadingZeros(HazardLevel.bufMask), '0'));

        HazardLevel[] outputArray = HazardLevel.fromBytes16(actualBytes);
        S2Lib.LOGGER.info("Output Array: {}", Arrays.toString(outputArray));

        Assertions.assertArrayEquals(testHazards, outputArray);
    }

    String toBinaryBytes(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);

        for(int i = 0; i < Byte.SIZE * bytes.length; i++) {
            sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
        }

        return sb.toString();
    }
}