//----------------------------------------------------------------------------
// $Id$
// $Source$
//----------------------------------------------------------------------------

package net.sf.gogui.go;

import java.util.ArrayList;

//----------------------------------------------------------------------------

public class MoveTest
    extends junit.framework.TestCase
{
    public static void main(String args[])
    {
        junit.textui.TestRunner.run(suite());
    }

    public static junit.framework.Test suite()
    {
        return new junit.framework.TestSuite(MoveTest.class);
    }

    public void testToString()
    {
        assertEquals("black A1", Move.create(0, 0, GoColor.BLACK).toString());
        assertEquals("white PASS",
                     Move.create(null, GoColor.WHITE).toString());
    }
}

//----------------------------------------------------------------------------