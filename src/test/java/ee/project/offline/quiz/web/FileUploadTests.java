package ee.project.offline.quiz.web;

import ee.project.offline.quiz.service.storage.FileSystemStorageService;
import ee.project.offline.quiz.service.storage.StorageFileNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//  refrence https://spring.io/guides/gs/uploading-files/
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class FileUploadTests {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private FileSystemStorageService storageService;

    @Test
    public void shouldSaveUploadedFile() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
                "text/plain", "Spring Framework".getBytes());
        this.mvc.perform(fileUpload("/upload").file(multipartFile))
                .andExpect(status().isOk());

        then(this.storageService).should().store(multipartFile);
    }

    @Test
    public void shouldSaveUploadedPicture() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.png",
                "image/png", "Spring Framework".getBytes());
        this.mvc.perform(fileUpload("/upload").file(multipartFile))
                .andExpect(status().isOk());

        then(this.storageService).should().store(multipartFile);
    }


    @SuppressWarnings("unchecked")
    @Test
    public void should404WhenMissingFile() throws Exception {
        given(this.storageService.loadAsResource("test.txt"))
                .willThrow(StorageFileNotFoundException.class);

        this.mvc.perform(get("/files/test.txt")).andExpect(status().isNotFound());
    }

}
