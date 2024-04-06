package xeonex.xeonex.controller;

import xeonex.xeonex.Exception.TokenInvalidException;
import xeonex.xeonex.Utils;
import xeonex.xeonex.domain.User.*;
import xeonex.xeonex.infra.security.TokenService;
import xeonex.xeonex.repositories.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

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

    @PutMapping("/me")
    public ResponseEntity<?> updateLoggedInUser(@RequestHeader("Authorization") String bearerToken,
                                                @RequestBody UserUpdateRequestDTO updateRequest) {

        String token = bearerToken.substring(7);
        String userLogin = tokenService.validateToken(token);
        User user = (User) userRepository.findByLogin( userLogin);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        if (updateRequest.getRisk() != null && updateRequest.getRisk() >= 5 && updateRequest.getRisk() <= 80) {
            user.setRisk(new Risk(updateRequest.getRisk()));
        }else{
            return ResponseEntity.badRequest().body("{\"error\": \"Invalid risk level, should be between 5 and 80\"}");
        }
        if (updateRequest.getBalanceAvailable() != null && updateRequest.getBalanceAvailable()   .compareTo( user.getBalanceAvailable()) >= 0) {
            user.setBalanceAvailable(updateRequest.getBalanceAvailable());
        }else{
            return ResponseEntity.badRequest().body("{\"error\": \"Invalid balance update, should be greater than the actual balance\"}");
        }
        if(updateRequest.getCurrency() == null){
            return ResponseEntity.badRequest().body("{\"error\": \"Currency is required\"}");
        }

        String currencyString = updateRequest.getCurrency();
        Currency currencyEnum = Currency.fromString(currencyString);
        user.setCurrency(currencyEnum);

        userRepository.save(user);

        return ResponseEntity.ok(UserMapper.toUserMeDTO(user));
    }


    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid AuthRegisterDTO authDTO) {



        if(this.userRepository.findByLogin(authDTO.login()) != null){
               return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST).body("{\"error\": \"Login/Username already in use\"}");
        }


        String encodedPassword =  new BCryptPasswordEncoder().encode(Utils.decodeJwt(authDTO.password()));
        User user = new User(authDTO.login(), encodedPassword, authDTO.role(),Currency.TetherDollar);

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
