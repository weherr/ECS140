Bradley Singer,	997990414
William Herr,	998103103

Part 5:
New BNF rules:

statement ::= assignment | print | do | if | forLoop
forLoop ::= '$' number ':' block '?'

Reasoning: We modeled our "forLoop" after the "do" non-terminal. We arbitrarily chose the "begin" and "end" characters to be "$" and "?" respectively because they were not yet used. Instead of using "guarded_command," we just needed to get a number because we wanted our for loop to loop a specific amount of times. Calling block not only allows anything to be in our for loop, it also handles scoping.
