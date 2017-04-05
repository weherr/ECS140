/* *** This file is given as part of the programming assignment. *** */

import java.util.*; // for Vector container

public class Parser {

    // tok is global to all these parsing methods;
    // scan just calls the scanner's scan method and saves the result in tok.
    private Token tok; // the current token
    // Blocks with unique tables
    private Vector<Vector<String>> v_block = new Vector<Vector<String>>();
    private void scan() {
	    tok = scanner.scan();
    }

    private Scan scanner;
    Parser(Scan scanner) {
	    this.scanner = scanner;
	    scan();
	    program();
	    if( tok.kind != TK.EOF )
	        parse_error("junk after logical end of program");
    }

    private void program() {
	    block();
    }

    private void block(){
        v_block.addElement( new Vector<String>() ); //add a new block
	    declaration_list();
	    statement_list();
	    v_block.remove( v_block.size() - 1 ); // pop last block
    }

    private void declaration_list() {
	// below checks whether tok is in first set of declaration.
	// here, that's easy since there's only one token kind in the set.
	// in other places, though, there might be more.
	// so, you might want to write a general function to handle that.
	    while( is(TK.DECLARE) ) {
	        declaration();
	    }
    }

    private void declaration() {
    	mustbe(TK.DECLARE);
    // 	mustbe(TK.ID);
    	if( tok.kind != TK.ID ) {
    	    System.err.println( "mustbe: want " + TK.ID + ", got " + tok);
    	    parse_error( "missing token (mustbe)" );
    	    System.exit(1);
    	}
    	
    	// Check table/add new variable
    	// If the variable is being redeclaired
    	if( v_block.get( v_block.size() - 1 ).contains(tok.string) ) {
    	    System.err.println( "redeclaration of variable " + tok.string );
    	} else {
    	    // add the var at the end of the most recent block
    	    v_block.get( v_block.size() - 1 ).add( new String(tok.string) );
    	}
    	
    	scan(); // remnants of "mustbe()"
	    while( is(TK.COMMA) ) {
	        scan();
	        // mustbe(TK.ID);
	        if( tok.kind != TK.ID ) {
        	    System.err.println( "mustbe: want " + TK.ID + ", got " + tok);
        	    parse_error( "missing token (mustbe)" );
        	    System.exit(1);
    	    }

        	if( v_block.get( v_block.size() - 1 ).contains(tok.string) ) {
        	    System.err.println( "redeclaration of variable " + tok.string );
        	} else {
        	    // add the var at the end of the most recent block
        	    v_block.get( v_block.size() - 1 ).add( new String(tok.string) );
        	}
        	
        	scan(); // remnants of "mustbe()"
	    } //while
    } //declaration()

    private void statement_list() {
        // assignment (2) | print | do | if
        while( is(TK.TILDE) || is(TK.ID) || 
        is(TK.PRINT) || is(TK.DO) || is(TK.IF) ) {
            statement();
        }
    }

    private void statement() {
        if( is(TK.TILDE) || is(TK.ID) )
            assignment();
        else if( is(TK.PRINT) )
            print_func();
        else if( is(TK.DO) )
            do_func();
        else if( is(TK.IF) )
            if_func();
        // else
        //     parse_error( "INVALID 'STATEMENT'" );
    }

    // "statement" parts
    private void assignment() {
        //System.err.println("IN ASSIGNMENT");
        ref_id();
        mustbe(TK.ASSIGN);
        expr();
    }
    
    private void print_func() {
        //System.err.println("IN PRINT");
        mustbe(TK.PRINT);
        expr();
    }
    
    private void do_func() {
        mustbe(TK.DO);
        guarded_command();
        mustbe(TK.ENDDO);
    }
    
    private void if_func() {
        mustbe(TK.IF);
        guarded_command();
        while( is(TK.ELSEIF) ) {
            scan();
            guarded_command();
        }
        
        if( is(TK.ELSE) ) {
            scan();
            block();
        }
        
        mustbe(TK.ENDIF);
    }
    // END "statement" parts
    // "assignment" parts
    private void ref_id() {
        int num = 0; // The block the scope operater indicates
        int depth = (v_block.size() - 1); // depth of nesting of curr block 
        
        if( is(TK.TILDE) ) {
            // scan();
            mustbe(TK.TILDE);
            //*********TESTING***********
            // System.err.println( "KIND = " + tok.kind + " NAME = "
            // + tok.string);
            if( is(TK.NUM) ) {
                num = Integer.parseInt(tok.string);
                // **************v TESTING v****************
                // System.err.println( "BLOCK = " + depth +"  NUM = " + num 
                // + "  LOOK = " + (depth - num) );
                // System.err.print( "LOOK Block:");
                // for(int k = 0; k < v_block.get(depth - num).size(); k++){
                //     System.err.print( " " + v_block.get(v_block.size()-1).get(k) );
                // }
                // System.err.print("\n");
                
                scan(); // get variable
                if( num > depth ){
                    // ^ looking past # of blocks (out of bounds of block vector)
                    System.err.print( "no such variable ~" + num);
                    System.err.println( tok.string + " on line "
                    + tok.lineNumber);
                } else if( !v_block.get(depth - num).contains(tok.string) ){
                    // ^ check given # of blocks up
                    // System.err.println( "Checking for: " + tok.string );
                    System.err.print( "no such variable ~" + num);
                    System.err.println( tok.string + " on line "
                    + tok.lineNumber);
                }
            } else {// no number -> global variable check
                if( !v_block.get(0).contains(tok.string) ){
                    System.err.print( "no such variable ~" + tok.string);
                    System.err.println( " on line " + tok.lineNumber);
                }
            }
        } // if TK.TILDE

        //mustbe(TK.ID);
        if( tok.kind != TK.ID ) {
    	    System.err.println( "mustbe: want " + TK.ID + ", got " + tok);
    	    parse_error( "missing token (mustbe)" );
    	}
    	// Check if variable is ANYWHERE in table
    	for(int i = 0; i < v_block.size(); i++) {
	        if( v_block.get(i).contains(tok.string) ) {
	            break;
	        } 
	        if( i == v_block.size() - 1 ) { // var not found
	            System.err.println( tok.string + 
	            " is an undeclared variable on line " + tok.lineNumber);
	            System.exit(1);
	        }
    	}
    	
    	scan(); // remnants of "mustbe()"
    }
    
    private void expr() {
        term();
        while( is(TK.PLUS) || is(TK.MINUS) ) { // addop
            addop();
            term();
        }
    }
    // END "assignment" parts

    private void guarded_command() {
        expr();
        mustbe(TK.THEN);
        block();
    }

    private void term() {
        factor();
        while( is(TK.TIMES) || is(TK.DIVIDE) ) { // multop
            multop();
            factor();
        }
    }
    
    private void factor() {
        if( is(TK.LPAREN) ) {
            mustbe(TK.LPAREN);
            expr();
            mustbe(TK.RPAREN);
        } else if( is(TK.TILDE) || is(TK.ID) ) {
            ref_id();
        } else if( is(TK.NUM) ) {
            mustbe(TK.NUM);
        }
        // else {
        //     parse_error( "INVALID 'FACTOR'" );
        // }
    }
    
    private void addop() {
        if( is(TK.PLUS) ) {
            mustbe(TK.PLUS);
        } else if( is(TK.MINUS) ) {
            mustbe(TK.MINUS);
        }
        // else {
        //     parse_error( "INVALID 'ADDOP'" );
        // }
    }
    
    private void multop() {
        if( is(TK.TIMES) ) {
            mustbe(TK.TIMES);
        } else if( is(TK.DIVIDE) ) {
            mustbe(TK.DIVIDE);
        }
        // else {
        //     parse_error( "INVALID 'MULTOP'" );
        // }
    }

    // is current token what we want?
    private boolean is(TK tk) {
        return tk == tok.kind;
    }

    // ensure current token is tk and skip over it.
    private void mustbe(TK tk) {
    	if( tok.kind != tk ) {
    	    System.err.println( "mustbe: want " + tk + ", got " + tok);
    	    parse_error( "missing token (mustbe)" );
    	}
    	scan();
    }

    private void parse_error(String msg) {
    	System.err.println( "can't parse: line "
    	    + tok.lineNumber + " " + msg );
    	System.exit(1);
    }
}
