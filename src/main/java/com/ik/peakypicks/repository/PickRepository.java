package com.ik.peakypicks.repository;
import com.ik.peakypicks.model.Pick;
import com.ik.peakypicks.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PickRepository extends JpaRepository <Pick, Long>  {
    List<Pick> findByUser(User user);

   Optional<Pick> findByGoogleIdAndUser(String googleId, User user);

}
