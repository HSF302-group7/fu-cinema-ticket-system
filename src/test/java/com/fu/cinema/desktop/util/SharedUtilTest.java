package com.fu.cinema.desktop.util;

import com.fu.cinema.desktop.model.PageResult;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SharedUtilTest {

    @Test
    @DisplayName("MoneyUtil: Định dạng tiền VNĐ đúng chuẩn")
    void testMoneyUtilFormat() {
        assertEquals("150.000 đ", MoneyUtil.format(BigDecimal.valueOf(150000)));
        assertEquals("1.250.000 đ", MoneyUtil.format(BigDecimal.valueOf(1250000)));
        assertEquals("0 đ", MoneyUtil.format(null));
        assertEquals("0 đ", MoneyUtil.format(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("PageResult: Chuyển đổi từ Spring Page sang record PageResult đúng cấu trúc")
    void testPageResultOf() {
        List<String> raw = List.of("Movie A", "Movie B", "Movie C");
        var page = new PageImpl<>(raw, PageRequest.of(0, 10), 3);

        PageResult<String> result = PageResult.of(page, String::toUpperCase);

        assertEquals(3, result.items().size());
        assertEquals("MOVIE A", result.items().get(0));
        assertEquals(0, result.page());
        assertEquals(1, result.totalPages());
        assertEquals(3L, result.totalElements());
    }

    static class SampleForm {
        @NotBlank(message = "Tên không được để trống")
        private String name;

        public SampleForm(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Test
    @DisplayName("FxValidator: Bắt đúng lỗi ConstraintViolation của Bean Validation")
    void testFxValidator() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        FxValidator fxValidator = new FxValidator(validator);

        SampleForm invalidForm = new SampleForm("");
        Map<String, String> errors = fxValidator.validate(invalidForm);

        assertFalse(errors.isEmpty());
        assertTrue(errors.containsKey("name"));
        assertEquals("Tên không được để trống", errors.get("name"));

        SampleForm validForm = new SampleForm("FU Cinema");
        Map<String, String> noErrors = fxValidator.validate(validForm);
        assertTrue(noErrors.isEmpty());
    }
}

