
/* Finist deterministic automate:
 * This class defines the transitions matrix that gives the information
 * about the actions that have to be done.
 * State:  COMPLETED
 */


public class AFD {
    private static final int TOTAL_STATES = 7;
    private static final int NUM_CHAR = 255;
    private MTpair[][] matrix;

    public AFD(){
        matrix = new MTpair[TOTAL_STATES][NUM_CHAR];
        for(int i = 0; i < TOTAL_STATES; i++){
            createPairs(i);
        }
    }

    private void createPairs (int state){
        switch(state){
            case 0: // Initial state
                for(int i = 0; i < NUM_CHAR; i++){
                //  Case letter or underscore
                    if((i >= 'A' && i <= 'Z') || (i >= 'a' && i <= 'z') || i == 'ñ' || i == 'Ñ'|| i == '_'){
                        matrix[state][i] = new MTpair(2, 'X');
                    }
                //  Case digit    
                    else if( i >= '0' && i <= '9'){
                        matrix[state][i] = new MTpair(1, 'X');
                    }
                //  Case slash     
                    else if( i == '/'){
                        matrix[state][i] = new MTpair(4, 'L');
                    }
                //  Case plus     
                    else if( i == '+'){
                        matrix[state][i] = new MTpair(6, 'X');
                    }
                //  Case single quote     
                    else if( i == '\''){
                        matrix[state][i] = new MTpair(3, 'L');
                    }
                //  Case comma   
                    else if( i == ','){
                        matrix[state][i] = new MTpair(12, 'F');
                    }  
                //  Case semicolon     
                    else if( i == ';'){
                        matrix[state][i] = new MTpair(13, 'E');
                    }
                //  Case left parentesis     
                    else if( i == '('){
                        matrix[state][i] = new MTpair(14, 'A');
                    }
                //  Case right parentesis     
                    else if( i == ')'){
                        matrix[state][i] = new MTpair(15, 'B');
                    }
                //  Case left parentesis     
                    else if( i == '{'){
                        matrix[state][i] = new MTpair(16, 'C');
                    }
                //  Case right parentesis     
                    else if( i == '}'){
                        matrix[state][i] = new MTpair(17, 'D');
                    }
                //  Case less than      
                    else if( i == '<'){
                        matrix[state][i] = new MTpair(18, 'K');
                    }
                //  Case negation     
                    else if( i == '!'){
                        matrix[state][i] = new MTpair(19, 'J');
                    }
                //  Case equal     
                    else if( i == '='){
                        matrix[state][i] = new MTpair(20, 'G');
                    }
                //  Case product     
                    else if( i == '*'){
                        matrix[state][i] = new MTpair(21, 'I');
                    }
                //  Case del     
                    else if( i == ' ' || i == '\t' || i == '\n' || i == '\r'){ // si el espacio no funciona cambiar por el codigo ascii directamente
                        matrix[state][i] = new MTpair(0, 'L');
                    }
                }
                break;
            case 1:
                for(int i = 0; i < NUM_CHAR; i++){
                    if( i >= '0' && i <= '9'){
                        matrix[state][i] = new MTpair(1, 'X');
                    }else{
                        matrix[state][i] = new MTpair(7, 'N');  
                    }
                }
                break;
            case 2: // variable name or reserved word
                for(int i = 0; i < NUM_CHAR; i++){
                //  Case letter or underscore or digit
                    if((i >= 'A' && i <= 'Z') || (i >= 'a' && i <= 'z') || i == '_' || (i >= '0' && i <= '9') ){
                        matrix[state][i] = new MTpair(2, 'X');
                    }else{
                        matrix[state][i] = new MTpair(8, 'O');
                    }
                }
                break;
            case 3: // Sigle quote
                for(int i = 0; i < NUM_CHAR; i++){
                    if(i == '\''){
                        matrix[state][i] = new MTpair(9, 'M');   
                    }else{
                        matrix[state][i] = new MTpair(3, 'X');
                    }
                }
                break;
            case 4: // Case comment
                matrix[state]['/'] = new MTpair(5, 'L');        // REVISAR PORQUE LEO
                break;
            case 5: 
                for(int i = 0; i < NUM_CHAR; i++){
                    if(i == '\n'){
                        matrix[state][i] = new MTpair(10, 'L'); // REVISAR PORQUE LEO   
                    }else{
                        matrix[state][i] = new MTpair(5, 'L'); // REVISAR PORQUE LEO    
                    }
                }
                break;
            case 6: // Case sum asignation
                matrix[state]['='] = new MTpair(11, 'H');
                break;
        }
    }

    /* Method: getMTpair(Integer state, Character action)
     * Return value: the MTpair object that corresponds to the state and action given. 
     */
    public MTpair getMTpair(Integer state, Character action){
        return matrix[state][action];
    }

}
