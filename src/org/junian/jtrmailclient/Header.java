
package org.junian.jtrmailclient;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author bakajunichi
 */
public class Header {

    private int index;
    private Map<String, String> headerList;

    public Header(int index) {
        headerList = new HashMap<String, String>();
        this.index = index;
    }

    public String getHeader(String headerName) {
        return headerList.get(headerName);
    }

    public void setHeader(String headerName, String headerValue) {
        headerList.put(headerName, headerValue);
    }

    public int getIndex() {
        return index;
    }
}
