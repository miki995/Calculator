package application;

import java.util.ArrayDeque;
import java.util.Deque;
import javax.swing.JLabel;

public class InputListener {

    static final double epsilon = 0.000000000001;
    int brackState = 0;
    static boolean result = false;
    JLabel display;
    JLabel expression;
    StringBuilder expString = new StringBuilder();
    Deque<Button> expArray = new ArrayDeque<>();

    private int decimal(String text) {
        return text.indexOf('.') + 1;
    }

    private void displayExpression(Button button) {
        expString.append(button.display);
        display.setText("");
        if (expString.length() > 64) {
            expression.setText("..." + expString.substring(expString.length() - 60, expString.length()));
        } else {
            expression.setText(expString.toString());
        }
    }

    private void displayExpression() {
        if (expString.length() > 64) {
            expression.setText("..." + expString.substring(expString.length() - 60, expString.length()));
        } else {
            expression.setText(expString.toString());
        }
    }

    public InputListener() {
    }

    public InputListener(JLabel display, JLabel expression) {
        this.display = display;
        this.expression = expression;
    }

    void action(Button button) {
        System.out.println(expString);
        if (result) {
            result = false;
            display.setText("");
            expression.setText("");
            expString = new StringBuilder();
        }
        System.out.println(expString);
        switch (button.name) {
            case "pi":
                if (display.getText().isEmpty()) {
                    display.setText(String.valueOf(Math.PI));
                }
                break;
            case "clearAll":
                display.setText("");
                expression.setText("");
                expString = new StringBuilder();
                expArray.clear();
                break;
            case "clearInput":
                display.setText("");
                break;
            case "pm":
                if (!display.getText().contains("-")) {
                    display.setText("-" + display.getText());
                } else if (display.getText().equals("-")) {
                    display.setText("");
                } else {
                    display.setText(display.getText().substring(1, display.getText().length()));
                }
                break;
            case "delete":
                delete();
                return;
            case "equal":
                if (display.getText().isEmpty() && expression.getText().isEmpty()) {
                    return;
                }
                if (display.getText().equals("-")) {
                    display.setText("0");
                }
                if (display.getText().length() > 0) {
                    expArray.addLast(new Button(null, "number", 0, display.getText(), 0));
                    expString.append(display.getText());
                }
                while (expArray.peekLast().isBinaryOperation()
                        || expArray.peekLast().isUnaryOperation()) {
                    delete();
                }
                displayExpression(button);
                display.setText("");
                Button output;
                try {
                    output = new ExpressionSolver().ShuntingYard(expArray);
                } catch (java.lang.NumberFormatException | java.lang.ArithmeticException e) {
                    output = null;
                }
                if (output == null || Double.isInfinite(Double.parseDouble(output.display))) {
                    display.setText("ERROR: OVERFLOW OR ...");
                    result = true;
                } else {
                    Double num = Double.parseDouble(output.display);
                    if (num <= Double.valueOf(Long.valueOf(Long.MAX_VALUE).toString())
                            && num - num.longValue() < epsilon) {
                        display.setText(Long.toString(num.longValue()));
                    } else {
                        display.setText(Double.toString(num));
                    }
                }
                
                result = true;
                expArray.clear();
                System.out.println(expString);
                return;
            case "closeBrack":
                if (display.getText().isEmpty()) {
                    boolean ret = true;
                    if (!expArray.isEmpty()) {
                        if ("closeBracknumber".contains(expArray.peekLast().name)) {
                            if (brackState > 0) {
                                ret = false;
                            }
                        }
                    }
                    if (ret) {
                        return;
                    }
                }
                if (brackState
                        > 0) {
                    brackState--;
                } else {
                    return;
                }

                expString.append(display.getText());
                if (!display.getText().isEmpty()) {
                    expArray.addLast(new Button(null, "number", 0, display.getText(), 0));
                }

                expArray.addLast(button);

                display.setText("");
                displayExpression(button);

                return;
            case "openBrack":
                if (!display.getText().isEmpty()) {
                    return;
                }
                displayExpression(button);

                expArray.addLast(button);
                brackState++;
                return;
            case "point":
                if (!expArray.isEmpty()) {
                    if ("closeBrack".contains(expArray.peekLast().name)) {
                        return;
                    }
                }

                if (display.getText()
                        .length() == 0) {
                    display.setText("0.");
                }

                if (!(display.getText()
                        .contains("."))) {
                    if (display.getText().length() < 42) {
                        display.setText(display.getText() + button.display);
                    }
                }

                return;
            case "sqr":
            case "fact":
                if (display.getText().isEmpty()) {
                    return;
                } else {
                    if(!expArray.isEmpty() && expArray.peekLast().isUnaryOperation())
                        return;
                    expString.append(display.getText()).append(button.display);
                }
                expArray.addLast(button);
                expArray.addLast(new Button(null, "number", 0, display.getText(), 0));
                display.setText("");
                if (expString.length() > 64) {
                    expression.setText("..." + expString.substring(expString.length() - 60, expString.length()));
                } else {
                    expression.setText(expString.toString());
                }
                return;
        }
        if (button.isUnaryOperation()) {
            if (!(display.getText().length() == 0)) {
                return;
            }
            if (!expArray.isEmpty()) {
                if ("closeBracksqr".contains(expArray.peekLast().name)) {
                    return;
                }
                if (expArray.peekLast().isUnaryOperation()) {
                    int len = expArray.peekLast().display.length();
                    expArray.pollLast();
                    expString.delete(expString.length() - len, expString.length());
                }
            }

            displayExpression(button);
            display.setText("");
            expArray.addLast(button);
        } else if (button.isBinaryOperation()) {
            if (display.getText().length() == 0) {
                if (!expArray.isEmpty()) {
                    if (!("closeBrack".contains(expArray.peekLast().name))) {
                        if (expArray.peekLast().isBinaryOperation()) {
                            int len = expArray.peekLast().display.length();
                            expArray.pollLast();
                            expString.delete(expString.length() - len, expString.length());
                        } else if (!(expArray.peekLast().isNumber())) {
                            return;
                        }
                    }
                } else {
                    return;
                }
            }

            if (display.getText().length() > 0) {
                expArray.addLast(new Button(null, "number", 0, display.getText(), 0));
            }
            expString.append(display.getText());
            expArray.addLast(button);
            display.setText("");
            displayExpression(button);
        }

        if (button.isNumber()) {
            if (!expArray.isEmpty() && expArray.peekLast().isNumber()) {
                return;
            }
            if (!expArray.isEmpty()) {
                if ("closeBrack".contains(expArray.peekLast().name)) {
                    return;
                }
            }
            if (display.getText().compareTo("0") == 0) {
                display.setText("");
            }

            if (decimal(display.getText()) == 0) {
                if (display.getText().length() < 18) {
                    display.setText(display.getText() + button.display);
                }
            } else if (display.getText().length() < 42) {
                display.setText(display.getText() + button.display);
            }
        }
    }

    private void delete() {
        if (expression.getText().isEmpty() && display.getText().isEmpty()) {
            return;
        }
        if (!display.getText().isEmpty()) {
            display.setText(display.getText().substring(0, display.getText().length() - 1));
        } else {
            if (expArray.peekLast().isNumber()) {
                display.setText(expArray.pollLast().display);
                int len = display.getText().length();
                if (!expArray.isEmpty() && expArray.peekLast().isUnaryOperation()
                        && "sqrfact".contains(expArray.peekLast().name)) {
                    len += expArray.pollLast().display.length();
                }
                expString.delete(expString.length() - len, expString.length());
                displayExpression();
            } else if (expArray.peekLast().isUnaryOperation() || expArray.peekLast().isBinaryOperation()) {
                int len = expArray.peekLast().display.length();
                expArray.pollLast();
                expString.delete(expString.length() - len, expString.length());
                displayExpression();
            }
        }
    }
}
