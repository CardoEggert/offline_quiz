package ee.project.offline.quiz.web;

import ee.project.offline.quiz.domain.Quiz;
import ee.project.offline.quiz.domain.dto.quiz.QuizDTO;
import ee.project.offline.quiz.service.QuizService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class QuizControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuizService quizService;

    @Test
    public void getNewQuizWithUserInfo() throws Exception{
        when(this.quizService.getNewQuiz("eesnimi perekonnanimi", "eesnimi.perekonnanimi@gmail.com")).thenReturn(new Quiz());
        when(this.quizService.convertQuiz(this.quizService.getNewQuiz("eesnimi perekonnanimi", "eesnimi.perekonnanimi@gmail.com"))).thenReturn(new QuizDTO());
        this.mockMvc.perform(get("/quiz/new/{user}/{email}", "eesnimi perekonnanimi", "eesnimi.perekonnanimi@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("questions").exists());
    }

    @Test
    public void getNewQuiz() throws Exception{
        when(this.quizService.getNewQuiz()).thenReturn(new Quiz());
        when(this.quizService.convertQuiz(this.quizService.getNewQuiz())).thenReturn(new QuizDTO());
        this.mockMvc.perform(get("/quiz/new"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("questions").exists());

    }

    @Test
    public void addQuestionsToDatabase() throws Exception{
        this.mockMvc.perform(post("/add/question")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "\t\"id\": null,\n" +
                        "\t\"description\": \"testSeletus\",\n" +
                        "\t\"hasPicture\": false,\n" +
                        "\t\"picturePath\": null,\n" +
                        "\t\"multipleChoice\": false,\n" +
                        "\t\"answers\": [\n" +
                        "\t\t{\n" +
                        "\t\t\t\"id\": null,\n" +
                        "\t\t\t\"description\": \"testSeletusVastus1\",\n" +
                        "\t\t\t\"points\": 5,\n" +
                        "\t\t\t\"correctAnswer\": false\n" +
                        "\t\t}, \n" +
                        "\t\t{\n" +
                        "\t\t\t\"id\": null,\n" +
                        "\t\t\t\"description\": \"testSeletusVastus2\",\n" +
                        "\t\t\t\"points\": 5,\n" +
                        "\t\t\t\"correctAnswer\": false\n" +
                        "\t\t}, \n" +
                        "\t\t{\n" +
                        "\t\t\t\"id\": null,\n" +
                        "\t\t\t\"description\": \"testSeletusVastus3\",\n" +
                        "\t\t\t\"points\": 5,\n" +
                        "\t\t\t\"correctAnswer\": true\n" +
                        "\t\t}, \n" +
                        "\t\t{\n" +
                        "\t\t\t\"id\": null,\n" +
                        "\t\t\t\"description\": \"testSeletusVastus4\",\n" +
                        "\t\t\t\"points\": 5,\n" +
                        "\t\t\t\"correctAnswer\": false\n" +
                        "\t\t}\n" +
                        "\t]\n" +
                        "}")).andExpect(status().isOk());
    }

}
