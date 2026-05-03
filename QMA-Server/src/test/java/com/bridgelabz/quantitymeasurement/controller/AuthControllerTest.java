package com.bridgelabz.quantitymeasurement.controller;

import com.bridgelabz.quantitymeasurement.dto.LoginRequest;
import com.bridgelabz.quantitymeasurement.dto.RegisterRequest;
import com.bridgelabz.quantitymeasurement.entity.User;
import com.bridgelabz.quantitymeasurement.repository.UserRepository;
import com.bridgelabz.quantitymeasurement.security.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Ignore security so we don't get 401 Unauthorized
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testLoginSuccess() throws Exception {
        // 1. Create a dummy login request
        LoginRequest req = new LoginRequest();
        req.setEmail("test@test.com");
        req.setPassword("secret");

        // 2. Mock the authentication manager to return a fake Authentication object
        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);

        // 3. Mock JwtUtils to return a dummy token
        Mockito.when(jwtUtils.generateJwtToken(anyString())).thenReturn("dummy-token");

        // Convert the object to JSON
        String jsonStr = objectMapper.writeValueAsString(req);

        // 4. Test the API
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonStr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("dummy-token"));
    }

    @Test
    public void testRegisterSuccess() throws Exception {
        // 1. Create a new user request
        RegisterRequest req = new RegisterRequest();
        req.setName("Deepak");
        req.setEmail("deepak@test.com");
        req.setPassword("pass");

        // 2. Mock repository so it pretends the email is not in use
        Mockito.when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(anyString())).thenReturn("hashed-pass");

        String jsonStr = objectMapper.writeValueAsString(req);

        // 3. Call the API and expect it to work
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonStr))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully!"));
    }

    @Test
    public void testRegisterFailsWhenEmailExists() throws Exception {
        // 1. Create request
        RegisterRequest req = new RegisterRequest();
        req.setName("Deepak");
        req.setEmail("deepak@test.com");
        req.setPassword("pass");

        // 2. Mock the database to say "Yes, this email already exists"
        User dummyUser = new User();
        Mockito.when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(dummyUser));

        String jsonStr = objectMapper.writeValueAsString(req);

        // 3. The API should return a 400 Bad Request error
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonStr))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Email is already in use!"));
    }
}
