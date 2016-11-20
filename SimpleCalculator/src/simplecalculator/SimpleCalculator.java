package simplecalculator;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;

/**
 * This (public) class represents a simple calculator.
 * 
 * @author Lukas Kudela (lukas[DOT]kudela[AT]gmail[DOT]com)
 */
public class SimpleCalculator
{
    // Private instance fields
    
    /** The variables of the calculator. */
    private Hashtable< Character, Double > variables;
    
    // Private static fields
    
    private static final String OPERATORS = "+-*/";
    private static final String TOKEN_DELIMITER_REGEX = "[ \t]+";
    
    // Public instance constructors
    
    /**
     * Creates a new simple calculator.
     */
    public SimpleCalculator()
    {
        variables = new Hashtable< Character, Double >();
    }
    
    // Public instance methods
    
    // =========================================================================
    // EVALUATE
    // =========================================================================
    
    /**
     * Evaluates the expression.
     * 
     * @param expression the expression.
     
     * @return the value of the expression.
     * 
     * @throws java.lang.IllegalArgumentException if the expression is illegal.
     */
    public double evaluate( String expression )
        throws IllegalArgumentException
    {
        // The expression is split on the equality sign to determine its nature.
        String[] sides = expression.split( "=" );
        int equalitySignsCount = sides.length - 1;
        
        double result;
        switch (equalitySignsCount)
        {
            // The expression contains no equality sign, hence it is a simple expression.
            case 0:
                
                // Evaluate the expression as a simple expression.
                result = evaluateInfix( expression );
                
                break;
                
            // The expression contains one equality sign, hence it is an assignment expression.
            case 1:
                
                // Split the expression into the left-hand side and the right-hand side.
                String leftHandSide = sides[ 0 ];
                String rightHandSide = sides[ 1 ];
                
                // Evaluate the right-hand side (giving the result of the expression).
                result = evaluateInfix( rightHandSide );
                
                // Assign the result to the left-hand side.
                assign( leftHandSide, result );
                
                break;
                
            // The expression contains two or more equality signs, hence it is an illegal expression.
            default:
                
                // Throw the exception notifying the caller, that an illegal expression
                // has been passed as an argument.
                throw new IllegalArgumentException();
        }
        
        return result;
    }
    
    // =========================================================================
    // EVALUATE INFIX
    // =========================================================================
    
    /**
     * Evaluates the (tokenized) expression in the infix notation.
     * 
     * @param infixExpression the (tokenized) expression in the infix notation.
     * 
     * @return the value of the expression.
     * 
     * @throws java.lang.IllegalArgumentException if the expression is illegal.
     */
    private double evaluateInfix( String[] tokenizedInfixExpression )
        throws IllegalArgumentException
    {
        String[] tokenizedPostfixExpression = convertInfixToPostfix( tokenizedInfixExpression );
        return evaluatePostfix( tokenizedPostfixExpression );
    }
    
    /**
     * Evaluates the expression in the infix notation.
     * 
     * @param infixExpression the expression in the infix notation.
     * 
     * @return the value of the expression.
     * 
     * @throws java.lang.IllegalArgumentException if the expression is illegal.
     */
    private double evaluateInfix( String infixExpression )
        throws IllegalArgumentException
    {
        String[] tokenizedPostfixExpression = convertInfixToPostfix( infixExpression );
        return evaluatePostfix( tokenizedPostfixExpression );
    }
    
    // =========================================================================
    // CONVERT INFIX TO POSTFIX
    // =========================================================================
    
    /**
     * Converts the (tokenized) expression in the infix notation into the expression in the postfix notation.
     * 
     * @param tokenizedInfixExpression the (tokenized) expression in the infix notation.
     * 
     * @return the expression in the postfix notation.
     * 
     * @throws java.lang.IllegalArgumentException if the expression is illegal.
     */
    private String[] convertInfixToPostfix( String[] tokenizedInfixExpression )
        throws IllegalArgumentException
    {        
        Stack< String > tokenStack = new Stack< String >();
        
        ArrayList< String > tokenizedPostfixExpression = new ArrayList< String >();

        for (String token : tokenizedInfixExpression)
        {
            if (isOperand( token ))
            {
                // Token is an operand.
                tokenizedPostfixExpression.add( token );
            }
            else if (token.equals( "(" ))
            {
                // Token is a left parenthesis.
                tokenStack.push( token );
            }
            else if (token.equals( ")" ))
            {
                // Token is a right parenthesis.
                boolean openingParenthesisFound = false;
                while (!tokenStack.isEmpty())
                {
                    token = tokenStack.pop();
                    if (token.equals( "(" ))
                    {
                        openingParenthesisFound = true;
                        break;
                    }
                    tokenizedPostfixExpression.add( token );
                }
                
                // If the stack is already empty before finding a "(", that expression is not a valid expression.
                if (!openingParenthesisFound)
                {
                    throw new IllegalArgumentException();
                }
            }
            else if (isOperator( token ))
            {
                // Token is an operator.
                while (!tokenStack.isEmpty())
                {
                    String topToken = tokenStack.peek();
                    
                    if (!topToken.equals( "(" ) && isOperator( topToken ) && higherOrEqualPrecedence( topToken, token ))
                    {
                        tokenizedPostfixExpression.add( tokenStack.pop() );
                    }
                    else
                    {
                        break;
                    }
                }
                tokenStack.push( token );
            }
            else
            {
                // Token is illegal.
                throw new IllegalArgumentException();
            }
        }
        
        while (!tokenStack.isEmpty())
        {
            String token = tokenStack.pop();
            tokenizedPostfixExpression.add( token );
        }
        
        String[] a = new String[ 0 ];
        return tokenizedPostfixExpression.toArray( a );
    }
    
    /**
     * Converts the expression in the infix notation into the expression in the postfix notation.
     * 
     * @param infixExpression the expression in the infix notation.
     * 
     * @return the expression in the postfix notation.
     * 
     * @throws java.lang.IllegalArgumentException if the expression is illegal.
     */
    private String[] convertInfixToPostfix( String infixExpression )
        throws IllegalArgumentException
    {
        // Tokenize (trim and split) the expression.
        String[] tokenizedInfixExpression = tokenizeExpression( infixExpression );
        
        // Evaluate the tokenized expression.
        return convertInfixToPostfix( tokenizedInfixExpression );
    }
    
    /**
     * Tokenizes the expression.
     * 
     * @param expression the expression.
     * 
     * @return the tokenized expression.
     */
    private String[] tokenizeExpression( String expression )
    {
        // Surround all the parentheses with a leading and a trailing sapce character.
        expression = expression.replaceAll( "[(]", " ( " );
        expression = expression.replaceAll( "[)]", " ) " );
        
        // Trim the expression.
        expression = expression.trim();
        
        // Split the expression into the tokens.
        String[] tokens = expression.split( TOKEN_DELIMITER_REGEX );
        
        return tokens;
    }
    
    /**
     * Determines whether an operator has higher or greater precedence than another operator.
     * 
     * @param operator1 the first operator.
     * @param operator2 the second operator.
     * 
     * @return <c>true</c> if the first operator has higher or equal precedence than the second operator, <c>false</c> otherwise.
     */
    private boolean higherOrEqualPrecedence( String operator1, String operator2 )
    {
        return (getPrecedence( operator1 ) >= getPrecedence( operator2 ));
    }
    
    /**
     * Gets the precedence of an operator.
     * Accepts only valid operators (+, -, *, /).
     * 
     * @param operator the operator.
     * 
     * @return the precedence of the operator.
     * 
     * @throws java.lang.IllegalArgumentException if the operator is illegal.
     */
    private int getPrecedence( String operator )
        throws IllegalArgumentException
    {
        char ch = operator.charAt( 0 );
        
        switch (ch)
        {
            case '+':
            case '-':
                
                return 0;
                
            case '*':
            case '/':
                
                return 1;
                
            default:
                
                throw new IllegalArgumentException();
        }
    }
    
    // =========================================================================
    // EVALUATE POSTFIX
    // =========================================================================
    
    /**
     * Evaluates the (tokenized) expression in the postfix notation.
     * 
     * @param tokenizedPostfixExpression the (tokenized) expression in the postfix notation.
     * 
     * @return the value of the expression.
     * 
     * @throws java.lang.IllegalArgumentException if the expression is illegal.
     */
    private double evaluatePostfix( String[] tokenizedPostfixExpression )
        throws IllegalArgumentException
    {
        Stack< Double > tokenStack = new Stack< Double >();
        
        for (String token : tokenizedPostfixExpression)
        {
            if (isOperand( token ))
            {
                // The token is an operand (number or variable).
                
                // Get the value of the operand.
                double value = getOperandValue( token );
                
                // Push the value of the operand onto the stack.
                tokenStack.push( value );
            }
            else if (isOperator( token ))
            {
                // The token is an operator.
                
                // Get the operands of the operator.
                double rightOperand = tokenStack.pop();
                double leftOperand = tokenStack.pop();
                
                // The value of the operator applied to its operands.
                double value;
                
                char operator = token.charAt( 0 );
                switch (operator)
                {
                    // Addition.
                    case '+':
                        
                        value = leftOperand + rightOperand;
                        break;
                    
                    // Subtraction.
                    case '-':
                        
                        value = leftOperand - rightOperand;
                        break;
                    
                    // Multiplication.
                    case '*':
                        
                        value = leftOperand * rightOperand;
                        break;
                    
                    // Division.
                    case '/':
                        
                        value = leftOperand / rightOperand;
                        break;
                    
                    // Illegal operand => Illegal argument.
                    default:
                        throw new IllegalArgumentException();
                }
                
                // Push the value of the operator onto the stack.
                tokenStack.push( value );
            }
            else
            {
                // Token is not an operand nor an operator, hence the postfix expression is invalid.
                throw new IllegalArgumentException();
            }
        }
        
        return tokenStack.pop();
    }
    
    
    /**
     * Evaluates the expression in the postfix notation.
     * 
     * @param postfixExpression the expression in the postfix notation.
     * 
     * @return the value of the expression.
     * 
     * @throws java.lang.IllegalArgumentException if the expression is illegal.
     */
    private double evaluatePostfix( String postfixExpression )
    {
        // Tokenize (trim and split) the expression.
        String[] tokenizedPostfixExpression = tokenizeExpression( postfixExpression );
        
        // Evaluate the tokenized expression.
        return evaluatePostfix( tokenizedPostfixExpression );
    }
    
    /**
     * Determine whether a string is an operand (number or variable).
     * 
     * @param str the (trimmed) string.
     * 
     * @return <c>true</c> if the string is an operand, <c>false</c> otherwise.
     */
    private boolean isOperand( String str )
    {
        return (isNumber( str ) || isVariable( str )); 
    }
    
    /**
     * Determines whether a string is a number.
     * 
     * @param str the (trimmed) string.
     * 
     * @return <c>true</c> if the string is a double, <c>false</c> otherwise.
     */
    private boolean isNumber( String str )
    {
        try
        {
            Double.parseDouble( str );
        }
        catch (NumberFormatException ex)
        {
            return false;
        }
        
        return true;
    }
    
    /**
     * Determines whether the string is a variable.
     * 
     * @param str the (trimmed) string.
     * 
     * @return <c>true</c> if the string is a variable, <c>false</c> otherwise.
     */
    private boolean isVariable( String str )
    {
        // If the string does not contain exactly one character, it is not a variable.
        if (str.length() != 1)
        {
            return false;
        }
        
        // Get the single char.
        char ch = str.charAt( 0 );
        
        // If the char is not a letter, or if it is not lower-case, it is not a variable.
        if (!Character.isLetter( ch ) || !Character.isLowerCase( ch ))
        {
            return false;
        }
        
        return true;
    }
    
    /**
     * Gets the value of the operand.
     * 
     * @param operand the (trimmed) operand (number or variable).
     * 
     * @return the value of the operand.
     */
    private double getOperandValue( String operand )
    {
        double value;
        
        try
        {
            // Operand is either a legal number (parsable double) ...
            value = Double.parseDouble( operand );
        }
        catch (NumberFormatException ex)
        {
            // ... or a legal variable.
            char variable = operand.charAt( 0 );
            value = (variables.containsKey( variable )) ? variables.get( variable ) : 0;
        }
        
        return value; 
    }
    
    /**
     * Determine whether the string is an operator (+, -, *, /).
     * 
     * @param str the (trimmed) string.
     * 
     * @return <c>true</c> if the string is an operator, <c>false</c> otherwise.
     */
    private boolean isOperator( String str )
    {
        // If the string does not contain exactly one character, it is not an operator.
        if (str.length() != 1)
        {
            return false;
        }
        
        // Get the single character.
        char ch = str.charAt( 0 );
        
        // Is the character an actual operator (+, -, *, /)?
        return (OPERATORS.indexOf( ch ) >= 0) ? true : false;
    }
    
    // =========================================================================
    // ASSIGN
    // =========================================================================
    
    /**
     * Assigns the result of the right-hand side into the left-hand side.
     * 
     * @param leftHandSide the left-hand side.
     * @param value the value of the right-hand side.
     * 
     * @throws java.lang.IllegalArgumentException if the left-hand side is illegal.
     */
    private void assign( String leftHandSide, double value )
        throws IllegalArgumentException
    {   
        // Trim the left-hand side.
        leftHandSide = leftHandSide.trim();
        
        // If the left-hand side is a variable, assign to it, otherwise throw the illegal argument exception.
        if (isVariable( leftHandSide ))
        {
            // Store the variable's name and value into the variables table.
            char variable = leftHandSide.charAt( 0 );
            variables.put( variable, value );
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }
}
