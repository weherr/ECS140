;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Your solution goes in this file ;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

 ;(defun simpleAdd3 (n)
 ;	(setq y (+ n 3))
 ;	)
 ; How does this function know to print y at end?

(defun cycle (n x)
	(loop for i from 1 to n
 		do
 			(setq head (car x))
 			(nconc x (list head))
 			(setq x (cdr x))
 	)
 	x
)

(defun split-list (a l)
	(setq answer '())
	(setq x '())
	(loop for item in l
		do (if (equal item a)
				(progn
					(unless (equal x nil)
						(progn
							(setq answer (append answer (list x)))
							(setq x '())
						)
					)
				)
				(progn
					(setq x (append x (list item)))
				)
			)

	)
	(unless (equal x nil)
		(setq answer (append answer (list x)))
	)
	answer
)


(defun range (l)
	(unless (equal 0 (length l))

		(setq max (car l))
		(setq min (car l))
		(setq mean 0)

		(loop for item in l
			do (when (> item max)
					(setq max item))
				(when (< item min)
					(setq min item))
				(setq mean (+ mean item))
		)

		(setq mean (/ mean (length l)))
		(setq answer (list min mean max))
	)

)



(defun search-count (a l)
	(setq num 0)
	(loop for item in l
		do (when (equal a item)
			(setq num (+ num 1))
			)
	)
	num
)


(defun pivot (num l)
	(setq less '())
	(setq greater '())

	(loop for item in l
		do (if (>= item num)
			(setq greater (append greater (list item)))
			(setq less (append less (list item)))
			)
	)

	(setq answer (list less greater))	
)



(defun quicksort (l)
	(unless (equal l '())
		(append 
			(quicksort (left (car l) (cdr l)))
			(cons (car l) nil)
			(quicksort (right (car l) (cdr l)))
		)
	)
)

(defun left (p l)
	(cond
		((or (null p)(null l)) nil)
		((< p (car l)) (left p (cdr l)))
		(t(cons (car l) (left p (cdr l))))
	)
)

(defun right (p l)
	(cond
		((or (null p)(null l)) nil)
		((>= p (car l)) (right p (cdr l)))
		(t(cons (car l) (right p (cdr l))))
	)
)

(defun match (first second)
	(setq ans T)
	(when (and (null first)(not(null second)))
		(setq ans NIL)
	)
	(when (> (length first)(length second)) 
		(setq ans NIL)
	)
	(loop while(not(null (car first))) do 
		(setq head1 (car first))
		(setq head2 (car second))
		(cond
			((equal head1 '?) 
				(when (equal head2 nil)
					(setq ans NIL)
				)
				(setq first (cdr first))
				(setq second (cdr second))
			)
			((equal head1 '!)
				(setq next (car (cdr first))) ; next = atom after head1 in first
				(setq numQ 0) 	; The number of question marks after an '!'
				(setq count 0) 	; The number of times we iterate throught the loop
				(loop while (equal next '?) do 	; Do the post '?'s and count them
					(setq first (cdr first))
					(setq second (cdr second))
					(setq head2 (car second))
					(setq next (car (cdr first)))
					(setq numQ (+ numQ 1))			
				)
				(when (and (null next) (null second)) 
					(setq ans NIL)
				)
				(loop while (and (and (not(equal next head2)) (not(null head2))) (not (equal next '!)) ) do
					(setq second (cdr second))	; Pop atom eaten by '!'
					(setq head2 (car second))
					(setq count (+ count 1))	; count how many atoms eaten by '!'
				)
				(when (and (equal count 0) (not (equal next '!)))
					(setq count (+ count 1))
					(setq second (cdr second))
				)
				(setq count (+ count 1))	; '!' must eat >= 1 atom (deals with immediatly hitting the delimiter)
				(setq second (cdr second))	; Pop the first instance of "next" in second
				(setq first (cdr first))	; Pop the '!'
				(unless (equal next '!)		; Don't pop if there is no delimiter
					(setq first (cdr first))	; Also pop "next" in first
				)
				; when there are '?'s after a '!' but there is nothing for '!' to eat
				(when (and (> numQ 0) (< count numQ))
					; (print "HERE?")
					(setq ans NIL)
				)
			)
			((equal head1 head2)
				; Pop the first element of both lists
				(setq first (cdr first))
				(setq second (cdr second))
			)
			((not(equal head1 head2))
				(setq first (cdr first))
				(setq second (cdr second))
				(setq ans NIL)
			)
		) ;end of switch statement	
	) ;end loop through first
	(when (not(equal (length first) (length second)))
		(setq ans NIL)
	)
	ans
)

(defun matrix-add (m1 m2)
	(setq ans '())
	(setq ans_row '())
	(setq row_m1 (car m1))
	(setq row_m2 (car m2))
	(setq row_elm_m1 (car row_m1))
	(setq row_elm_m2 (car row_m2))
	; Loop through each row and each atom in each row
	(loop while (not (null row_m1)) do
		(loop while (not (null row_elm_m1)) do
			; Add the two elements togther and append to ans list
			(setq sum (+ row_elm_m1 row_elm_m2))
			(setq ans_row (append ans_row (list sum)))
			(setq row_elm_m1 (car (cdr row_m1)))	; get next elm in rows
			(setq row_m1 (cdr row_m1))
			(setq row_elm_m2 (car (cdr row_m2)))
			(setq row_m2 (cdr row_m2))
		)

		(setq ans (append ans (list ans_row)))	; append the added row
		(setq ans_row '())						; clear the ans_row buffer
		
		(setq row_m1 (car (cdr m1)))	; get next row of matrices
		(setq row_elm_m1 (car row_m1))
		(setq m1 (cdr m1))
		(setq row_m2 (car (cdr m2)))
		(setq row_elm_m2 (car row_m2))
		(setq m2 (cdr m2))
	)

	ans
)

(defun matrix-multiply (m1 m2)
	(setq num_rows_m1 (length m1))
	(setq num_rows_m2 (length m2))
	(setq num_col_m1 (length (car m1)))
	(setq num_col_m2 (length (car m2)))
	(setq ans '())

	(loop for i from 0 to (- num_rows_m1 1) do
		(setq row_ans '())
		(loop for j from 0 to (- num_col_m2 1) do
			(setq product 0)
			(loop for k from 0 to (- num_col_m1 1) do
				(setq elm_m1 (nth k (nth i m1)))
				(setq elm_m2 (nth j (nth k m2)))
				; (format t "~%M1 elem: ~S   M2 elem: ~S" elm_m1 elm_m2)
				(setq product (+ product (* elm_m1 elm_m2)))
			)
			(setq row_ans (append row_ans (list product)))
		)
		(setq ans (append ans (list row_ans)))
	)

	ans
)





; (defun matrix-multiply (m1 m2)
; 	(setq ans '())
; 	(setq ans_row '())
; 	(setq row_m1 (car m1))
; 	(setq row_m2 (car m2))
; 	(setq col_m1 (car row_m1))
; 	(setq col_m2 (car row_m2))
; 	(setq num_rows (length m1))
	

; 	(loop while (not (null row_m1)) do		
; 		(setq product 0)
; 		(setq count 0)
; 		(setq out_copy m2)
; 		(loop while () do
; 			(setq m2_copy m2)
; 			(setq c 0)
; 			(loop while(not (equal c count)) do
; 				(setq m2_copy (cdr m2_copy))
; 				(setq c (+ c 1))
; 			)

; 			(setq product (+ product (* col_m1 col_m2)))
; 		)

; 		(setq ans_row (append ans_row (list product)))

; 		(setq m1 (cdr m1))
; 		(setq row_m1 (car m1))
; 	)

; 	ans
; )







