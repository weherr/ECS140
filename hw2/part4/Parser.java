/* *** This file is given as part of the programming assignment. *** */

import java.util.*; // for Vector container

public class Parser {

    // tok is global to all these parsing methods;
    // scan just calls the scanner's scan method and saves the result in tok.
    private Token tok; // the current token
    // Blocks with unique tables
    private Vector<Vector<String>> v_block = new Vector<Vector<String>>();
    // Save expr for printf argument
    private String expr_str = new String( "" );
    // tells when to record the expression in expr_str for printf
    private boolean isprint = false;
    // if there is a declaration_list, add a ';' to the end
    private boolean declared_bool = false;
    // solves the comma problem when the first variable in a declaration list
    // is a redeclaration e.g. we don't want this: int , a, b;
    private boolean first_var = false;
    // Signals the case when there is no tilde and thus the var is local
    private boolean no_tilde = true;
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
        System.out.println( "#include <stdio.h>" );
        System.out.println( "int main(){" );
	    block();
	   // System.out.println( "printf(\"" + "\\n" + "\");" );
	    System.out.println( "return 0;\n} /* END MAIN */" );
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
	        declared_bool = true;
	        declaration();
	        if( declared_bool ){
	            System.out.println( ";" );
	        }
	 
	        declared_bool = false;
	    }
    }

    private void declaration() {
    	mustbe(TK.DECLARE);
    	System.out.print( "int " );
    // 	mustbe(TK.ID);
    	if( tok.kind != TK.ID ) {
    	    System.err.println( "mustbe: want " + TK.ID + ", got " + tok);
    	    parse_error( "missing token (mustbe)" );
    	    System.exit(1);
    	}
    	
    	// Check table/add new variable
    	// If the variable is being redeclared
    	if( v_block.get( v_block.size() - 1 ).contains(tok.string) ) {
    	    System.err.println( "redeclaration of variable " + tok.string );
    	} else {
    	    System.out.print( "x_" + (v_block.size()-1) + tok.string );
    	    first_var = true;
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
        	    if( first_var ){ // if the first var is not a duplicate
	                System.out.print( ", " );
	            }
	            
	            System.out.print( "x_" + (v_block.size()-1) + tok.string );
	            first_var = true;
        	    // add the var at the end of the most recent block
        	    v_block.get( v_block.size() - 1 ).add( new String(tok.string) );
        	}
        	
        	scan(); // remnants of "mustbe()"
	    } //while
	    
	    first_var = false;
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
        System.out.print( " = " );
        expr();
        System.out.println(";");
    }
    
    private void print_func() {
        //System.err.println("IN PRINT");
        mustbe(TK.PRINT);
        isprint = true;
        System.out.print( "printf(\"%d\\n\"" );
        expr();
        isprint = false;
        System.out.println( "," + expr_str + ");" );
        expr_str = ""; // clear the expr buffer for the next printf
    }
    
    private void do_func() {
        System.out.print( "while( (" );
        mustbe(TK.DO);
        guarded_command();
        mustbe(TK.ENDDO);
        System.out.println( "} /* END WHILE */" );
    }
    
    private void if_func() {
        System.out.print( "if( (" ); // negated because E rules are opposite C
        mustbe(TK.IF);
        guarded_command();
        while( is(TK.ELSEIF) ) {
            System.out.print( "}\nelse if( (" );
            scan();
            guarded_command();
        }
        
        if( is(TK.ELSE) ) {
            System.out.print( "}\nelse\n{\n" );
            scan();
            block();
        }
        
        mustbe(TK.ENDIF);
        System.out.println( "} /* END IF */\n" );
    }
    // END "statement" parts
    // "assignment" parts
    private void ref_id() {
        int num = -1; // The block the scope operater indicates
        int depth = (v_block.size() - 1); // depth of nesting of curr block 
        
        if( is(TK.TILDE) ) {
            // scan();
            mustbe(TK.TILDE);
            no_tilde = false;
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
                    System.exit(1);
                } else if( !v_block.get(depth - num).contains(tok.string) ){
                    // ^ check given # of blocks up
                    // System.err.println( "Checking for: " + tok.string );
                    System.err.print( "no such variable ~" + num);
                    System.err.println( tok.string + " on line "
                    + tok.lineNumber);
                    System.exit(1);
                }
            } else {// no number -> global variable check
                if( !v_block.get(0).contains(tok.string) ){
                    System.err.print( "no such variable ~" + tok.string);
                    System.err.println( " on line " + tok.lineNumber);
                    System.exit(1);
                }
                num = -2; // tilde with no number = global block
            }
        }
        
        //mustbe(TK.ID);
        if( tok.kind != TK.ID ) {
    	    System.err.println( "mustbe: want " + TK.ID + ", got " + tok);
    	    parse_error( "missing token (mustbe)" );
    	}
    	// Check if variable is ANYWHERE in table
    	for(int i = (v_block.size() - 1); i > -1; i--) {
	        if( v_block.get(i).contains(tok.string) ) {
	            if( no_tilde ){
	                num = i;
	            }
	            break;
	        } 
	        if( i == 0 ) { // var not found
	            System.err.println( tok.string + 
	            " is an undeclared variable on line " + tok.lineNumber);
	            System.exit(1);
	        }
    	}
    	
    // 	System.err.println( "num = " + num + " Token = " + tok.string
    // 	+ " Print? " + isprint);
    	
    	if( isprint ){
            // add to the expression
            if( no_tilde ){ // no_tilde, so use block num
                String temp_tok = new String( "x_" 
                + num + tok.string );
                expr_str = expr_str + temp_tok;
            }else if( num == 0 ){ // 0, thus curr block
                String temp_tok = new String( "x_" 
                + (v_block.size()-1) + tok.string );
                expr_str = expr_str + temp_tok;
            } else if( num == -2 ){ // use the global block var
                String temp_tok = new String( "x_0" + tok.string );
                expr_str = expr_str + temp_tok;
            } else { // any block num given
                String temp_tok = new String( "x_" 
                + ((v_block.size()-1) - num) + tok.string );
                expr_str = expr_str + temp_tok;
            }
        } else {
            if( no_tilde ){
                System.out.print( "x_" + num + tok.string );
            } else if ( num == 0 ){ // 0, thus curr block
                System.out.print( "x_" + (v_block.size()-1) + tok.string );
            } else if( num == -2 ){ // use the global var
                System.out.print( "x_0" + tok.string );
            } else { // any block num given
                System.out.print( "x_" + ((v_block.size()-1) - num) + tok.string );
            }
        }
    	
    	no_tilde = true;
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
        // Added the "< 1" to adhear to E's condition rules
        System.out.println( ") < 1 )\n{" );
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
            if( isprint ){
                expr_str = expr_str + tok.string;
            } else {
                System.out.print( "( " );
            }
            
            mustbe(TK.LPAREN);
            expr();
            // mustbe(TK.RPAREN);
            if( tok.kind != TK.RPAREN ) {
        	    System.err.println( "mustbe: want " + TK.RPAREN + ", got " + tok);
        	    parse_error( "missing token (mustbe)" );
    	    }
    	    
            if( isprint ){
                expr_str = expr_str + tok.string;
            } else {
                System.out.print( " )" );
            }
            
            scan(); // remnants of mustbe()
        } else if( is(TK.TILDE) || is(TK.ID) ) {
            ref_id();
        } else if( is(TK.NUM) ) {
            if( isprint ){
                // add to the expression
                expr_str = expr_str + tok.string;
            } else {
                System.out.print( tok.string );
            }

            mustbe(TK.NUM); // scan()
        }
        // else {
        //     parse_error( "INVALID 'FACTOR'" );
        // }
    }
    
    private void addop() {
        if( is(TK.PLUS) ) {
            if( isprint ){
                expr_str = expr_str + tok.string;
            } else {
                System.out.print( " + " );
            }
        
            mustbe(TK.PLUS);
        } else if( is(TK.MINUS) ) {
            if( isprint ){
                expr_str = expr_str + tok.string;
            } else {
                System.out.print( " - " );
            }
            
            mustbe(TK.MINUS);
        }
        // else {
        //     parse_error( "INVALID 'ADDOP'" );
        // }
    }
    
    private void multop() {
        if( is(TK.TIMES) ) {
            if( isprint ){
                expr_str = expr_str + tok.string;
            } else {
                System.out.print( " * " );
            }
            
            mustbe(TK.TIMES);
        } else if( is(TK.DIVIDE) ) {
            if( isprint ){
                expr_str = expr_str + tok.string;
            } else {
                System.out.print( " / " );
            }

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