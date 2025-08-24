package com.school.project.wahr_oder_watt.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter zur Verarbeitung von JWT-Authentifizierung bei jedem Request.
 * Überprüft das Authorization-Header auf ein gültiges JWT und setzt
 * die Authentifizierung im SecurityContext, falls das Token gültig ist.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final CustomUserDetailsService userDetailsService;

  /**
   * Konstruktor für JwtAuthenticationFilter.
   *
   * @param jwtUtil Dienst zur Verarbeitung von JWT-Token
   * @param userDetailsService Dienst zum Laden von Benutzerdetails
   */
  public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
    this.jwtUtil = jwtUtil;
    this.userDetailsService = userDetailsService;
  }

  /**
   * Filtert eingehende HTTP-Anfragen und prüft, ob ein gültiges JWT vorhanden ist.
   * Setzt die Authentifizierung im SecurityContext, wenn das Token gültig ist.
   *
   * @param request HTTP-Anfrage
   * @param response HTTP-Antwort
   * @param filterChain Filterkette
   * @throws ServletException bei Servlet-Fehlern
   * @throws IOException bei Ein-/Ausgabefehlern
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String authHeader = request.getHeader("Authorization");
    String token = null;
    String username = null;

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      token = authHeader.substring(7);
      username = jwtUtil.extractUsername(token);
    }

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      var userDetails = userDetailsService.loadUserByUsername(username);
      if (jwtUtil.validateToken(token, userDetails)) {
        var authToken = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }
    filterChain.doFilter(request, response);
  }
}