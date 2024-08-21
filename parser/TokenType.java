package lab09_05_11.parser;


public enum TokenType { 
	// symbols      aggiungo open vector e close vector, e il foreach..in; cambio di conseguenza mylangtokenizer
	ASSIGN, MINUS, PLUS, TIMES, NOT, AND, EQ, STMT_SEP, PAIR_OP, OPEN_PAR, CLOSE_PAR, OPEN_BLOCK, CLOSE_BLOCK, OPEN_VECTOR, CLOSE_VECTOR,
	// keywords
	PRINT, VAR, BOOL, IF, ELSE, FST, SND, FOREACH, IN,
	// non singleton categories
	SKIP, IDENT, NUM,   
	// end-of-file
	EOF, 	
}
