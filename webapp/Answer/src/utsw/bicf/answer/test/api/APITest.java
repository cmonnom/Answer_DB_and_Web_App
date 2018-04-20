package utsw.bicf.answer.test.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.Token;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/utsw/bicf/answer/test/all_components.xml" })
@WebAppConfiguration
public class APITest {
	
	@Autowired
	private WebApplicationContext wac ;
	
	@Autowired
	private ModelDAO modelDAO;
	
	private MockMvc mockMvc;
	
	@Before
	public void setup() throws Exception {
	    this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}
	

	@Test
	public void parseMDAEmail() throws Exception {
		Token token = modelDAO.getAPIToken();
	    this.mockMvc
	    .perform(get("/parseMDAEmail")
	    		.param("token", token.getToken()).param("emailPath", "1520937360_email.html"))
	    .andDo(print())
	    .andExpect(jsonPath("$.mrn").value("92166634"));
	}
}
