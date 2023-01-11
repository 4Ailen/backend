package com.aliens.friendship.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.aliens.friendship.domain.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
