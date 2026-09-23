package com.railsetu.repository;

import com.railsetu.domain.Booking;
import com.railsetu.domain.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByPnrNumber(String pnrNumber);

    List<Booking> findByTrainIdAndJourneyDate(Long trainId, LocalDate journeyDate);

    List<Booking> findByTrainIdAndJourneyDateGreaterThanEqual(Long trainId, LocalDate journeyDate);

    long countByTrainIdAndJourneyDateGreaterThanEqualAndBookingStatusIn(Long trainId, LocalDate journeyDate, List<BookingStatus> statuses);

    @Query("SELECT b FROM Booking b WHERE " +
           "(:search IS NULL OR LOWER(b.pnrNumber) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "  OR LOWER(b.train.trainNumber) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "  OR LOWER(b.train.trainName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "  OR LOWER(b.contactEmail) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:status IS NULL OR b.bookingStatus = :status) AND " +
           "(:trainId IS NULL OR b.train.id = :trainId) AND " +
           "(:journeyDate IS NULL OR b.journeyDate = :journeyDate)")
    Page<Booking> searchBookings(@Param("search") String search,
                                @Param("status") BookingStatus status,
                                @Param("trainId") Long trainId,
                                @Param("journeyDate") LocalDate journeyDate,
                                Pageable pageable);

    @Query("SELECT COALESCE(SUM(b.totalFare), 0.0) FROM Booking b WHERE b.bookingStatus != 'CANCELLED'")
    Double calculateTotalGrossRevenue();

    @Query("SELECT COALESCE(SUM(b.totalFare), 0.0) FROM Booking b WHERE b.bookingStatus != 'CANCELLED' AND CAST(b.createdAt AS LocalDate) = :date")
    Double calculateTodayRevenue(@Param("date") LocalDate date);

    @Query("SELECT b.train.trainNumber, b.train.trainName, COUNT(b.id), COALESCE(SUM(b.totalFare), 0.0) " +
           "FROM Booking b WHERE b.bookingStatus != 'CANCELLED' GROUP BY b.train.trainNumber, b.train.trainName")
    List<Object[]> findRevenueByTrain();

    @Query("SELECT b.coachClass, COUNT(b.id), COALESCE(SUM(b.totalFare), 0.0) " +
           "FROM Booking b WHERE b.bookingStatus != 'CANCELLED' GROUP BY b.coachClass")
    List<Object[]> findRevenueByClass();

    long countByBookingStatus(BookingStatus status);
}
