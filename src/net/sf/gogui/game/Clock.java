//----------------------------------------------------------------------------
// $Id$
//----------------------------------------------------------------------------

package net.sf.gogui.game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import net.sf.gogui.game.TimeSettings;
import net.sf.gogui.go.GoColor;
import net.sf.gogui.util.StringUtil;

/** Time control for a Go game.
    If the clock is not initialized with Clock.setTimeSettings, the clock
    will count upwards, otherwise the time settings with main and/or
    byoyomi time are used. The time unit is milliseconds.
    TODO: Classes in net.sf.gogui.game should not depend on java.awt, maybe
    use a function update(currentTime) to update the clock, that would also
    make testing easier
*/
public final class Clock
    implements ConstClock
{
    public interface Listener
    {
        void clockChanged(ConstClock clock);
    }

    public Clock()
    {
        ActionListener listener = new ActionListener() {
                public void actionPerformed(ActionEvent event)
                {
                    updateListener();
                }
            };
        m_timer = new javax.swing.Timer(1000, listener);
        reset();
    }

    /** Get moves left.
        Requires: getUseByoyomi() and isInByoyomi(color)
    */
    public int getMovesLeft(GoColor color)
    {
        assert(getUseByoyomi() && isInByoyomi(color));
        return getRecord(color).m_movesLeft;
    }

    /** Get time left.
        Requires: isInitialized()
    */
    public long getTimeLeft(GoColor color)
    {
        assert(isInitialized());
        TimeRecord record = getRecord(color);
        long time = record.m_time;
        if (getUseByoyomi())
            return (getByoyomi() - time);
        else
            return (getPreByoyomi() - time);
    }

    public String getTimeString(GoColor color)
    {
        TimeRecord record = getRecord(color);
        long time = record.m_time;
        if (m_toMove.equals(color))
            time += System.currentTimeMillis() - m_startMoveTime;
        if (isInitialized())
        {
            if (record.m_isInByoyomi)
                time = getByoyomi() - time;
            else
                time = getPreByoyomi() - time;
        }
        int movesLeft = -1;
        if (isInitialized() && record.m_isInByoyomi)
        {
            movesLeft = record.m_movesLeft;
        }
        // Round time to seconds
        time = time / 1000L;
        return getTimeString((double)time, movesLeft);
    }

    /** If not in byoyomi movesLeft &lt; 0 */
    public static String getTimeString(double timeLeft, int movesLeft)
    {
        StringBuffer buffer = new StringBuffer(8);
        buffer.append(StringUtil.formatTime((long)timeLeft));
        if (movesLeft >= 0)
        {
            buffer.append('/');
            buffer.append(movesLeft);
        }
        return buffer.toString();
    }

    public void halt()
    {
        if (m_toMove == GoColor.EMPTY)
            return;
        TimeRecord record = getRecord(m_toMove);
        long time = System.currentTimeMillis() - m_startMoveTime;
        record.m_time += time;
        m_toMove = GoColor.EMPTY;
        updateListener();
        m_timer.stop();
    }

    public boolean isInitialized()
    {
        return (m_timeSettings != null);
    }

    public boolean isInByoyomi(GoColor color)
    {
        return getUseByoyomi() && getRecord(color).m_isInByoyomi;
    }

    public boolean isRunning()
    {
        return (m_toMove != GoColor.EMPTY);
    }

    public boolean lostOnTime(GoColor color)
    {
        if (! isInitialized())
            return false;
        TimeRecord record = getRecord(color);
        long time = record.m_time;
        if (getUseByoyomi())
            return record.m_byoyomiExceeded;
        else
            return (time > getPreByoyomi());
    }

    public void reset()
    {
        reset(GoColor.BLACK);
        reset(GoColor.WHITE);
        m_toMove = GoColor.EMPTY;
        updateListener();
    }

    public void reset(GoColor color)
    {
        TimeRecord timeRecord = getRecord(color);
        timeRecord.m_time = 0;
        timeRecord.m_movesLeft = 0;
        timeRecord.m_isInByoyomi = false;
        timeRecord.m_byoyomiExceeded = false;
        updateListener();
    }

    /** Register listener for clock changes.
        Only one listener supported at the moment.
    */
    public void setListener(Listener listener)
    {
        m_listener = listener;
    }

    public void setTimeSettings(TimeSettings settings)
    {
        m_timeSettings = settings;
    }

    /** Set time left.
        @param color Color to set the time for.
        @param time New value for time left.
        @param movesLeft -1, if not in byoyomi.
    */
    public void setTimeLeft(GoColor color, long time, int movesLeft)
    {
        halt();
        TimeRecord record = getRecord(color);
        record.m_isInByoyomi = (movesLeft >= 0);
        if (record.m_isInByoyomi)
        {
            record.m_time = getByoyomi() - time;
            record.m_movesLeft = movesLeft;
            record.m_byoyomiExceeded = time > 0;
        }
        else
        {
            record.m_time = getPreByoyomi() - time;
            record.m_movesLeft = -1;
            record.m_byoyomiExceeded = false;
        }
        if (m_toMove != GoColor.EMPTY)
            startMove(m_toMove);
        updateListener();
    }

    /** Start time for a move.
        If the clock was already running for the same color, the passed time
        for this color and the current move is discarded.
    */
    public void startMove(GoColor color)
    {
        if  (m_toMove != GoColor.EMPTY)
            stopMove();
        m_toMove = color;
        m_startMoveTime = System.currentTimeMillis();
        m_timer.start();
    }

    /** Stop time for a move.
        If the clock was running, the time for the move is added to the
        total time for the color the clock was running for; otherwise
        this function does nothing.
    */
    public void stopMove()
    {
        if (m_toMove == GoColor.EMPTY)
            return;
        TimeRecord record = getRecord(m_toMove);
        long time = System.currentTimeMillis() - m_startMoveTime;
        record.m_time += time;
        if (isInitialized() && getUseByoyomi())
        {
            if (! record.m_isInByoyomi
                && record.m_time > getPreByoyomi())
            {
                record.m_isInByoyomi = true;
                record.m_time -= getPreByoyomi();
                assert(getByoyomiMoves() > 0);
                record.m_movesLeft = getByoyomiMoves();
            }
            if (record.m_isInByoyomi)
            {
                if (record.m_time > getByoyomi())
                    record.m_byoyomiExceeded = true;
                assert(record.m_movesLeft > 0);
                --record.m_movesLeft;
                if (record.m_movesLeft == 0)
                {
                    record.m_time = 0;
                    assert(getByoyomiMoves() > 0);
                    record.m_movesLeft = getByoyomiMoves();
                }
            }
        }
        m_toMove = GoColor.EMPTY;
        updateListener();
        m_timer.stop();
    }    

    private static class TimeRecord
    {
        public boolean m_isInByoyomi;

        public boolean m_byoyomiExceeded;

        public int m_movesLeft;

        public long m_time;
    }

    private long m_startMoveTime;

    private GoColor m_toMove = GoColor.EMPTY;

    private final TimeRecord m_timeRecordBlack = new TimeRecord();

    private final TimeRecord m_timeRecordWhite = new TimeRecord();

    private TimeSettings m_timeSettings;

    private Listener m_listener;

    private final javax.swing.Timer m_timer;

    private TimeRecord getRecord(GoColor c)
    {
        if (c == GoColor.BLACK)
            return m_timeRecordBlack;
        else
            return m_timeRecordWhite;
    }

    private long getByoyomi()
    {
        return m_timeSettings.getByoyomi();
    }

    private int getByoyomiMoves()
    {
        return m_timeSettings.getByoyomiMoves();
    }

    private long getPreByoyomi()
    {
        return m_timeSettings.getPreByoyomi();
    }

    private boolean getUseByoyomi()
    {
        return m_timeSettings.getUseByoyomi();
    }

    private void updateListener()
    {
        if (m_listener != null)
            m_listener.clockChanged(this);
    }
}
