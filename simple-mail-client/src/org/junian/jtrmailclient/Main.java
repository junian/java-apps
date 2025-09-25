package org.junian.jtrmailclient;

import org.junian.jtrmailclient.gui.MainFrame;

/**
 *
 * @author bakajunichi
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if(args[0].equalsIgnoreCase("-gui"))
            MainFrame.main(args);
    }

}
