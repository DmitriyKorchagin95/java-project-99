//package hexlet.code.controller.api;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//@SpringBootTest
//@AutoConfigureMockMvc(addFilters = false)
//class UsersControllerTestTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Test
//    void createUser() throws Exception {
//
//        var body = """
//                {
//                  "firstname": "John",
//                  "lastname": "Doe",
//                  "email": "john@example.com",
//                  "birthday": "1995-05-10"
//                }
//                """;
//
//        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(body))
//                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").exists())
//                .andExpect(jsonPath("$.firstname").value("John")).andExpect(jsonPath("$.lastname").value("Doe"))
//                .andExpect(jsonPath("$.email").value("john@example.com"))
//                .andExpect(jsonPath("$.birthday").value("1995-05-10"));
//    }
//
//    @Test
//    void deleteUser() throws Exception {
//        var body = """
//                {
//                  "firstname": "Alex",
//                  "lastname": "White",
//                  "email": "alex@example.com",
//                  "birthday": "1993-11-30"
//                }
//                """;
//
//        var response = mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(body))
//                .andReturn();
//
//        var json = response.getResponse().getContentAsString();
//        var id = json.replaceAll(".*\"id\":(\\d+).*", "$1");
//
//        mockMvc.perform(delete("/api/users/{id}", id)).andExpect(status().isNoContent());
//    }
//
//    @Test
//    void getAllUsers() throws Exception {
//
//        mockMvc.perform(get("/api/users")).andExpect(status().isOk()).andExpect(jsonPath("$.content").isArray())
//                .andExpect(jsonPath("$.totalElements").exists()).andExpect(jsonPath("$.totalPages").exists())
//                .andExpect(jsonPath("$.size").exists()).andExpect(jsonPath("$.number").exists());
//    }
//
//    @Test
//    void getUserById() throws Exception {
//
//        var body = """
//                {
//                  "firstname": "Kate",
//                  "lastname": "Smith",
//                  "email": "kate@example.com",
//                  "birthday": "1998-02-15"
//                }
//                """;
//
//        var response = mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(body))
//                .andReturn();
//
//        var json = response.getResponse().getContentAsString();
//        var id = json.replaceAll(".*\"id\":(\\d+).*", "$1");
//
//        mockMvc.perform(get("/api/users/{id}", id)).andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(Integer.parseInt(id)))
//                .andExpect(jsonPath("$.firstname").value("Kate")).andExpect(jsonPath("$.lastname").value("Smith"))
//                .andExpect(jsonPath("$.email").value("kate@example.com"))
//                .andExpect(jsonPath("$.birthday").value("1998-02-15"));
//    }
//
//    @Test
//    void updateUser() throws Exception {
//
//        var createBody = """
//                {
//                  "firstname": "Mike",
//                  "lastname": "Brown",
//                  "email": "mike@example.com",
//                  "birthday": "1990-01-01"
//                }
//                """;
//
//        var response = mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(createBody))
//                .andReturn();
//
//        var json = response.getResponse().getContentAsString();
//        var id = json.replaceAll(".*\"id\":(\\d+).*", "$1");
//
//        var updateBody = """
//                {
//                  "firstname": "Michael",
//                  "lastname": "Brown",
//                  "email": "michael@example.com",
//                  "birthday": "1991-06-20"
//                }
//                """;
//
//        mockMvc.perform(put("/api/users/{id}", id).contentType(MediaType.APPLICATION_JSON).content(updateBody))
//                .andExpect(status().isOk()).andExpect(jsonPath("$.firstname").value("Michael"))
//                .andExpect(jsonPath("$.lastname").value("Brown"))
//                .andExpect(jsonPath("$.email").value("michael@example.com"))
//                .andExpect(jsonPath("$.birthday").value("1991-06-20"));
//    }
//}
