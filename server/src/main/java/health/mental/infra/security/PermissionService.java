package health.mental.infra.security;

import health.mental.domain.User.User;
import health.mental.domain.User.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PermissionService {

    private final Map<String, Map<HttpMethod, Set<UserRole>>> urlPermissions;

    private final Map<UserRole, List<SimpleGrantedAuthority>> hierarchy;

    public PermissionService() {
        // hierarchy
        hierarchy = new HashMap<>();

        SimpleGrantedAuthority user = new SimpleGrantedAuthority(UserRole.USER.toString());
        SimpleGrantedAuthority psico = new  SimpleGrantedAuthority(UserRole.PSICO.toString());
        SimpleGrantedAuthority admin = new SimpleGrantedAuthority(UserRole.ADMIN.toString());

        hierarchy.put(UserRole.ADMIN, List.of(psico, user, admin));
        hierarchy.put(UserRole.PSICO, List.of(user,psico));
        hierarchy.put(UserRole.USER, List.of(user));

        // permissions
        urlPermissions = new HashMap<>();


        Map<HttpMethod, Set<UserRole>> auth = new HashMap<>();
        auth.put(HttpMethod.POST, null);
        urlPermissions.put("/auth/login", auth);
        urlPermissions.put("/auth/register", auth);


        auth = new HashMap<>();
        auth.put(HttpMethod.POST, null);
        auth.put(HttpMethod.GET, null);
        urlPermissions.put("/gpt/ask", auth);
        urlPermissions.put("/coin/{coinName}", auth);

        auth = new HashMap<>();
        auth.put(HttpMethod.POST, Set.of(UserRole.ADMIN));
        urlPermissions.put("/product", auth);

        auth = new HashMap<>();
        auth.put(HttpMethod.GET, Set.of(UserRole.PSICO));
        urlPermissions.put("/product/{id}", auth);

    }

    public Map<String, Map<HttpMethod, Set<UserRole>>> getUrlPermissions() {
        return urlPermissions;
    }

    public Map<UserRole,List<SimpleGrantedAuthority>> getHierarchyMap() {
        return hierarchy;
    }
    public boolean hasPermission(HttpServletRequest request, UserDetails user) {
        String url = request.getRequestURI();
        String method = request.getMethod();


        Pattern pattern = Pattern.compile("/\\d+$");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            url = matcher.replaceFirst("/{id}");
        }

        try {
            Set<UserRole> roles  = urlPermissions.get(url).get(HttpMethod.valueOf(method));
            return user.getAuthorities().stream().anyMatch(a -> roles.contains(UserRole.valueOf(a.getAuthority().split("_")[1].toUpperCase())));
        }catch (NullPointerException e){
            return true;
        }




    }
}
