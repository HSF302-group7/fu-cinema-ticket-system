package com.fu.cinema.desktop.session;

import com.fu.cinema.dto.SessionUser;
import com.fu.cinema.exception.BusinessException;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import org.springframework.stereotype.Component;

/**
 * Quản lý phiên đăng nhập của ứng dụng Desktop (thay cho HttpSession của Web).
 * Singleton: 1 người dùng / 1 cửa sổ ứng dụng.
 */
@Component
public class DesktopSession {

    private volatile SessionUser user; // Đọc được từ background thread
    private final ReadOnlyStringWrapper displayName = new ReadOnlyStringWrapper("Quản trị viên (Admin)");

    public void login(SessionUser user) { // Gọi trên FX thread
        this.user = user;
        displayName.set(user.getDisplayName());
    }

    public void logout() {
        this.user = null;
        displayName.set("");
    }

    public SessionUser getUser() {
        return user;
    }

    public ReadOnlyStringProperty displayNameProperty() {
        return displayName.getReadOnlyProperty();
    }

    /**
     * Defense in depth — kiểm tra quyền ở mọi thao tác ghi của Facade (BR-D1).
     */
    public void requireAdmin() {
        SessionUser u = user;
        if (u == null || !u.isAdmin()) {
            throw new BusinessException("Bạn không có quyền thực hiện thao tác này");
        }
    }
}
