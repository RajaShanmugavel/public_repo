package com.abc.raja.usermgmt.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.abc.raja.usermgmt.bean.UserBean;
import com.abc.raja.usermgmt.dao.UserString;
import com.abc.raja.usermgmt.dao.UserStringDao;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private UserStringDao dao;
	
	@MockBean
	private UserBean bean;
	
	@Test
	@Ignore
	public void shouldReturn() throws Exception{
		UserString usr = new UserString();
		given(this.bean.newUser()).willReturn(usr);
		doNothing().when(dao.save(usr));
	}
	
	
	

}
