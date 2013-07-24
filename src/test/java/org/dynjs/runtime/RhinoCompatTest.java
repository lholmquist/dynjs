package org.dynjs.runtime;

import org.junit.Ignore;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Test Rhino compatibility
 */
public class RhinoCompatTest extends AbstractDynJSTestSupport {
    @Test
    public void testDefineGetter() {
        eval("f = function() {}");
        eval("x = {}");
        assertThat(eval("typeof Object.prototype.__defineGetter__")).isEqualTo("function");
        assertThat(eval("typeof x.__defineGetter__")).isEqualTo("function");
        assertThat(eval("typeof f.__defineGetter__")).isEqualTo("function");

        eval("x.__defineGetter__('foo', function() { return 'bar' });");
        assertThat(eval("x.foo")).isEqualTo("bar");
    }

    @Test
    @Ignore
    public void testDefineSetter() {
        assertThat(eval("typeof Object.prototype.__defineSetter__")).isEqualTo("function");
        eval(
                "var X = function() {" +
                        "this.bar = 'bar';" +
                        "this.__defineSetter__('foo', function(val) { bar = val; });" +
                        "this.__defineGetter__('foo', function() { return bar ); });" +
                        "};" +
                        "x = new X();"
        );
        assertThat(eval("typeof x.__defineGetter__")).isEqualTo("function");
        assertThat(eval("x.foo")).isEqualTo("bar");
        eval("x.foo = 'foobar'");
        assertThat(eval("x.foo")).isEqualTo("foobar");
    }
}
