package stylechecker;
import java.io.*;
import java.util.*;

/**
 * The StyleChecker class is a tool to help programmers adhere to common coding 
 * standards. This program checks for common style mistakes in Java programs.
 * 
 * @author jeffrowell
 */
public class StyleChecker
{

    private final static String INPUT_FILE = "input/trevor_blank_lines.txt";
    private final static String OUTPUT_FILE = "output/Fixed_Style_Output.txt";
    private final static int COMMON_INPUT_SIZE = 1000;
    private static ArrayList<String> inputDataLines = new ArrayList<>
                                                            (COMMON_INPUT_SIZE);

    
    /**
     * Opens the file containing the program to check for style errors, then
     * generates a report based on the errors found, if any.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        String outputFile = OUTPUT_FILE;
        File outputDataFile = null;
        PrintWriter output = null;
        
        try
        {
            outputDataFile = new File(outputFile);
            output = new PrintWriter(outputDataFile);
        }
        
        catch (FileNotFoundException ex) 
        {
            System.err.println("ERROR: " + OUTPUT_FILE + " not found");
            System.exit(0);
        }
        
        openInputFile();
        generateReport(output);
    }
    
    /**
     * Opens the input file if found, otherwise outputs an error message. Reads
     * each line of input from the input file and adds each line to the
     * inputDataLines list.
     */
    private static void openInputFile()
    {
        Scanner inputScanner;
        File inputFile = null;
        
        try 
        {
            inputFile = new File(INPUT_FILE);
            inputScanner = new Scanner(inputFile);
            
            while (inputScanner.hasNext())
            {
                inputDataLines.add(inputScanner.nextLine());
            }
        }
        
        catch(FileNotFoundException ex) 
        {
            System.err.println("ERROR: " + INPUT_FILE + " not found");
            System.exit(0);
        }
    }
    
    
    /**
     * Given a program, finds every line of code in the program that should be 
     * indented by three spaces but is not, and adds each line number with this 
     * style error to a list. We assume that lines will not be indented too 
     * much, and over indentation errors will not be caught.
     * 
     * @param lineList  An ArrayList of the input file lines
     * @return indentErrorLines An ArrayList containing the line numbers of the 
     *                          program that contain the block indentation 
     *                          coding style error.
     */
    private static ArrayList<Integer> findIndentError(ArrayList<String>     
                                                      lineList) 
    {
        int openBraceCount = 0;
        int closeBraceCount = 0;
        int requiredWhitespace = 0;
        int j = 0;
        final int EXPECTED_SPACE = 3;
        ArrayList<Integer> indentErrorLines = new 
                                              ArrayList<>(COMMON_INPUT_SIZE);
        
        while (j < inputDataLines.size() - 1) 
        {
            String currentLine = lineList.get(j).trim();
            String nextLine = lineList.get(j + 1);
            boolean specialCase = (currentLine.startsWith("for") 
                               || currentLine.startsWith("if")
                               || currentLine.startsWith("else") 
                               || currentLine.startsWith("while")
                               || currentLine.startsWith("do ") 
                               || currentLine.startsWith("switch"));
            
            if (currentLine.contains("{") && !isBetweenQuotes(currentLine, '{')) 
            {
                openBraceCount++;
                
                if (openBraceCount > 1)
                {
                    requiredWhitespace += EXPECTED_SPACE;
                }
            }
            
            if (nextLine.contains("}") 
                && !isBetweenQuotes(nextLine.trim(), '}')) 
            {
                closeBraceCount++;
                requiredWhitespace -= EXPECTED_SPACE;
            }
            
            if (specialCase && !currentLine.contains("{") 
                && !nextLine.contains("{") 
                && nextLine.length() > requiredWhitespace + 5)
            {
                if (openBraceCount > 0 && (openBraceCount - closeBraceCount) > 0
                    && !(Character.isWhitespace(nextLine.charAt(
                                                        requiredWhitespace + 3))
                    && Character.isWhitespace(nextLine.charAt(  
                                                        requiredWhitespace + 4))
                    && Character.isWhitespace(nextLine.charAt(
                                                      requiredWhitespace + 5))))
                {
                    indentErrorLines.add(j + 2);
                } 
            }
            if (nextLine.length() > requiredWhitespace + 2) 
            {
                if ((openBraceCount - closeBraceCount) > 0
                        && !(Character.isWhitespace(nextLine.charAt(
                                                      requiredWhitespace))
                        && Character.isWhitespace(nextLine.charAt(  
                                                      requiredWhitespace + 1))
                        && Character.isWhitespace(nextLine.charAt(
                                                      requiredWhitespace + 2))))
                {
                    indentErrorLines.add(j + 2);
                }
            }
            
            j++;
        }
        return indentErrorLines;
    }
    
    /**
     * Given a program, finds every line of code in the program that contains
     * a line of code where there needs to be curly braces but is not, and 
     * adds each line number with this style error to a list. We assume that
     * there are no conditionals and loops on the same line.
     * 
     * @param lineList  An ArrayList of the input file lines
     * @return optionalBraceErrorLines  An ArrayList containing the line numbers
     *                                  of the program that contain the optional
     *                                  curly braces error.
     */
    private static ArrayList<Integer> findOptionalBraceError(ArrayList<String> 
                                                            lineList)
    {
       ArrayList<Integer> optionalBraceErrorLines = new ArrayList<>
                                                            (COMMON_INPUT_SIZE);
       
       for (int i = 0; i < lineList.size() - 1; i++)
       {
           String currentLine = lineList.get(i).trim();
           String nextLine = lineList.get(i + 1);
           boolean noBraceFlag = !currentLine.contains("{") 
                                  && !nextLine.contains("{");
           
           if (currentLine.startsWith("if") && noBraceFlag)
           {
               optionalBraceErrorLines.add(i + 1);
           }
           
           if (currentLine.startsWith("else") && noBraceFlag)
           {
               optionalBraceErrorLines.add(i + 1);
           }
           
           if (currentLine.startsWith("while") && noBraceFlag)
           {
               optionalBraceErrorLines.add(i + 1);
           }
           
           if (currentLine.startsWith("for") && noBraceFlag)
           {
               optionalBraceErrorLines.add(i + 1);
           }
           
           if (currentLine.startsWith("switch") && noBraceFlag)
           {
               optionalBraceErrorLines.add(i + 1);
           }
           
           if (currentLine.startsWith("do ") && noBraceFlag)
           {
               optionalBraceErrorLines.add(i + 1);
           }
       }
       return optionalBraceErrorLines;
    }
    
    /**
     * Given an array of character of a line from inputDataLines, counts the 
     * number of appropriate spaces needed to pad the fixed open and close
     * brace.
     * 
     * @param lineArray    An array of chars from inputDataLines
     * @return count       The number of appropriate spaces required
     */
    private static int countSpacing(char[] lineArray)
    {
       int count = 0;
       
       for (int k = 0; k < lineArray.length; k++) 
       {
          if (lineArray[k] != ' ') 
          {
             count = k;
             break;
          }
       }

       return count;
    }
    
    /**
     * Given an index number where the '{' should appear in a line, returns a
     * string with the appropriate spacing and the '{'.
     * 
     * @param braceIndex   The index of the string where the '{' should occur
     * @return fixedOpen   The corrected string with the '{' and appropriate
     *                     spacing
     */
    private static String fixOpenBrace(int braceIndex)
    {
       String fixedOpen = "";

       while (fixedOpen.length() != braceIndex) 
       {
          fixedOpen += " ";
       }
       
       fixedOpen += "{";
       return fixedOpen;
    }
    
    /**
     * Given an index number where the '}' should appear in a line, returns a
     * string with the appropriate spacing and the '}'.
     * 
     * @param braceIndex   The index of the string where the '}' should occur
     * @return fixedClose  The corrected string with the '}' and appropriate
     *                     spacing
     */
    private static String fixCloseBrace(int braceIndex)
    {
       String fixedClose = "";

       while (fixedClose.length() != braceIndex) 
       {
          fixedClose += " ";
       }
       
       fixedClose += "}";
       return fixedClose;
    }
    
    /**
     * Fixes a program that contains optional brace errors by adding open and 
     * close braces where they belong. We assume we will not see more than 2
     * if/else/for/while/do statements whose only line is another if/else/while/
     * for/do statement.
     * 
     * @param lineList  The lines of the program with errors
     * @param output    The text output stream to a specified output file
     */
    private static void fixOptionalBraceError(ArrayList<String> lineList, 
                                              PrintWriter output)
    {
        ArrayList<Integer> braceErrorLines = findOptionalBraceError(lineList);
        int lineNumber;
        int offset = 0;
        
        for (int i = 0; i < braceErrorLines.size(); i++)
        {
            lineNumber = braceErrorLines.get(i) + offset;
            String currentLine = lineList.get(lineNumber - 1);
            char[] lineArray = currentLine.toCharArray();
            int braceIndex = countSpacing(lineArray);
            
            // Pads appropriate whitespace before concatenating the brace
            String fixedOpen = fixOpenBrace(braceIndex);
            String fixedClose = fixCloseBrace(braceIndex);
            
            // If there are nested loops, conditionals
            if (braceErrorLines.contains(lineNumber - offset) 
                && braceErrorLines.contains(lineNumber - offset + 1))
            {
                currentLine = lineList.get(lineNumber);
                lineArray = currentLine.toCharArray();
                lineList.add(lineNumber, fixedOpen);
                lineList.add(lineNumber + 3, fixedClose);
                braceIndex = countSpacing(lineArray);
                
                fixedOpen = fixOpenBrace(braceIndex);
                fixedClose = fixCloseBrace(braceIndex);
                
                lineList.add(lineNumber + 2, fixedOpen);
                lineList.add(lineNumber + 4, fixedClose);
                offset += 4; // the number of lines added by fixing braces
                i++; // skip the next line with nested error
            }
            else
            {
                lineList.add(lineNumber, fixedOpen);
                lineList.add(lineNumber + 2, fixedClose);
                offset += 2; // the number of lines added by fixing braces'
            }
        }
        
        output.println("\nOptional Curly Brace Correction(s):");
        output.println("=====================================");
        for (String line: lineList)
        {
            output.println(line);
        }
    }
    
    /**
     * Given a program, finds every line of code in the program that contains
     * a line segment where there needs to be spaces before and after a binary
     * operator but is not, and adds each line number with this style error to 
     * a list.
     * 
     * @param lineList  An ArrayList of the input file lines
     * @return operatorSpaceErrorLines  An ArrayList containing the line numbers
     *                                  of the program that contain the binary
     *                                  operator space error.
     */
    private static ArrayList<Integer> findOperatorSpaceError(ArrayList<String> 
                                                            lineList)
    {
        ArrayList<Integer> operatorSpaceErrorLines = new ArrayList<>
                                                            (COMMON_INPUT_SIZE);
        
        for (int i = 0; i < lineList.size() - 1; i++)
        {
            String currentLine = lineList.get(i);
            
            if (currentLine.contains("+"))
            {
                if (noOperatorSpace(currentLine, "+"))
                {
                    operatorSpaceErrorLines.add(i + 1);
                }
            }
            
            if (currentLine.contains("-"))
            {
                if (noOperatorSpace(currentLine, "-"))
                {
                    operatorSpaceErrorLines.add(i + 1);
                }
            }
            
            if (currentLine.contains("*") && currentLine.length() > 3
                && !currentLine.trim().startsWith("import"))
            {
                if (noOperatorSpace(currentLine, "*"))
                {
                    operatorSpaceErrorLines.add(i + 1);
                }
            }
            
            if (currentLine.contains("/") && currentLine.length() > 3)
            {
                if (noOperatorSpace(currentLine, "/"))
                {
                    operatorSpaceErrorLines.add(i + 1);
                }
            }
            
            if (currentLine.contains("="))
            {
                if (noOperatorSpace(currentLine, "="))
                {
                    operatorSpaceErrorLines.add(i + 1);
                }
            }
            
            if (currentLine.contains("%"))
            {
                if (noOperatorSpace(currentLine, "%"))
                        {
                         operatorSpaceErrorLines.add(i + 1);   
                        }
            }
        }
        return operatorSpaceErrorLines;   
    }
    
    /**
     * Given a line that is being processed and a specific binary operator 
     * character from that line, returns true if there is not a whitespace 
     * character before and after the specified binary operator, false 
     * otherwise.
     * 
     * @param currentLine   The currentLine being processed from inputDataLines
     * @param character     The specified binary operator from the current line
     */
    private static boolean noOperatorSpace(String currentLine, String character)
    {
        char[] lineArray = currentLine.toCharArray();
        int operatorIndex = currentLine.indexOf(character);
        char onceBefore = '\0';
        char onceAfter = lineArray[operatorIndex + 1];
        char twiceBefore = '\0';
        char twiceAfter = lineArray[operatorIndex + 2];
        
        if (operatorIndex > 1)
        {
            onceBefore = lineArray[operatorIndex - 1];
            twiceBefore = lineArray[operatorIndex - 2];
        }
                
        if (character.equals("+"))
        {
            if (onceBefore == '+' || onceAfter == '+' )
            {
                return false;
            }
        }
        
        if (character.equals("-"))
        {
            if (onceBefore == '-' || onceAfter == '-')
            {
                return false;
            }
        }
        
        if (character.equals("="))
        {
            if ( !((onceBefore == '<') || (onceBefore == '>') 
                    || (onceBefore == '!') || (onceBefore == '=') 
                    || (onceAfter == '=')) 
                    && (!Character.isWhitespace(onceBefore)
                    || !Character.isWhitespace(onceAfter)))
            {
                return true;
            }
        }
        
        else if (!Character.isWhitespace(onceBefore)
                 || !Character.isWhitespace(onceAfter) 
                 || Character.isWhitespace(twiceBefore)
                 || Character.isWhitespace(twiceAfter))
        {
            return true;
        }
        return false;
    }
    
    /**
     * Given a program, finds every line of code in the program that contains
     * a line segment where the open or close curly brace should be on a new 
     * line but is not, and adds each line number with this style error to a 
     * list. We will assume that braces will be on the same line, and errors 
     * where there are one or more blank lines before the brace will not be 
     * considered.
     * 
     * @param lineList  An ArrayList of the input file lines
     * @return braceAlignmentErrorLines  An ArrayList containing the line numbers
     *                                  of the program that contain the curly
     *                                  brace on the same line error.
     */
    private static ArrayList<Integer> findBraceAlignmentError(ArrayList<String> 
                                                             lineList)
    {
        ArrayList<Integer> braceAlignmentErrorLines = new ArrayList<>
                                                            (COMMON_INPUT_SIZE);
        
        for (int i = 0; i < lineList.size() - 1; i++)
        {
            String currentLine = lineList.get(i).trim();
            boolean onSameLine = currentLine.endsWith("{");
            
            if ( (currentLine.contains("class") 
                  || currentLine.contains("public") 
                  || currentLine.contains("private")) && onSameLine)
            {
                braceAlignmentErrorLines.add(i + 1);
            }
            
            if (currentLine.startsWith("if") && onSameLine)
            {
                braceAlignmentErrorLines.add(i + 1);
            }
            
            if (currentLine.startsWith("else") && onSameLine)
            {
                braceAlignmentErrorLines.add(i + 1);
            }
            
            if (currentLine.startsWith("for") && onSameLine)
            {
                braceAlignmentErrorLines.add(i + 1);
            }
            
            if (currentLine.startsWith("while") && onSameLine)
            {
                braceAlignmentErrorLines.add(i + 1);
            }
            
            if (currentLine.startsWith("switch") && onSameLine)
            {
                braceAlignmentErrorLines.add(i + 1);
            }
            
            if (currentLine.startsWith("do ") && onSameLine)
            {
                braceAlignmentErrorLines.add(i + 1);
            }
            
            if (currentLine.endsWith("}") && currentLine.length() > 1)
            {
                braceAlignmentErrorLines.add(i + 1);
            }
        }
        return braceAlignmentErrorLines;   
    }
    
    /**
     * Given a line of code from inputDataLines and a specific character,
     * checks if the specific character is in the line of code contained within
     * quotations.
     * 
     * @param currentLine   the current line of code from inputDataLines
     * @param letter        the specific character to check for within quotes
     * 
     * @return true     if the specific character is within quotes
     *         false    if the specific character is not within quotes
     */
    private static boolean isBetweenQuotes(String currentLine, char letter)
    {
        int letterIndex = currentLine.indexOf(letter);
        int singleOpenIndex = currentLine.indexOf("'");
        int singleCloseIndex = currentLine.lastIndexOf("'");
        int doubleOpenIndex = currentLine.indexOf("\"");
        int doubleCloseIndex = currentLine.lastIndexOf("\"");
        
        if (singleOpenIndex >= 0)
        {
            
            if ((singleOpenIndex < letterIndex) 
                 && (letterIndex < singleCloseIndex))
            {
                return true;
            }
        }
        
        else if (doubleOpenIndex >= 0)
        {
            
            if ((doubleOpenIndex < letterIndex) 
                 && (letterIndex < doubleCloseIndex))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Given a program, finds every line of code in the program that contains
     * a line segment where the length exceeds the maximum line length, and adds
     * each line number with this style error to a list.
     * 
     * @param lineList  An ArrayList of the input file lines
     * @return longLineErrorLines  An ArrayList containing the line numbers of
     *                             the program that contain the long line error.
     */
    private static ArrayList<Integer> findLongLines(ArrayList<String> lineList)
    {
        ArrayList<Integer> longLineErrorLines = new 
                                                ArrayList<>(COMMON_INPUT_SIZE);
        final int MAX_LINE_LENGTH = 80;
        
        for (int i = 0; i < lineList.size(); i++)
        {
            if (lineList.get(i).length() > MAX_LINE_LENGTH)
            {
                longLineErrorLines.add(i + 1);
            }
        }
        
        return longLineErrorLines;   
    }
    
    /**
     * Given a program, finds every line of code in the program that contains   
     * a line segment with two lines of code that should be on two separate 
     * lines but are not, and adds each line number with this style error to 
     * a list.
     * 
     * @param lineList  An ArrayList of the input file lines
     * @return multiCodeSameLineErrorLines  An ArrayList containing the line
     *                                      numbers of the program that contain
     *                                      the two lines of code on the same  
     *                                      line error.
     */
    private static ArrayList<Integer> findMultiCodeSameLine(ArrayList<String> 
                                                             lineList)
    {
        ArrayList<Integer> multiCodeSameLineErrorLines = new ArrayList<>
                                                            (COMMON_INPUT_SIZE);
        
        for (int i = 0; i < lineList.size(); i++)
        {
            String currentLine = lineList.get(i).trim();
            int numSemiColon = countSemiColon(currentLine);
            
            if ((currentLine.startsWith("if")
                || currentLine.startsWith("while") 
                || currentLine.startsWith("do ")
                || currentLine.startsWith("switch")
                || currentLine.startsWith("else")) && currentLine.contains(";"))
            {
                multiCodeSameLineErrorLines.add(i + 1);
            } 
            
            if (currentLine.startsWith("for") && numSemiColon > 2)
            {
                multiCodeSameLineErrorLines.add(i + 1);
            }
            
            if (numSemiColon > 1 && !currentLine.startsWith("for"))
            {
                multiCodeSameLineErrorLines.add(i + 1);
            }
        }
        
        return multiCodeSameLineErrorLines; 
    }
    
    /**
     * Given a line of input from inputDataLines, counts the number of 
     * occurrences of ';' in the line.
     * 
     * @param currentLine   The current line being processed in inptDataLines
     * @return counter      The number of occurrences of ';' in the line
     */
    private static int countSemiColon(String currentLine)
    {
        int counter = 0;
        
        for (int i = 0; i < currentLine.length(); i++)
        {
           if (currentLine.charAt(i) == ';')
           {
               counter++;
           }
        }
        return counter;
    }
    
    /**
     * Given a program, finds every line of code in the program that contains
     * a method block that should have a blank line before it  but does not, and 
     * adds each line number with this style error to a list. We assume that 
     * the program given will not contain methods with parameters that take up 
     * more than one line.
     * 
     * @param lineList  An ArrayList of the input file lines
     * @return noBlankLineErrorLines  An ArrayList containing the line numbers
     *                                of the program that contain missing blank 
     *                                line error.
     */
    private static ArrayList<Integer> findNoBlankLineError(ArrayList<String> 
                                                          lineList)
    {
        ArrayList<Integer> noBlankLineErrorLines = new ArrayList<>  
                                                            (COMMON_INPUT_SIZE);
        
        for (int i = 0; i < lineList.size() - 2; i++)
        {
            String currentLine = lineList.get(i).trim();
            String nextLine = lineList.get(i + 1).trim();
            String nextNextLine = lineList.get(i + 2).trim();
            
            if ((currentLine.contains("class") && currentLine.contains("{")
                && !nextLine.equals("")) || (isMethod(nextLine) 
                && (nextLine.contains("{") || nextNextLine.contains("{")) 
                && currentLine.length() > 0))
            {
               if (!noBlankLineErrorLines.contains(i + 2))
               {
                   noBlankLineErrorLines.add(i + 2);
               }
            }
            
            else if (currentLine.contains("class") && nextLine.contains("{")
                     && !nextNextLine.equals(""))
            {
               if (!noBlankLineErrorLines.contains(i + 3))
               {
                   noBlankLineErrorLines.add(i + 3);
               }
            }
            
//            if (isMethod(nextLine) && (nextLine.contains("{") 
//                     || nextNextLine.contains("{")) && currentLine.length() > 0 
//                     && !currentLine.equals("@Override"))
//            {
//                noBlankLineErrorLines.add(i + 2);
//            }
        }
        
        return noBlankLineErrorLines; 
    }
    
    /**
     * Fixes a program that contains blank line errors before any methods, and
     * before and after any private data field declarations, by adding blank
     * lines in the appropriate places.
     * 
     * @param lineList  The lines of the program with errors
     * @param output    The text output stream to a specified output file
     */
    private static void fixBlankLineError(ArrayList<String> lineList, 
                                          PrintWriter output)
    {
        ArrayList<Integer> blankLineErrors = findNoBlankLineError(lineList);
        int offset = -1; // -1 because the line number is 1 greater than index
        
        for (int i: blankLineErrors)
        {
            String currentLine = inputDataLines.get(i + offset);
            
            if (currentLine.contains("}") 
                && currentLine.length() > 1)
            {
                int braceIndex = currentLine.indexOf("}");
                String brace = currentLine.substring(0, braceIndex + 1);
                String remainder = " " + currentLine.substring(braceIndex + 1);
                
                inputDataLines.remove(i + offset);
                inputDataLines.add(i + offset, brace);
                inputDataLines.add(i + 1 + offset, "");
                inputDataLines.add(i + 2 + offset, remainder);
                offset += 2;
            }
            
            else 
            {
                lineList.add(i + offset, "");
                offset += 1;
            }
        }
        output.println("\nBlank Line Error Correction(s):");
        output.println("=================================");
        
        for (String line: lineList)
        {
            output.println(line);
        }
    }
    
    /**
     * Given two consecutive lines from inputDataLines, checks if the first line
     * given is a method. We assume that we will not be passed lines that are
     * methods with parameters that take up more than one line.
     * 
     * @param line       A line given from inputDataLines
     * @param nextLine   The successive line from inputDataLines 
     */
    private static boolean isMethod(String line)
    {
       boolean result = false;
       
       if (((line.contains("private") || line.contains("public"))
               && line.contains("(")))
       {
           result = true;
       }
       return result;
    }
    
    /**
     * Outputs a report to a specified output file containing the style report
     * authors name, the test program authors name, the errors checked for,
     * and a list of all of the errors that were found and the line numbers they
     * were found on.
     * 
     * 
     * @param output The output file to written to
     * 
     */
    private static void generateReport(PrintWriter output)
    {
        ArrayList<Integer> indentErrorLines = findIndentError(inputDataLines);
        ArrayList<Integer> longLineErrorLines = findLongLines(inputDataLines);
        ArrayList<Integer> optionalBraceErrorLines = 
                                        findOptionalBraceError(inputDataLines);
        ArrayList<Integer> multiCodeSameLineErrorLines = 
                                        findMultiCodeSameLine(inputDataLines);
        ArrayList<Integer> operatorSpaceErrorLines = 
                                        findOperatorSpaceError(inputDataLines);
        ArrayList<Integer> braceAlignmentErrorLines = 
                                        findBraceAlignmentError(inputDataLines);
        ArrayList<Integer> noBlankLineErrorLines = 
                                        findNoBlankLineError(inputDataLines);
        
        final int ERROR_SUM = indentErrorLines.size() 
                            + longLineErrorLines.size()
                            + optionalBraceErrorLines.size() 
                            + multiCodeSameLineErrorLines.size()
                            + operatorSpaceErrorLines.size()
                            + braceAlignmentErrorLines.size()
                            + noBlankLineErrorLines.size();
        
        final String MY_NAME = "Style report by Jeff Rowell";
        String authorName = "Test program author: ";
        String errorTypes = "Error(s) checked: ";
        String todaysDate = "10/12/2017";
        
        for (int i = 0; i < inputDataLines.size(); i++)
        {
            if (inputDataLines.get(i).contains("/*"))
            {
                authorName += inputDataLines.get(i + 1).trim();
                
                if (inputDataLines.get(i + 2).trim().startsWith("Error"))
                {
                    int index = inputDataLines.get(i + 2).indexOf(":");
                    errorTypes += inputDataLines.get(i + 2).substring
                                                            (index + 1).trim();
                }
                
                else
                {
                    errorTypes += inputDataLines.get(i + 2).trim();
                }
                break;
            }
        }
        
        output.println("Date: " + todaysDate);
        output.println(MY_NAME + "\n" + authorName + "\n" + errorTypes + "\n");
        output.println("Style errors found:");
        output.println("====================");
        
        if (!(indentErrorLines.isEmpty() && longLineErrorLines.isEmpty()
                && optionalBraceErrorLines.isEmpty()
                && multiCodeSameLineErrorLines.isEmpty() 
                && operatorSpaceErrorLines.isEmpty()
                && braceAlignmentErrorLines.isEmpty()
                && noBlankLineErrorLines.isEmpty())) 
        {
            for (int i = 0; i < indentErrorLines.size(); i++) 
            {
                output.println("Line " + indentErrorLines.get(i)
                        + ": statement block indentation error");
            }
            
            for (int i = 0; i < longLineErrorLines.size(); i++) 
            {
                output.println("Line " + longLineErrorLines.get(i)
                        + ": exceeds the maximum line length");
            }
            
            for (int i = 0; i < optionalBraceErrorLines.size(); i++) 
            {
                output.println("Line " + optionalBraceErrorLines.get(i)
                        + ": statement block missing curly braces");
            }
            
            for (int i = 0; i < multiCodeSameLineErrorLines.size(); i++)
            {
                output.println("Line " + multiCodeSameLineErrorLines.get(i) 
                           +   ": two separate lines of code on the same line");
            }
            
            for (int i = 0; i < operatorSpaceErrorLines.size(); i++)
            {
                output.println("Line " + operatorSpaceErrorLines.get(i)
                             + ": needs exactly one space between operator");
            }
            
            for (int i = 0; i < braceAlignmentErrorLines.size(); i++)
            {
                output.println("Line " + braceAlignmentErrorLines.get(i)
                             + ": brace alignment error");
            }
            
            for (int i = 0; i < noBlankLineErrorLines.size(); i++)
            {
                output.println("Line " + noBlankLineErrorLines.get(i) 
                             + ": missing blank line before statement");
            }
        }
        
        else
        {
            output.println("No style errors found!");
        }
        output.println("\nTotal style errors: " + ERROR_SUM + "\n");
        
        if (noBlankLineErrorLines.size() > 0)
        {
            fixBlankLineError(inputDataLines, output);
        }
        
        if (optionalBraceErrorLines.size() > 0)
        {
            fixOptionalBraceError(inputDataLines, output);
        }
        output.close();
    } 
}