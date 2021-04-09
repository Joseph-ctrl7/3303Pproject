public class ElevatorData {
    private int location;
    private int port;
    private String state;

    public ElevatorData(int location, int port, String state){
        this.location = location;
        this.port = port;
        this.state = state;
    }

    public void setState(String state){
        this.state = state;
    }
    public void setLocation(int location){
        this.location = location;
    }
    public void setPort(){
        this.port = port;
    }
    public String getState(){
        return this.state;
    }
    public int getLocation(){
        return this.location;
    }
    public int getPort(){
        return this.port;
    }

}
