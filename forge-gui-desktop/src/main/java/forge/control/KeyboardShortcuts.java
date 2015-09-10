package forge.control;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.apache.commons.lang3.StringUtils;

import forge.Singletons;
import forge.game.spellability.StackItemView;
import forge.gui.framework.EDocID;
import forge.gui.framework.SDisplayUtil;
import forge.model.FModel;
import forge.properties.ForgePreferences;
import forge.properties.ForgePreferences.FPref;
import forge.screens.home.settings.VSubmenuPreferences.KeyboardShortcutField;
import forge.screens.match.CMatchUI;

/** 
 * Consolidates keyboard shortcut assembly into one location
 * for all shortcuts in the project.
 * 
 * Just map a new Shortcut object here, set a default in preferences,
 * and you're done.
 */
public class KeyboardShortcuts {

    public static List<Shortcut> getKeyboardShortcuts() {
        return attachKeyboardShortcuts(null);
    }

    /**
     * Attaches all keyboard shortcuts for match UI,
     * and returns a list of shortcuts with necessary properties for later access.
     *
     * @return List<Shortcut> Shortcut objects
     */
    @SuppressWarnings("serial")
    public static List<Shortcut> attachKeyboardShortcuts(final CMatchUI matchUI) {
        final JComponent c = Singletons.getView().getFrame().getLayeredPane();
        final InputMap im = c.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        final ActionMap am = c.getActionMap();

        final List<Shortcut> list = new ArrayList<Shortcut>();

        //========== Match Shortcuts
        /** Show stack. */
        final Action actShowStack = new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!Singletons.getControl().getCurrentScreen().isMatchScreen()) { return; }
                SDisplayUtil.showTab(EDocID.REPORT_STACK.getDoc());
            }
        };

        /** Show combat. */
        final Action actShowCombat = new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!Singletons.getControl().getCurrentScreen().isMatchScreen()) { return; }
                SDisplayUtil.showTab(EDocID.REPORT_COMBAT.getDoc());
            }
        };

        /** Show console. */
        final Action actShowConsole = new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!Singletons.getControl().getCurrentScreen().isMatchScreen()) { return; }
                SDisplayUtil.showTab(EDocID.REPORT_LOG.getDoc());
            }
        };

        /** Show dev panel. */
        final Action actShowDev = new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!Singletons.getControl().getCurrentScreen().isMatchScreen()) { return; }
                if (ForgePreferences.DEV_MODE) {
                    SDisplayUtil.showTab(EDocID.DEV_MODE.getDoc());
                }
            }
        };

        /** Concede game. */
        final Action actConcede = new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!Singletons.getControl().getCurrentScreen().isMatchScreen()) { return; }
                if (matchUI == null) { return; }
                matchUI.concede();
            }
        };

        /** End turn. */
        final Action actEndTurn = new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!Singletons.getControl().getCurrentScreen().isMatchScreen()) { return; }
                if (matchUI == null) { return; }
                matchUI.getGameController().passPriorityUntilEndOfTurn();
            }
        };

        /** Alpha Strike. */
        final Action actAllAttack = new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!Singletons.getControl().getCurrentScreen().isMatchScreen()) { return; }
                if (matchUI == null) { return; }
                matchUI.getGameController().alphaStrike();
            }
        };

        /** Targeting visualization overlay. */
        final Action actTgtOverlay = new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!Singletons.getControl().getCurrentScreen().isMatchScreen()) { return; }
                if (matchUI == null) { return; }
                matchUI.getCDock().toggleTargeting();
            }
        };

        /** Auto-yield current item on stack (and respond Always Yes if applicable). */
        final Action actAutoYieldAndYes = new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!Singletons.getControl().getCurrentScreen().isMatchScreen()) { return; }
                if (matchUI == null) { return; }
                StackItemView si = matchUI.getGameView().peekStack();
                if (si != null && si.isAbility() && matchUI.isLocalPlayer(si.getActivatingPlayer())) {
                    matchUI.setShouldAutoYield(si.getKey(), true);
                    int triggerID = Integer.valueOf(si.getSourceTrigger());
                    if (si.isOptionalTrigger()) {
                        matchUI.setShouldAlwaysAcceptTrigger(triggerID);
                    }
                    matchUI.getGameController().passPriority();
                }
            }
        };

        /** Auto-yield current item on stack (and respond Always No if applicable). */
        final Action actAutoYieldAndNo = new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!Singletons.getControl().getCurrentScreen().isMatchScreen()) { return; }
                if (matchUI == null) { return; }
                StackItemView si = matchUI.getGameView().peekStack();
                if (si != null && si.isAbility() && matchUI.isLocalPlayer(si.getActivatingPlayer())) {
                    matchUI.setShouldAutoYield(si.getKey(), true);
                    int triggerID = Integer.valueOf(si.getSourceTrigger());
                    if (si.isOptionalTrigger()) {
                        matchUI.setShouldAlwaysDeclineTrigger(triggerID);
                    }
                    matchUI.getGameController().passPriority();
                }
            }
        };

        
        //========== Instantiate shortcut objects and add to list.
        list.add(new Shortcut(FPref.SHORTCUT_SHOWSTACK, "Match: show stack panel", actShowStack, am, im));
        list.add(new Shortcut(FPref.SHORTCUT_SHOWCOMBAT, "Match: show combat panel", actShowCombat, am, im));
        list.add(new Shortcut(FPref.SHORTCUT_SHOWCONSOLE, "Match: show console panel", actShowConsole, am, im));
        list.add(new Shortcut(FPref.SHORTCUT_SHOWDEV, "Match: show dev panel", actShowDev, am, im));
        list.add(new Shortcut(FPref.SHORTCUT_CONCEDE, "Match: concede game", actConcede, am, im));
        list.add(new Shortcut(FPref.SHORTCUT_ENDTURN, "Match: pass priority until EOT or next stack event", actEndTurn, am, im));
        list.add(new Shortcut(FPref.SHORTCUT_ALPHASTRIKE, "Match: Alpha Strike (attack with all available)", actAllAttack, am, im));
        list.add(new Shortcut(FPref.SHORTCUT_SHOWTARGETING, "Match: toggle targeting visual overlay", actTgtOverlay, am, im));
        list.add(new Shortcut(FPref.SHORTCUT_AUTOYIELD_ALWAYS_YES, "Match: auto-yield ability on stack (Always Yes)", actAutoYieldAndYes, am, im));
        list.add(new Shortcut(FPref.SHORTCUT_AUTOYIELD_ALWAYS_NO, "Match: auto-yield ability on stack (Always No)", actAutoYieldAndNo, am, im));
        return list;
    } // End initMatchShortcuts()

    /**
     * 
     * Instantiates a shortcut key instance with various properties for use
     * throughout the project.
     *
     */
    public static class Shortcut {
        /** */
        private final FPref prefkeys;
        /** */
        private final String description;
        /** */
        private final Action handler;
        /** */
        private final ActionMap actionMap;
        /** */
        private final InputMap inputMap;
        /** */
        private KeyStroke key;
        /** */
        private String str;

        /**
         * 
         * Instantiates a shortcut key instance with various properties for use
         * throughout the project.
         * 
         * @param prefkey0 String, ident key in forge.preferences
         * @param description0 String, description of shortcut function
         * @param handler0 Action, action on execution of shortcut
         * @param am0 ActionMap, of container targeted by shortcut
         * @param im0 InputMap, of container targeted by shortcut
         */
        public Shortcut(final FPref prefkey0, final String description0,
                final Action handler0, final ActionMap am0, final InputMap im0) {

            prefkeys = prefkey0;
            description = description0;
            handler = handler0;
            actionMap = am0;
            inputMap = im0;
            attach();
        }

        /** @return {@link java.lang.String} */
        public String getDescription() {
            return description;
        }

        /**
         * String ident key in forge.preferences.
         * @return {@link java.lang.String}
         */
        public FPref getPrefKey() {
            return prefkeys;
        }

        /** */
        public void attach() {
            detach();
            str = FModel.getPreferences().getPref(prefkeys);
            if (!str.isEmpty()) {
                key = assembleKeystrokes(str.split(" "));
    
                // Attach key stroke to input map...
                inputMap.put(key, str);
                
                // ...then attach actionListener to action map
                actionMap.put(str, handler);
            }
        }

        /** */
        public void detach() {
            inputMap.remove(key);
            actionMap.remove(str);
        }
    } // End class Shortcut

    private static KeyStroke assembleKeystrokes(final String[] keys0) {
        int[] inputEvents = new int[2];
        int modifier = 0;
        int keyEvent = 0;

        inputEvents[0] = 0;
        inputEvents[1] = 0;

        // If CTRL or SHIFT is pressed, it must be passed as a modifier,
        // in the form of an input event object. So, first test if these were pressed.
        // ALT shortcuts will be ignored.
        for (final String s : keys0) {
            if (s.equals("16"))      { inputEvents[0] = 16; }
            else if (s.equals("17")) { inputEvents[1] = 17; }
            else {
                keyEvent = Integer.valueOf(s);
            }
        }

        // Then, convert to InputEvent.
        if (inputEvents[0] == 16 && inputEvents[1] != 17) {
            modifier = InputEvent.SHIFT_DOWN_MASK;
        }
        else if (inputEvents[0] != 16 && inputEvents[1] == 17) {
            modifier = InputEvent.CTRL_DOWN_MASK;
        }
        else if (inputEvents[0] != 0 && inputEvents[1] != 0) {
            modifier = InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK;
        }

        return KeyStroke.getKeyStroke(keyEvent, modifier);
    }

    /**
     * - Adds keycode to list stored in name of a text field.
     * - Code is not added if already in list.
     * - Backspace removes last code in list.
     * - Sets text of text field with character equivalent of keycodes.
     * 
     * @param e &emsp; KeyEvent
     */
    public static void addKeyCode(final KeyEvent e) {
        final KeyboardShortcutField ksf = (KeyboardShortcutField) e.getSource();
        final String newCode = Integer.toString(e.getKeyCode());
        final String codestring = ksf.getCodeString();
        List<String> existingCodes;

        if (codestring != null) {
            existingCodes = new ArrayList<String>(Arrays.asList(codestring.split(" ")));
        } else {
            existingCodes = new ArrayList<String>();
        }

        // Backspace (8) will remove last code from list.
        if (e.getKeyCode() == 8) {
            existingCodes.remove(existingCodes.size() - 1);
        } else if (!existingCodes.contains(newCode)) {
            existingCodes.add(newCode);
        }

        ksf.setCodeString(StringUtils.join(existingCodes, ' '));
    }
}
