import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import simplecalculator.SimpleCalculator;

/**
 * This (public) class represents the entry point of the application.
 * 
 * @author Lukas Kudela (lukas[DOT]kudela[AT]gmail[DOT]com)
 */
public class Main
{
    /** The invalid expression error string. */
    static final String ERROR = "CHYBA";
    
    /**
     * The entry point of the application.
     * 
     * @param args The command line arguments.
     */
    public static void main( String[] args )
    {   
        // PREREQUISITES ======================================================
        
        // The input stream.
        InputStream inputStream = System.in;
        // Open the input stream reader on the input stream.
        InputStreamReader inputStreamReader = new InputStreamReader( inputStream );
        // Open the buffered reader on the input stream reader.
        BufferedReader bufferedReader = new BufferedReader( inputStreamReader );

        // The print stream.
        PrintStream printStream = System.out;
        
        // Create the calculator.
        SimpleCalculator simpleCalculator = new SimpleCalculator();

        // APPLICATION LOGIC ===================================================
        
        try
        {
            // Read the expression from the input.
            // May throw an IOException.
            String expression;
            while ((expression = bufferedReader.readLine()) != null)
            {
                // Evaluate the expression.
                // May throw an IllegalArgumentException.
                double value = simpleCalculator.evaluate( expression );
         
                // Print the result to the output.
                printStream.println( value );
            }
        }
        catch (IllegalArgumentException ex)
        {
            printStream.println( ERROR );         
        }
        catch (IOException ex)
        {
            System.err.println( ex.getMessage() );
        }

        // POSTREQUISITES ======================================================
        
        // Close the buffeered reader and the input stream reader.
        try
        {
            bufferedReader.close();
            inputStreamReader.close();
        }
        catch (IOException ex)
        {
            System.err.println( ex.getMessage() );
        }
    }
}
