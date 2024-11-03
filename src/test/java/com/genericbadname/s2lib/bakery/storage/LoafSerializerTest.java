package com.genericbadname.s2lib.bakery.storage;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class LoafSerializerTest {
    @BeforeAll
    static void beforeAll() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void write() {
    }

    @Test
    void read() {
    }
}