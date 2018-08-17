package com.uob.meniga;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.uob.meniga.model.BatchJobConfig;

import org.junit.Assert;
import org.mockito.Mockito;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import javax.sql.DataSource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MenigaExtractorTest.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("tables,transactions")
public class MenigaExtractorTest {
	
	@Rule
    public OutputCapture outputCapture = new OutputCapture();


	@MockBean
	public JobBuilderFactory jobBuilderFactory;
	
	@MockBean
	public StepBuilderFactory stepBuilderFactory;
	
	@MockBean
	public DataSource dataSource;
	
    @MockBean
    BatchJobConfig configuration;

    private String[] columnNames = new String[]{"CIF_NUMBER", "CPM_CODE", "CPM_INCENTIVE_VALUE", "CPM_INCENTIVE_TYPE", "CPM_DELIVERED", "CPM_CLICK", "CPM_CLAIM", "CPM_NOTIFIED"};
    private Object[] columnValues = new Object[]{"CIF_NUMBER", "CPM_CODE", 5.0, "AMOUNT", 1, 1, 1, 1};

    @Test
    public void testNoQuery() throws IOException {
        configuration.setQuery("");

        Assert.assertEquals(configuration.getQuery(),null);
        //Mockito.verify(namedParameterJdbcTemplate, Mockito.times(0)).query(Mockito.any(String.class), Mockito.any(Map.class), Mockito.any(RowCallbackHandler.class));
    }
	


}
