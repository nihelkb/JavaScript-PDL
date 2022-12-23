
public class Token {
//  Atributos de un token
    private Integer id;
    private Object value;

//  Constructor de un token 
    public Token(Integer id, Object value){
        this.id = id;
        this.value = value;
    }

    public Integer getID(){
        return id;
    }

    public Object getValue(){
        return value;
    }

    public String toString(){
        return "<" + id + "," + value + ">";
    }
}
