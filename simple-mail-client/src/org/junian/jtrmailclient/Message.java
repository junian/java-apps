
package org.junian.jtrmailclient;

/**
 *
 * @author bakajunichi
 */
public class Message {
    private boolean markedAsDelete;
    private int index;
    private Header headers;

    public Message(int index){
        this.index = index;
        headers = new Header(index);
        markedAsDelete = false;
    }

    public String getHeader(String headerName){
        return headers.getHeader(headerName);
    }

    public void setHeader(String headerName, String value) {
        headers.setHeader(headerName.toLowerCase().trim(), value);
    }

    public Header getHeader() {
        return headers;
    }

    public void markAsDelete(){
        markedAsDelete = true;
    }

    public boolean isMarkedAsDelete() {
        return markedAsDelete;
    }

    public String getMessageFile(){
        return (index + ".html");
    }
}
