package health.mental.controller;

import health.mental.Exception.TokenExpiredExceptions;
import health.mental.Exception.TokenInvalidException;
import health.mental.Utils;
import health.mental.domain.User.*;
import health.mental.infra.security.TokenService;
import health.mental.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import jakarta.validation.Valid;
import org.apache.catalina.connector.Response;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;


    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthDTO authDTO) {

        if(userRepository.findByLogin(authDTO.login()) == null){
              register(new AuthRegisterDTO(authDTO.login(), authDTO.password(), UserRole.USER));
        }





        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(authDTO.login(), Utils.decodeJwt(authDTO.password()));
            authenticationManager.authenticate(usernamePassword);
            var authentication = authenticationManager.authenticate(usernamePassword);
            var token = tokenService.generateToken((User) authentication.getPrincipal());
            return ResponseEntity.ok(new LoginResponseDTO(token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("{\"error\": \"Invalid login or password\"}");
        }catch (TokenInvalidException e){
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("{\"error\": \"Invalid token\"}");
        }

    }


    @GetMapping("/me")
    public ResponseEntity<?> getLoggedInUser(@RequestHeader("Authorization") String bearerToken) {



            String token = bearerToken.substring(7);
            String userLogin = tokenService.validateToken(token);

            userRepository.findByLogin(userLogin);


            return ResponseEntity.ok( UserMapper.toUserMeDTO((User) userRepository.findByLogin(userLogin)));




    }


    /*
    @GetMapping("/test")
    public ResponseEntity test(HttpServletRequest request) {

        return ResponseEntity.ok().body("Test");
    }
*/
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid AuthRegisterDTO authDTO) {



        if(this.userRepository.findByLogin(authDTO.login()) != null){
               return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST).body("{\"error\": \"Login/Username already in use\"}");
        }


        String encodedPassword =  new BCryptPasswordEncoder().encode(Utils.decodeJwt(authDTO.password()));
        User user = new User(authDTO.login(), encodedPassword, authDTO.role());
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }



// Para formalarios com multipart

/*
    @PostMapping("/register")
    public ResponseEntity register(@Valid  HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            String login = multipartRequest.getParameter("login");
            String password = multipartRequest.getParameter("password");
            MultipartFile img =  multipartRequest.getFile("img");


            String directory = "src/main/resources/static/img/" ;

            File directoryFile = new File(directory);
            if (!directoryFile.exists()) {
                directoryFile.mkdirs();
            }


            String filePath = directory + login+ img.getOriginalFilename().substring(img.getOriginalFilename().lastIndexOf("."));


            File file = new File(filePath);
            file.createNewFile();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(img.getBytes());
            fos.close();



        return ResponseEntity.ok().body("folder");

        } catch (Exception e) {

            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).body("{\"error\": \"Internal Server Error\"}");
        }
    }
*/

}
