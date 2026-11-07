package com.fu.cinema.desktop.facade;

import com.fu.cinema.desktop.model.GenreRow;
import com.fu.cinema.desktop.session.DesktopSession;
import com.fu.cinema.dto.GenreForm;
import com.fu.cinema.dto.SessionUser;
import com.fu.cinema.entity.Genre;
import com.fu.cinema.enums.Role;
import com.fu.cinema.exception.BusinessException;
import com.fu.cinema.service.GenreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenreFacadeTest {

    @Mock
    private GenreService genreService;

    @Mock
    private DesktopSession session;

    private GenreFacade genreFacade;

    @BeforeEach
    void setUp() {
        genreFacade = new GenreFacade(genreService, session);
    }

    @Test
    void testFindAll() {
        Genre g1 = new Genre("Action", "Action movies");
        g1.setGenreId(1L);
        Genre g2 = new Genre("Comedy", "Comedy movies");
        g2.setGenreId(2L);

        when(genreService.findAll()).thenReturn(List.of(g1, g2));

        List<GenreRow> rows = genreFacade.findAll();

        assertEquals(2, rows.size());
        assertEquals("Action", rows.get(0).name());
        assertEquals("Action movies", rows.get(0).description());
        assertEquals("Comedy", rows.get(1).name());
    }

    @Test
    void testSaveNewGenre() {
        GenreForm form = new GenreForm();
        form.setGenreName("Drama");
        form.setDescription("Drama movies");

        genreFacade.save(form);

        verify(session).requireAdmin();
        verify(genreService).create(form);
    }

    @Test
    void testSaveUpdateGenre() {
        GenreForm form = new GenreForm();
        form.setGenreId(10L);
        form.setGenreName("Sci-Fi");
        form.setDescription("Science fiction");

        genreFacade.save(form);

        verify(session).requireAdmin();
        verify(genreService).update(10L, form);
    }

    @Test
    void testDeleteSuccess() {
        genreFacade.delete(5L);

        verify(session).requireAdmin();
        verify(genreService).delete(5L);
    }

    @Test
    void testDeleteBR05Violation() {
        doThrow(new BusinessException("Không thể xoá thể loại vì vẫn còn phim thuộc thể loại này!"))
                .when(genreService).delete(1L);

        BusinessException ex = assertThrows(BusinessException.class, () -> genreFacade.delete(1L));
        assertEquals("Không thể xoá thể loại vì vẫn còn phim thuộc thể loại này!", ex.getMessage());
        verify(session).requireAdmin();
    }
}
