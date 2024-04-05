package health.mental.domain.User;

public class UserMapper {

    public static  UserMeDTO toUserMeDTO(User user) {
        return new UserMeDTO(user.getLogin(), user.getRole(), user.getBalanceInvested(), user.getBalanceAvailable(), user.getBalanceInvested().add(user.getBalanceAvailable())  );
    }
}
