//-----------------------------------------------------------------------------
// $Id$
// $Source$
//-----------------------------------------------------------------------------

package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import go.*;
import utils.GuiUtils;

//-----------------------------------------------------------------------------

public class ScoreDialog
    extends JDialog
{
    public ScoreDialog(Frame owner, ActionListener listener)
    {
        super(owner, "Score - GoGui");
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        JPanel panelDetails = new JPanel(new GridLayout(0, 2, 0, 0));
        m_territoryBlack = createEntry(panelDetails, "Territory Black");
        m_territoryWhite = createEntry(panelDetails, "Territory White");
        m_areaBlack = createEntry(panelDetails, "Area Black");
        m_areaWhite = createEntry(panelDetails, "Area White");
        m_capturedBlack = createEntry(panelDetails, "Captured Black");
        m_capturedWhite = createEntry(panelDetails, "Captured White");
        m_komi = createEntry(panelDetails, "Komi");
        m_resultChinese = createEntry(panelDetails, "Result Chinese");
        m_resultJapanese = createEntry(panelDetails, "Result Japanese");
        m_rules = createEntry(panelDetails, "Rules");
        JPanel panelResult = new JPanel(new GridLayout(0, 2, 0, 0));
        m_result = createEntry(panelResult, "Game result");
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(panelDetails);
        panel.add(GuiUtils.createFiller());
        panel.add(new JSeparator(SwingConstants.HORIZONTAL));
        panel.add(GuiUtils.createFiller());
        panel.add(panelResult);
        JButton okButton = new JButton("Ok");
        okButton.setActionCommand("score-done");
        okButton.addActionListener(listener);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("score-cancel");
        cancelButton.addActionListener(listener);
        Object options[] = { okButton, cancelButton };
        JOptionPane optionPane = new JOptionPane(panel,
                                                 JOptionPane.PLAIN_MESSAGE,
                                                 JOptionPane.OK_CANCEL_OPTION,
                                                 null, options, options[0]);
        setContentPane(optionPane);
        pack();
    }

    public void showScore(Score score)
    {
        m_territoryBlack.setText(Integer.toString(score.m_territoryBlack));
        m_territoryWhite.setText(Integer.toString(score.m_territoryWhite));
        m_areaBlack.setText(Integer.toString(score.m_areaBlack));
        m_areaWhite.setText(Integer.toString(score.m_areaWhite));
        m_capturedBlack.setText(Integer.toString(score.m_capturedBlack));
        m_capturedWhite.setText(Integer.toString(score.m_capturedWhite));
        m_komi.setText(Float.toString(score.m_komi));
        m_resultChinese.setText(Score.formatResult(score.m_resultChinese));
        m_resultJapanese.setText(Score.formatResult(score.m_resultJapanese));
        m_rules.setText(score.m_rules == go.Board.RULES_JAPANESE ? "Japanese"
                        : "Chinese");
        m_result.setText(score.formatResult());
    }

    private JLabel m_territoryBlack;

    private JLabel m_territoryWhite;

    private JLabel m_areaBlack;

    private JLabel m_areaWhite;

    private JLabel m_capturedBlack;

    private JLabel m_capturedWhite;

    private JLabel m_komi;

    private JLabel m_resultChinese;

    private JLabel m_resultJapanese;

    private JLabel m_rules;

    private JLabel m_result;

    private JLabel createEntry(JPanel panel, String text)
    {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(label);
        JLabel entry = new JLabel("        ");
        entry.setHorizontalAlignment(SwingConstants.RIGHT);
        entry.setBorder(BorderFactory.createLoweredBevelBorder());
        panel.add(entry);
        return entry;
    }
}

//-----------------------------------------------------------------------------
