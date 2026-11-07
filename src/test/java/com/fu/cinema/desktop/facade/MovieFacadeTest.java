package com.fu.cinema.desktop.facade;

import com.fu.cinema.desktop.model.GenreItem;
import com.fu.cinema.desktop.model.MovieRow;
import com.fu.cinema.desktop.model.PageResult;
import com.fu.cinema.desktop.session.DesktopSession;
import com.fu.cinema.dto.MovieForm;
import com.fu.cinema.entity.Genre;
import com.fu.cinema.entity.Movie;
import com.fu.cinema.enums.AgeRating;
import com.fu.cinema.enums.MovieStatus;
import com.fu.cinema.exception.BusinessException;
import com.fu.cinema.service.GenreService;
import com.fu.cinema.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieFacadeTest {

    @Mock
    private MovieService movieService;

    @Mock
    private GenreService genreService;

    @Mock
    private DesktopSession session;

    private MovieFacade movieFacade;

    @BeforeEach
    void setUp() {
        movieFacade = new MovieFacade(movieService, genreService, session);
    }

    @Test
    void testSearch_Success() {
        Genre genre = new Genre("Hành động", "Phim hành động kịch tính");
        genre.setGenreId(1L);

        Movie movie = new Movie();
        movie.setMovieId(100L);
        movie.setTitle("Avatar 2");
        movie.setGenre(genre);
        movie.setDuration(192);
        movie.setReleaseDate(LocalDate.of(2022, 12, 16));
        movie.setAgeRating(AgeRating.P);
        movie.setStatus(MovieStatus.SHOWING);

        when(movieService.searchForAdmin("Avatar", 1L, MovieStatus.SHOWING, 0, 10, "releaseDate"))
                .thenReturn(new PageImpl<>(List.of(movie)));

        PageResult<MovieRow> result = movieFacade.search("Avatar", 1L, MovieStatus.SHOWING, "releaseDate", 0);

        assertNotNull(result);
        assertEquals(1, result.items().size());
        assertEquals(100L, result.items().get(0).id());
        assertEquals("Avatar 2", result.items().get(0).title());
        assertEquals("Hành động", result.items().get(0).genreName());
        assertEquals(192, result.items().get(0).duration());
        assertEquals(LocalDate.of(2022, 12, 16), result.items().get(0).releaseDate());
        assertEquals(AgeRating.P, result.items().get(0).ageRating());
        assertEquals(MovieStatus.SHOWING, result.items().get(0).status());
    }

    @Test
    void testGenres() {
        Genre g1 = new Genre("Action", "Action desc");
        g1.setGenreId(1L);
        Genre g2 = new Genre("Horror", "Horror desc");
        g2.setGenreId(2L);

        when(genreService.findAll()).thenReturn(List.of(g1, g2));

        List<GenreItem> items = movieFacade.genres();

        assertEquals(2, items.size());
        assertEquals(1L, items.get(0).id());
        assertEquals("Action", items.get(0).name());
        assertEquals(2L, items.get(1).id());
        assertEquals("Horror", items.get(1).name());
    }

    @Test
    void testGetForm() {
        MovieForm form = new MovieForm();
        form.setMovieId(10L);
        form.setTitle("Dune 2");

        when(movieService.getForm(10L)).thenReturn(form);

        MovieForm result = movieFacade.getForm(10L);

        assertNotNull(result);
        assertEquals("Dune 2", result.getTitle());
    }

    @Test
    void testSaveCreate() {
        MovieForm form = new MovieForm();
        form.setTitle("Oppenheimer");

        movieFacade.save(form);

        verify(session).requireAdmin();
        verify(movieService).create(form);
    }

    @Test
    void testSaveUpdate() {
        MovieForm form = new MovieForm();
        form.setMovieId(20L);
        form.setTitle("Oppenheimer");

        movieFacade.save(form);

        verify(session).requireAdmin();
        verify(movieService).update(20L, form);
    }

    @Test
    void testDeleteSuccess() {
        when(movieService.delete(5L)).thenReturn(true);

        boolean result = movieFacade.delete(5L);

        assertTrue(result);
        verify(session).requireAdmin();
        verify(movieService).delete(5L);
    }

    @Test
    void testDeleteWithShows_ReturnsFalse() {
        when(movieService.delete(5L)).thenReturn(false);

        boolean result = movieFacade.delete(5L);

        assertFalse(result);
        verify(session).requireAdmin();
        verify(movieService).delete(5L);
    }

    @Test
    void testSave_WhenNotAdmin_ThrowsBusinessException() {
        doThrow(new BusinessException("Bạn không có quyền")).when(session).requireAdmin();

        MovieForm form = new MovieForm();
        assertThrows(BusinessException.class, () -> movieFacade.save(form));
    }

    @Test
    void testDelete_WhenNotAdmin_ThrowsBusinessException() {
        doThrow(new BusinessException("Bạn không có quyền")).when(session).requireAdmin();

        assertThrows(BusinessException.class, () -> movieFacade.delete(5L));
    }
}

