package com.libs.mathExpressionCalculator.test;

import com.libs.mathExpressionCalculator.Main;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MainTest {

    @Test
    @DisplayName("Evaluate Expression Test (Main Test)")
    public void evaluateExpressionTest() {
        assertEquals(4.0f, Main.evaluateExpression("2+2"));
        assertEquals(4.0f, Main.evaluateExpression("       2   +  2           "));
        assertEquals(8.0f, Main.evaluateExpression("2+3*2"));
        assertEquals(8.0f, Main.evaluateExpression("2+(3*2)"));
        assertEquals(10.0f, Main.evaluateExpression("(2+3)*2"));
        assertEquals(4.0f, Main.evaluateExpression("-2+3*2"));
        assertEquals(4.0f, Main.evaluateExpression("-2+(3*2)"));
        assertEquals(-10.0f, Main.evaluateExpression("-(2+3)*2"));
        assertEquals(-1.0f, Main.evaluateExpression("2+(4+(3)"));
        assertEquals(71.0f, Main.evaluateExpression("7-(5-(2+3)*7-8/(1+3))*2"));
        //assertEquals(775.6552f, Main.evaluateExpression("34.2 * (7 - 9.807 * (5.4 - 7)) + 65.4 / (4 * (3 - (2+4) * 7.6))"));
        assertEquals(-1.0f, Main.evaluateExpression(" "));
    }

    @Test
    @DisplayName("Bracket Split Test")
    public void bracketSplitTest() {
        String expr1 = "7-(5-(2+3)*7-8/(1+3))*2";
        String[] exp11 = {"7-","(","5-","(","2+3",")","*7-8/","(","1+3",")",")","*2"};
        List<Integer> exp12 = List.of(1,3,5,7,9,10);
        List<Integer> exp13 = List.of(1,2,1,2,1,0);
        List<Object> res1 = Main.bracketSplit(expr1);
        String[] res11 = (String[]) res1.get(0);
        //if (res1.get(1) instanceof List<?>) System.out.println("res1.get(1) instanceof List<?>");
        List<Integer> res12 = (List<Integer>) res1.get(1);
        //if (res1.get(2) instanceof List<?>) System.out.println("res1.get(2) instanceof List<?>");
        List<Integer> res13 = (List<Integer>) res1.get(2);

        String expr2 = "34.2 * (7 - 9.807 * (5.4 - 7)) + 65.4 / (4 * (3 - (2+4) * 7.6))";
        String[] exp21 = {"34.2 * ","(","7 - 9.807 * ","(","5.4 - 7",")",")"," + 65.4 / ","(","4 * ","(","3 - ","(","2+4",")"," * 7.6",")",")"};
        List<Integer> exp22 = List.of(1,3,5,6,8,10,12,14,16,17);
        List<Integer> exp23 = List.of(1,2,1,0,1,2,3,2,1,0);
        List<Object> res2 = Main.bracketSplit(expr2);
        String[] res21 = (String[]) res2.get(0);
        //if (res2.get(1) instanceof List<?>) System.out.println("res2.get(1) instanceof List<?>");
        List<Integer> res22 = (List<Integer>) res2.get(1);
        //if (res2.get(2) instanceof List<?>) System.out.println("res2.get(2) instanceof List<?>");
        List<Integer> res23 = (List<Integer>) res2.get(2);

        assertEquals(exp11.length, res11.length);
        for (int i = 0; i < exp11.length; i++)
            assertEquals(exp11[i], res11[i]);
        assertEquals(exp12.size(), res12.size());
        for (int i = 0; i < exp12.size(); i++)
            assertEquals(exp12.get(i), res12.get(i));
        assertEquals(exp13.size(),res13.size());
        for (int i = 0; i < exp13.size(); i++)
            assertEquals(exp13.get(i), res13.get(i));

        assertEquals(exp21.length, res21.length);
        for (int i = 0; i < exp21.length; i++)
            assertEquals(exp21[i], res21[i]);
        assertEquals(exp22.size(), res22.size());
        for (int i = 0; i < exp22.size(); i++)
            assertEquals(exp22.get(i), res22.get(i));
        assertEquals(exp23.size(), res23.size());
        for (int i = 0; i < exp23.size(); i++)
            assertEquals(exp23.get(i), res23.get(i));
    }

    @Test
    @DisplayName("Bracket Sequence Validation Test")
    public void bracketsSequenceIsValidTest() {
        List<Integer> levelList1 = List.of(1,2,1,0,1,2,3,2,1,0);
        assertTrue(Main.bracketsSequenceIsValid(levelList1));

        List<Integer> levelList2 = List.of(1,2,1,2,1,0);
        assertTrue(Main.bracketsSequenceIsValid(levelList2));

        List<Integer> levelList3 = List.of(1,0,-1,0,1,0);
        assertFalse(Main.bracketsSequenceIsValid(levelList3));

        List<Integer> levelList4 = List.of(1,2,1,2,1,2);
        assertFalse(Main.bracketsSequenceIsValid(levelList4));
    }

    @Test
    @DisplayName("Single evaluation Test")
    public void singleEvaluationTest() {
        String[] exprs1 = {"34.2 *", "(", "7 - 9.807 * ", "(", "5.4 - 7", ")", ")", " + 65.4 / ", "(",
                "4 * ", "(", "3 - ", "(", "2+4", ")", " * 7.6", ")", ")"};
        List<Integer> indexes1 = List.of(1,3,5,6,8,10,12,14,16,17);
        List<Integer> level1 = List.of(1,2,1,0,1,2,3,2,1,0);
        String[] exp11 = {"34.2 *", "(", "7 - 9.807 * ", "[1.5999999]", ")", " + 65.4 / ", "(",
                "4 * ", "(", "3 - ", "6.0", " * 7.6", ")", ")"};
        List<Integer> exp12 = List.of(1,4,6,8,12,13);
        List<Integer> exp13 = List.of(1,0,1,2,1,0);
        List<Object> res1 = Main.singleEvaluation(exprs1, indexes1, level1);
        String[] res11 = (String[]) res1.get(0);
        List<Integer> res12 = (List<Integer>) res1.get(1);
        List<Integer> res13 = (List<Integer>) res1.get(2);

        String[] exprs2 = {"7-", "(", "5-", "(", "2+3", ")", "*7-8/", "(", "1+3", ")", ")", "*2"};
        List<Integer> indexes2 = List.of(1,3,5,7,9,10);
        List<Integer> level2 = List.of(1,2,1,2,1,0);
        String[] exp21 = {"7-", "(", "5-", "5.0", "*7-8/", "4.0", ")", "*2"};
        List<Integer> exp22 = List.of(1,6);
        List<Integer> exp23 = List.of(1,0);
        List<Object> res2 = Main.singleEvaluation(exprs2, indexes2, level2);
        String[] res21 = (String[]) res2.get(0);
        List<Integer> res22 = (List<Integer>) res2.get(1);
        List<Integer> res23 = (List<Integer>) res2.get(2);

        String[] exprs3 = {"(","5-2.0",")"};
        List<Integer> indexes3 = List.of(0,2);
        List<Integer> level3 = List.of(1,0);
        String[] exp31 = {"3.0"};
        List<Integer> exp32 = new ArrayList<>(0);
        List<Integer> exp33 = new ArrayList<>(0);
        List<Object> res3 = Main.singleEvaluation(exprs3, indexes3, level3);
        String[] res31 = (String[]) res3.get(0);
        List<Integer> res32 = (List<Integer>) res3.get(1);
        List<Integer> res33 = (List<Integer>) res3.get(2);

        String[] exprs4 = {"34.2*","(","7-9.807*[1.6]",")","+65.4/","(","4*","(","3-6.0*7.6",")",")"};
        List<Integer> indexes4 = List.of(1,3,5,7,9,10);
        List<Integer> level4 = List.of(1,0,1,2,1,0);
        String[] exp41 = {"34.2*","22.6912","+65.4/","(","4*","[42.6]",")"};
        List<Integer> exp42 = List.of(3,6);
        List<Integer> exp43 = List.of(1,0);
        List<Object> res4 = Main.singleEvaluation(exprs4, indexes4, level4);
        String[] res41 = (String[]) res4.get(0);
        List<Integer> res42 = (List<Integer>) res4.get(1);
        List<Integer> res43 = (List<Integer>) res4.get(2);

        assertEquals(exp11.length, res11.length);
        for (int i = 0; i < exp11.length; i++)
            assertEquals(exp11[i], res11[i]);
        assertEquals(exp12.size(), res12.size());
        for (int i = 0; i < exp12.size(); i++)
            assertEquals(exp12.get(i), res12.get(i));
        assertEquals(exp13.size(),res13.size());
        for (int i = 0; i < exp13.size(); i++)
            assertEquals(exp13.get(i), res13.get(i));

        assertEquals(exp21.length, res21.length);
        for (int i = 0; i < exp21.length; i++)
            assertEquals(exp21[i], res21[i]);
        assertEquals(exp22.size(), res22.size());
        for (int i = 0; i < exp22.size(); i++)
            assertEquals(exp22.get(i), res22.get(i));
        assertEquals(exp23.size(), res23.size());
        for (int i = 0; i < exp23.size(); i++)
            assertEquals(exp23.get(i), res23.get(i));

        assertEquals(exp31.length, res31.length);
        for (int i = 0; i < exp31.length; i++)
            assertEquals(exp31[i], res31[i]);
        assertEquals(exp32.size(), res32.size());
        for (int i = 0; i < exp32.size(); i++)
            assertEquals(exp32.get(i), res32.get(i));
        assertEquals(exp33.size(), res33.size());
        for (int i = 0; i < exp33.size(); i++)
            assertEquals(exp33.get(i), res33.get(i));

        assertEquals(exp41.length, res41.length);
        for (int i = 0; i < exp41.length; i++)
            assertEquals(exp41[i], res41[i]);
        assertEquals(exp42.size(), res42.size());
        for (int i = 0; i < exp42.size(); i++)
            assertEquals(exp42.get(i), res42.get(i));
        assertEquals(exp43.size(), res43.size());
        for (int i = 0; i < exp43.size(); i++)
            assertEquals(exp43.get(i), res43.get(i));
    }

    @Test
    @DisplayName("Product Expression Evaluation Test")
    public void productExpressionEvaluationTest() {
        String expr1 = "5*8/4*6/3";
        String expr2 = "5*[-8]/4*[6]/3";
        String expr3 = "5*8/-4*6/3";
        String expr4 = "5*8/*4*6/3";
        String expr5 = "5*[8]/4*[6]/3";
        float[] exp1 = {20.0f,1.0f};
        float[] exp2 = {-1.0f,0.0f}; // there should not be either [] and -
        float[] exp3 = {-20.0f,1.0f};
        float[] exp4 = {-1.0f,0.0f};
        float[] exp5 = {20.0f,1.0f};
        float[] res1 = Main.productExpressionEvaluation(expr1);
        float[] res2 = Main.productExpressionEvaluation(expr2);
        float[] res3 = Main.productExpressionEvaluation(expr3);
        float[] res4 = Main.productExpressionEvaluation(expr4);
        float[] res5 = Main.productExpressionEvaluation(expr5);

        assertEquals(exp1[0],res1[0]);
        assertEquals(exp1[1],res1[1]);
        assertEquals(exp2[0],res2[0]);
        assertEquals(exp2[1],res2[1]);
        assertEquals(exp3[0],res3[0]);
        assertEquals(exp3[1],res3[1]);
        assertEquals(exp4[0],res4[0]);
        assertEquals(exp4[1],res4[1]);
        assertEquals(exp5[0],res5[0]);
        assertEquals(exp5[1],res5[1]);
    }

    @Test
    @DisplayName("Simple Expression Evaluation Test")
    public void simpleExpressionEvaluationTest() {
        String expr1 = "2+3*6-8/2+1";
        String expr2 = "-2+3*6-8/2+1";
        String expr3 = "[2]+3*6-8/2+1";
        String expr4 = "-[2]+3*6-8/2+1";
        String expr5 = "2+3**6-8/2+1";
        float[] exp1 = {17.0f,1.0f};
        float[] exp2 = {13.0f,1.0f};
        float[] exp3 = {13.0f,1.0f};
        float[] exp4 = {17.0f,1.0f};
        float[] exp5 = {-1.0f,0.0f};
        float[] res1 = Main.simpleExpressionEvaluation(expr1);
        float[] res2 = Main.simpleExpressionEvaluation(expr2);
        float[] res3 = Main.simpleExpressionEvaluation(expr3);
        float[] res4 = Main.simpleExpressionEvaluation(expr4);
        float[] res5 = Main.simpleExpressionEvaluation(expr5);

        assertEquals(exp1[0],res1[0]);
        assertEquals(exp1[1],res1[1]);
        assertEquals(exp2[0],res2[0]);
        assertEquals(exp2[1],res2[1]);
        assertEquals(exp3[0],res3[0]);
        assertEquals(exp3[1],res3[1]);
        assertEquals(exp4[0],res4[0]);
        assertEquals(exp4[1],res4[1]);
        assertEquals(exp5[0],res5[0]);
        assertEquals(exp5[1],res5[1]);
    }

    @Test
    @DisplayName("Simple Expression Evaluation with impossible input [-a]")
    public void impossibleSquareBracketsAndMinusSignSimpleExpressionEvaluationTest() {
        //There should be an exception due to inappropriate substring bounds

        float[] res1;
        float[] res2;
        try {
            res1 = Main.simpleExpressionEvaluation("-2+3*[-6]-8/2+1");
        } catch (StringIndexOutOfBoundsException e) {
            res1 = new float[]{1.9273921903247318f, 0.0f};
        }
        try {
            res2 = Main.simpleExpressionEvaluation("[-2]+3*6-8/2+1");
        } catch (StringIndexOutOfBoundsException e) {
            res2 = new float[]{2.9273921903247318f, 0.0f};
        }

        assertEquals(1.9273921903247318f, res1[0]);
        assertEquals(2.9273921903247318f, res2[0]);
    }

    @Test
    @DisplayName("Check Product Expression Test")
    public void checkProductExpressionTest() {
        String expr1 = "5*8/4*6/3";
        assertTrue(Main.checkProductExpression(expr1));

        String expr2 = "5*8/*4*6/3";
        assertFalse(Main.checkProductExpression(expr2));
    }

    @Test
    @DisplayName("Merge Consecutive Strings Test")
    public void mergeConsecutiveStringsTest() {
        String[] expr1 = {"34.2 *", "(", "7 - 9.807 * ", "[1.5999999]", ")", " + 65.4 / ", "(",
                "4 * ", "(", "3 - ", "6.0", " * 7.6", ")", ")"};
        List<Integer> idx1 = List.of(1,4,6,8,12,13);
        String[] exp11 = {"34.2 *", "(", "7 - 9.807 * [1.5999999]", ")", " + 65.4 / ", "(",
                "4 * ", "(", "3 - 6.0 * 7.6", ")", ")"};
        List<Integer> exp12 = List.of(1,3,5,7,9,10);
        List<Object> res1 = Main.mergeConsecutiveStrings(expr1, idx1);
        String[] res11 = (String[]) res1.get(0);
        List<Integer> res12 = (List<Integer>) res1.get(1);

        String[] expr2 = {"7-", "(", "5-", "5.0", "*7-8/", "4.0", ")", "*2"};
        List<Integer> idx2 = List.of(1,6);
        String[] exp21 = {"7-", "(", "5-5.0*7-8/4.0", ")", "*2"};
        List<Integer> exp22 = List.of(1,3);
        List<Object> res2 = Main.mergeConsecutiveStrings(expr2, idx2);
        String[] res21 = (String[]) res2.get(0);
        List<Integer> res22 = (List<Integer>) res2.get(1);

        String[] expr3 = {"5.0", "*2"};
        List<Integer> idx3 = new ArrayList<>(0);
        String[] exp31 = {"5.0*2"};
        List<Integer> exp32 = new ArrayList<>(0);
        List<Object> res3 = Main.mergeConsecutiveStrings(expr3, idx3);
        String[] res31 = (String[]) res3.get(0);
        List<Integer> res32 = (List<Integer>) res3.get(1);

        String[] expr4 = {"34.2*","22.6912","+65.4/","(","4*","[42.6]",")"};
        List<Integer> idx4 = List.of(3,6);
        String[] exp41 = {"34.2*22.6912+65.4/","(","4*[42.6]",")"};
        List<Integer> exp42 = List.of(1,3);
        List<Object> res4 = Main.mergeConsecutiveStrings(expr4, idx4);
        String[] res41 = (String[]) res4.get(0);
        List<Integer> res42 = (List<Integer>) res4.get(1);

        assertEquals(exp11.length, res11.length);
        for (int i = 0; i < exp11.length; i++)
            assertEquals(exp11[i], res11[i]);
        assertEquals(exp12.size(), res12.size());
        for (int i = 0; i < exp12.size(); i++)
            assertEquals(exp12.get(i), res12.get(i));

        assertEquals(exp21.length, res21.length);
        for (int i = 0; i < exp21.length; i++)
            assertEquals(exp21[i], res21[i]);
        assertEquals(exp22.size(), res22.size());
        for (int i = 0; i < exp22.size(); i++)
            assertEquals(exp22.get(i), res22.get(i));

        assertEquals(exp31.length, res31.length);
        for (int i = 0; i < exp31.length; i++)
            assertEquals(exp31[i], res31[i]);
        assertEquals(exp32.size(), res32.size());
        for (int i = 0; i < exp32.size(); i++)
            assertEquals(exp32.get(i), res32.get(i));

        assertEquals(exp41.length, res41.length);
        for (int i = 0; i < exp41.length; i++)
            assertEquals(exp41[i], res41[i]);
        assertEquals(exp42.size(), res42.size());
        for (int i = 0; i < exp42.size(); i++)
            assertEquals(exp42.get(i), res42.get(i));
    }

    public static void main(String[] args) {
        MainTest mainTest = new MainTest();
        mainTest.bracketSplitTest();
        mainTest.bracketsSequenceIsValidTest();
        mainTest.singleEvaluationTest();
        mainTest.productExpressionEvaluationTest();
        mainTest.simpleExpressionEvaluationTest();
        mainTest.impossibleSquareBracketsAndMinusSignSimpleExpressionEvaluationTest();
        mainTest.checkProductExpressionTest();
        mainTest.mergeConsecutiveStringsTest();
        mainTest.evaluateExpressionTest();

        // ---------------------------------------------------------------------------------------------------------- //
        // Switch with strings
        System.out.println("Switch with strings: --------------------------------------------------------------");
        System.out.println(switchEqualsTest("due"));
        System.out.println(switchEqualsTest("tre"));

        // ---------------------------------------------------------------------------------------------------------- //
        // What do initial capacity and size mean
        System.out.println("\"What do initial capacity and size mean\" test: ----------------------------------");
        sizeListTest(0);
        sizeListTest(5);

        // ---------------------------------------------------------------------------------------------------------- //
        // Behavior of parseFloat
        System.out.println("Behavior of parseFloat: -----------------------------------------------------------");
        System.out.println(Float.parseFloat("-5.760064692154110"));

        // ---------------------------------------------------------------------------------------------------------- //
        // Behavior of replace
        System.out.println("Behavior of replace: --------------------------------------------------------------");
        System.out.println("5 * 6 / 8 - 0  .6 ".replace(" ",""));
    }

    /*
     * Calculate the result of a valid mathematical expression without brackets.
     * @param expression The mathematical expression. PRECONDITION: it must be valid (see {@code checkValidationTest}).
     * @throws IllegalStateException If another symbol besides + and - is detected when there should be only these two.
     * @return The result of the expression in float precision.
     */ /*
    public static float bracketFreeTest(String expression) {
        String[] splitExpressions = expression.split("((?<=\\+)|(?=\\+))|((?<=\\-)|(?=\\-))");

        float nextFloat = productExpressionTest(splitExpressions[0]);
        float res = nextFloat;
        for (int i = 1; i < splitExpressions.length; i += 2) {
            nextFloat = productExpressionTest(splitExpressions[i+1]);
            switch (splitExpressions[i]) {
                case "+" -> res += nextFloat;
                case "-" -> res -= nextFloat;
                default -> throw new IllegalStateException("Unexpected value: " + splitExpressions[i] + ". Nor \"+\" nor \"-\"");
            }
        }
        return res;
    }*/

    private static int switchEqualsTest(String str) {
        return switch (str) {
            case "uno" -> 1;
            case "due" -> 2;
            case "tre" -> 3;
            default -> -1;
        };
    }

    private static void sizeListTest(int initialCapacity) {
        List<Integer> list = new ArrayList<>(initialCapacity);
        System.out.println(list.size());
        try {
            System.out.println(list.get(0));
        } catch (IndexOutOfBoundsException e) {
            System.out.println("There are no elements (java.lang.IndexOutOfBoundsException).");
        }
    }
}