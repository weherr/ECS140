abstract class Element {


	// Empty Print function this will be overridden in the following classes
	public void Print(){}

	// Empty function so i can use GetElm for other classes when its a known Element
	public Element GetElm(){
		return this;
	}

	// Empty function so i can use GetNext for other classes when its a known Element
	public Sequence GetNext(){
		Sequence s = new Sequence();
		return s;
	}

}

// Class MyChar which extends Element. A MyChar holds a value which is a single char
class MyChar extends Element{

	private Character value;

	// MyChar constructor - sets value to 0
	public MyChar() {
		value = '0';
		return;
	}

	@Override
	public Element GetElm() {
		return this;
	}

	// A function to get the value
	public Character Get(){
		return value;
	}

	// A function to set the value
	public void Set(Character val){
		value = val;
		return;
	}


	// A function to print the value
	@Override
	public void Print(){
		System.out.print("'" + value + "'");
	}
}

// Class MyInteger which extends Element. A MyInteger holds a single value which is a number
class MyInteger extends Element{

	private Integer value;

	// The MyInteger constructor which sets the value to be 0
	public MyInteger() {
		value = 0;
		return;
	}

	@Override
	public Element GetElm() {
		return this;
	}

	// A function to get the integer
	public Integer Get(){
		return value;
	}

	// A function to set the integer value
	public void Set(Integer val){
		value = val;
		return;
	}

	// A function to print the held integer
	@Override
	public void Print(){
		System.out.print(value);
	}

}

/*Class Seequence which extends Element. A sequence can be thought of as a node in a linked list.
Each seqence holds an element which can be a MyChar, MyInteger, or another Sequence. Also stores 
a reference to the next Sequence which creates a chain (much like a linked list)
*/
class Sequence extends Element{

	private Element s_elm;
	private Sequence next;
	private Sequence nextFlat;   // holds the next sequence in the flat chain
	private boolean isFlat;		// Tells print which sequence chain to follow next or nextFlat

	// Sequence class constructor which sets both the held element and next sequence to null.
	public Sequence() {
		s_elm = null;
		next = null;
		nextFlat = null;
		isFlat = false;
		return;
	}


	public SequenceIterator begin(){
		SequenceIterator begin = new SequenceIterator();
		begin.SetSequence(this);
		return begin;
	}


	// checks to see if there is an end sequence if not I add it if there is I just return it.
	public SequenceIterator end(){
		if(this.GetLast().GetElm() instanceof MyChar){
			if(((MyChar)this.GetLast().GetElm()).Get() == '+'){
				SequenceIterator endItr = new SequenceIterator();
				endItr.SetSequence(this.GetLast().GetNext()); 
				return endItr;
			}
		}
		SequenceIterator endItr = new SequenceIterator();
		Sequence end = new Sequence();
		MyChar elm = new MyChar();
		elm.Set('+');
		end.SetElm(elm);
		endItr.SetSequence(end);
		this.GetLast().SetNext(end);

		return endItr;
	}

	// Returns the first element in the sequence. Because this can only be called on the 
	// first sequence in a chain it returns "this" element.
	public Element first(){
		return s_elm;
	}

	// Sets the Element of the current sequence
	public void SetElm(Element elm){
		this.s_elm = elm;
	}

	// Returns the Element held in this sequence used only in other sequence 
	// functions not in given TEST files
	@Override
	public Element GetElm(){
		return s_elm;
	}

	// Sets the next in the sequence chain.
	public void SetNext(Sequence e){
		this.next = e;
	}

	// Sets the next flat sequence in the chain.
	public void SetNextFlat(Sequence e){
		this.nextFlat = e;
	}

	//Returns the next in the sequence chain.
	public Sequence GetNext(){
		return next;
	}

	public Sequence GetNextFlat(){
		return nextFlat;
	}

	// Return the last Sequence in a chain
	public Sequence GetLast(){
		Sequence itr = new Sequence();
		if(this.GetElm() == null){
			System.err.println("Nothing in Sequence, exiting program");
			System.exit(1);		
		}
		else if(this.GetNext() == null){
			return this;
		}

		else if(this.GetNext().GetElm() instanceof MyChar && ((MyChar)this.GetNext().GetElm()).Get() == '+'){
			return this;
		}

		else{
			itr = this.GetNext();
			while(itr.GetNext() != null){
				if(itr.GetNext().GetElm() instanceof MyChar && ((MyChar)itr.GetNext().GetElm()).Get() == '+'){
					return itr;
				}
				itr = itr.GetNext();
			}
		}
		return itr;
	}

	// Returns the last sequence in the flatten chain
	public Sequence GetLastFlat(){
		Sequence itr = new Sequence();
		if(this.GetNextFlat() == null){
			return this;
		}

		else{
			itr = this.GetNextFlat();
			while(itr.GetNextFlat() != null){
				itr = itr.GetNextFlat();
			}
		}
		return itr;
	}


	// Adds a new sequence (storing the argument elm) into the sequence chain at a given postition.
	public void add(Element elm, int pos){
		// If pos is less than 0 this is an invalid position so exit the program
		if(pos<0){
			System.err.println("Invalid pos, exiting");
			System.exit(1);
		}
		// If no Element in sequence yet we at it to the first spot.
		// Also checks that we want to put at pos 0. If not then we are entering an invalid pos 
		// which is handled in the else statment.
		if(s_elm == null && pos == 0){
			this.SetElm(elm);
		}
		// If adding to the beginning of sequence
		// Were adding a new sequence to pos 1 and swapping the elm with the first 
		else if(pos==0){
			Sequence insert = new Sequence();
			insert.SetElm(this.GetElm());
			Sequence hold = this.GetNext();
			this.SetNext(insert);
			insert.SetNext(hold);
			this.SetElm(elm);			
		}
		// If we want to put at pos 1 we need a special case because we can not create a 
		// reference to "this" (the first in the sequence chain) without changing what this refers to.
		else if(pos == 1){
			Sequence insert = new Sequence();
			insert.SetElm(elm);
			Sequence hold = this.GetNext();
			this.SetNext(insert);
			insert.SetNext(hold);
		}
		else{
			Sequence location = this.GetNext();
			int i = 1;
			while(i != pos - 1){
				if(location.GetNext() == null){
					System.err.println("Invalid pos, exiting");
					System.exit(1);
				}
				// check to see if element is end plus
				if(location.GetNext().GetElm() instanceof MyChar && ((MyChar)location.GetNext().GetElm()).Get() == '+'){
					System.err.println("Invalid pos, exiting");
					System.exit(1);
				}
				location = location.GetNext();
				i++;
			}
			Sequence insert = new Sequence();
			insert.SetElm(elm);
			Sequence temp = location.GetNext();
			location.SetNext(insert);
			insert.SetNext(temp);

		}
	}

	// A function to delete works similar to add with special cases for deleting the first and second
	// elements.
	// NOTE: might need to check if pos is greater than # of elements
	public void delete(int pos){
		if(pos<0){
			System.err.println("Invalid pos, exiting");
			System.exit(1);
		}

		// If no elements in the sequence we can not delete anything so throw an error exit the program (MAYBE)????
		if(this.GetElm() == null){
			System.err.println("Called delete with no elements in the Sequence");
			//System.exit(1);	
		}

		// NOTE: DO WE NEED!!!!!!!!!!!????
		// If one element set first to null 
		if(this.GetNext() == null){
			this.SetElm(null);
		}

		if(this.GetNext().GetElm() instanceof MyChar && ((MyChar)this.GetNext().GetElm()).Get() == '+'){
			this.SetElm(null);
		}

		// If we want to delete the first pos we need to replace it with the element in the second pos
		// Then skip over the second position
		else if(pos == 0){
			this.SetElm(this.GetNext().GetElm());
			this.SetNext(this.GetNext().GetNext());
		}
		else if(pos == 1){
			this.SetNext(this.GetNext().GetNext());
		}
		else{
			Sequence location = this.GetNext();
			int i = 1;
			while(i != pos - 1){
				location = location.GetNext();
				i++;
			}
			location.SetNext(location.GetNext().GetNext());
		}
	}

	public Sequence rest(){
		return this.GetNext();
	}

	// uses an itr to return the length of the chain
	public int length(){
		if(this.GetElm() == null){
			return 0;
		}
		int count = 1;
		Sequence itr = new Sequence();
		itr = this;
		while(itr.GetNext() != null){
			if(itr.GetNext().GetElm() instanceof MyChar && ((MyChar)itr.GetNext().GetElm()).Get() == '+'){
				return count;
			}
			itr = itr.GetNext();
			count++;
		}
		return count;
	}

	// Returns the Element at a given pos
	public Element index(int pos){
		if(this.GetElm() == null){
			System.err.println("Nothng in sequence");
			System.exit(1);
		}
		Sequence itr = new Sequence();
		itr = this;
		int count = 0;
		while(count != pos){
			itr = itr.GetNext();
			count++;
		}
		return itr.GetElm();	
	}


	// This function cretes a new first sequence then creates a flatten chain from it
	// The reason I did it this way was because it needed to be a shallow copy (to pass the tests)
	// however I needed to change the "next" if there was an instance of a sequence.
	// To solve this I created a new next called flatNext and started a chain from the new head
	public Sequence flatten(){
        Sequence old = this;
        Sequence flat = new Sequence();
        flat.isFlat = true;
        while(old != null){
        	if(old.GetElm() instanceof Sequence){
        		Sequence insert = new Sequence();
        		insert = ((Sequence)old.GetElm()).flatten();
     			if(flat.GetElm() == null){
    				flat = insert;
    			}
    			else{
        			flat.GetLastFlat().SetNextFlat(insert); 
        		}      		
        	}
        	else{
        		if(flat.GetElm() == null){
        			flat.SetElm(old.GetElm());
        		}
        		else{
        			flat.GetLastFlat().SetNextFlat(old);
        		}
        	}
        	old = old.GetNext();
        }

        return flat;
	}
	

	// Function copy which is recursive loops through the old and creates a new sequence for every 
	// old sequence
	public Sequence copy(){
        Sequence old = this;
        Sequence head = new Sequence();
        int pos = 0;
        while(old != null){
        	if(old.GetElm() instanceof Sequence){
        		head.add(((Sequence)old.GetElm()).copy(), pos);
        	}
        	else{
        		if(old.GetElm() instanceof MyChar){
	        		MyChar insert = new MyChar();
	        		insert.Set(((MyChar)old.GetElm()).Get());
	        		head.add(insert, pos);	  
	        	}
	        	else{
	        		MyInteger insert = new MyInteger();
	        		insert.Set(((MyInteger)old.GetElm()).Get());
	        		head.add(insert, pos);       		
	        	}
        	}
        	old = old.GetNext();
        	pos = pos + 1;
        }
        return head;
	}



	public void Print(){
		Sequence itr = new Sequence();
		itr = this;
		System.out.print("[ ");
		if(this.isFlat == false){
			while(itr != null){
				if(itr.GetElm() instanceof MyChar && ((MyChar)itr.GetElm()).Get() == '+'){
					System.out.print("]");
					return;
				}
				itr.GetElm().Print();
				System.out.print(" ");
				itr = itr.GetNext();
			}
		}
		else{
			while(itr != null){
				if(itr.GetElm() != null){   
					itr.GetElm().Print();
					System.out.print(" ");
				}
				itr = itr.GetNextFlat();
			}			
		}
		System.out.print("]");
	}


}

class SequenceIterator extends Sequence{
	private Sequence seq;

	public SequenceIterator(){
		this.seq = null;
	}

	public void SetSequence(Sequence sequence){
		this.seq = sequence;
	}

	public Element get(){
		return this.seq.GetElm();
	}

	public Sequence GetSequence(){
		return seq;
	}

	// Advances the itr to next sequence
	public void advance(){
		if(this.GetSequence().GetNext() == null){
			System.err.println("already at end - exiting program");
			System.exit(1);
		}
		this.seq = this.GetSequence().GetNext();
	}


	// Our equal should be only used to compare the end Sequence Iterator
	// This is why we cast as my char because our end special char is a MyChar with the value '+'
	public boolean equal(SequenceIterator other){
		if(this.get() instanceof MyChar && other.get() instanceof MyChar){
			if(((MyChar)this.get()).Get() == ((MyChar)other.get()).Get()){
				return true;
			}
		}
		return false;

	}
}

class Matrix extends Sequence{

	private int numrow;
	private int numcol;
	private Sequence[] mat;

	// Create a matrix of a certain size.
	// We use a Sequence array and create the right amount of inner sequences for each one.
	public Matrix(int rowsize, int colsize){
		this.numrow = rowsize;
		this.numcol = colsize;
		this.mat = new Sequence[rowsize];
		for(int r=0; r<rowsize; r++){
			Sequence head = new Sequence();
			MyInteger zero = new MyInteger();
			head.SetElm(zero);
			mat[r] = head;
			for(int i=1; i<colsize;i++){
				MyInteger zero2 = new MyInteger();
				mat[r].add(zero2, i);
			}
		}
	}

	public int GetRow(){
		return this.numrow;
	}

	public int GetCol(){
		return this.numcol;
	}

	public void Set(int rowsize, int colsize, int value){
		Sequence itr = new Sequence();

		itr = this.mat[rowsize];
		for(int i=0;i<colsize;i++){
			itr = itr.GetNext();
		}
		((MyInteger)itr.GetElm()).Set(value);

	}

	public int Get(int rowsize, int colsize){
		Sequence itr = new Sequence();
		itr = this.mat[rowsize];
		for(int i=0;i<colsize;i++){
			itr = itr.GetNext();
		}
		return ((MyInteger)itr.GetElm()).Get();
	}

	public Matrix Sum(Matrix mat){
		if(mat.GetCol() != this.GetCol() || mat.GetRow() != this.GetRow()){
			System.out.println("Matricies need to have same number rows and collums for add");
			System.exit(1);
		}
		// Copy this
		Matrix sum = new Matrix(this.GetRow(), this.GetCol());
		for(int r=0; r<this.GetRow(); r++){
			sum.mat[r] = this.mat[r].copy();
		}

		Sequence itr = new Sequence();
		Sequence itr2 = new Sequence();
		for(int r=0; r<this.GetRow(); r++){
			itr = sum.mat[r];
			itr2 = mat.mat[r];
			for(int i=0; i<this.GetCol(); i++){
				int s = ((MyInteger)itr.GetElm()).Get();
				s += ((MyInteger)itr2.GetElm()).Get();
				((MyInteger)itr.GetElm()).Set(s);
				itr = itr.GetNext();
				itr2 = itr2.GetNext();
			}
		}

		return sum;
	}

	public Matrix Product(Matrix mat){
		if(this.GetCol() != mat.GetRow()){
			System.out.println("Matrix dimensions incompatible for Product");
			//NEED TO RETURN OSMETHING I THINK
			System.exit(1);
		}
		Matrix product = new Matrix(this.GetRow(), mat.GetCol());
		int sum;
		for(int whichRow=0; whichRow<this.GetRow(); whichRow++){
			for(int c=0; c<mat.GetCol(); c++){
				sum = 0;
				for(int x=0; x<this.GetCol(); x++){
					sum += this.Get(whichRow, x) * mat.Get(x, c);
				}
				product.Set(whichRow,c, sum);
			}
		}

		return product;
	}


	@Override
	public void Print(){
		for(int r=0; r<this.numrow; r++){
			mat[r].Print();
			System.out.println();
		}
	}

}


class Map{
	private Pair first;

	public Map(){
		this.first = null;
	}

	public Pair GetFirst(){
		return this.first;
	}

	public void SetFirst(Pair p){
		this.first = p;
	}


	public void add(Pair inval){
		Pair itr = this.GetFirst();
		Pair pre = new Pair();
		if(itr == null){
			this.SetFirst(inval);
		}
		else if(itr.GetKey().Get() > inval.GetKey().Get()){
			this.SetFirst(inval);
			this.GetFirst().SetNext(itr);
		}
		else if(itr.GetNext() == null && itr.GetKey().Get() < inval.GetKey().Get()){
			itr.SetNext(inval);
		}
		else{
			while (itr != null && itr.GetKey().Get() <= inval.GetKey().Get()){
				pre = itr;
				itr = itr.GetNext();
			}
			if(itr == null){
				pre.SetNext(inval);
			}
			else{
				pre.SetNext(inval);
				inval.SetNext(itr);
			}
		}
	}

	public MapIterator begin(){
		MapIterator begin = new MapIterator();
		begin.SetPair(this.first);
		return begin;

	}

	public MapIterator end(){
		if(this.GetLast().GetKey().Get() != '+' && this.GetLast().GetValue() instanceof MyChar &&
			((MyChar)this.GetLast().GetValue()).Get() != '+'){
				MapIterator endItr = new MapIterator();
				endItr.SetPair(this.GetLast().GetNext()); 
				return endItr;
		}
		MapIterator endItr = new MapIterator();
		Pair end = new Pair();
		MyChar plus = new MyChar();
		plus.Set('+');


		end.SetKey(plus);
		end.SetValue(plus);
		endItr.SetPair(end);
		this.GetLast().SetNext(end);

		return endItr;
	}

	public Pair GetLast(){
		Pair itr = new Pair();
		itr = first;
		while(itr.GetNext() != null){
			if(itr.GetNext().GetKey().Get() == '+' && itr.GetNext().GetValue() instanceof MyChar
				&& ((MyChar)itr.GetNext().GetValue()).Get() == '+'){
				return itr;
			}
			itr = itr.GetNext();
		}
		return itr;
	}

	public MapIterator find(MyChar key){
		Pair itr = this.GetFirst();
		while(itr != null && itr.GetKey().Get() != key.Get()){
			itr = itr.GetNext();
		}
		MapIterator x;
		if(itr == null){
			x = this.end();
		}
		else{
			x = new MapIterator();
			x.SetPair(itr);
		}
		return x;
	}

	public void Print(){
		Pair itr = this.GetFirst();
		System.out.print("[ ");
		while(itr.GetNext() != null){
			System.out.print("(");
			itr.GetKey().Print();
			System.out.print(' ');
			itr.GetValue().Print();
			System.out.print(") ");
			itr = itr.GetNext();
		}
		System.out.print("(");
		itr.GetKey().Print();
		System.out.print(' ');
		itr.GetValue().Print();
		System.out.print(") ");
		System.out.print("]");
	}

}



class Pair{
	private MyChar key;
	private Element value;
	private Pair next;

	public Pair(MyChar key, Element val){
		this.key = key;
		this.value = val;
	}

	public Pair(){
		this.key = null;
		this.value = null;
		this.next = null;
	}

	public MyChar GetKey(){
		return this.key;
	}

	public Element GetValue(){
		return this.value;
	}

	public void SetNext(Pair p){
		this.next = p;
	}

	public void SetKey(MyChar c){
		this.key = c;
	}

	public void SetValue(Element e){
		this.value = e;
	}

	public Pair GetNext(){
		return this.next;
	}

	public void Print(){
		System.out.print("(");
		this.GetKey().Print();
		System.out.print(" ");
		this.GetValue().Print();
		System.out.print(")");
	}
}


class MapIterator{
	private Pair pair;

	public MapIterator(){
		this.pair = null;
	}

	public void SetPair(Pair p){
		this.pair = p;
	}

	public Pair get(){
		return this.pair;
	}

	public void advance(){
		if(this.get().GetNext() == null){
			System.err.println("already at end - exiting program");
			System.exit(1);
		}
		this.pair = this.get().GetNext();
	}


	public boolean equal(MapIterator other){
		if(this.get().GetKey().Get() == other.get().GetKey().Get()){
			return true;
		}
		return false;

	}

}