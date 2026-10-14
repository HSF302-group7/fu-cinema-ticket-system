package com.fu.cinema.service;

import com.fu.cinema.dto.RatingSummary;
import com.fu.cinema.dto.ReviewForm;
import com.fu.cinema.entity.Customer;
import com.fu.cinema.entity.Movie;
import com.fu.cinema.entity.Review;
import com.fu.cinema.entity.ReviewId;
import com.fu.cinema.exception.BusinessException;
import com.fu.cinema.repository.BookingRepository;
import com.fu.cinema.repository.CustomerRepository;
import com.fu.cinema.repository.MovieRepository;
import com.fu.cinema.repository.ReviewRepository;
import com.fu.cinema.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Test
    void canReview_whenCustomerIdNull_returnsFalse() {
        assertFalse(reviewService.canReview(null, 1L));
    }

    @Test
    void canReview_whenNotWatched_returnsFalse() {
        when(bookingRepository.countWatched(eq(1L), eq(2L), any(LocalDateTime.class))).thenReturn(0L);
        assertFalse(reviewService.canReview(1L, 2L));
    }

    @Test
    void canReview_whenWatched_returnsTrue() {
        when(bookingRepository.countWatched(eq(1L), eq(2L), any(LocalDateTime.class))).thenReturn(1L);
        assertTrue(reviewService.canReview(1L, 2L));
    }

    @Test
    void save_whenNotWatched_throwsBusinessException() {
        when(bookingRepository.countWatched(eq(1L), eq(2L), any(LocalDateTime.class))).thenReturn(0L);
        ReviewForm form = new ReviewForm();
        form.setRating(5);
        form.setComment("Phim hay");

        assertThrows(BusinessException.class, () -> reviewService.save(1L, 2L, form));
    }

    @Test
    void save_whenNewReview_createsReview() {
        when(bookingRepository.countWatched(eq(1L), eq(2L), any(LocalDateTime.class))).thenReturn(1L);
        when(reviewRepository.findById(new ReviewId(1L, 2L))).thenReturn(Optional.empty());
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        Movie movie = new Movie();
        movie.setMovieId(2L);
        when(customerRepository.getReferenceById(1L)).thenReturn(customer);
        when(movieRepository.getReferenceById(2L)).thenReturn(movie);

        ReviewForm form = new ReviewForm();
        form.setRating(5);
        form.setComment("Phim rat xuat sac");

        reviewService.save(1L, 2L, form);

        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void save_whenExistingReview_updatesReview() {
        when(bookingRepository.countWatched(eq(1L), eq(2L), any(LocalDateTime.class))).thenReturn(1L);
        Review existing = new Review();
        existing.setId(new ReviewId(1L, 2L));
        existing.setRating(3);
        existing.setComment("Binh thuong");
        when(reviewRepository.findById(new ReviewId(1L, 2L))).thenReturn(Optional.of(existing));

        ReviewForm form = new ReviewForm();
        form.setRating(5);
        form.setComment("Xem lai thay rat hay");

        reviewService.save(1L, 2L, form);

        assertEquals(5, existing.getRating());
        assertEquals("Xem lai thay rat hay", existing.getComment());
        verify(reviewRepository).save(existing);
    }

    @Test
    void getForm_whenReviewExists_returnsFormWithData() {
        Review existing = new Review();
        existing.setRating(4);
        existing.setComment("Tam on");
        when(reviewRepository.findById(new ReviewId(1L, 2L))).thenReturn(Optional.of(existing));

        ReviewForm form = reviewService.getForm(1L, 2L);

        assertNotNull(form);
        assertEquals(4, form.getRating());
        assertEquals("Tam on", form.getComment());
    }

    @Test
    void getForm_whenReviewDoesNotExist_returnsEmptyForm() {
        when(reviewRepository.findById(new ReviewId(1L, 2L))).thenReturn(Optional.empty());

        ReviewForm form = reviewService.getForm(1L, 2L);

        assertNotNull(form);
        assertNull(form.getRating());
        assertNull(form.getComment());
    }

    @Test
    void findByMovie_returnsReviewList() {
        when(reviewRepository.findByMovie_MovieIdOrderByReviewDateDesc(1L)).thenReturn(List.of());
        List<Review> result = reviewService.findByMovie(1L);
        assertNotNull(result);
        verify(reviewRepository).findByMovie_MovieIdOrderByReviewDateDesc(1L);
    }
}
