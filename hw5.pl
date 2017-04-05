/*******************************************/
/**    Your solution goes in this file    **/ 
/*******************************************/

/**********************  PART 1  **********************/

/* 1) */
year_1953_1996_novels(X):- novel(X,1996); novel(X, 1953).

/* 2) */
period_1800_1900_novels(X):- novel(X, R), R >= 1800, R =< 1900.

/* 3) */
lotr_fans(X):- fan(X, Y), member(the_lord_of_the_rings, Y).

/* 4) */
match_2(Y,Z) :- member(X,Y),member(X,Z),!.
author_names(X):- fan(chandler, Y), author(X,Z), match_2(Y, Z).

/* 5) */
fans_names(X):- author(brandon_sanderson, Y), fan(X, Z), match_2(Y, Z).

/* 6) */
mutual_novels(X):- 	novel(X, D), fan(monica, Y), fan(phoebe, Z), match_2([X], Y), match_2([X], Z);
					novel(X, D), fan(monica, Y), fan(ross, Z), match_2([X], Y), match_2([X], Z);
					novel(X, D), fan(phoebe, Y), fan(ross, Z), match_2([X], Y), match_2([X], Z).

/**********************  PART 2  **********************/

/* 1) */
isMember(X, [H|T]):- X = H; isMember(X, T). 

/* 2) */
isUnion([],L,L).
isUnion([H|T],L2,L3):- isMember(H,L2), isUnion(T,L2,L3), !.
isUnion([X|L1],L2,[X|L3]):- isUnion(L1,L2,L3).

/* 3) */
isIntersection([],_,[]).
isIntersection([X|L1],L2,[X|L3]):- isMember(X, L2), isIntersection(L1,L2,L3), !.
isIntersection([X|L1],L2,L3):- isIntersection(L1,L2,L3).

/* 4) */
isEqual([],[]), !.
isEqual([],L2).
isEqual([H|T],L2):- isMember(H,L2), isEqual(T,L2).

/* 5) */
myappend([],L,L).
myappend([X|L1],L2,[X|L3]):- myappend(L1,L2,L3).

powSetTail(X, [Y], [[X| Y]]).
powSetTail(X, [H| T], P) :- myappend([X], H, Temp), powSetTail(X, T, Tail),
        					myappend([Temp], Tail, P), !.

powerSet([], [[]]).
powerSet([H| T], P) :- 	powerSet(T, PowTail), powSetTail(H, PowTail, Pow),
        				myappend(Pow, PowTail, P), !.
