package dev.mvmo.sicklang.test;

import dev.mvmo.sicklang.internal.object.bool.BooleanObject;
import dev.mvmo.sicklang.internal.object.number.IntegerObject;
import dev.mvmo.sicklang.internal.object.string.StringObject;
import org.junit.Test;
import static org.junit.Assert.*;

public class SickObjectTest {

    @Test
    public void test$stringHashKey() {
        var hello1 = new StringObject("Hello World");
        var hello2 = new StringObject("Hello World");

        var diff1 = new StringObject("My name is maurice");
        var diff2 = new StringObject("My name is maurice");

        assertEquals(hello1.hashCode(), hello2.hashCode());
        assertEquals(diff1.hashCode(), diff2.hashCode());
        assertNotEquals(diff1.hashCode(), hello1.hashCode());
    }

    @Test
    public void test$booleanHashKey() {
        var t = new BooleanObject(true);
        var t1 = new BooleanObject(true);

        var f = new BooleanObject(false);
        var f1 = new BooleanObject(false);

        assertEquals(t.hashCode(), t1.hashCode());
        assertEquals(f.hashCode(), f1.hashCode());
        assertNotEquals(t.hashCode(), f.hashCode());
    }

    @Test
    public void test$integerHashKey() {
        var i = new IntegerObject(1);
        var i1 = new IntegerObject(1);

        var j = new IntegerObject(-1);
        var j1 = new IntegerObject(-1);

        assertEquals(i.hashCode(), i1.hashCode());
        assertEquals(j.hashCode(), j1.hashCode());
        assertNotEquals(i.hashCode(), j.hashCode());
    }

}
