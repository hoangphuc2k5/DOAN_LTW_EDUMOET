package com.stackoverflow;

import com.stackoverflow.model.*;
import com.stackoverflow.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class FeatureIntegrationTests {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private WebSocketService webSocketService;

    @Test
    @Transactional
    public void testQuestionCreationWithImagesAndGroup() throws IOException {
        // Create a test user
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        // Create a test group
        UserGroup group = new UserGroup();
        group.setName("Test Group");
        group.setCreator(user);
        group.getMembers().add(user);
        UserGroup savedGroup = groupService.createGroup(group);

        // Create a question with images
        Question question = new Question();
        question.setTitle("Test Question");
        question.setBody("Test Body");
        question.setAuthor(user);
        question.setGroup(savedGroup);

        Question savedQuestion = questionService.save(question);

        // Test image upload
        MockMultipartFile imageFile = new MockMultipartFile(
            "test-image.jpg",
            "test-image.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );

        ImageAttachment attachment = imageService.saveImage(imageFile, savedQuestion.getId(), null, user);

        // Verify everything was saved correctly
        assertNotNull(savedQuestion.getId());
        assertEquals(savedGroup.getId(), savedQuestion.getGroup().getId());
        assertNotNull(attachment.getId());
        assertEquals(savedQuestion.getId(), attachment.getQuestion().getId());
    }

    @Test
    @Transactional
    public void testGroupFunctionality() {
        // Create test users
        User creator = new User();
        creator.setUsername("creator");
        
        User member = new User();
        member.setUsername("member");

        // Create and verify group
        UserGroup group = new UserGroup();
        group.setName("Test Group");
        group.setCreator(creator);
        group.getMembers().add(creator);

        UserGroup savedGroup = groupService.createGroup(group);
        assertNotNull(savedGroup.getId());
        assertEquals(1, savedGroup.getMembers().size());

        // Add member
        groupService.addMember(savedGroup, member);
        assertEquals(2, savedGroup.getMembers().size());
        assertTrue(savedGroup.getMembers().contains(member));
    }

    @Test
    @Transactional
    public void testImageUploadAndValidation() {
        try {
            // Test valid image
            MockMultipartFile validImage = new MockMultipartFile(
                "valid-image.jpg",
                "valid-image.jpg",
                "image/jpeg",
                "test image content".getBytes()
            );

            // Test invalid file type
            MockMultipartFile invalidFile = new MockMultipartFile(
                "invalid-file.txt",
                "invalid-file.txt",
                "text/plain",
                "test content".getBytes()
            );

            User user = new User();
            user.setUsername("testuser");

            // Valid image should succeed
            ImageAttachment attachment = imageService.saveImage(validImage, null, null, user);
            assertNotNull(attachment.getId());

            // Invalid file should fail
            assertThrows(IllegalArgumentException.class, () -> {
                imageService.saveImage(invalidFile, null, null, user);
            });
        } catch (IOException e) {
            fail("Should not throw IOException during test: " + e.getMessage());
        }
    }

    @Test
    @Transactional
    public void testWebSocketNotifications() {
        User user = new User();
        user.setUsername("testuser");

        UserGroup group = new UserGroup();
        group.setName("Test Group");
        group.setCreator(user);
        group.getMembers().add(user);

        Question question = new Question();
        question.setTitle("Test Question");
        question.setBody("Test Body");
        question.setAuthor(user);
        question.setGroup(group);

        // Test notifications are sent without errors
        assertDoesNotThrow(() -> {
            webSocketService.notifyNewPost(group, question);
            webSocketService.notifyUser(user, "Test", "Test Message", "TEST");
        });
    }
}