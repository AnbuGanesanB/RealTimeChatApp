package com.example.RealTimeChatApplication.configuration;

import com.example.RealTimeChatApplication.model.user.User;
import com.example.RealTimeChatApplication.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {

        String email = null;
        try{
            if (request instanceof ServletServerHttpRequest servletRequest) {

                HttpServletRequest req = servletRequest.getServletRequest();
                String jwtToken = req.getParameter("token");

                email = jwtService.extractEmail(jwtToken);
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
                User contextUser = (User)userDetails;

                if (jwtToken != null && jwtService.isTokenValid(jwtToken,contextUser)) {

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    auth.setDetails(new WebAuthenticationDetails(servletRequest.getServletRequest()));

                    SecurityContextHolder.getContext().setAuthentication(auth);

                    attributes.put("principal", auth);
                    attributes.put("user", contextUser);
                    return true;
                }
            }
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        } catch (ExpiredJwtException e) {
            System.out.println("WebSocket token expired");
            response.setStatusCode(HttpStatus.I_AM_A_TEAPOT);

            /*if (response instanceof ServerHttpResponseDecorator respDecorator) {
                System.out.println("Yes, Response is ServerHttpResponseDecorator");
                respDecorator.getHeaders().set("X-Auth-Failure", "JWT_EXPIRED");
            }*/
            return false;
        }

    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception ex) {}

}
