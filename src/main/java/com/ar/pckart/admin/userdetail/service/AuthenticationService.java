package com.ar.pckart.admin.userdetail.service;

import java.security.Principal;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ar.pckart.admin.Admin;
import com.ar.pckart.admin.Role;
import com.ar.pckart.admin.config.JwtService;
import com.ar.pckart.admin.config.JwtUsersService;
import com.ar.pckart.admin.dto.AuthenticationRequest;
import com.ar.pckart.admin.dto.AuthenticationResponse;
import com.ar.pckart.admin.dto.RegisterRequest;
import com.ar.pckart.admin.repo.AdminRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
	
	private final AdminRepository repository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final JwtUsersService jwtUsersService; // to connect users ms
	private final AuthenticationManager authenticationManager;
	
	public AuthenticationResponse register(RegisterRequest request) {
		var admin = Admin.builder()
				.fullname(request.getFullname())
				.email(request.getEmail())
				.mobile(request.getMobile())
				.username(request.getUsername())
				.password(passwordEncoder.encode(request.getPassword()))
				.role(Role.valueOf(request.getRole()))
				.build();
	
		repository.findByUsername(request.getUsername()).ifPresent(
				u-> {
					throw new AdminLoginException("username "+u.getUsername()+" already exist");
				});
		
		repository.save(admin);
		
		var jwtToken = jwtService.generateToken(new AdminUserDetails(admin));
		return AuthenticationResponse.builder()
				.token(jwtToken)
				.build();
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						 request.getUsername(),
						 request.getPassword()
						 )
				);
		
		var admin = repository.findByUsername(request.getUsername())
				.orElseThrow();
		var jwtToken = jwtService.generateToken(new AdminUserDetails(admin));
		return AuthenticationResponse.builder()
				.token(jwtToken)
				.username(admin.getUsername())
				.role(admin.getRole().name())
				.build();
	}

	public AuthenticationResponse genTokenUser(HttpServletRequest req) {
		String username = req.getUserPrincipal().getName();
		System.out.println(username+"|||||||||"+req.getUserPrincipal().getName());
		Principal principal = req.getUserPrincipal();
		if(principal instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) principal;
			System.err.println(userDetails.getAuthorities());
		}
		var jwtUserToken = jwtUsersService.generateToken(username); 
		return AuthenticationResponse.builder()
				.username(username)
				.token(jwtUserToken)
				.build();
	}
}


/*
public String genTokenUser(HttpServletRequest req) {
	//String username = req.getUserPrincipal().getName();
	Principal principal = req.getUserPrincipal();
	UserDetails userDetails = null;
	if(principal instanceof UserDetails) {
		 userDetails = (UserDetails) principal;
	}
	return jwtUsersService.generateToken(userDetails);
}
*/
