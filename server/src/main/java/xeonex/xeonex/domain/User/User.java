package xeonex.xeonex.domain.User;

import lombok.*;
import xeonex.xeonex.infra.security.PermissionService;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Collection;

@Entity(name = "users")
@Table(name = "users")

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@EqualsAndHashCode(of = {"id"})
@ToString

public class User implements UserDetails {



    @Transient
    private int defaultMoney = 1000;

    @Transient
    @Value("${user.default.risk}")
    private int defaultRisk;



    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;



    @Column(name = "currency")
    private Currency currency;

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Column(name = "login", unique = true)
    private String login;

    @Column(name = "password")
    private String password;

    @Column(name = "balance_invested")
    private BigDecimal balanceInvested;

    @Column(name = "balance_available")
    private BigDecimal balanceAvailable;

    @Column(name = "risk")
    @Embedded
    private Risk risk;
    @Column(name = "role")
    private UserRole role;

    @Column(name = "img")
    private String img;

    public User(String login, String password, UserRole role, Currency currency) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.balanceInvested = new BigDecimal(0);
        this.balanceAvailable = new BigDecimal(defaultMoney);
        this.risk = new Risk(5);
        this.currency = currency;
        this.img = "";
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        PermissionService permissionService = new PermissionService();
        return permissionService.getHierarchyMap().get(role);
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}
