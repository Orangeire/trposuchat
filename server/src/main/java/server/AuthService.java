package server;

public interface AuthService {

    /**
     * @return - nickname если пользоатель есть и null если пользователя нет
     */
    String getNicknameByLoginAndPassword(String login, String password);


    boolean registration(String login, String password, String nickname);

}
