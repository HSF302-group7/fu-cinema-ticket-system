package com.fu.cinema.desktop.session;

import com.fu.cinema.desktop.facade.AuthFacade;
import com.fu.cinema.dto.SessionUser;
import com.fu.cinema.enums.Role;
import com.fu.cinema.exception.BusinessException;
import com.fu.cinema.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DesktopSessionTest {

    private DesktopSession session;

    @Mock
    private AuthService authService;

    private AuthFacade authFacade;

    @BeforeEach
    void setUp() {
        session = new DesktopSession();
        authFacade = new AuthFacade(authService);
    }

    @Test
    @DisplayName("DesktopSession: Quản trị viên đăng nhập thành công và kiểm tra quyền requireAdmin() pass")
    void testAdminSession() {
        SessionUser admin = new SessionUser(1L, null, "Admin User", Role.ADMIN);
        session.login(admin);

        assertEquals("Admin User", session.displayNameProperty().get());
        assertEquals(admin, session.getUser());
        assertDoesNotThrow(() -> session.requireAdmin());
    }

    @Test
    @DisplayName("DesktopSession: Chưa đăng nhập hoặc logout thì requireAdmin() ném BusinessException")
    void testRequireAdminWhenLoggedOut() {
        session.logout();
        BusinessException ex = assertThrows(BusinessException.class, () -> session.requireAdmin());
        assertEquals("Bạn không có quyền thực hiện thao tác này", ex.getMessage());
    }

    @Test
    @DisplayName("AuthFacade (BR-D1): Tài khoản CUSTOMER bị từ chối đăng nhập vào ứng dụng Desktop")
    void testCustomerCannotLoginDesktop() {
        SessionUser customer = new SessionUser(2L, 2L, "Customer An", Role.CUSTOMER);
        when(authService.login("customer@gmail.com", "Pass@123")).thenReturn(customer);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authFacade.loginAdmin("customer@gmail.com", "Pass@123"));
        assertEquals("Ứng dụng quầy vé chỉ dành cho nhân viên (tài khoản ADMIN)", ex.getMessage());
    }

    @Test
    @DisplayName("AuthFacade: Tài khoản ADMIN đăng nhập thành công vào Desktop")
    void testAdminCanLoginDesktop() {
        SessionUser admin = new SessionUser(1L, null, "Admin User", Role.ADMIN);
        when(authService.login("admin@fucinema.vn", "Admin@123")).thenReturn(admin);

        SessionUser result = authFacade.loginAdmin("admin@fucinema.vn", "Admin@123");
        assertNotNull(result);
        assertTrue(result.isAdmin());
        assertEquals("Admin User", result.getDisplayName());
    }
}

