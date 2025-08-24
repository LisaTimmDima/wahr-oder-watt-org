package com.school.project.wahr_oder_watt.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.function.Function;
import org.springframework.stereotype.Component;

/**
 * Utility-Klasse für die JWT-Token-Verwaltung.
 */
@Component
public class JwtUtil {
  private final String secretKey = "geheimes_jwt_secret";
  private final long jwtExpirationMs = 86400000; // 1 Tag in Millisekunden

  /** Generiert ein JWT-Token für den angegebenen Benutzernamen.
   *
   * @param username Der Benutzername, für den das Token generiert werden soll.
   * @return Das generierte JWT-Token.
   */
  public String generateToken(String username) {
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
        .signWith(SignatureAlgorithm.HS512, secretKey)
        .compact();
  }

  /** Extrahiert den Benutzernamen aus dem angegebenen JWT-Token.
   *
   * @param token Das JWT-Token, aus dem der Benutzername extrahiert werden soll.
   * @return Der extrahierte Benutzername.
   */
  public String getUsernameFromToken(String token) {
    return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
  }

  /**
   * Validiert das angegebene JWT-Token und prüft, ob der Benutzername im Token
   * mit dem Benutzernamen aus den UserDetails übereinstimmt.
   *
   * @param token Das zu validierende JWT-Token.
   * @param userDetails Die UserDetails des Benutzers, mit dem verglichen werden soll.
   * @return true, wenn das Token gültig ist und der Benutzername übereinstimmt, sonst false.
   */
  public boolean validateToken(String token, org.springframework.security.core.userdetails.UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && isTokenValid(token));
  }

  /**
   * Prüft, ob das JWT-Token gültig ist (z\.B\. nicht abgelaufen).
   *
   * @param token Das JWT-Token.
   * @return true, wenn das Token gültig ist, sonst false.
   */
  public boolean isTokenValid(String token) {
    try {
      final Date expiration = extractAllClaims(token).getExpiration();
      return expiration.after(new Date());
    } catch (Exception e) {
      return false;
    }
  }

  /** Extrahiert den Benutzernamen aus dem JWT-Token.
   *
   * @param token Das JWT-Token.
   * @return Der Benutzername.
   */
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /** Extrahiert ein bestimmtes Claim aus dem JWT-Token.
   *
   * @param token Das JWT-Token.
   * @param claimsResolver Funktion zur Extraktion des Claims.
   * @param <T> Der Typ des Claims.
   * @return Der extrahierte Claim.
   */
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /** Extrahiert alle Claims aus dem JWT-Token.
   *
   * @param token Das JWT-Token.
   * @return Die Claims des Tokens.
   */
  private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .setSigningKey(secretKey)
        .parseClaimsJws(token)
        .getBody();
  }
}
