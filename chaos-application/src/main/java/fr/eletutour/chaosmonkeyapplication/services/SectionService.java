package fr.eletutour.chaosmonkeyapplication.services;

import fr.eletutour.chaosmonkeyapplication.models.Video;
import fr.eletutour.chaosmonkeyapplication.repositories.VideoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SectionService {

    private final VideoRepository videoRepository;

    public SectionService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    public List<Video> getVideosBySection(String section) {
        // Since we didn't add the section field to the database schema update mechanism
        // yet (or maybe H2 creates it automatically in simple mode),
        // we might need to rely on the repository finding it, or filtering in memory if
        // we want to be safe in this transient state.
        // Assuming JPA and standard Spring Data magic:

        // Option 1: Add findBySection to Repository (Cleaner, but requires modifying
        // interface)
        // Option 2: Filter all videos (Slower, but safer for rapid prototyping without
        // touching many files)

        // Let's modify the Repository first if possible, but the user didn't explicitly
        // ask for it.
        // However, "exploiter ce champ" implies using it.
        // Given the user flow, I will filter in memory for now to avoid compilation
        // errors if I don't see the repository file to update it.
        // Wait, I can see all files. Let's check repository.

        return videoRepository.findAll().stream()
                .filter(v -> section.equalsIgnoreCase(v.getSection()))
                .collect(Collectors.toList());
    }
}
