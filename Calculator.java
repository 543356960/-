import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Calculator {
    /**
     * 获得操作符权值
     * @param c 操作符
     * @return  返回操作符权值
     */
    public int getPriorityValue(char c){
        return switch (c) {
            case '+', '-' -> 1;
            case '*', '/' -> 2;
            case '^' -> 3;
            default -> 0;
        };
    }

    /**
     * 将byte流分割成整数和小数的部分合成成double类型
     * @param integer 整数部分
     * @param decimal 小数部分
     * @return  返回实际双精度数
     */
    public Double numToDouble(List<Character> integer, List<Character> decimal){
        char [] a = new char[integer.size()];
        char [] b = new char[decimal.size()];
        for (int i = 0; i < integer.size(); i++) {
            a[i] = integer.get(i);
        }
        for (int i = 0; i < decimal.size(); i++) {
            b[i] = decimal.get(i);
        }
        String vol = new String(a) + "." + new String(b);
        return Double.valueOf(vol);
    }

    /**
     * 将中缀表达式转化为后缀表达式
     * @param bytes 输入byte流
     * @return 返回一个后缀表达式List
     */
    public List<Object> transform(byte[] bytes){
        /*转换过程需要用到栈，具体过程如下：
        1）如果遇到操作数，我们就直接将其输出。
        2）如果遇到操作符，则我们将其放入到栈中，遇到左括号时我们也将其放入栈中。
        3）如果遇到一个右括号，则将栈元素弹出，将弹出的操作符输出直到遇到左括号为止。注意，左括号只弹出并不输出。
        4）如果遇到任何其他的操作符，如（“+”， “*”，“（”）等，从栈中弹出元素直到遇到发现更低优先级的元素(或者栈为空)为止。弹出完这些元素后，才将遇到的操作符压入到栈中。有一点需要注意，只有在遇到" ) "的情况下我们才弹出" ( "，其他情况我们都不会弹出" ( "。
        5）如果我们读到了输入的末尾，则将栈中所有元素依次弹出。*/
        List<Character> integer = null;  // 整数
        List<Character> decimal = null;  // 小数
        List<Character> pointer = null;  // 指针
        List<Object> suffix = new ArrayList<>();   // 后缀表达式
        Stack<Character> operator = new Stack<>();
        for (byte b : bytes){
            {
                // 忽略空格
                if (b == ' '){
                    continue;
                }
                // 转化小数
                if (b == '.'){
                    pointer = decimal = new ArrayList<>();
                } else if (b >= '0' && b <= '9') {
                    if (integer == null) {
                        pointer = integer = new ArrayList<>();
                    }
                    pointer.add((char) b);
                } else {
                    /*获得操作符(数字)*/
                    if(integer!=null) {
                        if (decimal == null){
                            decimal = new ArrayList<>();
                            decimal.add('0');
                        }
                        suffix.add(numToDouble(integer, decimal)); // 操作数直接输出
                        integer = null;
                        decimal = null;
                    }

                    if (b == '+' || b == '-' || b == '*' || b == '/' || b == '^' || b == '(' || b == ')'){
                        while (true){
                            if (b == '(' ||operator.empty() || getPriorityValue((char)b) > getPriorityValue(operator.peek())) {
                                operator.push((char) b);
                                break;
                            }
                            if (b == ')' && operator.peek()=='('){
                                operator.pop();
                                break;
                            }
                            if (getPriorityValue((char)b) <= getPriorityValue(operator.peek())){
                                suffix.add(operator.pop());
                            }
                        }
                    } else if (b == '='){
                        while (!operator.empty()){
                            suffix.add(operator.pop());
                        }
                        return suffix;
                    } else {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 计算后缀表达式
     * @param expression 后缀表达式
     * @return 返回结果
     */
    public double calculationExpression(List<Object> expression) throws NumberFormatException{
        Stack<Object> expressionStack = new Stack<>();
        for (Object o : expression){
            if (o.getClass() == Double.class){
                expressionStack.push(o);
            } else if (o.getClass() == Character.class){
                char operator = (char)o;
                Double b = (Double) expressionStack.pop();
                Double a = (Double) expressionStack.pop();
                switch (operator) {
                    case '+' -> expressionStack.push(a + b);
                    case '-' -> expressionStack.push(a - b);
                    case '*' -> expressionStack.push(a * b);
                    case '/' -> {
                        if (b == 0) {
                            throw new NumberFormatException("除数不能为0!");
                        }
                        expressionStack.push(a / b);
                    }
                    case '^' -> expressionStack.push(Math.pow(a, b));
                }
            }
        }
        if (!expression.isEmpty()) {
            return (Double) expressionStack.pop();
        }
        return 0.0;
    }
    public static void main(String[] args) {
        if (args.length == 1){
            byte[] bytes = (args[0] + "=").getBytes();
            Calculator calculator = new Calculator();
            List<Object> transform = calculator.transform(bytes);
            try{
                System.out.println(calculator.calculationExpression(transform));
            } catch (NumberFormatException e){
                System.out.println(e.getLocalizedMessage());
            }
            catch (Exception e){
                System.out.println("表达式错误!");
            }

        }
    }
}
