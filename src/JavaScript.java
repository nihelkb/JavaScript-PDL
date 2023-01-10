import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaScript { 
//  PROCESSOR ATRIBUTES
    private static AFD afd;
    private static Character c;    // Character that has been read
    private static boolean errorStateLexSint = false;
    private static boolean errorState = false;
    private static int line = 1;   // It must have at least 1 line
    private static List<String> keywords;
    private static List<String> symbolsTable;
    private static Token sigToken;
    private static String tokensStrings[] = {"$", "let", "int", "string", "boolean", "if", 
    "do", "while", "function", "return", "print", "input", "(", ")", "{","}",";",",",
    "=","+=","*","!","<","cadena de caracteres","constante entera","identificador (nombre de una variable o función)"};
    private static List<Object> tablaSimGlobal = new ArrayList<Object>();
    private static List<Object> tablaSimLocal = new ArrayList<Object>();
    private static Map<String,Integer> mapaTSG = new HashMap<String,Integer>();
    private static Map<String,Integer> mapaTSL = new HashMap<String,Integer>();
    private static List<Object> tablasLocal = new ArrayList<Object>();
    private static List<Object> nombreLocales = new ArrayList<Object>();
    private static boolean zonaDeclaracion = false;
    private static int desplLocal = 0;
    private static int desplGlobal = 0;
    private static boolean tablaG = true;
    private static int contadorFunciones = 1;
    private static int funcionAct = 0;    
    private static String lexema = "";
    private static boolean bucle = false;
    private static boolean esConstEnt = false;
    private static boolean tieneRetorno = false;
    private static boolean dentroFun = false;

//  Types
    private static final String vacio = "vacio";
    private static final String ok = "tipo_ok";
    private static final String error = "tipo_error";
    private static final String fun = "funcion";
    private static final String logico = "logico";
    private static final String ent = "entero";
    private static final String cadena = "cadena";

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

        while(actualState < 7 && !errorStateLexSint){
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
                        if(c == '"'){
                            GenError(16, "");
                        }else{
                            GenError(1, c.toString());
                        }
                        break;
                    case 4:
                        GenError(4, "");
                        break;
                    case 6: 
                        //GenError(1, c.toString());
                        GenError(1, cad);
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
                    lexema = cad;
                    int index = searchKeyword(lexema);
                    if(index != -1){ // it is a keyword
                        token = GenToken(index, " ", "palabra reservada " + lexema);
                    }else{
                        boolean contenido = false;
                        if(zonaDeclaracion){
                            if(tablaG){
                                contenido = mapaTSG.containsKey(lexema);  
                            }else{
                                contenido = mapaTSL.containsKey(lexema);
                            }
                            if(contenido){ // si ya esta contenido
                                int pos;
                                if(tablaG){
                                    pos = mapaTSG.get(lexema);
                                }else{
                                    pos = mapaTSL.get(lexema);
                                }
                                token = GenToken(25, pos, "identificador " + lexema);
                                GenError(5, lexema);
                            }else{ // no esta contenido
                                List <Object> atributos = new ArrayList<Object>();
                                atributos.add(0, lexema); // lexema
                                atributos.add(1, "");  // tipo
                                atributos.add(2, ""); // desplazamiento
                                int pos;
                                if(tablaG){
                                    atributos.add(3, "");  // num parametros
                                    List <Object> tipoParam = new ArrayList<Object>();
                                    atributos.add(4, tipoParam); // tipo de parametros 
                                    atributos.add(5, ""); // Tipo devuelto
                                    atributos.add(6, ""); // Etiqueta
                                    pos = tablaSimGlobal.size();
                                    mapaTSG.put(lexema, pos);
                                    tablaSimGlobal.add(atributos);
                                }else{
                                    pos = tablaSimLocal.size();
                                    mapaTSL.put(lexema, pos);
                                    tablaSimLocal.add(atributos);
                                }
                                token = GenToken(25, pos, "identificador " + lexema);
                            }
                        }else{
                            if(!mapaTSG.containsKey(lexema) && !mapaTSL.containsKey(lexema) ){
                                int pos = tablaSimGlobal.size();
                                //mapaTSG.put(lexema, pos);
                                token = GenToken(25, pos, "identificador " + lexema);
                                
                                //GenError(9, lexema);
                            }else{
                                if(mapaTSG.containsKey(lexema)){
                                    token = GenToken(25, mapaTSG.get(lexema), "identificador " + lexema);
                                }else{
                                    token = GenToken(25, mapaTSL.get(lexema), "identificador " + lexema);
                                }
                            }
                        }
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
            GenError(8, "");
        }
    }

    // PROCEDIMIENTOS

    private static void equipara(int t){
        if(errorStateLexSint){
            try {
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Se han producido errores. Consulte el fichero 'errors.txt' para encontrar los detalles.");
            System.exit(0);
        }
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

    private static String E(){
        writer.writeParse("1");
        String tipoR = R();
        String tipoE2 = E2();
        if(tipoR.equals(ent) && tipoE2.equals(logico)){
            return logico;
        }else{
            return tipoR;
        }    
    }

    @SuppressWarnings("unchecked")
    private static String E2(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 22){ // <
                writer.writeParse("2");
                int pos;
                boolean ex;
                String tipoId = "";
                if(dentroFun){
                    ex = mapaTSL.containsKey(lexema);
                    if(ex){ // local
                        pos = mapaTSL.get(lexema);
                        tipoId = (String)((ArrayList<Object>)(tablaSimLocal.get(pos))).get(1);
                    }else{
                        pos = mapaTSG.get(lexema);
                        tipoId = (String)((ArrayList<Object>)(tablaSimGlobal.get(pos))).get(1);
                    }
                }else{
                    ex = mapaTSG.containsKey(lexema);
                    if(ex){
                        pos = mapaTSG.get(lexema);
                        tipoId = (String)((ArrayList<Object>)(tablaSimGlobal.get(pos))).get(1);
                    }
                }
                if(!tipoId.equals(ent) && !tipoId.equals("")){
                    GenError(25, "");
                    equipara(22); // <
                    R();
                    E2();
                    return error;
                } 
                else if(tipoId.equals("")){
                    equipara(22); // <
                    R();
                    E2();
                    return error;
                }
                equipara(22); // <
                String tipoR = R();
                String tipoE2 = E2();
                if(tipoR.equals(ent) && (tipoE2.equals(ent) || tipoE2.equals(vacio))){
                    return logico;
                }else{
                    GenError(25, ""); // ambos tipo entero
                    return error;
                }
            }else if(id == 13 || id == 16 || id == 17 ){ // ) ; ,
                writer.writeParse("3");
                return vacio;
            }else{
                GenError(7, tokensStrings[22] + "' | '" + tokensStrings[13] + "' | '" + tokensStrings[16] + "' | '" + tokensStrings[17]);
                return error;
            }
        }
        return ok;
    }

    private static String R(){
        writer.writeParse("4");
        String tipoU = U();
        String tipoR2 = R2();
        if(tipoR2.equals(ent) && tipoU.equals(ent)){
            return ent;
        }else{
            return tipoU;
        }
    }

    @SuppressWarnings("unchecked")
    private static String R2(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 20){ // *
                writer.writeParse("5");
                if(!esConstEnt){
                    String tipoId;
                    int pos;
                    boolean existe = mapaTSL.containsKey(lexema);
                    if(dentroFun){
                        if(existe){ // coge de local
                            pos = mapaTSL.get(lexema);
                            tipoId = (String)((ArrayList<Object>)(tablaSimLocal.get(pos))).get(1);
                        }else{ // coge de global
                            pos = mapaTSG.get(lexema);
                            tipoId = (String)((ArrayList<Object>)(tablaSimGlobal.get(pos))).get(1);
                        }
                    }else{
                        pos = mapaTSG.get(lexema);
                        tipoId = (String)((ArrayList<Object>)(tablaSimGlobal.get(pos))).get(1);
                    }
                    if(!tipoId.equals(ent)){
                        GenError(27, "");
                        equipara(20); // *
                        U();
                        R2();
                        return error;
                    }
                }
                equipara(20); // *
                String tipoU = U();
                String tipoR2 = R2();
                if(tipoU.equals(ent) && (tipoR2.equals(ent) || tipoR2.equals(vacio))){
                    return ent;
                }else{
                    GenError(27, "");
                    return error;
                }
            }else if(id == 22 || id == 13 || id == 16 || id == 17){ // < ) ; ,
                writer.writeParse("6");
                return vacio;
            }else{
                GenError(7, tokensStrings[20] + "'| '" + tokensStrings[22] + "' | '" + tokensStrings[13] + "' | '" + tokensStrings[16] + "' | '" + tokensStrings[17]);
                return error;
            }            
        }
        return ok;  
    }

    private static String U(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 21){ // !
                writer.writeParse("7");
                equipara(21); // !
                String tipoV = V();
                if(tipoV.equals(logico)){
                    return logico;
                }else{
                    GenError(26, "");
                    return error;
                }
            }else if(id == 25 || id == 12 || id == 24 || id == 23){ // id ( ent cad
                writer.writeParse("8");
                String tipoV = V();
                return tipoV;
            }else{
                GenError(7, tokensStrings[21] + "' | '" + tokensStrings[25] + "' | '" + tokensStrings[12] + "' | '" + tokensStrings[24] + "' | '" + tokensStrings[23]);
                return error;
            }
        }
        return ok;
    }
    
    @SuppressWarnings("unchecked")
    private static String V(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 25){ // id
                writer.writeParse("9");
                esConstEnt = false;
                //equipara(25); // id
                int posEx = buscoIdTS(lexema); // 0 si existe, -1 si no
                if(posEx == -1){ 
                    GenError(9, lexema);
                    equipara(25); // id
                    V2(posEx);
                    return error;
                }
                int pos = 0;;
                String tipoId = "";
                boolean existe;
                if(dentroFun){
                    existe = mapaTSL.containsKey(lexema);
                    if(existe){ // esta en local
                        pos = mapaTSL.get(lexema);
                        tipoId = (String)((ArrayList<Object>)tablaSimLocal.get(pos)).get(1);
                    }else{
                        pos = mapaTSG.get(lexema);
                        tipoId = (String)((ArrayList<Object>)tablaSimGlobal.get(pos)).get(1);
                    }
                }else{
                    existe = mapaTSG.containsKey(lexema);
                    if(existe){
                        pos = mapaTSG.get(lexema);
                        tipoId = (String)((ArrayList<Object>)tablaSimGlobal.get(pos)).get(1);
                    }
                }
                equipara(25); // id
                String[] tipoV2 = V2(posEx).split(" ");
                if(tipoId.equals(fun)){
                    /*if(tipoV2[0].equals(vacio)){
                        GenError(14, "");
                        return error;
                    }else{*/
                        List<String> tipoParametro;
                        String numeroParametros;
                        String tipoDevuelto;
                        tipoParametro = (ArrayList<String>)((ArrayList<Object>)tablaSimGlobal.get(pos)).get(4);
                        int numeroParametrosTabla = (Integer)((ArrayList<Object>)tablaSimGlobal.get(pos)).get(3);
                        numeroParametros = Integer.toString(numeroParametrosTabla);
                        tipoDevuelto = (String)((ArrayList<Object>)tablaSimGlobal.get(pos)).get(5);
                        boolean parametros = tipoV2[1].equals(numeroParametros);
                        if(!parametros){
                            GenError(28, numeroParametros);
                            return error;
                        }
                        int i = 2;
                        String tipoFalla = "";
                        String tipoDebe = "";
                        while(i < tipoV2.length && parametros ){
                            if(!(tipoV2[i].equals(tipoParametro.get(i-2)))){
                                parametros = false;
                                tipoFalla = tipoV2[i];
                                tipoDebe = tipoParametro.get(i-2);
                            }
                            i++;
                        }
                        if(parametros){
                            return tipoDevuelto;
                        }else{
                            GenError(11, tipoFalla + " " + tipoDebe); 
                            return error;
                        }          
                    //}
                }else{
                    return tipoId;
                }
            }else if(id == 12){ // ( 
                writer.writeParse("10");
                equipara(12); // (
                String tipoE = E();
                equipara(13); // )
                return tipoE;
            }else if(id == 24){ // ent**********
                writer.writeParse("11");
                esConstEnt = true;
                equipara(24); // ent
                return ent;
            }else if(id == 23){ // cad
                writer.writeParse("12");
                equipara(23); // cad
                return cadena;
            }else{
                GenError(7, tokensStrings[25] + "' | '" + tokensStrings[12] + "' | '" + tokensStrings[24] + "' | '" + tokensStrings[23]);
                return error;
            }
        }
        return ok;
    }
    
    @SuppressWarnings("unchecked")
    private static String V2(int exId){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 12){ // (
                writer.writeParse("13");
                if(exId == -1){ // no declarada
                    equipara(12); // (
                    L();
                    equipara(13); // )
                    return error;
                }
                int pos = mapaTSG.get(lexema);
                if(!(((ArrayList<Object>)tablaSimGlobal.get(pos)).get(1).equals(fun))){
                    GenError(17, lexema);
                    equipara(12); // (
                    L();
                    equipara(13); // )
                    return error;
                }
                equipara(12); // (
                String tipoL = L();
                equipara(13); // )
                return tipoL;
            }else if(id == 20 || id == 22 || id == 13 || id == 16 || id == 17){ // * < ) ; ,
                writer.writeParse("14");
                return vacio + " 0";
            }else{
                GenError(7, tokensStrings[12] + "' | '" + tokensStrings[20] + "' | '" + tokensStrings[22] + "' | '" + tokensStrings[13] + "' | '" + tokensStrings[16] + "' | '" + tokensStrings[17]);
                return error;
            }
        }
        return ok;
    }

    @SuppressWarnings("unchecked")
    private static String S(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 25){ // id
                writer.writeParse("15");
                int pos = (Integer)(sigToken.getValue());
                int existe = buscoIdTS(lexema);
                equipara(25); // id
                if(existe == -1){
                    ArrayList<Object> atribs = new ArrayList<Object>(); 
                    atribs.add(lexema); // Lexema
                    atribs.add(ent); //  Tipo
                    atribs.add(desplGlobal); // Desplazamiento
                    desplGlobal++;
                    atribs.add(""); //  Num param
                    atribs.add(new ArrayList<Object>()); //  lista param
                    atribs.add(""); //  tipo retorno
                    atribs.add(""); // etiqueta
                    tablaSimGlobal.add(pos,atribs);
                    pos = tablaSimGlobal.size() - 1;
                    mapaTSG.put(lexema, pos);
                }
                String tipo = "";
                boolean posEx;
                if(dentroFun){ // si esta dentro de una funcion
                    posEx = mapaTSL.containsKey(lexema);
                    if(posEx){ // esta en local
                        tipo = (String)((ArrayList<Object>)tablaSimLocal.get(pos)).get(1);
                    }else{
                        tipo = (String)((ArrayList<Object>)tablaSimGlobal.get(pos)).get(1);
                    }
                }else{
                    posEx = mapaTSG.containsKey(lexema);
                    if(posEx){
                        tipo = (String)((ArrayList<Object>)tablaSimGlobal.get(pos)).get(1);
                    }
                }
                String tipoS2 = S2(tipo);
                String[] tiposS2 = tipoS2.split(" ");
                if(tipo.equals(fun)){
                    String numeroParam;
                    ArrayList<String> tipoParam;
                    int numeroParametrosTabla = (Integer)((ArrayList<Object>)tablaSimGlobal.get(pos)).get(3);
                    numeroParam = Integer.toString(numeroParametrosTabla);
                    tipoParam = (ArrayList<String>)((ArrayList<Object>)tablaSimGlobal.get(pos)).get(4);
                    boolean coincidenParam = tiposS2[2].equals(numeroParam);      // Se reciben el mismo numero de param que se requieren 
                    if(!coincidenParam){
                        GenError(28, numeroParam);
                        return error;
                    }
                    String tipoFalla = "";
                    String tipoDebe = "";
                    for(int i = 3; i < tiposS2.length && coincidenParam; i++){
                        coincidenParam = tiposS2[i].equals(tipoParam.get(i-3));   // Los tipos son correctos incluido el orden
                        if(!coincidenParam){
                            tipoFalla = tiposS2[i];
                            tipoDebe = tipoParam.get(i-3);
                        }
                    }
                    if(coincidenParam){
                        return ok;
                    }else{
                        GenError(11, tipoFalla + " " + tipoDebe);
                        return error;
                    }        
                }else if(tiposS2[0].equals(tipo)){
                    return ok;
                }/*else{
                    GenError(23, tipo);
                    return error;
                }*/
            }else if(id == 10){ // print
                writer.writeParse("16");
                equipara(10); // print
                String tipoE = E();
                //equipara(16); // ;
                if(tipoE.equals(cadena) || tipoE.equals(ent)){
                    equipara(16); // ;
                    return ok;
                }else{
                    GenError(12, "");
                    equipara(16); // ;
                    return error;
                }
            }else if(id == 11){ // input
                writer.writeParse("17");
                equipara(11); // input
                if(sigToken.getValue().equals(" ")){ // no es id
                    GenError(6, "id");
                    equipara(25); // id
                    equipara(16); // ;
                    return error;
                }
                int pos = (int)sigToken.getValue();
                int existe = buscoIdTS(lexema);
                equipara(25); // id
                if(existe == -1){
                    ArrayList<Object> atribs = new ArrayList<Object>(); 
                    atribs.add(lexema); // Lexema
                    atribs.add(ent); //  Tipo
                    atribs.add(desplGlobal); // Desplazamiento
                    desplGlobal++;
                    atribs.add(""); //  Num param
                    atribs.add(new ArrayList<Object>()); //  lista param
                    atribs.add(""); //  tipo retorno
                    atribs.add(""); // etiqueta
                    tablaSimGlobal.add(atribs);
                    pos = tablaSimGlobal.size() - 1;
                    mapaTSG.put(lexema, pos);
                }
                String tipo;
                if(mapaTSG.containsKey(lexema)){
                    tipo = (String)((ArrayList<Object>)tablaSimGlobal.get(pos)).get(1);
                }else{
                    tipo = (String)((ArrayList<Object>)tablaSimLocal.get(pos)).get(1);
                }
                if(tipo.equals(cadena) || tipo.equals(ent)){
                    equipara(16); // ;
                    return ok;
                }else{
                    GenError(31, "");
                    equipara(16); // ;
                    return error;
                }
            }else if(id == 9){ // return
                writer.writeParse("18");
                equipara(9); // return
                String tipoX = X();
                if(!tablaG){
                    String tipoRetorno = (String)((ArrayList<Object>)tablaSimGlobal.get(funcionAct)).get(5);
                    if(!tipoRetorno.equals(vacio)){
                        tieneRetorno = true;
                    }
                    String tipos;
                    if(tipoRetorno.equals(tipoX)){
                        tipos = ok + " " + tipoRetorno;
                    }else{
                        tipos = tipoX + " " + tipoRetorno;
                        if(tipoRetorno.equals(vacio)){
                            GenError(22, "");
                        }else{
                            if(!tipoX.equals(error)){
                                GenError(20, tipos);
                            }
                        }
                        equipara(16); // ;
                        return error + " " + error; 
                    }
                    equipara(16); // ;
                    return tipos;
                }else{
                    equipara(16); // ;
                    return error + " " + error;
                }
            }else{
                GenError(7, tokensStrings[25] + "' | '" + tokensStrings[10] + "' | '" + tokensStrings[11] + "' | '" + tokensStrings[9]);
                return error;
            }
        }
        return ok;
    }
    
    @SuppressWarnings("unchecked")
    private static String S2(String tipoVar){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 18){ // =
                writer.writeParse("19");
                equipara(18); // =
                String tipoE = E();
                if(!tipoE.equals(tipoVar)){
                    GenError(23, tipoVar);
                    equipara(16); // ;
                    return error;
                }
                equipara(16); // ;
                return tipoE;
            }else if(id == 12){ // (
                writer.writeParse("20");
                int pos = mapaTSG.get(lexema);
                String tipoId = (String)((ArrayList<Object>)(tablaSimGlobal.get(pos))).get(1);
                if(!tipoId.equals(fun)){
                    GenError(9, lexema);
                    equipara(12); // (
                    L();
                    equipara(13); // )
                    equipara(16); // ;
                    return error;
                }
                equipara(12); // (
                String tipoL = L();
                equipara(13); // )
                equipara(16); // ;
                return fun + " " + tipoL;
            }else if(id == 19){ // +=
                writer.writeParse("21");
                int pos = mapaTSG.get(lexema);
                String tipoId = (String)((ArrayList<Object>)(tablaSimGlobal.get(pos))).get(1);
                if(!tipoId.equals(ent)){
                    GenError(24, "");
                    equipara(19); // +=
                    E();
                    equipara(16); // ;
                    return error;
                }
                equipara(19); // +=
                String tipoE = E();
                if(!tipoE.equals(ent)){
                    GenError(24, "");
                    equipara(16); // ;
                    return error;
                }
                equipara(16); // ;
                return tipoE;
            }else{
                GenError(7, tokensStrings[18] + "' | '" + tokensStrings[12] + "' | '" + tokensStrings[19]);
                return error;
            }
        }
        return ok;
    }

    private static String L(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 21 || id == 25 || id == 12 || id == 24 || id == 23){ // ! id ( ent lexema
                writer.writeParse("22");
                String tipoE = E();
                String[] devueltoQ = Q().split(" ");
                if(devueltoQ[0].equals(vacio)){
                    return "tipo_ok 1 "+ tipoE;
                }else{
                    devueltoQ[1] = "" + (Integer.parseInt(devueltoQ[1]) + 1);
                    return String.join(" ", devueltoQ) + " " + tipoE;
                }
            }
            else if(id == 13){ // )
                writer.writeParse("23");
                return vacio + " 0";
            }else{
                GenError(7, tokensStrings[12]+"' | '"+tokensStrings[13]+"' | '" + tokensStrings[21]+"' | '"+tokensStrings[23]+"' | '"+ tokensStrings[24]+"' | '"+tokensStrings[25]);
                return error;
            }
        }
        return ok;
    }
    
    private static String Q(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 17){ //Coma
                writer.writeParse("24");
                equipara(17); // Coma
                String tipoE = E();
                String[] devueltoQ = Q().split(" ");
                if(devueltoQ[0].equals(vacio)){
                    return "tipo_ok 1 " + tipoE;
                }else{
                    devueltoQ[1] = "" + (Integer.parseInt(devueltoQ[1]) + 1);
                    return String.join(" ", devueltoQ) + " " + tipoE;
                }
            }else if(id == 13){ // )
                writer.writeParse("25");
                return vacio;
            }else{
                GenError(7, tokensStrings[17]+"' | '"+tokensStrings[13]);
                return error;
            }
        } 
        return ok;
    }

    
    private static String X(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 21 || id == 25 || id == 12 || id == 23 || id == 24){ /// ! id ( ent lexema
                writer.writeParse("26"); 
                String tipoE = E();
                return tipoE;
            }else if(id == 16){ //Punto y coma
                writer.writeParse("27");
                return vacio;
            }else{
                GenError(0, tokensStrings[21]+"' | '"+tokensStrings[25]+"' | '" + tokensStrings[12]+"' | '"+tokensStrings[23]+"' | '"+ tokensStrings[24]+"' | '"+tokensStrings[16]);
                return error;
            }
        }
        return ok;
    }

    private static String B(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 5){ // if
                writer.writeParse("28");
                equipara(5); // if
                equipara(12); // parIzzq
                String tipoE = E();
                if(!tipoE.equals(logico)){
                    GenError(10, "");
                    equipara(13); // parDer
                    S();
                    return error;
                }else{
                    equipara(13); // parDer
                    String tipoS = S();
                    return tipoS.split(" ")[0]; // devuelve tipo
                }     
            }else if(id == 1){ // let
                writer.writeParse("29");
                zonaDeclaracion = true;
                equipara(1);  // let
                if(!(sigToken.getID()==25)){
                    GenError(29,"");
                    equipara(25); // id
                    T();
                    equipara(16); // punto y coma
                    return error;
                }
                int pos = (Integer)sigToken.getValue(); // posicion en tabla de simbolos
                equipara(25); // id
                zonaDeclaracion = false;
                String[] tipoT = T().split(" ");
                if(tablaG){
                    insertarTipoTS(pos, tipoT[0], tablaSimGlobal);
                    insertarDespTS(pos, desplGlobal, tablaSimGlobal);
                    desplGlobal = desplGlobal + Integer.parseInt(tipoT[1]);
                }else{
                    insertarTipoTS(pos, tipoT[0], tablaSimLocal);
                    insertarDespTS(pos, desplLocal, tablaSimLocal);
                    desplLocal = desplLocal + Integer.parseInt(tipoT[1]);
                }
                equipara(16); // punto y coma
                return ok;
            }else if(id == 25 || id == 9 || id == 10 || id == 11){ // id print input return 
                writer.writeParse("30");
                String tipoS = S();
                return tipoS;
            }else if(id == 6){// do
                writer.writeParse("31");
                equipara(6); // do
                equipara(14); // llaveizq
                if(sigToken != null){              // ----------------------------------------
                    id = sigToken.getID();
                    bucle = true;
                    String tipoC = C();
                    bucle = false;
                    equipara(15); // llaveDer
                    equipara(7);  // while
                    equipara(12); // parIzq
                    String tipoE = E();;
                    if(!tipoE.equals(logico)){
                        GenError(10, "");
                        equipara(13); // parDer
                        equipara(16); // ;
                        return error;
                    }else{
                        equipara(13); // parDer
                        equipara(16); 
                        return tipoC;
                    }
                }
            }else{
                GenError(7, tokensStrings[5]+"' | '"+tokensStrings[1]+"' | '" + tokensStrings[25]+"' | '"+tokensStrings[9]+"' | '"+ tokensStrings[10]+"' | '"+tokensStrings[11]+"' | '"+tokensStrings[6]);
                return error;
            }
        }
        return ok;
    }

    private static String T(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 2){ // int 
                writer.writeParse("32");
                equipara(2); // int
                String tipos = ent + " 1";
                return tipos;
            }
            else if(id == 4){ // boolean
                writer.writeParse("33");
                equipara(4);  // boolean
                String tipos = logico + " 1";
                return tipos;
            }
            else if(id == 3){ // string
                writer.writeParse("34");
                equipara(3); // string
                String tipos = cadena + " 64";
                return tipos;
            }
            else{
                GenError(7, tokensStrings[2]+"' | '"+tokensStrings[4]+"' | '" + tokensStrings[3]);
                String tipos = error + " 0";
                return tipos;
             }
        }
        return ok;
    }

    @SuppressWarnings("unchecked")
    private static String F(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 8){ // function
                writer.writeParse("35");
                dentroFun = true;
                zonaDeclaracion = true;
                equipara(8); // function
                if(sigToken.getValue() == " "){ // No es identificador
                    GenError(13, "");
                    equipara(25); // id
                    H();
                    equipara(12); // (
                    A();
                    equipara(13); // )
                    zonaDeclaracion = false;
                    equipara(14); // {
                    C();
                    equipara(15); // }
                    return error;
                }
                funcionAct = (Integer)sigToken.getValue();  // guardamos la posicion de ts para saber que funcion utilizamos
                String nombreFuncion = lexema;
                nombreLocales.add(lexema);                 // nombre identificador de funcion
                equipara(25);  // id
                tablaG = false;  // desactivamos tabla global
                // Inicializamos valores nueva tabla local
                tablaSimLocal = new ArrayList<Object>();
                mapaTSL = new HashMap<String,Integer>();
                desplLocal = 0;
                insertarTipoTS(funcionAct, fun, tablaSimGlobal);
                String tipoH = H();
                insertarTipoRetTS(funcionAct, tipoH);
                String etiq = nuevaEtiquetaTS();
                insertarEtiquetaTS(funcionAct, etiq);
                contadorFunciones++;
                // Parametros
                equipara(12); // (
                String tipoA = A();
                equipara(13); // )
                zonaDeclaracion = false;
                String[] tiposA = tipoA.split(" ");
                if(tiposA[0].equals(error)){
                    equipara(14); // {
                    C();
                    equipara(15); // }
                    tablaG = true;
                    return error;
                } 
                insertarNumParamTS(funcionAct, Integer.parseInt(tiposA[1]));
                insertarTipoParamTS(funcionAct, tiposA);
                equipara(14); // {
                String tipoC = C();
                if(!tieneRetorno && !tipoH.equals(vacio)){
                    GenError(30, nombreFuncion);
                    equipara(15); // }
                    tablaG = true;
                    return error;
                }
                tablaG = true;
                tablasLocal.add(tablaSimLocal);
                String tipoRetorno = (String)((ArrayList<Object>)tablaSimGlobal.get(funcionAct)).get(5);
                if(tipoC.equals(vacio) && !(tipoRetorno.equals(vacio))){
                    GenError(21, tipoRetorno);
                    equipara(15); // }
                    return error;
                }else{
                    equipara(15); // }
                    return ok;
                }
            }else{
                GenError(7, tokensStrings[8]+"' | '"+tokensStrings[5]+"' | '" + tokensStrings[1]+"' | '"+tokensStrings[66]+"' | '"+ tokensStrings[10]+"' | '"+tokensStrings[25]+"' | '"+tokensStrings[11]+"' | '"+tokensStrings[9]);
                return error;
            }
        }
        return ok;
    }

    private static String H(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 2 || id == 3 || id == 4){// int boolean string
                writer.writeParse("36");
                String[] tipoT = T().split(" ");
                return tipoT[0];
            }else if(id == 12){ // parIzq
                writer.writeParse("37");
                return vacio;
            }else{
                GenError(7,tokensStrings[2]+"' | '"+tokensStrings[3]+"' | '" + tokensStrings[4]+"' | '"+tokensStrings[12]);
                return error;
            }
        }
        return ok;
    }

    private static String A(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 2 || id == 3 || id == 4){ // int boolean string
                writer.writeParse("38");
                String[] devueltoT = T().split(" ");
                int pos = (Integer)(sigToken.getValue());
                equipara(25); // id
                insertarTipoTS(pos, devueltoT[0], tablaSimLocal);
                insertarDespTS(pos, desplLocal, tablaSimLocal);
                desplLocal = desplLocal + Integer.parseInt(devueltoT[1]);
                String[] devueltoK = K().split(" ");
                if(devueltoK[0].equals(vacio)){
                    return "tipo_ok 1 " + devueltoT[0];
                }else{
                    devueltoK[1] = "" + (Integer.parseInt(devueltoK[1]) + 1);
                    return String.join(" ", devueltoK) + " " + devueltoT[0];
                }
            }else if(id == 13){ // parDer
                writer.writeParse("39");
                return vacio + " 0";
            }else{
                GenError(7, tokensStrings[2]+"' | '"+tokensStrings[3]+"' | '" + tokensStrings[4]+"' | '"+tokensStrings[13]);
                return error;
            }
        }
        return ok;
    }

    private static String K(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 17){ // coma
                writer.writeParse("40");
                equipara(17); // coma
                String[] devueltoT = T().split(" ");
                equipara(25); // id
                int pos = tablaSimLocal.size() - 1;
                insertarTipoTS(pos, devueltoT[0], tablaSimLocal);
                insertarDespTS(pos, desplLocal, tablaSimLocal);
                desplLocal = desplLocal + Integer.parseInt(devueltoT[1]);
                String[] devueltoK = K().split(" ");
                if(devueltoK[0].equals(vacio)){
                    return "tipo_ok 1 " + devueltoT[0];
                }else{
                    devueltoK[1] = "" + (Integer.parseInt(devueltoK[1]) + 1);
                    return String.join(" ", devueltoK) + " " + devueltoT[0];
                }
            }else if(id == 13){ // parDer
                writer.writeParse("41");
                return vacio + " 0";
            }else{
                GenError(7, tokensStrings[17]+"' | '"+tokensStrings[13]);
                return error;
            }
        }
        return ok;
    }

    private static String C(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 5 || id == 1 || id == 6 || id == 25 || id == 10 || id == 11 || id == 9){ // if let do id print input return
                writer.writeParse("42");
                String tipoB = B();
                if(tipoB.equals(error)){
                    C();
                    return error;
                }
                String tipoC = C();
                if(tipoC.equals(vacio)){
                    return ok;
                }else{
                    return tipoC;
                }
            }else if(id == 15){ // llaveDer
                writer.writeParse("43");
                return vacio;
            }else{
                if(lexema.equals("function")){
                   if(bucle){
                        GenError(19, "");
                   }else{
                        GenError(18, "");
                   }
                }else{
                    GenError(7, tokensStrings[5] + "' | '" + tokensStrings[1] + "' | '" + tokensStrings[6] + "' | '" + tokensStrings[25] + "' | '" + tokensStrings[10] + "' | '" + tokensStrings[11] + "' | '" + tokensStrings[9] + "' | '" + tokensStrings[15]);
                }
                return error;
            }
        }
        return ok;
    }
    
    private static String P(){
        if(sigToken != null){
            int id = sigToken.getID();
            if(id == 5 || id == 1 || id == 6 || id == 25 || id == 10 || id == 11 || id == 9){ // if let do id print input return
                writer.writeParse("44");
                String tipoB = B();
                String tipoP = P();
                if(tipoB.equals(vacio)){
                    return tipoP;
                }else if(tipoB.equals(error) || tipoP.equals(error)){
                    return error;
                }
            }else if(id == 8){ // function
                writer.writeParse("45");
                String tipoF = F();
                tieneRetorno = false;
                dentroFun = false;
                String tipoP = P();
                if(tipoF.equals(vacio)){
                    return tipoP;
                }else if(tipoF.equals(error) || tipoP.equals(error)){
                    return error;
                }
            }else if(id == 65535){ // EOF $
                writer.writeParse("46");
                return vacio;
            }else{
                GenError(7, tokensStrings[5] + "' | '" + tokensStrings[1] + "' | '" + tokensStrings[6] + "' | '" + tokensStrings[25] + "' | '" + tokensStrings[10] + "' | '" + tokensStrings[11] + "' | '" + tokensStrings[9] + "' | '" + tokensStrings[8] + "' | $ (EOF)" );
                return error;
            }
        }
        return ok;
    }

//  AUXILIAR FUNCTIONS

    private static int buscoIdTS(String id){
        int pos = -1;
        if(tablaG){
             if(mapaTSG.containsKey(id)){
                pos = 0;
            }
        }else{ 
             if(mapaTSL.containsKey(id) || mapaTSG.containsKey(id)){
                 pos = 0;
            }
        }
        return pos;
    }

    @SuppressWarnings("unchecked")
    private static void insertarTipoTS(int pos, String tipo,  List<Object> lista){
        ArrayList<Object> listaAtributos = (ArrayList<Object>)lista.get(pos);
        listaAtributos.set(1,tipo);
        lista.set(pos, listaAtributos);
    }

    @SuppressWarnings("unchecked")
    private static void insertarDespTS(int pos, int desp,  List<Object> lista){
        ArrayList<Object> listaAtributos = (ArrayList<Object>)lista.get(pos);
        listaAtributos.set(2,desp);
        lista.set(pos, listaAtributos);
    }
    
    @SuppressWarnings("unchecked")
    private static void insertarNumParamTS(int pos, int param){
        ArrayList<Object> listaAtributos = (ArrayList<Object>)tablaSimGlobal.get(pos);
        listaAtributos.set(3,param);
        tablaSimGlobal.set(pos, listaAtributos);
    }

    @SuppressWarnings("unchecked")
    private static void insertarTipoParamTS(int pos,  String[] lista){
        ArrayList<Object> listaAtributos = (ArrayList<Object>)tablaSimGlobal.get(pos);
        ArrayList<Object> listaParametros = (ArrayList<Object>)listaAtributos.get(4);
        for(int i = 2; i<lista.length;i++){
            listaParametros.add(lista[i]);
        }
        listaAtributos.set(4,listaParametros);
        tablaSimGlobal.set(pos,listaAtributos);
    }

    @SuppressWarnings("unchecked")
    private static void insertarTipoRetTS(int pos, String ret){
        ArrayList<Object> listaAtributos = (ArrayList<Object>)tablaSimGlobal.get(pos);
        listaAtributos.set(5, ret);
        tablaSimGlobal.set(pos, listaAtributos);
    }

    @SuppressWarnings("unchecked")
    private static void insertarEtiquetaTS(int pos, String etiqueta){
        ArrayList<Object> listaAtributos = (ArrayList<Object>)tablaSimGlobal.get(pos);
        listaAtributos.set(6, etiqueta);
        tablaSimGlobal.set(pos, listaAtributos);
    }

    private static String nuevaEtiquetaTS(){
        String nombre = "EtFun";
        nombre = nombre + contadorFunciones;
        return nombre;
    }
    
    private static Token GenToken(Integer id, Object value, String comment){
        Token token = new Token(id, value);
        writer.writeToken(token.toString() + "  // token " + comment + "\n");
        return token;
    }

    private static void GenError (int error, String data){
        int numero;
        if(data.equals("id")){
            data="identicador (nombre de una variable o función)";
        }else if(data.equals("ent")){
            data = "constante entera";
        }else if(data.equals("lexema")){
            data = "cadena de caracteres";
        }
       if(sigToken.getID()==65535){
            numero=0;
        }
       else{
            numero=sigToken.getID();
        }
        switch(error){
            case 1: 
                writer.writeError("Error léxico (1): Línea " + line + ": Se ha producido un error en la generación del token. No se esperaba el carácter '" + data + "'.\n");
                errorStateLexSint = true;
                GenTS();
                Terminar();
                break;
            case 2:
                writer.writeError("Error léxico (2): Línea " + line + ": Se ha superado el número máximo de caracteres: 64. Número actual de caracteres: " + data + "\n");
                errorStateLexSint = true;
                GenTS();
                Terminar();
                break;
            case 3:
                writer.writeError("Error léxico (3): Línea " + line + ": Entero fuera de rango. El número no debe ser superior a 32767.\n");
                errorStateLexSint = true;
                GenTS();
                Terminar();
                break;
            case 4:
                writer.writeError("Error léxico (4): Línea " + line + ": No se esperaba el carácter '/'. En caso de querer escribir un comentario, solo se admite el siguiente formato: // Comentario\n");
                errorStateLexSint = true;
                GenTS();
                Terminar();
                break;
            case 5:
                writer.writeError("Error semántico (5): Línea " + line + ": Ya existe el identificador " + data + ". Elija otro nombre.\n");
                errorState = true;
                //GenTS();
                //Terminar();
                break;
            case 6:
                writer.writeError("Error sintáctico (6): Línea " + line + ": Se ha encontrado '" + tokensStrings[numero] + "' y se esperaba '" + data + "'\n");
                errorStateLexSint = true;
                GenTS();
                Terminar();
                break;
            case 7:
                writer.writeError("Error sintáctico (7): Línea " + line + ": Se ha encontrado '" + tokensStrings[numero] + "' y se esperaba uno de estos tokens: '" + data + "'\n");
                errorStateLexSint = true;
                GenTS();
                Terminar();
                break;
            case 8:
                writer.writeError("Error sintáctico (8): Línea " + line + ": Se ha encontrado '" + tokensStrings[numero] + "' y se esperaba el fin de fichero.\n");
                errorStateLexSint = true;
                GenTS();
                Terminar();
                break;
            case 9:
                writer.writeError("Error semántico (9): Línea " + line + ": El identificador '" + data + "' no está declarado.\n");
                errorState = true;
                //GenTS();
                //Terminar();
                break;
            case 10:
                writer.writeError("Error semántico (10): Línea " + line + ": La expresión debe ser de tipo lógico.\n");
                errorState = true;
                //GenTS();
                //Terminar();
                break;
            case 11:
                String[] datos = data.split(" ");
                writer.writeError("Error semántico (11): Línea " + line + ": Se ha encontrado un parámetro de tipo " + datos[0] + " y se esperaba un parámetro de tipo "+ datos[1] + ".\n");
                errorState = true;
                //GenTS();
                //Terminar();
                break;
            case 12:
                writer.writeError("Error semántico (12): Línea " + line + ": La instrucción print solo evalua expresiones de tipo cadena o de tipo entero.\n");
                errorState = true;
                //GenTS();
                //Terminar();
                break;
            case 13:
                writer.writeError("Error semántico (13): Línea " + line + ": No se ha definido un nombre para la función.\n");
                errorState = true;
                //GenTS();
                //Terminar();
                break;
            case 14:
                writer.writeError("Error semántico (14): Línea " + line + ": La función no tiene definido un valor de retorno.\n");
                errorState = true;
                //GenTS();
                //Terminar();
                break;
            case 15:
                writer.writeError("Error semántico (15): Línea " + line + ": Ambos lados de la expresión deben de ser de tipo entero.\n");
                errorState = true;
                //GenTS();
                //Terminar();
                break;
            case 16:
                writer.writeError("Error léxico (16): Línea " + line + ": Para definir las cadenas de caracteres use comillas simples en vez de dobles.\n");
                errorStateLexSint = true;
                GenTS();
                Terminar();
                break;
            case 17:
                writer.writeError("Error semántico (17): Línea " + line + ": La función " + data + " no está declarada.\n");
                errorState = true;
                //GenTS();
                //Terminar();
                break;
            case 18:
                writer.writeError("Error semántico (18): Línea " + line + ": El lenguaje no permite la definición de funciones anidadas.\n");
                errorState = true;
                //GenTS();
                //Terminar();
                break;
            case 19:
                writer.writeError("Error semántico (19): Línea " + line + ": El lenguaje no permite la definición de funciones dentro de un bucle.\n");
                errorState = true;
                //GenTS();
                //Terminar();
                break;
            case 20:
                String[] tipos = data.split(" ");
                writer.writeError("Error semántico (20): Línea " + line + ": El tipo de retorno (" +tipos[0] +") no coincide con el definido en la función (" + tipos[1] + ").\n");
                errorState = true;
                //GenTS();
                //Terminar();
                break;
            case 21:
                writer.writeError("Error semántico (21): Línea " + line + ": La función tiene que devolver una expresión de tipo " + data + ".\n");
                errorState = true;
                //GenTS();
                //Terminar();
                break;
            case 22:
                writer.writeError("Error semántico (22): Línea " + line + ": Una función sin retorno no debe devolver nada.\n");
                errorState = true;
                //GenTS();
                //Terminar();
                break;
            case 23:
                writer.writeError("Error semántico (23): Línea " + line + ": Ambos lados de la expresión deben de ser de tipo " + data + ".\n");
                errorState = true;
                //GenTS();
                //Terminar();
                break;  
            case 24:
                writer.writeError("Error semántico (24): Línea " + line + ": Los operadores aritméticos (+=) solo se aplican sobre tipos enteros.\n");
                errorState = true;
                //GenTS();
                //Terminar();
                break;
            case 25:
                writer.writeError("Error semántico (25): Línea " + line + ": Los operadores relacionales (<) solo se aplican sobre tipos enteros.\n");
                errorState = true;
                //GenTS();
                //Terminar();
                break;   
            case 26:
                writer.writeError("Error semántico (26): Línea " + line + ": Los operadores lógicos (!) solo se aplican sobre tipos lógicos.\n");
                errorState = true;
                //GenTS();
                //Terminar();
                break;
            case 27:
                writer.writeError("Error semántico (27): Línea " + line + ": Los operadores aritméticos (*) solo se aplican sobre tipos enteros.\n");
                errorState = true;
                //GenTS();
                //Terminar();
                break; 
            case 28:
                if(data.equals("1")){
                    writer.writeError("Error semántico (28): Línea " + line + ": No coincide el número de parámetros de la función. Debería tener " + data +" parámetro.\n");
                }else{
                    writer.writeError("Error semántico (28): Línea " + line + ": No coincide el número de parámetros de la función. Debería tener " + data +" parámetros.\n");
                }
                errorState = true;
                //GenTS();
                //Terminar();
                break; 
            case 29:
                writer.writeError("Error semántico (29): Línea " + line + ": El orden para declarar una variable es: let + identificador + tipo\n");
                errorState = true;
                //GenTS();
                //Terminar();
                break;
            case 30:
                writer.writeError("Error semántico (30): Línea " + line + ": La función '"+ data +"' debe tener retorno al no ser de tipo vacio.\n");
                errorState = true;
                //GenTS();
                //Terminar();
                break;
            case 31:
                writer.writeError("Error semántico (31): Línea " + line + ": La instrucción input solo evalua expresiones de tipo cadena o de tipo entero.\n");
                errorState = true;
                //GenTS();
                //Terminar();
                break;
        }
}
    private static void Terminar(){
        try {
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Se han producido errores. Consulte el fichero 'errors.txt' para encontrar los detalles.");
        System.exit(0);
    }
    
    @SuppressWarnings("unchecked")
    private static void GenTS(){
        writer.writeTS("CONTENIDO DE LA TABLA DE SIMBOLOS GLOBAL #1 :\n");
        writer.writeTS("\n");
        for(int i = 0; i < tablaSimGlobal.size(); i++){
            List<Object> lista =(ArrayList<Object>)tablaSimGlobal.get(i);
            writer.writeTS("* LEXEMA :\t\t'" + lista.get(0) + "'\n");
            writer.writeTS("  ATRIBUTOS:\n");
            writer.writeTS("+ tipo:\t\t\t'" + lista.get(1)+"'\n");
            if(!(lista.get(2).equals(""))){
                writer.writeTS("+ despl:\t\t" + lista.get(2) + "\n");
            }
            
            if(!(lista.get(3).equals(""))){
                writer.writeTS("+ numParam:\t\t"+lista.get(3) + "\n");
                List<Object> tipos =(ArrayList<Object>)lista.get(4);
                int contador = 1;
        
                for(int j = 0; j<tipos.size();j++){
                     writer.writeTS("+ TipoParam" + contador + ":\t'" + tipos.get(j) + "'\n");
                     contador++;
                }
                writer.writeTS("+ TipoRetorno:\t'" + lista.get(5)+"'\n");
                writer.writeTS("+ EtiqFuncion:\t'" +lista.get(6)+"'\n");
            }
            writer.writeTS("--------- --------- \n");
        }

        writer.writeTS("----------------------------------------- \n");
        int numeroTablasLocales = 2;
        for( int k = 0; k < tablasLocal.size(); k++){
            String nombre = (String)nombreLocales.get(k);
            List<Object> local =(ArrayList<Object>)tablasLocal.get(k);
            writer.writeTS("CONTENIDO DE LA TABLA DE SIMBOLOS LOCAL DE LA FUNCION "+ nombre +" #"+ numeroTablasLocales+" :\n");
            writer.writeTS("\n");
            int p = 0;
            while ( p < local.size()){
                List<Object> atribL =(ArrayList<Object>)local.get(p);
                writer.writeTS("* LEXEMA : '" + atribL.get(0) + "'\n");
                writer.writeTS("  ATRIBUTOS:\n");
                writer.writeTS("+ tipo:\t\t'" + atribL.get(1)+"'\n");
                writer.writeTS("+ despl:\t" + atribL.get(2) + "\n");
                writer.writeTS("--------- --------- \n");
                p++;
            }
             writer.writeTS("----------------------------------------- \n");
            numeroTablasLocales++;
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
        SyntaticAnalizer();
        GenTS();
        destroyTS();
        reader.close();
        writer.close();
        if(errorState){
            System.out.println("Se han producido errores. Consulte el fichero 'errors.txt' para encontrar los detalles.");
        }else{
            System.out.println("Todo bien jiji");
        }
        System.exit(SUCCED);
    }
}