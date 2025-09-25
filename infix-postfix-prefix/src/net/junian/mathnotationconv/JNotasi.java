package net.junian.mathnotationconv;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Uchiha Junichi
 */
public class JNotasi {

    private JStack tumpukan;
    private String[] data;

    /**
     * method untuk memisahkan sebuah string menjadi substring
     * @param ekspresi
     * @return substring yang dipisahkan spasi
     */
    private String[] splitter(String ekspresi){
        ekspresi = ekspresi.replace("+", " + ");
        ekspresi = ekspresi.replace("-", " - ");
        ekspresi = ekspresi.replace("*", " * ");
        ekspresi = ekspresi.replace("/", " / ");
        ekspresi = ekspresi.replace("(", " ( ");
        ekspresi = ekspresi.replace(")", " ) ");
        return ekspresi.split(" ");
    }

    /**
     * method untuk mengecek apakah suatu elemen operator atau bukan
     * @param elemen
     * @return true jika merupakan operator
     */
    private boolean isOperator(String elemen){
        if(elemen.equalsIgnoreCase("+") ||
           elemen.equalsIgnoreCase("-") ||
           elemen.equalsIgnoreCase("*") ||
           elemen.equalsIgnoreCase("/") ||
           elemen.equalsIgnoreCase("(") ||
           elemen.equalsIgnoreCase(")")){
            return true;
        }
        return false;
    }

    /**
     * method untuk mendapatkan hierarchy yang diinterpretasikan dengan integer
     * @param elemen
     * @return ukran prioritas
     */
    private int hierarchy(String elemen){
        if(elemen.equals("+") || elemen.equals("-"))
            return 1;
        else if(elemen.equals("*") || elemen.equals("/"))
            return 2;
        return 0;
    }

    /**
     * method untuk mengubah notasi infix menjaid postfix
     * @param ekspresi
     * @return notasi postfix
     */
    public String infix_to_postfix(String ekspresi){
        String result = "";
        tumpukan = new JStack();
        data = splitter(ekspresi);

        for(int i=0; i<data.length; i++){
            if(isOperator(data[i])){
                if(data[i].equals(")")){
                    while(!tumpukan.peek().equals("(")){
                        result += tumpukan.pop() + " ";
                    }
                    tumpukan.pop();
                }
                else if(tumpukan.isEmpty() || hierarchy(data[i])>=hierarchy(tumpukan.peek()) ||
                        data[i].equals("(")){
                    tumpukan.push(data[i]);
                }
                else{
                    while(!tumpukan.isEmpty() && !tumpukan.peek().equals("(")){
                        result += tumpukan.pop() + " ";
                    }
                    tumpukan.push(data[i]);
                }
            }
            else if(!data[i].equals("")){
                result += data[i] + " ";
            }
        }

        while(!tumpukan.isEmpty()){
            result += tumpukan.pop() + " ";
        }

        return result;
    }

    /**
     * method untuk mengubah notasi infix menjadi prefix
     * @param ekspresi
     * @return notasi prefix
     */
    public String infix_to_prefix(String ekspresi){
        String result = "";
        tumpukan = new JStack();
        data = splitter(ekspresi);

        for(int i=data.length-1; i>=0; i--){
            if(isOperator(data[i])){
                if(data[i].equals("(")){
                    while(!tumpukan.peek().equals(")")){
                        result = " " + tumpukan.pop() + result;
                    }
                    tumpukan.pop();
                }
                else if(tumpukan.isEmpty() || hierarchy(data[i])>=hierarchy(tumpukan.peek()) ||
                        data[i].equals(")"))
                    tumpukan.push(data[i]);
                else{
                    while(!tumpukan.isEmpty() && !tumpukan.peek().equals(")")){
                        result = " " + tumpukan.pop() + result;
                    }
                    tumpukan.push(data[i]);
                }
            }
            else if(!data[i].equals("")){
                result = " " + data[i] + result;
            }
        }

        while(!tumpukan.isEmpty()){
            result = " " + tumpukan.pop() + result;
        }

        return result;
    }

    /**
     * method untuk mengubah notasi postfix menjadi infix
     * @param ekspresi
     * @return notasi infix
     */
    public String postfix_to_infix(String ekspresi){
        String result = "";
        String right = "", left = "";
        tumpukan = new JStack();
        data = splitter(ekspresi);

        for(int i=0; i<data.length; i++){
            if(isOperator(data[i])){
                right = tumpukan.pop();
                left = tumpukan.pop();
                tumpukan.push("(" + left + data[i]+ right + ")" );
            }
            else if(!data[i].equals("")){
                tumpukan.push(data[i]);
            }
        }

        result += tumpukan.pop();

        return result;
    }

    /**
     * method untuk mengubah notasi prefix menjadi infix
     * @param ekspresi
     * @return notasi infix
     */
    public String prefix_to_infix(String ekspresi){
        String result = "";
        String right = "", left = "";
        tumpukan = new JStack();
        data = splitter(ekspresi);

        for(int i=data.length-1; i>=0; i--){
            if(isOperator(data[i])){
                left = tumpukan.pop();
                right = tumpukan.pop();
                tumpukan.push("(" + left + data[i]+ right + ")" );
            }
            else if(!data[i].equals("")){
                tumpukan.push(data[i]);
            }
        }

        result += tumpukan.pop();

        return result;
    }

    /**
     * method untuk mengubah notasi prefix menjadi postfix
     * @param ekspresi
     * @return notasi postfix
     */
    public String prefix_to_postfix(String ekspresi){
        String result = "";
        result = infix_to_postfix(prefix_to_infix(ekspresi));
        return result;
    }

    /**
     * method untuk mengubah notasi ostfix menjadi prefix
     * @param ekspresi
     * @return notasi prefix
     */
    public String postfix_to_prefix(String ekspresi){
        String result = "";
        result = infix_to_prefix(postfix_to_infix(ekspresi));
        return result;
    }
}
