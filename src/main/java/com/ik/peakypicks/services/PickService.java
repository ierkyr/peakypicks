package com.ik.peakypicks.services;

import com.ik.peakypicks.dto.PickDTO;
import com.ik.peakypicks.model.Pick;
import com.ik.peakypicks.model.User;
import com.ik.peakypicks.repository.PickRepository;
import com.ik.peakypicks.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PickService {

    private PickRepository pickRepository;
    private UserRepository userRepository;

    @Autowired
    public PickService(PickRepository pickRepository, UserRepository userRepository) {
        this.pickRepository = pickRepository;
        this.userRepository = userRepository;
    }
    public String savePick(PickDTO pickDTO, String username) {
        Optional<User> user = userRepository.findByUsername(username);

        if (!user.isPresent()) {
            throw new IllegalArgumentException("User not found");
        }
        Optional<Pick> existingPick = pickRepository.findByGoogleIdAndUser(pickDTO.getGoogleId(), user.get());

        if (existingPick.isPresent()) {
            System.out.println("Pick already saved");
            return "Pick already saved for this user!";
        }

        Pick pick = new Pick(
                pickDTO.getGoogleId(),
                pickDTO.getName(),
                pickDTO.getLocation(),
                pickDTO.getRating(),
                pickDTO.getNumberOfReviews(),
                pickDTO.getTypes(),
                pickDTO.getPrimaryType(),
                pickDTO.getLowCordinates(),
                pickDTO.getHighCordinates()
        );
        pick.setUser(user);

        pickRepository.save(pick);  // Persist to DB
        return "Pick saved successfully!";
    }

    public String deletePick(String googleId, String username) {
        // Find the user by username
        Optional<User> user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            throw new IllegalArgumentException("User not found");
        }

        // Find the pick by googleId and user
        Optional<Pick> existingPick = pickRepository.findByGoogleIdAndUser(googleId, user.get());
        if (existingPick.isPresent()) {
            // Delete the pick if found
            pickRepository.delete(existingPick.get());
            return "Pick deleted successfully.";
        } else {
            // Throw an error if no matching pick is found
            throw new IllegalArgumentException("Pick not found for the given user.");
        }
    }


    public List<Pick> getUserPicks(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            return pickRepository.findByUser(userOptional.get());
        } else {
            throw new IllegalArgumentException("User not found with username: " + username);
        }
    }
    private String extractGoogleId(String googleIdJson) {
        // Ensure whitespace and quotes are handled properly
        googleIdJson = googleIdJson.trim();
        String regex = "\\{\"googleId\"\\s*:\\s*\"(.*?)\"}";
        return googleIdJson.replaceAll(regex, "$1");
    }

}
