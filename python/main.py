import math
import re


def _evaluate_number(expression: str):
    try:
        return int(expression)
    except ValueError:
        try:
            return float(expression)
        except ValueError:
            raise ValueError(f"Could not resolve {expression}")


def _evaluate_single_expression(expression: str):
    """
    Evaluate a numerical expression with possible math functions.
    """
    try:
        index_bracket = expression.index("[")
    except ValueError:
        index_bracket = -1

    if index_bracket == -1:
        return _evaluate_number(expression)
    else:
        assert expression[-1] == "]"
        if index_bracket == 0:
            return _evaluate_number(expression[1:-1])
        else:
            assert expression[:index_bracket] in dir(math)
            math_fn = getattr(math,expression[:index_bracket])
            number = _evaluate_number(expression[index_bracket+1:-1])
            result = math_fn(number)
            if isinstance(number, int) and result % 1 == 0:
                result = int(result)
            return result


def _evaluate_power_expression(expression: str):
    """
    Evaluate a mathematical expression with "^".
    """
    single_expressions = re.split("(\^)", expression)
    result = _evaluate_single_expression(single_expressions[-1])
    len_s_exprs = len(single_expressions)
    assert len_s_exprs % 2 == 1
    for i in reversed(range(0,len_s_exprs-1,2)):
        symbol = single_expressions[i+1]
        m_expr = single_expressions[i]
        assert symbol == "^", f"Fatal error in _evaluate_power_expression: expected ^ but got {symbol}"
        result = _evaluate_single_expression(m_expr) ** result
    return result


def _evaluate_multiplicative_expression(expression: str):
    """
    Evaluate a mathematical expression with *,/,^ (or **)
    """
    expression = expression.replace("**","^")
    single_expressions = re.split("([\*/])", expression)
    result = _evaluate_power_expression(single_expressions[0])
    len_s_exprs = len(single_expressions)
    assert len_s_exprs % 2 == 1
    for i in range(2,len_s_exprs,2):
        symbol = single_expressions[i-1]
        m_expr = single_expressions[i]
        if symbol == "*":
            result *= _evaluate_power_expression(m_expr)
        else:
            assert symbol == "/", f"Fatal error in _evaluate_multiplicative_expression: expected * or / but got {symbol}"
            second_result = _evaluate_multiplicative_expression(m_expr)
            if isinstance(result,int) and isinstance(second_result, int) and result % second_result == 0:
                result //= _evaluate_multiplicative_expression(m_expr)   # could throw ZeroDivisionError
            else:
                result /= _evaluate_multiplicative_expression(m_expr)   # could throw ZeroDivisionError
    return result


def _find_last_symbol(expression: str):
    """
    Find the last index of "+", "-", "*", "/" or "^".
    Return -1 if there is no such symbol.
    """
    i = len(expression) - 1
    while i > -1:
        if expression[i] in ["+", "-", "*", "/", "^"]:
            return i
        i -= 1
    return -1


def _add_hidden_multiplication(expression: str):
    """
    Add hidden "*" signs.

    Args:
        expression: a mathematical expression given as a string.
        It can include +,-,*,/,^(or **) and every name
        of the functions of the Python module math.
        No brackets. Only square brackets which contained a number are allowed.
    """
    split_expression_square_brackets = re.split("([\[\]])", expression)
    len_split_expression_square_brackets = len(split_expression_square_brackets)
    i = 0
    while i < len_split_expression_square_brackets:
        expr = split_expression_square_brackets[i]
        if expr == "[" and i > 0:
            prev_expr = split_expression_square_brackets[i-1]
            if prev_expr == "" or prev_expr[-1] == "]":
                split_expression_square_brackets[i] = "*" + expr
            else:
                index_last_symbol = _find_last_symbol(prev_expr)
                if index_last_symbol != len(prev_expr) - 1:
                    math_fn = prev_expr[index_last_symbol+1:]
                    try:
                        float(math_fn)
                        split_expression_square_brackets[i] = "*" + expr
                    except ValueError:
                        if not math_fn in dir(math):
                            raise ValueError(f"Could not resolve {math_fn}")
            assert split_expression_square_brackets[i+1] not in ["[", "]"] 
            assert split_expression_square_brackets[i+2] == "]"
            i += 3
        else:
            i += 1 
    return "".join(split_expression_square_brackets)


def _evaluate_expression_without_brackets(expression: str):
    """
    Evaluate a mathematical expression given as a string.
    It can include +,-,*,/,^(or **) and every name
    of the functions of the Python module math.
    No brackets. Only square brackets which contained a number are allowed.
    """
    #Add a 0 if begins with "-"
    if expression[0] == "-":
        expression = "0" + expression
    expression = _add_hidden_multiplication(expression)
    multiplicative_expressions = re.split("([\+-])", expression)
    result = _evaluate_multiplicative_expression(multiplicative_expressions[0])
    len_m_exprs = len(multiplicative_expressions)
    assert len_m_exprs % 2 == 1
    for i in range(2,len_m_exprs,2):
        symbol = multiplicative_expressions[i-1]
        m_expr = multiplicative_expressions[i]
        if symbol == "+":
            result += _evaluate_multiplicative_expression(m_expr)
        else:
            assert symbol == "-", f"Fatal error in _evaluate_expression_without_brackets: expected + or - but got {symbol}"
            result -= _evaluate_multiplicative_expression(m_expr)
    return result


def _evaluate_semiexpression(semiexpression: str, start: int, level: int):
    """
    Evaluate a mathematical expression (which is supposed to be a
    substring of a proper expression, semiexpression = expression[start:])
    until it reaches a closed bracket at the base level of indentation.
    It returns also the index of this closed bracket (if any, otherwise -1).

    Args:
        semiexpression: the expression under examination
        start: the characters that there were before semiexpression in the
               supposed original expression
        level: the absolute level of indentation compared to the original expression

    Example:
        _evaluate_expression("5+sqrt(4))+2") returns (7,9).
        Indeed the first ")" close an other bracket. The first ")"
        at the base level is the second one, at the 9th index;
        besides, 5+sqrt(4) = 7
    """
    try:
        first_open = semiexpression.index("(")
    except ValueError:
        first_open = -1
    try:
        first_closed = semiexpression.index(")")
    except ValueError:
        first_closed = -1

    if first_open == -1:
        if first_closed == -1:
            # No brackets
            return _evaluate_expression_without_brackets(semiexpression), -1
        elif first_closed == 0:
            raise ValueError(f"Brackets wrapping nothing at index {start}")
        else:
            # No further open brackets.
            # Evaluate bracket-free expression until ")"
            return _evaluate_expression_without_brackets(semiexpression[:first_closed]), first_closed
    else:
        if first_closed == -1:
            raise ValueError(f"Unclosed bracket at index {start + first_closed}")
        elif first_closed < first_open:
            return _evaluate_expression_without_brackets(semiexpression[:first_closed]), first_closed
        else:
            inner_start = 0
            while True:     
                evaluate_first_in_brackets_expression, index_of_final_closed_bracket = _evaluate_semiexpression(semiexpression[first_open+1:], start + first_open + 1 + inner_start, level + 1)
                index_of_final_closed_bracket += first_open + 1
                len_evaluated_expression = index_of_final_closed_bracket - first_open - 1
                next = semiexpression[index_of_final_closed_bracket+1:]
                first_semiexpression = semiexpression[0:first_open] + "[" + str(evaluate_first_in_brackets_expression) + "]"
                try:
                    first_closed = next.index(")")
                except ValueError:
                    first_closed = -1
                try:
                    first_open = next.index("(")
                except ValueError:
                    first_open = -1
                if first_closed == -1:
                    if level > 0:
                        raise ValueError(f"Unclosed bracket at {start}")
                    else:
                        return _evaluate_expression_without_brackets(first_semiexpression + next), -1 
                elif first_open == -1 or first_closed < first_open:
                    return _evaluate_expression_without_brackets(first_semiexpression + next[:first_closed]), index_of_final_closed_bracket + first_closed + 1 + inner_start
                else:
                    # Restart with the following (adjusting inner_start
                    # to compensate the shift due to do evaluation):
                    semiexpression = first_semiexpression + next
                    first_open += len(first_semiexpression)
                    inner_start += len_evaluated_expression - len(str(evaluate_first_in_brackets_expression))


def evaluate_expression(expression: str):
    """
    Evaluate a mathematical expression given as a string.
    It can include +,-,*,/,^(or **),(,) and every name
    of the functions of the Python module math.
    """

    result, outer_closed_bracket = _evaluate_semiexpression(expression.replace(" ",""), 0, 0)
    if outer_closed_bracket != -1:
        raise ValueError(f"Unmatched closed bracket at index {outer_closed_bracket}")
    return result
    


# TEST ---------------

def test_one():
    assert evaluate_expression("4*(5+sqrt(12/3)+2*(2+5^1))-8") == 76
    try:
        evaluate_expression("4*(5+sqrt(12/3)+2*(2+5^1))-8)")
    except ValueError:
        pass
    else:
        assert False
    try:
        assert evaluate_expression("4*(5+sqrt(12/3)+2*(2+5^1)-8")
    except ValueError:
        pass
    else:
        assert False
    assert evaluate_expression("2(1+2)(2**3-5)") == 18

if __name__ == "__main__":
    test_one()
    print("Done.")