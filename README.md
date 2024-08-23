# Implementazione di un parser in Java per un semplice linguaggio

Il progetto consiste nell'implementazione di un'estensione del linguaggio sviluppato durante gli ultimi laboratori Java nel corso di LPO dell'Università di Genova.

L'interfaccia da linea di comando:

il programma da eseguire può essere letto da un file di testo <filename> con l’opzione -i <filename>, altrimenti viene letto dallo standard input
l'output del programma in esecuzione può essere salvato su un file di testo <filename> con l’opzione -o <filename>, altrimenti viene usato lo standard output
l’opzione -ntc (abbreviazione di no-type-checking) permette di eseguire il programma senza effettuare prima il controllo di semantica statica del typechecker
# Definizione del linguaggio

### Sintassi

Il linguaggio contiene le nuove parole chiave foreach e in e i nuovi simboli [ e ].

La sintassi del linguaggio è definita da questa grammatica in forma EBNF:

'''
Prog ::= StmtSeq EOF

StmtSeq ::= Stmt (';' StmtSeq)?

Stmt ::= 'var'? IDENT '=' Exp | 'print' Exp |  'if' '(' Exp ')' Block ('else' Block)? | 'foreach' IDENT 'in' Exp Block

Block ::= '{' StmtSeq '}'

Exp ::= And (',' And)* 

And ::= Eq ('&&' Eq)* 

Eq ::= Add ('==' Add)*

Add ::= Mul ('+' Mul)*

Mul::= Atom ('*' Atom)*

Atom ::= 'fst' Atom | 'snd' Atom | '-' Atom | '!' Atom | BOOL | NUM | IDENT | '(' Exp ')' | '[' Exp ';' Exp ']' 
'''

La grammatica non richiede trasformazioni e può essere utilizzata così com'è per sviluppare un parser per il linguaggio con un solo token di lookahead.

Sono stati aggiunti

il literal di tipo vector  '[' Exp ';' Exp ']'
lo statement 'foreach' IDENT 'in' Exp Block

### Regole della semantica statica

il literal '[' Exp1 ';' Exp2 ']' è corretto e ha tipo vector se Exp1 e Exp2 hanno tipo int

l'espressione Exp1 '+' Exp2 è corretta e ha tipo int se Exp1 e Exp2 hanno tipo int

l'espressione Exp1 '+' Exp2 è corretta e ha tipo vector se Exp1 e Exp2 hanno tipo vector

l'espressione Exp1 '*' Exp2 è corretta e ha tipo int se Exp1 e Exp2 hanno tipo int

l'espressione Exp1 '*' Exp2 è corretta e ha tipo int se Exp1 e Exp2 hanno tipo vector

l'espressione Exp1 '*' Exp2 è corretta e ha tipo vector se Exp1 ha tipo inte Exp2 ha tipo vector oppure Exp1 ha tipo vectore Exp2 ha tipo int

lo statement 'foreach' IDENT 'in' Exp Block è corretto se Exp ha tipo vector rispetto all'ambiente corrente env e Block è corretto rispetto all'ambiente ottenuto aggiungendo a env un nuovo scope annidato dove l'unica variabile dichiarata è IDENT di tipo int.

### Regole della semantica dinamica

se Exp1 e Exp2 si valutano negli interi ind e dim, allora '[' Exp1 ';' Exp2 ']' si valuta nel vettore di dimensione dim che contiene 1 in corrispondenza dell'indice ind e 0 nelle altre posizioni. Gli indici iniziano da 0, viene sollevata un'eccezione se dim è negativo, oppure ind non è maggiore o uguale di 0 e minore di dim.
Esempio

print [3;5]

stampa

[0;0;0;1;0]

se Exp1 e Exp2 si valutano negli interi i1 e i2, allora Exp1'+'Exp2 si valuta nell'intero i1+i2

se Exp1 e Exp2 si valutano nei vettori v1 e v2, allora Exp1'+'Exp2 si valuta nel vettore v1+v2; viene sollevata un'eccezione se i due vettori non hanno la stessa dimensione. La somma di vettori è definita da

[a_0;...;a_n]+[b_0;...;b_n]=[a_0+b_0;...;a_n+b_n]

se Exp1 e Exp2 si valutano negli interi i1 e i2, allora Exp1'*'Exp2 si valuta nell'intero i1*i2

se Exp1 e Exp2 si valutano nei vettori v1 e v2, allora Exp1'*'Exp2 si valuta nell'intero ottenuto dal prodotto scalare di v1 e v2; viene sollevata un'eccezione se i due vettori non hanno la stessa dimensione. Il prodotto scalare di vettori è definito da

[a_0;...;a_n]*[b_0;...;b_n]=a_0*b_0+...+a_n*b_n

se Exp1 e Exp2 si valutano in un intero i e in un vettore v, o viceversa, allora Exp1'*'Exp2 si valuta nel vettore ottenuto dal prodotto misto tra i e v. Il prodotto misto tra un intero e un vettore è definito da

i*[a_0;...;a_n]=[a_0;...;a_n]*i= [i*a_0;...;i*a_n]

l'esecuzione dello statement 'foreach' IDENT 'in' Exp Block consiste nella valutazione dell'espressione Exp rispetto all'ambiente corrente env; deve essere restituito un vettore v sui cui elementi viene iterata l'esecuzione di Block rispetto a un ambiente ottenuto da env aggiungendo uno scope annidato contenente la sola variabile IDENT alla quale viene assegnato a ogni iterazione un elemento di v in ordine dall'indice minimo al massimo. Inizialmente la variabile  IDENT viene inizializzata con un valore intero arbitrario.

Esempio:

foreach i in [0;3]+2*[1;3]+3*[2;3]{print i}

stampa

1
2
3
