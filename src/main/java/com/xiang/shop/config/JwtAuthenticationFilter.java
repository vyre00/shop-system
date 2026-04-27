package com.xiang.shop.config;

import com.xiang.shop.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 從 HTTP Header 拿出名為 "Authorization" 的票根
        String authHeader = request.getHeader("Authorization");

        // 2. 檢查票根是否存在，且是不是以 "Bearer " 開頭 (業界標準)
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // 擷取 Bearer 後面的亂碼

            // 3. 驗證這張票是不是偽造的、有沒有過期
            if (jwtUtil.validateToken(token)) {
                String account = jwtUtil.getAccountFromToken(token);
                String role = jwtUtil.getRoleFromToken(token);

                // 💡 亮點：Spring Security 的角色判斷，習慣上必須加上 "ROLE_" 前綴！
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

                // 4. 製作一張內部通行證，並把使用者資訊存入 Security 的上下文中
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(account, null, Collections.singletonList(authority));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 5. 檢查完畢，放行給下一個關卡
        filterChain.doFilter(request, response);
    }
}