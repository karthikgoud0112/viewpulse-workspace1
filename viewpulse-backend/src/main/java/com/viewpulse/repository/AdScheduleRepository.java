package com.viewpulse.repository;

import com.viewpulse.model.AdSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AdScheduleRepository extends JpaRepository<AdSchedule, Long> {
    
    // Find all schedules for a device
    List<AdSchedule> findByDeviceId(Long deviceId);
    
    // Find all schedules for a video
    List<AdSchedule> findByVideoId(Long videoId);
    
    // Find active schedules for a device
    List<AdSchedule> findByDeviceIdAndIsActiveTrue(Long deviceId);
    
    // Find schedules for current time and day
    @Query("SELECT s FROM AdSchedule s WHERE s.deviceId = :deviceId " +
           "AND s.isActive = true " +
           "AND (s.dayOfWeek = :dayOfWeek OR s.dayOfWeek = 'all') " +
           "AND s.startTime <= :currentTime " +
           "AND s.endTime >= :currentTime " +
           "ORDER BY s.priority DESC")
    List<AdSchedule> findActiveSchedulesForDeviceAndTime(
        @Param("deviceId") Long deviceId,
        @Param("dayOfWeek") String dayOfWeek,
        @Param("currentTime") LocalTime currentTime
    );
    
    // Delete all schedules for a device
    void deleteByDeviceId(Long deviceId);
    
    // Delete all schedules for a video
    void deleteByVideoId(Long videoId);
}
