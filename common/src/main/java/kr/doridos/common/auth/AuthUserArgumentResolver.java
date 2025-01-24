package kr.doridos.common.auth;

import jakarta.servlet.http.HttpServletRequest;
import kr.doridos.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@AllArgsConstructor
public class AuthUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final TokenUtil tokenUtil;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            throw new AuthException(ErrorCode.TOKEN_NOT_FOUND);
        }

        token = token.substring(7);

        return tokenUtil.extractUserInfo(token);
    }
}
