//----------------------------------------------------------------------------
// $Id$
//----------------------------------------------------------------------------

package net.sf.gogui.gogui;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import net.sf.gogui.game.ConstClock;
import net.sf.gogui.game.ConstGameTree;
import net.sf.gogui.game.ConstNode;
import net.sf.gogui.game.NodeUtil;
import net.sf.gogui.go.GoColor;
import net.sf.gogui.gui.Bookmark;
import net.sf.gogui.gui.GameTreePanel;
import net.sf.gogui.gui.RecentFileMenu;
import net.sf.gogui.util.Platform;

/** Menu bar for GoGui. */
public class GoGuiMenuBar
{
    public GoGuiMenuBar(ActionListener listener, GoGuiActions actions,
                        RecentFileMenu.Callback recentCallback,
                        RecentFileMenu.Callback recentGtpCallback)
    {
        m_listener = listener;
        m_menuBar = new JMenuBar();
        m_menuFile = createMenuFile(actions, recentCallback);
        m_menuBar.add(m_menuFile);
        m_menuBar.add(createMenuGame(actions));
        m_menuBar.add(createMenuEdit(actions));
        m_menuBar.add(createMenuGo(actions));
        m_menuShell = createMenuShell(recentGtpCallback);
        m_menuBar.add(m_menuShell);
        m_menuBookmarks = createMenuBookMarks();
        m_menuBar.add(m_menuBookmarks);
        m_menuSettings = createMenuSettings();
        m_menuBar.add(m_menuSettings);
        m_menuHelp = createMenuHelp(actions);
        m_menuBar.add(m_menuHelp);
        setHeaderStyleSingle(true);
    }

    public void addRecent(File file)
    {
        try
        {
            File canonicalFile = file.getCanonicalFile();
            if (canonicalFile.exists())
                file = canonicalFile;
        }
        catch (IOException e)
        {
        }
        m_recent.add(file);
        m_recent.updateEnabled();
    }

    public void addRecentGtp(File file)
    {
        try
        {
            File canonicalFile = file.getCanonicalFile();
            if (canonicalFile.exists())
                file = canonicalFile;
        }
        catch (IOException e)
        {
        }
        m_recentGtp.add(file);
        m_recentGtp.updateEnabled();
    }

    public void enableCleanup(boolean enable)
    {
        m_itemCleanup.setEnabled(enable);
    }

    public boolean getAnalyzeOnlySupported()
    {
        return m_itemAnalyzeOnlySupported.isSelected();
    }

    public boolean getAutoNumber()
    {
        return m_itemAutoNumber.isSelected();        
    }

    public boolean getTimeStamp()
    {
        return m_itemTimeStamp.isSelected();        
    }

    public boolean getCleanup()
    {
        return m_itemCleanup.isSelected();
    }

    public boolean getCommandCompletion()
    {
        return m_itemCommandCompletion.isSelected();
    }

    public boolean getCommentFontFixed()
    {
        return m_itemCommentFontFixed.isSelected();
    }

    public boolean getBeepAfterMove()
    {
        return m_itemBeepAfterMove.isSelected();
    }

    public int getGameTreeLabels()
    {
        if (m_itemGameTreeNumber.isSelected())
            return GameTreePanel.LABEL_NUMBER;
        if (m_itemGameTreeMove.isSelected())
            return GameTreePanel.LABEL_MOVE;
        return GameTreePanel.LABEL_NONE;
    }

    public int getGameTreeSize()
    {
        if (m_itemGameTreeLarge.isSelected())
            return GameTreePanel.SIZE_LARGE;
        if (m_itemGameTreeSmall.isSelected())
            return GameTreePanel.SIZE_SMALL;
        if (m_itemGameTreeTiny.isSelected())
            return GameTreePanel.SIZE_TINY;
        return GameTreePanel.SIZE_NORMAL;
    }

    public JMenuBar getMenuBar()
    {
        return m_menuBar;
    }

    public boolean getShowAnalyze()
    {
        return m_itemShowAnalyze.isSelected();
    }

    public boolean getShowCursor()
    {
        return m_itemShowCursor.isSelected();
    }

    public boolean getShowGrid()
    {
        return m_itemShowGrid.isSelected();
    }

    public boolean getShowInfoPanel()
    {
        return m_itemShowInfoPanel.isSelected();
    }

    public boolean getShowLastMove()
    {
        return m_itemShowLastMove.isSelected();
    }

    public boolean getShowShell()
    {
        return m_itemShowShell.isSelected();
    }

    public boolean getShowSubtreeSizes()
    {
        return m_itemShowSubtreeSizes.isSelected();
    }

    public boolean getShowToolbar()
    {
        return m_itemShowToolbar.isSelected();
    }

    public boolean getShowTree()
    {
        return m_itemShowTree.isSelected();
    }

    public boolean getShowVariations()
    {
        return m_itemShowVariations.isSelected();
    }

    public void setAnalyzeOnlySupported(boolean enable)
    {
        m_itemAnalyzeOnlySupported.setSelected(enable);        
    }

    public void setAutoNumber(boolean enable)
    {
        m_itemAutoNumber.setSelected(enable);        
    }

    public void setBookmarks(ArrayList bookmarks)
    {
        for (int i = 0; i < m_bookmarkItems.size(); ++i)
            m_menuBookmarks.remove((JMenuItem)m_bookmarkItems.get(i));
        if (m_bookmarksSeparator != null)
        {
            m_menuBookmarks.remove(m_bookmarksSeparator);
            m_bookmarksSeparator = null;
        }
        if (bookmarks.size() == 0)
            return;
        m_bookmarksSeparator = new JSeparator();
        m_menuBookmarks.add(m_bookmarksSeparator);
        for (int i = 0; i < bookmarks.size(); ++i)
        {
            Bookmark bookmark = (Bookmark)bookmarks.get(i);
            JMenuItem item = new JMenuItem(bookmark.m_name);
            m_menuBookmarks.addItem(item, "bookmark-" + i);
            m_bookmarkItems.add(item);
        }
    }

    public void selectBoardSizeItem(int size)
    {
        for (int i = 0; i < s_possibleBoardSizes.length; ++i)
            if (s_possibleBoardSizes[i] == size)
            {
                m_itemBoardSize[i].setSelected(true);
                return;
            }
        m_itemBoardSizeOther.setSelected(true);
    }

    public void setBeepAfterMove(boolean enable)
    {
        m_itemBeepAfterMove.setSelected(enable);
    }

    public void setCleanup(boolean enable)
    {
        m_itemCleanup.setSelected(enable);
    }

    public void setCommandCompletion(boolean enable)
    {
        m_itemCommandCompletion.setSelected(enable);
    }
    public void setCommentFontFixed(boolean enable)
    {
        m_itemCommentFontFixed.setSelected(enable);
    }

    public void setGameTreeLabels(int mode)
    {
        switch (mode)
        {
        case GameTreePanel.LABEL_NUMBER:
            m_itemGameTreeNumber.setSelected(true);
            break;
        case GameTreePanel.LABEL_MOVE:
            m_itemGameTreeMove.setSelected(true);
            break;
        case GameTreePanel.LABEL_NONE:
            m_itemGameTreeNone.setSelected(true);
            break;
        default:
            break;
        }
    }

    public void setGameTreeSize(int mode)
    {
        switch (mode)
        {
        case GameTreePanel.SIZE_LARGE:
            m_itemGameTreeLarge.setSelected(true);
            break;
        case GameTreePanel.SIZE_NORMAL:
            m_itemGameTreeNormal.setSelected(true);
            break;
        case GameTreePanel.SIZE_SMALL:
            m_itemGameTreeSmall.setSelected(true);
            break;
        case GameTreePanel.SIZE_TINY:
            m_itemGameTreeTiny.setSelected(true);
            break;
        default:
            break;
        }
    }

    /** Is it a single menu bar or does a tool bar exist? */
    public void setHeaderStyleSingle(boolean isSingle)
    {
        // For com.jgoodies.looks
        getMenuBar().putClientProperty("jgoodies.headerStyle",
                                       isSingle ? "Single" : "Both");
    }

    public void setTimeStamp(boolean enable)
    {
        m_itemTimeStamp.setSelected(enable);        
    }

    public void setNormalMode()
    {
        m_recent.updateEnabled();
        m_recentGtp.updateEnabled();
        m_itemSetup.setSelected(false);
        m_itemSetupBlack.setEnabled(false);
        m_itemSetupWhite.setEnabled(false);
    }

    public void setShowAnalyze(boolean enable)
    {
        m_itemShowAnalyze.setSelected(enable);
    }

    public void setShowCursor(boolean enable)
    {
        m_itemShowCursor.setSelected(enable);
    }

    public void setShowGrid(boolean enable)
    {
        m_itemShowGrid.setSelected(enable);
    }

    public void setShowInfoPanel(boolean enable)
    {
        m_itemShowInfoPanel.setSelected(enable);
    }

    public void setShowLastMove(boolean enable)
    {
        m_itemShowLastMove.setSelected(enable);
    }

    public void setShowShell(boolean enable)
    {
        m_itemShowShell.setSelected(enable);
    }

    public void setShowSubtreeSizes(boolean enable)
    {
        m_itemShowSubtreeSizes.setSelected(enable);
    }

    public void setShowToolbar(boolean enable)
    {
        m_itemShowToolbar.setSelected(enable);
    }

    public void setShowTree(boolean enable)
    {
        m_itemShowTree.setSelected(enable);
    }

    public void setShowVariations(boolean enable)
    {
        m_itemShowVariations.setSelected(enable);
    }

    /** Enable/disable items according to current position. */
    public void update(ConstGameTree gameTree, ConstNode node)
    {
        ConstNode father = node.getFatherConst();
        boolean hasFather = (father != null);
        boolean hasChildren = (node.getNumberChildren() > 0);
        boolean hasNextVariation = (NodeUtil.getNextVariation(node) != null);
        boolean hasNextEarlierVariation =
            (NodeUtil.getNextEarlierVariation(node) != null);
        boolean hasPrevEarlierVariation =
            (NodeUtil.getPreviousEarlierVariation(node) != null);
        boolean hasPrevVariation =
            (NodeUtil.getPreviousVariation(node) != null);
        boolean isInMain = NodeUtil.isInMainVariation(node);
        boolean treeHasVariations = gameTree.hasVariations();
        m_itemGoto.setEnabled(hasFather || hasChildren);
        m_itemGotoVar.setEnabled(hasFather || hasChildren);
        m_itemNextEarlierVariation.setEnabled(hasNextEarlierVariation);
        m_itemPreviousEarlierBackward.setEnabled(hasPrevEarlierVariation);
        m_itemBackToMainVar.setEnabled(! isInMain);
        m_itemTruncate.setEnabled(hasFather);
        m_itemTruncateChildren.setEnabled(hasChildren);
        m_itemMakeMainVar.setEnabled(! isInMain);
        m_itemKeepOnlyMainVar.setEnabled(isInMain && treeHasVariations);
        m_itemKeepOnlyPosition.setEnabled(hasFather || hasChildren);
        if (! NodeUtil.isInCleanup(node))
            setCleanup(false);
    }

    private boolean m_findNextEnabled;

    private boolean m_isComputerDisabled;

    private static int s_possibleBoardSizes[] = { 9, 11, 13, 15, 17, 19 };

    private static int s_possibleHandicaps[] = { 0, 2, 3, 4, 5, 6, 7, 8, 9 };

    private static final int SHORTCUT =
        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

    private final ActionListener m_listener;

    private JCheckBoxMenuItem m_itemAutoNumber;

    private JCheckBoxMenuItem m_itemBeepAfterMove;

    private JCheckBoxMenuItem m_itemCleanup;

    private JCheckBoxMenuItem m_itemCommandCompletion;

    private JCheckBoxMenuItem m_itemShowAnalyze;

    private JCheckBoxMenuItem m_itemShowCursor;

    private JCheckBoxMenuItem m_itemShowGrid;

    private JCheckBoxMenuItem m_itemShowLastMove;

    private JCheckBoxMenuItem m_itemShowShell;

    private JCheckBoxMenuItem m_itemShowSubtreeSizes;

    private JCheckBoxMenuItem m_itemShowTree;

    private JCheckBoxMenuItem m_itemShowVariations;

    private JCheckBoxMenuItem m_itemSetup;

    private JCheckBoxMenuItem m_itemTimeStamp;

    private JMenuChecked m_menuComputerColor;

    private final JMenuChecked m_menuBookmarks;

    private final JMenuChecked m_menuFile;

    private final JMenuChecked m_menuHelp;

    private final JMenuChecked m_menuShell;

    private final JMenuChecked m_menuSettings;

    private final JMenuBar m_menuBar;

    private JMenuItem m_itemAnalyzeOnlySupported;

    private JMenuItem m_itemBackToMainVar;

    private JMenuItem[] m_itemBoardSize;

    private JMenuItem m_itemBoardSizeOther;

    private JMenuItem m_itemClockHalt;

    private JMenuItem m_itemClockRestore;

    private JMenuItem m_itemClockResume;

    private JMenuItem m_itemCommentFontFixed;

    private JMenuItem m_itemGameTreeLarge;

    private JMenuItem m_itemGameTreeMove;

    private JMenuItem m_itemGameTreeNormal;

    private JMenuItem m_itemGameTreeNone;

    private JMenuItem m_itemGameTreeNumber;

    private JMenuItem m_itemGameTreeSmall;

    private JMenuItem m_itemGameTreeTiny;

    private JMenuItem m_itemGoto;

    private JMenuItem m_itemGotoVar;

    private JMenuItem m_itemKeepOnlyMainVar;

    private JMenuItem m_itemKeepOnlyPosition;

    private JMenuItem m_itemMakeMainVar;

    private JMenuItem m_itemNextEarlierVariation;

    private JMenuItem m_itemPreviousEarlierBackward;

    private JMenuItem m_itemSetupBlack;

    private JMenuItem m_itemSetupWhite;

    private JMenuItem m_itemShowInfoPanel;

    private JMenuItem m_itemShowToolbar;

    private JMenuItem m_itemSaveCommands;

    private JMenuItem m_itemSaveLog;

    private JMenuItem m_itemTruncate;

    private JMenuItem m_itemTruncateChildren;

    private JSeparator m_bookmarksSeparator;

    private RecentFileMenu m_recent;

    private RecentFileMenu m_recentGtp;

    private final ArrayList m_bookmarkItems = new ArrayList();

    private JMenuChecked createBoardSizeMenu()
    {
        JMenuChecked menu = createMenu("Board Size", KeyEvent.VK_S);
        ButtonGroup group = new ButtonGroup();
        int n = s_possibleBoardSizes.length;
        m_itemBoardSize = new JMenuItem[n];
        for (int i = 0; i < n; ++i)
        {
            String s = Integer.toString(s_possibleBoardSizes[i]);
            JMenuItem item = menu.addRadioItem(group, s, "board-size-" + s);
            m_itemBoardSize[i] = item;
        }
        menu.addSeparator();
        JMenuItem item =
            menu.addRadioItem(group, "Other", "board-size-other");
        m_itemBoardSizeOther = item;
        return menu;
    }

    private JMenuChecked createClockMenu(GoGuiActions actions)
    {
        JMenuChecked menu = createMenu("Clock", KeyEvent.VK_K);
        menu.addItem(actions.m_actionClockHalt, KeyEvent.VK_H);
        menu.addItem(actions.m_actionClockResume, KeyEvent.VK_R);
        menu.addItem(actions.m_actionClockRestore, KeyEvent.VK_S);
        return menu;
    }

    private JMenuChecked createComputerColorMenu(GoGuiActions actions)
    {
        ButtonGroup group = new ButtonGroup();
        JMenuChecked menu = createMenu("Computer Color", KeyEvent.VK_C);
        menu.addRadioItem(group, actions.m_actionComputerBlack, KeyEvent.VK_B);
        menu.addRadioItem(group, actions.m_actionComputerWhite, KeyEvent.VK_W);
        menu.addRadioItem(group, actions.m_actionComputerBoth, KeyEvent.VK_T);
        menu.addRadioItem(group, actions.m_actionComputerNone, KeyEvent.VK_N);
        return menu;
    }

    private JMenuChecked createHandicapMenu()
    {
        JMenuChecked menu = createMenu("Handicap", KeyEvent.VK_H);
        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < s_possibleHandicaps.length; ++i)
        {
            String s = Integer.toString(s_possibleHandicaps[i]);
            JMenuItem item = menu.addRadioItem(group, s, "handicap-" + s);
            if (s_possibleHandicaps[i] == 0)
                item.setSelected(true);
        }
        return menu;
    }

    private JMenuChecked createMenu(String name, int mnemonic)
    {
        JMenuChecked menu = new JMenuChecked(name, m_listener);
        menu.setMnemonic(mnemonic);
        return menu;
    }

    private JMenuChecked createMenuBookMarks()
    {
        JMenuChecked menu = createMenu("Bookmarks", KeyEvent.VK_B);
        menu.addItem("Add Bookmark", KeyEvent.VK_A, KeyEvent.VK_B,
                     SHORTCUT, "add-bookmark");
        menu.addItem("Edit Bookmarks...", KeyEvent.VK_E,
                     "edit-bookmarks");
        return menu;
    }

    private JMenuChecked createMenuConfigureAnalyze()
    {
        JMenuChecked menu = new JMenuChecked("Configure Analyze", m_listener);
        menu.setMnemonic(KeyEvent.VK_N);
        m_itemAnalyzeOnlySupported =
            new JCheckBoxMenuItem("Only Supported Commands");
        menu.addItem(m_itemAnalyzeOnlySupported, KeyEvent.VK_O,
                     "analyze-only-supported");
        return menu;
    }

    private JMenuChecked createMenuConfigureBoard()
    {
        JMenuChecked menu = new JMenuChecked("Configure Board", m_listener);
        menu.setMnemonic(KeyEvent.VK_B);
        m_itemShowCursor = new JCheckBoxMenuItem("Show Cursor");
        m_itemShowCursor.setSelected(true);
        menu.addItem(m_itemShowCursor, KeyEvent.VK_C,
                     "show-cursor");
        m_itemShowGrid = new JCheckBoxMenuItem("Show Grid");
        m_itemShowGrid.setSelected(true);
        menu.addItem(m_itemShowGrid, KeyEvent.VK_G, "show-grid");
        m_itemShowLastMove = new JCheckBoxMenuItem("Show Last Move");
        m_itemShowLastMove.setSelected(true);
        menu.addItem(m_itemShowLastMove, KeyEvent.VK_L, "show-last-move");
        m_itemShowVariations = new JCheckBoxMenuItem("Show Variations");
        m_itemShowVariations.setSelected(true);
        menu.addItem(m_itemShowVariations, KeyEvent.VK_V, "show-variations");
        m_itemBeepAfterMove = new JCheckBoxMenuItem("Beep After Move");
        menu.addItem(m_itemBeepAfterMove, KeyEvent.VK_B, "beep-after-move");
        m_itemCommentFontFixed =
            new JCheckBoxMenuItem("Fixed Size Comment Font");
        menu.addItem(m_itemCommentFontFixed, KeyEvent.VK_F,
                     "comment-font-fixed");
        return menu;
    }

    private JMenuChecked createMenuConfigureShell()
    {
        JMenuChecked menu = new JMenuChecked("Configure Shell", m_listener);
        menu.setMnemonic(KeyEvent.VK_H);
        m_itemCommandCompletion = new JCheckBoxMenuItem("Popup Completions");
        menu.addItem(m_itemCommandCompletion, KeyEvent.VK_P,
                     "command-completion");
        m_itemAutoNumber = new JCheckBoxMenuItem("Auto Number");
        menu.addItem(m_itemAutoNumber, KeyEvent.VK_A, "auto-number");
        m_itemTimeStamp = new JCheckBoxMenuItem("Timestamp");
        menu.addItem(m_itemTimeStamp, KeyEvent.VK_T, "timestamp");
        return menu;
    }

    private JMenuChecked createMenuConfigureTree()
    {
        JMenuChecked menu = new JMenuChecked("Configure Tree", m_listener);
        menu.setMnemonic(KeyEvent.VK_E);
        JMenuChecked menuLabel = createMenu("Labels", KeyEvent.VK_L);
        ButtonGroup group = new ButtonGroup();
        m_itemGameTreeNumber =
            menuLabel.addRadioItem(group, "Move Number", KeyEvent.VK_N,
                                   "gametree-number");
        m_itemGameTreeMove =
            menuLabel.addRadioItem(group, "Move", KeyEvent.VK_M,
                                   "gametree-move");
        m_itemGameTreeNone =
            menuLabel.addRadioItem(group, "None", KeyEvent.VK_O,
                                   "gametree-none");
        menu.add(menuLabel);
        JMenuChecked menuSize = createMenu("Size", KeyEvent.VK_S);
        group = new ButtonGroup();
        m_itemGameTreeLarge =
            menuSize.addRadioItem(group, "Large", KeyEvent.VK_L,
                                  "gametree-large");
        m_itemGameTreeNormal =
            menuSize.addRadioItem(group, "Normal", KeyEvent.VK_N,
                                  "gametree-normal");
        m_itemGameTreeSmall =
            menuSize.addRadioItem(group, "Small", KeyEvent.VK_S,
                                  "gametree-small");
        m_itemGameTreeTiny =
            menuSize.addRadioItem(group, "Tiny", KeyEvent.VK_T,
                                  "gametree-tiny");
        menu.add(menuSize);
        m_itemShowSubtreeSizes = new JCheckBoxMenuItem("Show Subtree Sizes");
        menu.addItem(m_itemShowSubtreeSizes, KeyEvent.VK_S,
                     "gametree-show-subtree-sizes");
        return menu;
    }

    private JMenuChecked createMenuEdit(GoGuiActions actions)
    {
        JMenuChecked menu = createMenu("Edit", KeyEvent.VK_E);
        menu.addItem(actions.m_actionFind, KeyEvent.VK_F);
        menu.addItem(actions.m_actionFindNext, KeyEvent.VK_N);
        menu.addSeparator();
        menu.addItem(actions.m_actionGameInfo, KeyEvent.VK_G);
        menu.add(createBoardSizeMenu());
        menu.add(createHandicapMenu());
        menu.addSeparator();
        m_itemMakeMainVar =
            menu.addItem("Make Main Variation", KeyEvent.VK_M,
                         "make-main-variation");
        m_itemKeepOnlyMainVar =
            menu.addItem("Delete Side Variations", KeyEvent.VK_D,
                         "keep-only-main-variation");
        m_itemKeepOnlyPosition =
            menu.addItem("Keep Only Position", KeyEvent.VK_K,
                         "keep-only-position");
        m_itemTruncate = menu.addItem("Truncate", KeyEvent.VK_T, "truncate");
        m_itemTruncateChildren
            = menu.addItem("Truncate Children", KeyEvent.VK_C,
                           "truncate-children");
        menu.addSeparator();
        m_itemSetup = new JCheckBoxMenuItem("Setup Mode");
        menu.addItem(m_itemSetup, KeyEvent.VK_S, "setup");
        ButtonGroup group = new ButtonGroup();
        m_itemSetupBlack = menu.addRadioItem(group, "Setup Black",
                                             KeyEvent.VK_B, "setup-black");
        m_itemSetupBlack.setSelected(true);
        m_itemSetupWhite = menu.addRadioItem(group, "Setup White",
                                             KeyEvent.VK_W, "setup-white");
        return menu;
    }

    private JMenuChecked createMenuExport(GoGuiActions actions)
    {
        JMenuChecked menu = new JMenuChecked("Export", m_listener);
        menu.setMnemonic(KeyEvent.VK_E);
        menu.addItem(actions.m_actionExportSgfPosition, KeyEvent.VK_S);
        menu.addItem(actions.m_actionExportLatexMainVariation, KeyEvent.VK_L);
        menu.addItem(actions.m_actionExportLatexPosition, KeyEvent.VK_P);
        menu.addItem(actions.m_actionExportTextPosition, KeyEvent.VK_T);
        menu.addItem(actions.m_actionExportTextPositionToClipboard,
                     KeyEvent.VK_C);
        return menu;
    }

    private JMenuChecked createMenuFile(GoGuiActions actions,
                                        RecentFileMenu.Callback callback)
    {
        JMenuChecked menu = createMenu("File", KeyEvent.VK_F);
        menu.addItem(actions.m_actionOpen, KeyEvent.VK_O);
        menu.add(createRecentMenu(callback));
        menu.addItem(actions.m_actionSave, KeyEvent.VK_S);
        menu.addItem(actions.m_actionSaveAs, KeyEvent.VK_A);
        menu.addSeparator();
        menu.add(createMenuImport(actions));
        menu.add(createMenuExport(actions));
        menu.addSeparator();
        menu.addItem(actions.m_actionPrint, KeyEvent.VK_P);
        menu.addSeparator();
        menu.addItem(actions.m_actionAttachProgram, KeyEvent.VK_T);
        menu.addItem(actions.m_actionDetachProgram, KeyEvent.VK_D);
        menu.addItem(actions.m_actionQuit, KeyEvent.VK_Q);
        return menu;
    }

    private JMenuChecked createMenuGame(GoGuiActions actions)
    {
        JMenuChecked menu = createMenu("Game", KeyEvent.VK_A);
        menu.addItem(actions.m_actionNewGame, KeyEvent.VK_N);
        menu.addSeparator();
        m_menuComputerColor = createComputerColorMenu(actions);
        menu.add(m_menuComputerColor);
        menu.addItem(actions.m_actionPlay, KeyEvent.VK_L);
        menu.addItem(actions.m_actionPlaySingleMove, KeyEvent.VK_S);
        menu.addItem(actions.m_actionInterrupt, KeyEvent.VK_T);
        m_itemCleanup = new JCheckBoxMenuItem("Cleanup");
        m_itemCleanup.setMnemonic(KeyEvent.VK_E);
        menu.addSeparator();
        menu.addItem(actions.m_actionPass, KeyEvent.VK_P);
        menu.add(createClockMenu(actions));
        menu.addItem(actions.m_actionScore, KeyEvent.VK_O);
        return menu;
    }

    private JMenuChecked createMenuGo(GoGuiActions actions)
    {
        int shiftMask = java.awt.event.InputEvent.SHIFT_MASK;
        JMenuChecked menu = createMenu("Go", KeyEvent.VK_G);
        menu.addItem(actions.m_actionBeginning, KeyEvent.VK_B);
        menu.addItem(actions.m_actionBackwardTen, KeyEvent.VK_W);
        menu.addItem(actions.m_actionBackward, KeyEvent.VK_K);
        menu.addItem(actions.m_actionForward, KeyEvent.VK_F);
        menu.addItem(actions.m_actionForwardTen, KeyEvent.VK_R);
        menu.addItem(actions.m_actionEnd, KeyEvent.VK_E);
        m_itemGoto =
            menu.addItem("Go to Move...", KeyEvent.VK_O, KeyEvent.VK_G,
                         SHORTCUT, "goto");
        menu.addSeparator();
        menu.addItem(actions.m_actionNextVariation, KeyEvent.VK_N);
        menu.addItem(actions.m_actionPreviousVariation, KeyEvent.VK_P);
        m_itemNextEarlierVariation =
            menu.addItem("Next Earlier Variation", KeyEvent.VK_X,
                         KeyEvent.VK_DOWN, SHORTCUT | shiftMask,
                         "next-earlier-variation");
        m_itemPreviousEarlierBackward =
            menu.addItem("Previous Earlier Variation", KeyEvent.VK_L,
                         KeyEvent.VK_UP, SHORTCUT | shiftMask,
                         "previous-earlier-variation");
        m_itemBackToMainVar =
            menu.addItem("Back to Main Variation", KeyEvent.VK_M,
                         KeyEvent.VK_M, SHORTCUT,
                         "back-to-main-variation");
        m_itemGotoVar =
            menu.addItem("Go to Variation...",
                         KeyEvent.VK_V, "goto-variation");
        return menu;
    }

    private JMenuChecked createMenuHelp(GoGuiActions actions)
    {
        JMenuChecked menu = createMenu("Help", KeyEvent.VK_H);
        menu.addItem(actions.m_actionDocumentation, KeyEvent.VK_G);
        menu.addItem(actions.m_actionAbout, KeyEvent.VK_A);
        return menu;
    }

    private JMenuChecked createMenuImport(GoGuiActions actions)
    {
        JMenuChecked menu = new JMenuChecked("Import", m_listener);
        menu.setMnemonic(KeyEvent.VK_I);
        menu.addItem(actions.m_actionImportTextPosition, KeyEvent.VK_T);
        menu.addItem(actions.m_actionImportTextPositionFromClipboard,
                     KeyEvent.VK_C);
        return menu;
    }

    private JMenuChecked createMenuShell(RecentFileMenu.Callback callback)
    {
        JMenuChecked menu = createMenu("Shell", KeyEvent.VK_L);
        m_itemSaveLog = menu.addItem("Save Log...", KeyEvent.VK_L,
                                     "gtpshell-save");
        m_itemSaveCommands = menu.addItem("Save Commands...",
                                          KeyEvent.VK_C,
                                          "gtpshell-save-commands");
        menu.addItem("Send File...", KeyEvent.VK_F,
                     "gtpshell-send-file");
        m_recentGtp = new RecentFileMenu("Send Recent",
                                         "net/sf/gogui/recentgtpfiles",
                                         callback);
        m_recentGtp.getMenu().setMnemonic(KeyEvent.VK_R);
        menu.add(m_recentGtp.getMenu());
        return menu;
    }

    private JMenuChecked createMenuSettings()
    {
        JMenuChecked menu = createMenu("Settings", KeyEvent.VK_S);
        m_itemShowToolbar = new JCheckBoxMenuItem("Show Toolbar");
        menu.addItem(m_itemShowToolbar, KeyEvent.VK_T,
                     "show-toolbar");
        m_itemShowInfoPanel = new JCheckBoxMenuItem("Show Info Panel");
        menu.addItem(m_itemShowInfoPanel, KeyEvent.VK_I,
                     "show-info-panel");
        menu.addSeparator();
        m_itemShowTree = new JCheckBoxMenuItem("Show Tree");
        menu.addItem(m_itemShowTree, KeyEvent.VK_R, KeyEvent.VK_F7,
                     getFunctionKeyShortcut(), "show-tree");
        m_itemShowShell = new JCheckBoxMenuItem("Show Shell");
        menu.addItem(m_itemShowShell, KeyEvent.VK_S, KeyEvent.VK_F8,
                     getFunctionKeyShortcut(), "show-shell");
        m_itemShowAnalyze = new JCheckBoxMenuItem("Show Analyze");
        menu.addItem(m_itemShowAnalyze, KeyEvent.VK_A, KeyEvent.VK_F9,
                     getFunctionKeyShortcut(), "analyze");
        menu.addSeparator();
        menu.add(createMenuConfigureBoard());
        menu.add(createMenuConfigureTree());
        menu.add(createMenuConfigureShell());
        menu.add(createMenuConfigureAnalyze());
        return menu;
    }

    private JMenu createRecentMenu(RecentFileMenu.Callback callback)
    {
        m_recent = new RecentFileMenu("Open Recent",
                                      "net/sf/gogui/recentfiles",
                                      callback);
        JMenu menu = m_recent.getMenu();
        menu.setMnemonic(KeyEvent.VK_R);
        return menu;
    }

    /** Get shortcut modifier for function keys.
        Returns 0, unless platform is Mac.
    */
    private static int getFunctionKeyShortcut()
    {
        if (Platform.isMac())
            return SHORTCUT;
        return 0;
    }
}

/** Menu with assertions for unique mnemonics and accelerators. */
class JMenuChecked
    extends JMenu
{
    public JMenuChecked(String text, ActionListener listener)
    {
        super(text);
        m_listener = listener;
    }

    public JMenuItem addItem(JMenuItem item, String command)
    {
        item.addActionListener(m_listener);
        item.setActionCommand(command);
        add(item);
        return item;
    }

    public JMenuItem addItem(AbstractAction action, int mnemonic)
    {
        JMenuItem item = new JMenuItem(action);
        item.setIcon(null);
        setMnemonic(item, mnemonic);
        add(item);
        return item;
    }

    public JMenuItem addItem(JMenuItem item, int mnemonic, String command)
    {
        setMnemonic(item, mnemonic);
        return addItem(item, command);
    }

    public JMenuItem addItem(String label, int mnemonic, String command)
    {
        JMenuItem item = new JMenuItem(label);
        return addItem(item, mnemonic, command);        
    }

    public JMenuItem addItem(JMenuItem item, int mnemonic, int accel,
                             int modifier, String command)
    {
        KeyStroke keyStroke = KeyStroke.getKeyStroke(accel, modifier); 
        assert(! s_accelerators.contains(keyStroke));
        s_accelerators.add(keyStroke);
        item.setAccelerator(keyStroke);
        return addItem(item, mnemonic, command);
    }

    public JMenuItem addItem(String label, int mnemonic, int accel,
                             int modifier, String command)
    {
        return addItem(new JMenuItem(label), mnemonic, accel, modifier,
                       command);
    }

    public JMenuItem addRadioItem(ButtonGroup group, String label,
                                  String command)
    {
        JMenuItem item = new JRadioButtonMenuItem(label);
        group.add(item);
        return addItem(item, command);
    }

    public JMenuItem addRadioItem(ButtonGroup group, String label,
                                  int mnemonic, String command)
    {
        JMenuItem item = new JRadioButtonMenuItem(label);
        group.add(item);
        return addItem(item, mnemonic, command);
    }

    public JMenuItem addRadioItem(ButtonGroup group, AbstractAction action,
                                  int mnemonic)
    {
        JMenuItem item = new GoGuiRadioButtonMenuItem(action);        
        group.add(item);
        item.setIcon(null);
        setMnemonic(item, mnemonic);
        add(item);
        return item;
    }

    /** Serial version to suppress compiler warning.
        Contains a marker comment for use with serialver.sourceforge.net
    */
    private static final long serialVersionUID = 0L; // SUID

    private final ActionListener m_listener;

    private final ArrayList m_mnemonics = new ArrayList();

    private static ArrayList s_accelerators = new ArrayList();

    private void setMnemonic(JMenuItem item, int mnemonic)
    {
        item.setMnemonic(mnemonic);
        Integer integer = new Integer(mnemonic);
        if (m_mnemonics.contains(integer))
        {
            System.err.println("Warning: duplicate mnemonic item "
                               + item.getText());
            assert(false);
        }
        m_mnemonics.add(integer);
    }
}

/** Radio menu item with additional "selected" action property. */
class GoGuiRadioButtonMenuItem
    extends JRadioButtonMenuItem
{
    public GoGuiRadioButtonMenuItem(AbstractAction action)
    {
        super(action);
        action.addPropertyChangeListener(new PropertyChangeListener() {
                public void  propertyChange(PropertyChangeEvent e) {
                    if (e.getPropertyName().equals("selected"))
                        setSelected(((Boolean)e.getNewValue()).booleanValue());
                } } );
    }
}
