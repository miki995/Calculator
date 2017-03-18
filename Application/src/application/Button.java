package application;

import javax.swing.JButton;

public class Button {

    JButton jButton;
    String name;
    int type;
    String display;
    int pref;

    public Button() {
    }

    public Button(JButton jButton, String name, int type, String display, int pref) {
        this.name = name;
        this.type = type;
        this.display = display;
        this.jButton = jButton;
        this.pref = pref;
    }

    boolean isNumber() {
        return type == 0;
    }

    boolean isUnaryOperation() {
        return type == 1;
    }

    boolean isBinaryOperation() {
        return type == 2;
    }

    boolean isVarConst() {
        return type == 3;
    }

    boolean isCommand() {
        return type == 4;
    }

    boolean isPlusMinus() {
        return type == 5;
    }

    boolean isDecPoint() {
        return type == 6;
    }
    
    boolean isDelete(){
        return name.equals("delete");
    }
}