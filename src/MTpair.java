
// State: COMPLETED 

public class MTpair {
//  Atributes of pairs
    private Integer state;
    private Character action;

    public MTpair(Integer state, Character action){
        this.state = state;
        this.action = action;
    }

    public Integer getState(){
        return state;
    }

    public Character getAction(){
        return action;
    }

    public String toString(){
        return "[" + state + "," + action.toString() + "]";
    }
}
