package com.libs.mathExpressionCalculator;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String expression = "1/1.";
        System.out.println(evaluateExpression(expression));
    }

    /**
     * It evaluates a mathematical expression with +,-,*,/,(,) and
     * returns the result of the expression, if it's valid. If not,
     * it returns -1 and it prints "Not valid".
     *
     * @param expression A String containing the mathematical expression,
     *                   with at most 2<sup>31</sup> characters.
     * @return A float with the result of the expression or -1 if the expression
     * is not valid.
     */
    public static float evaluateExpression(String expression) {
        // Remove all the spaces
        expression = expression.replace(" ", "");

        //Split according to brackets, memorize brackets indexes and "internality" level
        List<Object> bracketSplitList = bracketSplit(expression);
        String[] bracketsFreeExpressions = (String[]) bracketSplitList.get(0);
        List<Integer> indexesBracketsList = (List<Integer>) bracketSplitList.get(1);
        List<Integer> levelBracketsList = (List<Integer>) bracketSplitList.get(2);

        //Check the sequence of the brackets
        if (!bracketsSequenceIsValid(levelBracketsList)) {
            System.out.println("Not valid");
            return -1.0f;
        }

        while (levelBracketsList.size() > 0) {
            //Evaluate the innermost bracket-free expressions
            List<Object> singleEvaluationList = singleEvaluation(bracketsFreeExpressions, indexesBracketsList, levelBracketsList);
            if (!((Boolean) singleEvaluationList.get(3))) {
                System.out.println("Not valid");
                return -1.0f;
            }
            bracketsFreeExpressions = (String[]) singleEvaluationList.get(0);
            indexesBracketsList = (List<Integer>) singleEvaluationList.get(1);
            levelBracketsList = (List<Integer>) singleEvaluationList.get(2);

            //Merge the consecutive strings without brackets
            List<Object> mergingConsecutiveList = mergeConsecutiveStrings(bracketsFreeExpressions, indexesBracketsList);
            bracketsFreeExpressions = (String[]) mergingConsecutiveList.get(0);
            indexesBracketsList = (List<Integer>) mergingConsecutiveList.get(1);
        }

        //Final computation for an expression with no brackets
        // well... which SHOULD not have brackets
        if (bracketsFreeExpressions.length != 1)
            throw new IllegalStateException("Unexpected bracketsFreeExpressions.length: " + bracketsFreeExpressions.length + ".");
        float[] res = simpleExpressionEvaluation(bracketsFreeExpressions[0]);
        if (res[1] == 0.0f) {
            System.out.println("Not valid");
            return -1.0f;
        }
        return res[0];
    }

    /** Given a mathematical expression, it prints a split
     * version according to parenthesis,
     * each of these with its index and internal level.
     *
     * @param expression Mathematical expression
     * @return A list of three element:
     * <ol>
     *     <li>{@code String[] bracketFreeExpressions}: split version of {@code expression} according to parenthesis</li>
     *     <li>{@code List<Integer> indexesBracketsList}: the indexes of the brackets;</li>
     *     <li>{@code List<Integer> levelBracketsList}: the number of open brackets that
     *     are opened until the corresponding bracket.</li>
     * </ol>*/
    public static List<Object> bracketSplit(String expression) {
        String[] bracketFreeExpressions = expression.split("((?<=\\()|(?=\\())|((?<=\\))|(?=\\)))");
        int lengthBracketFreeExpressions = bracketFreeExpressions.length;

        List<Integer> indexesBracketsList = new ArrayList<>((int) (0.75 * lengthBracketFreeExpressions));
        List<Integer> levelBracketsList = new ArrayList<>((int) (0.75 * lengthBracketFreeExpressions));
        int level = 0;
        for (int i = 0; i < lengthBracketFreeExpressions; i++) {
            switch (bracketFreeExpressions[i]) {
                case "(" -> {
                    indexesBracketsList.add(i);
                    levelBracketsList.add(++level);
                }
                case ")" -> {
                    indexesBracketsList.add(i);
                    levelBracketsList.add(--level);
                }
            }
        }

        return List.of(bracketFreeExpressions, indexesBracketsList, levelBracketsList);
    }

    /**
     * Given the level of "internality" of the brackets of an expression, it checks whether it
     * corresponds to a valid sequence of open or close brackets.
     *
     * @param levelBracketsList List of the level of "internality" of the brackets of a mathematical expression.
     * @return It returns {@code true} if and only if the expression
     * (corresponding to the {@code levelBracketsList}) is valid.
     */
    public static boolean bracketsSequenceIsValid(List<Integer> levelBracketsList) {
        int levelBracketsListSize = levelBracketsList.size();
        if (levelBracketsListSize > 0) {
            if (levelBracketsList.get(levelBracketsListSize - 1) != 0) {
                return false;
            }
            for (int i = 0; i < levelBracketsListSize - 1; i++) {
                if (levelBracketsList.get(i) < 0) {
                    return false;
                }
                i = i + levelBracketsList.get(i);
            }
        }
        return true;
    }

    /**
     * A single evaluation step of a mathematical expression,
     * in which the expressions between the innermost pairs of brackets
     * are performed.
     *
     * @param expressions Split mathematical expression according to brackets given as {@code String[]};
     *                    the expressions evaluated are put in square brackets without the minus sign
     *                    if the result is negative ([<i>a</i>] <i>= -a</i>, <i>a</i> non-negative).
     * @param indexesBracketsList the indexes of the brackets;
     * @param levelBracketsList the number of open brackets that are opened until the corresponding bracket.
     * @return A list with the three updates version of the input parameters and a Boolean that declares whether
     * a non-valid expression has been detected. In this case, the first three elements are meaningless.
     */
    public static List<Object> singleEvaluation(String[] expressions,
                                                    List<Integer> indexesBracketsList,
                                                    List<Integer> levelBracketsList) {
        int[] innermostBracketsIndexes = new int[indexesBracketsList.size()];
        int innermostBracketsIndexesLength = 0;
        if (levelBracketsList.get(0) == levelBracketsList.get(1) + 1) {
            innermostBracketsIndexes[innermostBracketsIndexesLength] = 0;
            innermostBracketsIndexesLength++;
        }
        for (int i = 1; i < indexesBracketsList.size(); i++) {
            if (levelBracketsList.get(i) == levelBracketsList.get(i-1) + 1 &&
                    levelBracketsList.get(i + 1).equals(levelBracketsList.get(i - 1))) {
                innermostBracketsIndexes[innermostBracketsIndexesLength] = i;
                innermostBracketsIndexesLength++;
            }
        }

        String[] newExpressions = new String[expressions.length - 2*innermostBracketsIndexesLength];
        List<Integer> newIndexesBracketsList = new ArrayList<>(indexesBracketsList.size() - 2*innermostBracketsIndexesLength);
        List<Integer> newLevelBracketsList = new ArrayList<>(indexesBracketsList.size() - 2*innermostBracketsIndexesLength);

        int idx = 0;
        float[] tmp;
        for (int j = 0; j < indexesBracketsList.get(innermostBracketsIndexes[0]); j++) {
            newExpressions[idx] = expressions[idx];
            idx++;
        }
        for (int j = 0; j < innermostBracketsIndexesLength; j++) {
            tmp = simpleExpressionEvaluation(expressions[indexesBracketsList.get(innermostBracketsIndexes[j]) + 1]);
            if (tmp[1] == 0.0f) {
                return List.of(newExpressions, newIndexesBracketsList, newLevelBracketsList, new Boolean(false));
            }
            if (tmp[0] < 0) {
                newExpressions[idx] = "[" + Float.toString(tmp[0]).substring(1) + "]";
            } else {
                newExpressions[idx] = Float.toString(tmp[0]);
            }
            idx++;
            for (int i = indexesBracketsList.get(innermostBracketsIndexes[j]) + 3;
                 i < expressions.length &&
                         (j < innermostBracketsIndexesLength-1 &&
                                 i < indexesBracketsList.get(innermostBracketsIndexes[j+1]) ||
                                 j == innermostBracketsIndexesLength-1);
                 i++) {
                newExpressions[idx] = expressions[i];
                idx++;
            }
        }

        idx = 0;
        for (int j = 0; j < innermostBracketsIndexesLength; j++) {
            for (int i = idx + 2*j; i < innermostBracketsIndexes[j]; i++) {
                newIndexesBracketsList.add(indexesBracketsList.get(i) - 2*j);
                newLevelBracketsList.add(levelBracketsList.get(i));
                idx++;
            }
        }
        for (int i = idx + 2*innermostBracketsIndexesLength; i < indexesBracketsList.size(); i++) {
            newIndexesBracketsList.add(indexesBracketsList.get(i) - 2*innermostBracketsIndexesLength);
            newLevelBracketsList.add(levelBracketsList.get(i));
            idx++;
        }

        return List.of(newExpressions, newIndexesBracketsList, newLevelBracketsList, new Boolean(true));
    }

    /**
     * Calculate the result of a valid mathematical expression without brackets (expect for square
     * brackets, reserved for boxing a negative number <b>which is given without the minus sign</b>. Here the square
     * brackets actually represent the negative sign given to the wrapped number).
     * @param expression The mathematical expression.
     * @return An array with two float variables: the result of the expression and 1.0, if the expression is valid,
     * with the notation [<i>a</i>] <i>= -a</i>, <i>a</i> non-negative. Otherwise, it returns {-1.0, 0.0}.
     */
    public static float[] simpleExpressionEvaluation(String expression) {
        //Split in substrings according to + and -, keeping them.
        String[] splitExpressions = expression.split("((?<=\\+)|(?=\\+))|((?<=\\-)|(?=\\-))");
        int splitExpressionsLength = splitExpressions.length;

        byte startsWithSum;
        float[] nextFloat; //I will need it later.
        switch (splitExpressions[0]) {
            case "+" -> {
                startsWithSum = (byte) 1;
                nextFloat = productExpressionEvaluation(splitExpressions[1]);
            }
            case "-" -> {
                startsWithSum = (byte) 1;
                nextFloat = productExpressionEvaluation(splitExpressions[1]);
                nextFloat[0] = -nextFloat[0];
            }
            default -> {
                startsWithSum = (byte) 0;
                nextFloat = productExpressionEvaluation(splitExpressions[0]);
            }
        }
        //Validation
        if (nextFloat[1] == 0.0f) {
            return new float[]{-1.0f, 0.0f};
        }

        // First check. We could have either
        //     ["-" (or "+"), a number, "+" or "-", a number,  ...  , "+" or "-", a number]
        // or
        //     [a number, "+" or "-", a number,  ...  , "+" or "-", a number]
        if (splitExpressionsLength % 2 == startsWithSum) {
            return new float[]{-1.0f, 0.0f};
        }

        // Memorize every number in nextFloat, updating res, while checking the validation of the expression
        float res = nextFloat[0];
        for (int i = startsWithSum+1; i < splitExpressionsLength; i += 2) {
            nextFloat = productExpressionEvaluation(splitExpressions[i+1]);
            if (nextFloat[1] == 0.0f)
                return new float[]{-1.0f, 0.0f};
            switch (splitExpressions[i]) {
                case "+":
                    res += nextFloat[0];
                    break;
                case "-":
                    res -= nextFloat[0];
                    break;
                default:
                    return new float[]{-1.0f, 0.0f};
            }
        }
        return new float[]{res, 1.0f};
    }

    /**
     * It checks if a mathematical expression only with *,/ without brackets is valid.
     *
     * @param expression The mathematical expression
     * @return It returns {@code true} if and only if the expression is valid.
     */
    public static boolean checkProductExpression(String expression) {
        String[] expressions = expression.split("((?<=\\*)|(?=\\*))|((?<=\\/)|(?=\\/))");
        int expressionsLength = expressions.length;

        if (expressionsLength % 2 == 0) {
            return false;
        }
        for (int i = 0; i < expressionsLength; i += 2) {
            try {
                Float.parseFloat(expressions[i]);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        for (int i = 1; i < expressionsLength; i += 2) {
            switch (expressions[i]) {
                case "*":
                case "/":
                    break;
                default: return false;
            }
        }
        return true;
    }

    /**
     * Calculate the result of a valid mathematical expression only with *,/, without brackets (expect for square
     * brackets, reserved for boxing a negative number <b>which is given without the minus sign</b>. Here the square
     * brackets actually represent the negative sign given to the wrapped number).
     * @param expression The mathematical expression.
     * @return An array of two float variables: the result of the expression and 1.0, if the expression is valid,
     * with the notation [<i>a</i>] <i>= -a</i> Otherwise, it returns {-1.0,0.0}.
     */
    public static float[] productExpressionEvaluation(String expression) {
        //Split in substrings according to * and /, keeping them.
        String[] splitExpressions = expression.split("((?<=\\*)|(?=\\*))|((?<=\\/)|(?=\\/))");
        int splitExpressionsLength = splitExpressions.length;

        // First check
        if (splitExpressionsLength % 2 == 0) {
            return new float[]{-1.0f, 0.0f};
        }
        // Convert [a] in -a
        for (int i = 0; i < splitExpressionsLength; i++) {
            if (splitExpressions[i].length() > 0 && splitExpressions[i].charAt(0) == '[')
                splitExpressions[i] = "-" + splitExpressions[i].substring(1,splitExpressions[i].length()-1);
        }

        // Memorize every number in nextFloat, updating res, while checking the validation of the expression
        float nextFloat;
        try {
            nextFloat = Float.parseFloat(splitExpressions[0]);
        } catch (NumberFormatException e) {
            return new float[]{-1.0f, 0.0f};
        }
        float res = nextFloat;
        for (int i = 1; i < splitExpressions.length; i += 2) {
            try {
                nextFloat = Float.parseFloat(splitExpressions[i+1]);
            } catch (NumberFormatException e) {
                return new float[]{-1.0f, 0.0f};
            }
            switch (splitExpressions[i]) {
                case "*":
                    res *= nextFloat;
                    break;
                case "/":
                    res /= nextFloat;
                    break;
                default:
                    return new float[]{-1.0f, 0.0f};
            }
        }
        return new float[]{res, 1.0f};
    }

    /**
     * Given an array of substrings that divides a mathematical expression,
     * it merges the consecutive substrings which are not brackets. The corresponding
     * indexes of the brackets is updated.
     *
     * @param expressions An array of String whose concatenation is a mathematical expression and whose elements are
     *                    either a bracket or a bracket-free expression.
     * @param indexesBracketsList The indexes of the elements of {@code expression} which are brackets
     * @return A list of the updated version of the two input parameters.
     */
    public static List<Object> mergeConsecutiveStrings(String[] expressions, List<Integer> indexesBracketsList) {
        int expressionsLength = expressions.length;
        int indexesBracketListLength = indexesBracketsList.size();

        //Case without brackets
        if (indexesBracketsList.isEmpty()) {
            String str = "";

            //Case of empty expression
            if (expressionsLength == 0) {
                return List.of(expressions, indexesBracketsList);
            } else {
                //Case of proper expression => concatenate all
                str = expressions[0];
                for (int i = 1; i < expressionsLength; i++) {
                    str += expressions[i];
                }
            }
            String[] strWrapper = {str};
            return List.of(strWrapper,indexesBracketsList);
        }

        //From now on: there exist at least a bracket

        //The gap between two consecutive elements of indexesBracketsList is greater than or equal to 1
        //Memorize whether is latter
        //Define for i > 0
        //       isOneEqualToDeltaIndexesBrackets[i]
        //                    := indexesBracketsList.get(i) - indexesBracketsList.get(i-1) == 1
        // We also need the number of consecutive pairs of brackets in expressions
        //       numberConsecutiveBrackets (= number of true in isOneEqualToDeltaIndexesBrackets)

        //Build isOneEqualToDeltaIndexesBrackets and numberConsecutiveBrackets
        boolean[] isOneEqualToDeltaIndexesBrackets = new boolean[indexesBracketListLength];
        int numberConsecutiveBrackets = 0;
        for (int i = 1; i < indexesBracketListLength; i++) {
            if (indexesBracketsList.get(i) - indexesBracketsList.get(i-1) == 1) {
                isOneEqualToDeltaIndexesBrackets[i] = true;
                numberConsecutiveBrackets++;
            } else {
                isOneEqualToDeltaIndexesBrackets[i] = false;
            }
        }

        // Determine the length of the updated expressions
        // noInitialBrackets = expressions does not start with a bracket
        boolean noInitialBracket = !(expressions[0].equals("(") || expressions[0].equals(")"));
        // noInitialBrackets = expressions does not start with a bracket
        boolean noFinalBracket = !(expressions[expressionsLength-1].equals("(") ||
                expressions[expressionsLength-1].equals(")"));
        int newExpressionsLength = 2*indexesBracketListLength - 1 - numberConsecutiveBrackets;
        if (noFinalBracket) newExpressionsLength++;
        if (noInitialBracket) newExpressionsLength++;

        // Declare the updated expressions and indexesBrackets
        String[] newExpressions = new String[newExpressionsLength];
        int[] newIndexesBrackets = new int[indexesBracketListLength];

        //Build newExpressions and newIndexesBrackets

        int i = 0;
        // Concatenate the first substrings before the (existing!) first bracket
        if (noInitialBracket) {
            newExpressions[0] = expressions[0];
            for (int j = 1; j < indexesBracketsList.get(0); j++) {
                newExpressions[0] += expressions[j];
            }
            i = 1;
        }
        //Memorize the first bracket
        newExpressions[i] = expressions[indexesBracketsList.get(0)];
        newIndexesBrackets[0] = i;
        i++;
        //Scan an expression (if there is any) followed by a bracket at every loop
        for (int j = 1; j < indexesBracketListLength; j++) {
            //FIRSTLY: expression

            //The gap between two consecutive elements of indexesBracketsList is greater than or equal to 1
            //Distinguish the case "greater than 1" and "equal to 1".
                // Case "equal to 1":
                // Like expression[indexesBracketsList.get(j - 1)] = expression[indexesBracketsList.get(j) - 1],
                // expression[indexesBracketsList.get(j)], which is the following element of expressions, is still
                // a bracket (besides existing). We only have to memorize this, so do nothing
            if (!isOneEqualToDeltaIndexesBrackets[j]) {
                //Case "greater than 1":
                //We need a further step: memorize the concatenation of the substrings before the next bracket
                //(and there exists at least one of them. This one:)
                newExpressions[i] = expressions[indexesBracketsList.get(j - 1) + 1];
                //Concatenate the rest
                for (int k = indexesBracketsList.get(j - 1) + 2; k < indexesBracketsList.get(j); k++) {
                    newExpressions[i] += expressions[k];
                }
                i++;
            }

            //SECONDLY: the following bracket.
            newExpressions[i] = expressions[indexesBracketsList.get(j)];
            newIndexesBrackets[j] = i;
            i++;
        }
        //Concatenate the last substrings after the last (existing!) bracket
        if (noFinalBracket) {
            newExpressions[i] = expressions[indexesBracketsList.get(indexesBracketListLength-1)+1]; //it exists! The
            //     last bracket, in position indexesBracketsList.get(indexesBracketListLength-1),
            //     is not the last element of expressions here.
            // Concatenate the rest
            for (int j = indexesBracketsList.get(indexesBracketListLength-1)+2; j < expressionsLength; j++) {
                newExpressions[i] += expressions[j];
            }
        }

        // Convert int[] to List<Integer>
        List<Integer> newIndexesBracketsList = new ArrayList<>(indexesBracketListLength);
        for (int j = 0; j < indexesBracketListLength; j++) {
            newIndexesBracketsList.add(newIndexesBrackets[j]);
        }
        return List.of(newExpressions, newIndexesBracketsList);
    }
}
