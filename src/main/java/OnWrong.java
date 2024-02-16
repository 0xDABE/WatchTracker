public class OnWrong {

    public OnWrong(String errorText){
        Menu.wrongCommand = true;
        System.out.println(errorText);
    }
}
