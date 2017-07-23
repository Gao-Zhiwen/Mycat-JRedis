package io.mycat.jredis.datastruct;

import io.mycat.jredis.datastruct.obj.*;
import io.mycat.jredis.datastruct.util.MemoryManager;
import org.junit.Before;
import org.junit.Test;

/**
 * Desc:
 *
 * @date: 23/07/2017
 * @author: gaozhiwen
 */
public class BaseStructTest {
    @Before public void init() {
        MemoryManager.allocateMemory(1024 * 1024);
    }

    @Test public void byteTest() {
        UnsafeByte unsafeByte = new UnsafeByte();
        long address = MemoryManager.malloc(unsafeByte.sizeOf());
        unsafeByte.setAddress(address);
        unsafeByte.setValue((byte) 1);
        System.out.println(unsafeByte.getValue());
    }

    @Test public void shortTest() {
        UnsafeShort unsafeShort = new UnsafeShort();
        long address = MemoryManager.malloc(unsafeShort.sizeOf());
        unsafeShort.setAddress(address);
        unsafeShort.setValue((short) 1);
        System.out.println(unsafeShort.getValue());
    }

    @Test public void intTest() {
        UnsafeInteger unsafeInteger = new UnsafeInteger();
        long address = MemoryManager.malloc(unsafeInteger.sizeOf());
        unsafeInteger.setAddress(address);
        unsafeInteger.setValue(1);
        System.out.println(unsafeInteger.getValue());
    }

    @Test public void longTest() {
        UnsafeLong unsafeLong = new UnsafeLong();
        long address = MemoryManager.malloc(unsafeLong.sizeOf());
        unsafeLong.setAddress(address);
        unsafeLong.setValue(1L);
        System.out.println(unsafeLong.getValue());
    }

    @Test public void floatTest() {
        UnsafeFloat unsafeFloat = new UnsafeFloat();
        long address = MemoryManager.malloc(unsafeFloat.sizeOf());
        unsafeFloat.setAddress(address);
        unsafeFloat.setValue(1.0f);
        System.out.println(unsafeFloat.getValue());
    }

    @Test public void doubleTest() {
        UnsafeDouble unsafeDouble = new UnsafeDouble();
        long address = MemoryManager.malloc(unsafeDouble.sizeOf());
        unsafeDouble.setAddress(address);
        unsafeDouble.setValue(1.0);
        System.out.println(unsafeDouble.getValue());
    }

    @Test public void charTest() {
        UnsafeChar unsafeChar = new UnsafeChar();
        long address = MemoryManager.malloc(unsafeChar.sizeOf());
        unsafeChar.setAddress(address);
        unsafeChar.setValue('a');
        System.out.println(unsafeChar.getValue());
    }

    @Test public void boolTest() {
        UnsafeBoolean unsafeBoolean = new UnsafeBoolean();
        long address = MemoryManager.malloc(unsafeBoolean.sizeOf());
        unsafeBoolean.setAddress(address);
        unsafeBoolean.setValue(true);
        System.out.println(unsafeBoolean.getValue());
    }
}
