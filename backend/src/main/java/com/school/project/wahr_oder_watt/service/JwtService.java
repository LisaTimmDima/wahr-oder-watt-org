package com.school.project.wahr_oder_watt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  @Value("${app.jwt.secret}")
  private String secret;

  @Value("${app.jwt.expiration}")
  private long expirationMs;

  // Token erzeugen
  public String generateToken(String username) {
    Date now = new Date();
    Date exp = new Date(now.getTime() + expirationMs);
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(now)
        .setExpiration(exp)
        .signWith(SignatureAlgorithm.HS512, secret.getBytes(StandardCharsets.UTF_8))
        .compact();
  }

  public String extractUsername(String token) {
    return extractAllClaims(token).getSubject();
  }

  public boolean isValid(String token) {
    try {
      return !isExpired(token);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isTokenValid(String token, UserDetails user) {
    return extractUsername(token).equals(user.getUsername()) && isValid(token);
  }

  private boolean isExpired(String token) {
    Date exp = extractAllClaims(token).getExpiration();
    return exp != null && exp.before(new Date());
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
        .parseClaimsJws(token)
        .getBody();
  }
}