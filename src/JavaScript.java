import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */


public class JavaScript { 
//  PROCESSOR ATRIBUTES
    private static AFD afd;
    private static Character c;    // Character that has been read
    private static boolean errorState = false;
    private static int line = 1;   // It must have at least 1 line
    private static List<String> keywords;
    private static List<String> symbolsTable;
    private static Token sigToken;
    private static String tokensStrings[] = {"$", "let", "int", "string", "boolean", "if", 
    "do", "while", "function", "return", "print", "input", "(", ")", "{","}",";",",",
    "=","+=","*","!","<","cad","ent","id"};
    

//  RD/WR FILE ATRIBUTES
    private static Reader reader;
    private static Writer writer;

//  CONSTANTS
    private static final int SUCCED = 0;
    private static final int FAILURE = 1;
    private static final int EOF = 65535;

    public static Token LexicAnalizer(){
    //  Lexic analizer atributes
        int actualState = 0;        // Always starts at initial state 0
        Character actionToDo = null;    // List of actions that have to be done
        Token token = null;         // Token to be returned
        boolean rdNext = true;
    //  Auxiliar atributes for token
        MTpair pair = null;         // Stores the corresponding pair 
        String cad = "";            // Stores a string token

        while(actualState < 7 && !errorState){
            if (actualState == 5){
				while(c != '\n' && c != EOF){
                    c = reader.read();
                }
				actualState = 0;
		   	}
            if(c == '\n'){
                line++;
            }
            if(c == EOF){
                return new Token(EOF,"");
            }

            pair = afd.getMTpair(actualState, c);
            if(pair != null){ 
                actualState = pair.getState();
                actionToDo = pair.getAction();
            }else{ // Error
                switch(actualState){
                    case 0:
                       GenError(1, c.toString());
                       break;
                    case 4:
                        GenError(4, "");
                        break;
                    case 6: 
                        GenError(1, c.toString());
                }
            //  Prepare the next character for the next call to lexic analizer
                c = reader.read();
                return null;
            }

            switch(actionToDo){
                case 'A': 
                    token = GenToken(12, " ", "parIzq");
                    break;
                case 'B':
                    token = GenToken(13, " ", "parDrch");
                    break;
                case 'C':
                    token = GenToken(14, " ", "llaveIzq");
                    break;
                case 'D':
                    token = GenToken(15, " ", "llaveDrch");
                    break;
                case 'E':
                    token = GenToken(16, " ", "puntoYcoma");
                    break;
                case 'F':
                    token = GenToken(17, " ", "coma");
                    break;
                case 'G':
                    token = GenToken(18, " ", "asign");
                    break;
                case 'H':
                    token = GenToken(19, " ", "asignSuma");
                    break;
                case 'I':
                    token = GenToken(20, " ", "mult");
                    break;
                case 'J':
                    token = GenToken(21, " ", "neg");
                    break;
                case 'K':
                    token = GenToken(22, " ", "menor");
                    break;
                case 'L': // Read next char
                    break;
                case 'M': // String
                    if(cad.length() > 64){
                        GenError(2, "" + cad.length());
                    }else{
                        token = GenToken(23, "\"" + cad + "\"", "cadena");
                    }
                    break;
                case 'N': // Integer
                    if(Integer.parseInt(cad) > 32767){
                        GenError(3, "");
                    }else{
                        token = GenToken(24, cad, "constante entera");
                    }
                    rdNext = false;
                    break;
                case 'O': // Identifiers
                    int index = searchKeyword(cad);
                    if(index != -1){ // it is a keyword
                        token = GenToken(index, " ", "palabra reservada " + cad);
                    }else{
                        if(!symbolsTable.contains(cad)){
                            symbolsTable.add(cad);
                        } 
                        token = GenToken(25, symbolsTable.indexOf(cad), "identificador " + cad);
                    }
                    rdNext = false;
                    break;
                case 'X': // Concatenation
                    cad = cad + c;
                    break;
                default:
                    GenError(actualState, "leido");
                    break;
            }
            if(rdNext){
                c = reader.read();
            }
        }
        return token;
    } 

    public static void SyntaticAnalizer(){
        sigToken = LexicAnalizer();
        P();
        if(sigToken != null && sigToken.getID() != 65535){
            GenError(8, null);
        }
    }

    // PROCEDIMIENTOS

    private static void equipara(int t){
        if(sigToken != null){
            if(sigToken.getID() == t){
                sigToken = LexicAnalizer();
                /*if(sigToken == null) { // Si me devuelve un error escribo y exit 1
                    try {
                        writer.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                   // System.exit(1);
                }*/
            }else{
                GenError(6, tokensStrings[t]);
            }
        }
    }

    private static void E(){
        writer.writeParse("1");
        R();
        E2();    
    }

    private static void E2(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 22){ // <
                writer.writeParse("2"); 
                equipara(22); // <
                R();
                E2();
            }else if(id == 13 || id == 16 || id == 17 ){ // ) ; ,
                writer.writeParse("3");
            }else{
                GenError(7, tokensStrings[22] + "' | '" + tokensStrings[13] + "' | '" + tokensStrings[16] + "' | '" + tokensStrings[17]);
            }
        }
    }

    private static void R(){
        writer.writeParse("4");
        U();
        R2();    
    }

    private static void R2(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 20){ // *
                writer.writeParse("5");
                equipara(20); // *
                U();
                R2();
            }else if(id == 22 || id == 13 || id == 16 || id == 17){ // < ) ; ,
                writer.writeParse("6");
            }else{
                GenError(7, tokensStrings[20] + "'' | '" + tokensStrings[22] + "'' | '" + tokensStrings[13] + "'' | '" + tokensStrings[16] + "'' | '" + tokensStrings[17]);
            }            
        }  
    }

    private static void U(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 21){ // !
                writer.writeParse("7");
                equipara(21); // !
                V();
            }else if(id == 25 || id == 12 || id == 24 || id == 23){ // id ( ent cad
                writer.writeParse("8");
                V();
            }else{
                GenError(7, tokensStrings[21] + "' | '" + tokensStrings[25] + "' | '" + tokensStrings[12] + "' | '" + tokensStrings[24] + "' | '" + tokensStrings[23]);
            }
        }
    }

    private static void V(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 25){ // id
                writer.writeParse("9");
                equipara(25); // id
                V2();
            }else if(id == 12){ // ( 
                writer.writeParse("10");
                equipara(12); // (
                E();
                equipara(13); // )
            }else if(id == 24){ // ent
                writer.writeParse("11");
                equipara(24); // ent
            }else if(id == 23){ // cad
                writer.writeParse("12");
                equipara(23); // cad
            }else{
                GenError(7, tokensStrings[25] + "' | '" + tokensStrings[12] + "' | '" + tokensStrings[24] + "' | '" + tokensStrings[23]);
            }
        }
    }
    
    private static void V2(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 12){ // (
                writer.writeParse("13");
                equipara(12); // (
                L();
                equipara(13); // )
            }else if(id == 20 || id == 22 || id == 13 || id == 16 || id == 17){ // * < ) ; ,
                writer.writeParse("14");
            }else{
                GenError(7, tokensStrings[12] + "' | '" + tokensStrings[20] + "' | '" + tokensStrings[22] + "' | '" + tokensStrings[13] + "' | '" + tokensStrings[16] + "' | '" + tokensStrings[17]);
            }
        }
    }

    private static void S(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 25){ // id
                writer.writeParse("15");
                equipara(25); // id
                S2();
            }else if(id == 10){ // print
                writer.writeParse("16");
                equipara(10); // print
                E();
                equipara(16); // ;
            }else if(id == 11){ // input
                writer.writeParse("17");
                equipara(11); // input
                equipara(25); // id
                equipara(16); // ;
            }else if(id == 9){ // return
                writer.writeParse("18");
                equipara(9); // return
                X();
                equipara(16); // ;
            }else{
                GenError(7, tokensStrings[25] + "' | '" + tokensStrings[10] + "' | '" + tokensStrings[11] + "' | '" + tokensStrings[9]);
            }
        }
    }
    
    private static void S2(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 18){ // =
                writer.writeParse("19");
                equipara(18); // =
                E();
                equipara(16); // ;
            }else if(id == 12){ // (
                writer.writeParse("20");
                equipara(12); // (
                L();
                equipara(13); // )
                equipara(16); // ;
            }else if(id == 19){ // +=
                writer.writeParse("21");
                equipara(19); // +=
                E();
                equipara(16); // ;
            }else{
                GenError(7, tokensStrings[18] + "' | '" + tokensStrings[12] + "' | '" + tokensStrings[19]);
            }
        }
    }

    // JULIO 14

    private static void L(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 21 || id == 25 || id == 12 || id == 24 || id == 23){ // ! id ( ent cad
                writer.writeParse("22");
                E();
                Q();
            }
            else if(id == 13){ // )
                writer.writeParse("23");
            }
            else{
                GenError(7, tokensStrings[12]+"' | '"+tokensStrings[13]+"' | '" + tokensStrings[21]+"' | '"+tokensStrings[23]+"' | '"+ tokensStrings[24]+"' | '"+tokensStrings[25]);
            }
        }
    }
    
    private static void Q (){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 17){ //Coma
                writer.writeParse("24");
                equipara(17); // Coma
                E();
                Q();
            }
            else if(id == 13){ // )
                writer.writeParse("25");
            }
            else{
                GenError(7, tokensStrings[17]+"' | '"+tokensStrings[13]);
            }
        } 
    }

    private static void X(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 21 || id == 25 || id == 12 || id == 23 || id == 24){ /// ! id ( ent cad
                writer.writeParse("26"); 
                E();
            }
            else if(id == 16){ //Punto y coma
                writer.writeParse("27");
            }
            else{
                GenError(0, tokensStrings[21]+"' | '"+tokensStrings[25]+"' | '" + tokensStrings[12]+"' | '"+tokensStrings[23]+"' | '"+ tokensStrings[24]+"' | '"+tokensStrings[16]);
            }
        }
    }

    private static void B(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 5){ // if
                writer.writeParse("28");
                equipara(5); // if
                equipara(12); // parIzzq
                E();
                equipara(13); // parDer
                S();
            }
            else if(id == 1){ // let
                writer.writeParse("29");
                equipara(1);  // let
                equipara(25); // id
                T();
                equipara(16); // punto y coma
            }
            else if(id == 25 || id == 9 || id == 10 || id == 11){ // id print input return 
                writer.writeParse("30");
                //equipara(3); // string
                S();
            }
            else if(id == 6){// do
                writer.writeParse("31");
                equipara(6); // do
                equipara(14); // llaveizq
                if(sigToken != null){
                    //System.out.println(sigToken);
                    id = sigToken.getID();
                    while(id == 5 || id == 1 || id == 6 || id == 25 || id == 10 || id == 11 || id == 9){ // if let do id print input return
                        //System.out.println("entro");
                        C();
                        if(sigToken == null){
                            break;
                        }
                        id = sigToken.getID();
                    }
                    equipara(15); // llaveDer
                    equipara(7);  // while
                    equipara(12); // parIzq
                    E();
                    equipara(13); // parDer
                    equipara(16); 
                }
            }
            else{
                GenError(7, tokensStrings[5]+"' | '"+tokensStrings[1]+"' | '" + tokensStrings[25]+"' | '"+tokensStrings[9]+"' | '"+ tokensStrings[10]+"' | '"+tokensStrings[11]+"' | '"+tokensStrings[6]);
            }
        }
    }

    private static void T(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 2){ // int 
                writer.writeParse("32");
                equipara(2); // int
            }
            else if(id == 4){ // boolean
                writer.writeParse("33");
                equipara(4);  // boolean
            }
            else if(id == 3){ // string
                writer.writeParse("34");
                equipara(3); // string
            }
            else{
                GenError(7, tokensStrings[2]+"' | '"+tokensStrings[4]+"' | '" + tokensStrings[3]);
            }
        }
    }

    private static void F(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 8){ // function
                writer.writeParse("35");
                equipara(8); // function
                equipara(25); // id
                H();
                equipara(12); // (
                A();
                equipara(13); // )
                equipara(14); // {
                if(sigToken != null){
                    //System.out.println(sigToken);
                    id = sigToken.getID();
                    //System.out.println(id);
                    while(id == 5 || id == 1 || id == 6 || id == 25 || id == 10 || id == 11 || id == 9){ // if let do id print input return
                        //System.out.println("entro");
                        C();
                        if(sigToken == null){
                            break;
                        }
                        id = sigToken.getID();
                    }
                    equipara(15); // }
                }
            }else{
                GenError(7, tokensStrings[8]+"' | '"+tokensStrings[5]+"' | '" + tokensStrings[1]+"' | '"+tokensStrings[66]+"' | '"+ tokensStrings[10]+"' | '"+tokensStrings[25]+"' | '"+tokensStrings[11]+"' | '"+tokensStrings[9]);
            }
        }
    }

    private static void H(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 2 || id == 3 || id == 4){// int boolean string
                writer.writeParse("36");
                T();
            }
            else if(id == 12){ // parIzq
                writer.writeParse("37");
            }   
            else{
                GenError(7,tokensStrings[2]+"' | '"+tokensStrings[3]+"' | '" + tokensStrings[4]+"' | '"+tokensStrings[12]);
            }
        }
    }

     private static void A(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 2 || id == 3 || id == 4){ // int boolean string
                writer.writeParse("38");
                T();
                equipara(25); // id
                K();
            }
            else if(id == 13){ // parDer
                writer.writeParse("39");
            }   
            else{
                GenError(7, tokensStrings[2]+"' | '"+tokensStrings[3]+"' | '" + tokensStrings[4]+"' | '"+tokensStrings[13]);
            }
        }
    }

    private static void K(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 17){ // coma
                writer.writeParse("40");
                equipara(17); // coma
                T();
                equipara(25); // id
                K();
            }
            else if(id == 13){ // parDer
                writer.writeParse("41");
            }   
            else{
                GenError(7, tokensStrings[17]+"' | '"+tokensStrings[13]);
            }
        }
    }

    private static void C(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 5 || id == 1 || id == 6 || id == 25 || id == 10 || id == 11 || id == 9){ // if let do id print input  return
                writer.writeParse("42");
                B();
                C();
            }
            else if(id == 15){ // llaveDer
                writer.writeParse("43");
            }   
            else{
                GenError(7, tokensStrings[5] + "' | '" + tokensStrings[1] + "' | '" + tokensStrings[6] + "' | '" + tokensStrings[25] + "' | '" + tokensStrings[10] + "' | '" + tokensStrings[11] + "' | '" + tokensStrings[9] + "' | '" + tokensStrings[15]);
            }
        }
    }
    
    private static void P(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 5 || id == 1 || id == 6 || id == 25 || id == 10 || id == 11 || id == 9){ // if let do id print input return
                writer.writeParse("44");
                B();
                P();
            }else if(id == 8){ // function
                writer.writeParse("45");
                F();
                P();
            }else if( id == 65535){ // EOF $
                writer.writeParse("46");
            }else{
                GenError(7, tokensStrings[5] + "' | '" + tokensStrings[1] + "' | '" + tokensStrings[6] + "' | '" + tokensStrings[25] + "' | '" + tokensStrings[10] + "' | '" + tokensStrings[11] + "' | '" + tokensStrings[9] + "' | '" + tokensStrings[8] + "' | $ (EOF)" );
            }
        }
    }


//  AUXILIAR FUNCTIONS

    private static Token GenToken(Integer id, Object value, String comment){
        Token token = new Token(id, value);
        writer.writeToken(token.toString() + "  // token " + comment + "\n");
        //System.out.println(token.toString() + "// token " + comment + "\n");
        return token;
    }

    private static void GenError (int error, String data){
        int numero;
        if(sigToken.getID()==65535){
            numero=0;
        }
        else{
            numero=sigToken.getID();
        }
        switch(error){
            case 1: 
                writer.writeError("Error léxico (1): Línea " + line + ": Se ha producido un error en la generación del token. No se esperaba el carácter '" + data + "'\n");
                errorState = true;
                break;
            case 2:
                writer.writeError("Error léxico (2): Línea " + line + ": Se ha superado el número máximo de caracteres: 64. Número actual de caracteres: " + data + "\n");
                errorState = true;
                break;
            case 3:
                writer.writeError("Error léxico (3): Línea " + line + ": Entero fuera de rango. El número no debe ser superior a 32767.\n");
                errorState = true;
                break;
            case 4:
                writer.writeError("Error léxico (4): Línea " + line + ": No se esperaba el carácter '/'. En caso de querer escribir un comentario, solo se admite el siguiente formato: // Comentario\n");
                errorState = true;
                break;
            case 5:
                writer.writeError("Error léxico (5): Línea " + line + ": Ya existe el identificador " + data + ". Elija otro nombre.\n");
                errorState = true;
                break;
            case 6:
                writer.writeError("Error sintáctico (6): Línea " + line + ": Se ha encontrado '" + tokensStrings[numero] + "'' y se esperaba '" + data + "'\n");
                errorState = true;
                break;
            case 7:
                writer.writeError("Error sintáctico (7): Línea " + line + ": Se ha encontrado '" + tokensStrings[numero] + "'' y se esperaba uno de estos tokens: '" + data + "\n");
                errorState = true;
                break;
             case 8:
                writer.writeError("Error sintáctico (8): Línea " + line + ": Se ha encontrado '" + tokensStrings[numero] + "'' y se esperaba el fin de fichero.\n");
                errorState = true;
                break;
            
        }
    }

    private static void GenTS(){
        writer.writeTS("CONTENIDO DE LA TABLA DE SIMBOLOS #1 :\n");
        writer.writeTS("\n");
        for(int i = 0; i < symbolsTable.size(); i++){
            writer.writeTS("* LEXEMA : '" + symbolsTable.get(i) + "'\n");
            writer.writeTS("-----------------------------------------\n");
        }
    }

    private static void destroyTS(){
        symbolsTable.clear();
    }

    private static void fillKeywords(){
        String [] arrayKw = {null, "let", "int", "string", "boolean", "if", "do", "while", "function", "return", "print", "input"};
        keywords = Arrays.asList(arrayKw);
    }

    private static int searchKeyword(String token){
        return keywords.indexOf(token);
    }

    public static void main(String[] args) { // args contains the arguments
        if(args.length != 1){
            System.err.println("Uso: java JavaScript.java <input.txt>");
            System.exit(FAILURE);
        }

        afd = new AFD();
        symbolsTable = new ArrayList<>();
        reader = new Reader(args[0]);
        c = reader.read();
        writer = new Writer("tokens.txt", "ts.txt", "errors.txt", "parse.txt");
        fillKeywords();
        //while((t = LexicAnalizer()) != null && t.getID() != EOF );
        SyntaticAnalizer();
        GenTS();
        destroyTS();
        reader.close();
        writer.close();
        if(errorState){
            System.out.println("Se han producido errores. Consulte el fichero errors.txt para encontrar los detalles.");
        }else{
            System.out.println("Todo bien jiji");
        }
        System.exit(SUCCED);
    }
}
