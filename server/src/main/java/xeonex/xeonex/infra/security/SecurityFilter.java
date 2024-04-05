package xeonex.xeonex.infra.security;

import xeonex.xeonex.Exception.TokenInvalidException;
import xeonex.xeonex.Exception.TokenExpiredExceptions;
import xeonex.xeonex.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static xeonex.xeonex.Utils.sendError;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Autowired
    TokenService tokenService;
    @Autowired
    UserRepository userRepository;

    @Autowired
    PermissionService permissionService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);
        if (token != null) {
            try {
                var login = tokenService.validateToken(token);
                UserDetails user = userRepository.findByLogin(login);
                if(!permissionService.hasPermission(request, user)){
                    sendError(response, HttpServletResponse.SC_FORBIDDEN, "You don't have permission to access this resource");
                    return;
                }
                var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (TokenExpiredExceptions e) {
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
                return;
            } catch (TokenInvalidException e) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
                return;
            }
        }

        filterChain.doFilter(request, response);

    }



    private String recoverToken(HttpServletRequest request){
        var authHeader = request.getHeader("Authorization");
        if(authHeader == null) return null;
        return authHeader;
    }
}