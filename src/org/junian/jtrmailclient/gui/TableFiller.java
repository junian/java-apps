/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.junian.jtrmailclient.gui;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import org.junian.jtrmailclient.Header;
import org.junian.jtrmailclient.Pop3Client;

/**
 *
 * @author sainthackr
 */
public class TableFiller extends Thread{

    private Map<Integer, Header> headers;
    private String[] headerList;
    private DefaultTableModel dtm;
    private Pop3Client client;

    public TableFiller(Map<Integer, Header> headers, String[] headerList, DefaultTableModel dtm, Pop3Client client) throws UnknownHostException, IOException {
        this.headers = headers;
        this.headerList = headerList;
        this.dtm = dtm;
        this.client = new Pop3Client(client.getIpAddress(), client.getPort());
        this.client.login(client.getUsername(), client.getPassword());
    }

    @Override
    public void run() {
        dtm.setRowCount(0);
        for(int i=headerList.length-1; i>=0; i--) {
            int index = Integer.parseInt(headerList[i].split(" ")[0]);
            Header h = null;
            if(headers.get(index) == null) {
                try {
                    synchronized(client) {
                        h = client.getHeader(index);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(TableFiller.class.getName()).log(Level.SEVERE, null, ex);
                }
                synchronized(headers) {
                    headers.put(index, h);
                }
            }
            else {
                h = headers.get(index);
            }
            Object[] obj = {index, false, h.getHeader("from"), h.getHeader("subject")};
            synchronized(dtm) {
                dtm.addRow(obj);
            }
        }
    }
}
